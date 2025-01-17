package com.gxu.informationLibrary.serviceImpl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gxu.informationLibrary.dao.authDao;
import com.gxu.informationLibrary.dao.dataManageDao;
import com.gxu.informationLibrary.dao.dbManageDao;
import com.gxu.informationLibrary.dao.formManageDao;
import com.gxu.informationLibrary.entity.editEntity;
import com.gxu.informationLibrary.entity.recycleBin;
import com.gxu.informationLibrary.entity.response;
import com.gxu.informationLibrary.entity.statisticsResult;
import com.gxu.informationLibrary.server.dataServer;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;

import static com.gxu.informationLibrary.util.utils.getCookieByName;
import static com.gxu.informationLibrary.util.utils.updateCache;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class dataImpl implements dataServer {
    final dataManageDao dataManage;
    private final StringRedisTemplate redisTemplate;
    private final authDao authManage;
    private final formManageDao formManage;
    private final dataBaseDump dataDump;
    private final dbManageDao dbManager;

    public dataImpl(dataManageDao dataManage, StringRedisTemplate redisTemplate, authDao authManage, formManageDao formManage, dataBaseDump dataDump, dbManageDao dbManager) {
        this.dataManage = dataManage;
        this.redisTemplate = redisTemplate;
        this.authManage = authManage;
        this.formManage = formManage;
        this.dataDump = dataDump;
        this.dbManager = dbManager;
    }

    @Override
    public response<List<Map<String, String>>> getTableColumn(int form_id) {
        List<Map<String, String>> data = new ArrayList<>();
        try {
            data = dataManage.getTableColumn(form_id);
        } catch (Exception e) {
            return new response<>(500, e.getCause().getMessage(), data);
        }
        return new response<>(data);
    }

    @Override
    public response<String> insertData(String parma, HttpServletRequest request) {
        JSONObject insert = JSON.parseObject(parma);
        int form_id = insert.getInteger("form_id");
        Map<String, String> tb = dataManage.getTableByFormId(form_id);
        List<editEntity> columns = insert.getJSONArray("insert")
                .toJavaList(editEntity.class);
        String[] userCookie = Objects.requireNonNull(
                getCookieByName(request, "login_cookie")).split("_");
        columns.add(new editEntity("user", userCookie[1]));
        try {
            dataManage.insertData(tb.get("db_name"), tb.get("tb_name"), columns);
        } catch (Exception e) {
            return new response<>(500, e.getCause().getMessage(), "");
        }
        return new response<>("");
    }

    @Override
    public response<String> deleteData(String parma) {
        try {
            JSONObject deleteJSON = JSON.parseObject(parma);
            int form_id = deleteJSON.getIntValue("form_id");
            Map<String, String> tb = dataManage.getTableByFormId(form_id);
            int record_id = deleteJSON.getIntValue("record_id");
            JSONObject data = dataManage.queryDataById(tb.get("db_name"), tb.get("tb_name"), record_id);
            dataManage.removeToRecycle(form_id, data.toJSONString(), data.getString("user"));
            dataManage.deleteData(tb.get("db_name"), tb.get("tb_name"), record_id);
        } catch (Exception e) {
            return new response<>(500, e.getCause().getMessage(), "");
        }
        return new response<>("");
    }

    @Override
    public response<String> updateData(String parma) {
        try {
            JSONObject updateJSON = JSON.parseObject(parma);
            int form_id = updateJSON.getIntValue("form_id");
            Map<String, String> tb = dataManage.getTableByFormId(form_id);
            int record_id = updateJSON.getIntValue("record_id");
            List<editEntity> updates = updateJSON.getJSONArray("update").
                    toJavaList(editEntity.class);
            dataManage.updateData(tb.get("db_name"), tb.get("tb_name"), record_id, updates);
        } catch (Exception e) {
            return new response<>(500, e.getCause().getMessage(), "");
        }
        return new response<>("");
    }

    @Override
    public response<List<JSONObject>>
    queryData(String parma, HttpServletRequest request) {
        List<JSONObject> data = new ArrayList<>();
        try {
            JSONObject query = JSON.parseObject(parma);
            int form_id = query.getIntValue("form_id");
            String form_name = formManage.queryFormName(form_id);
            Map<String, String> tb = dataManage.getTableByFormId(form_id);
            List<editEntity> columns = query.getJSONArray("columns").toJavaList(editEntity.class);
            String[] userCookie = Objects.requireNonNull(getCookieByName(request, "login_cookie")).split("_");
            HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
            String auth;
            if (!"系统管理员".equals(userCookie[2])) {
                auth = hashOps.get("auth_" + userCookie[2] + "_" + form_name,
                        "search");
            } else {
                auth = "s1";
            }
            if (auth == null) {
                updateCache(userCookie, hashOps, authManage, form_name);
                auth = hashOps.get("auth_" + userCookie[2] + "_" + form_name,
                        "search");
            }
            data = dataManage.queryData(tb.get("db_name"), tb.get("tb_name"),
                    columns, "s0".equals(auth), userCookie[1]);
        } catch (Exception e) {
            return new response<>(500, e.getCause().getMessage(), data);
        }
        return new response<>(data);
    }

    @Override
    public response<statisticsResult> statistics(String parma, HttpServletRequest request) {
        statisticsResult data = new statisticsResult();
        List<Object> result = new ArrayList<>();
        List<Object> col_name = new ArrayList<>();
        List<Map<String, Object>> select;
        try {
            JSONObject statisticsJSON = JSON.parseObject(parma);
            int form_id = statisticsJSON.getIntValue("form_id");
            Map<String, String> tb = dataManage.getTableByFormId(form_id);
            String[] userCookie = Objects.requireNonNull(getCookieByName(request, "login_cookie")).split("_");
            select = dataManage.statistics(
                    statisticsJSON.getString("option"),
                    tb.get("db_name"), tb.get("tb_name"),
                    statisticsJSON.getString("field"),
                    statisticsJSON.getString("group_field"),
                    statisticsJSON.getBooleanValue("onlyUser"),
                    userCookie[1]);
            for (Map<String, Object> m : select) {
                result.add(m.get("result"));
                col_name.add(m.get("col_name"));
            }
            data.setResult(result);
            data.setCol_name(col_name);
        } catch (Exception e) {
            return new response<>(500, e.getCause().getMessage(), data);
        }
        return new response<>(data);
    }

    public response<String> uploadFile(@NotNull MultipartFile file) {
        response<String> res = new response<>("");
        File filePath = new File("./files");
        if (!filePath.exists()) {
            log.info("文件夹./dump创建：" + filePath.mkdir());
        }
        if (!file.isEmpty()) {
            try {
                BufferedOutputStream out = new BufferedOutputStream(
                        new FileOutputStream("./files/" + file.getOriginalFilename()));
                out.write(file.getBytes());
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
                res.setCode(500);
                res.setMsg("上传失败," + e.getMessage());
                return res;
            }
            res.setMsg("上传成功");

        } else {
            res.setCode(405);
            res.setMsg("上传失败，因为文件是空的.");
        }
        return res;
    }

    public void downloadFile(String parma, @NotNull HttpServletResponse response,HttpServletRequest request) throws IOException {
        // 读到流中
        JSONObject fileJSON = JSON.parseObject(parma);
        String file_name = fileJSON.getString("file_name");
        try {
            InputStream inputStream = new FileInputStream("./files/"+file_name);// 文件的存放路径
            response.setContentType("application/octet-stream");
            response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(file_name, "UTF-8"));
            ServletOutputStream outputStream = response.getOutputStream();
            byte[] b = new byte[1024];
            int len;
            //从输入流中读取一定数量的字节，并将其存储在缓冲区字节数组中，读到末尾返回-1
            while ((len = inputStream.read(b)) > 0) {
                outputStream.write(b, 0, len);
            }
            inputStream.close();
        }catch (Exception e){
                log.info(e.getMessage());
        }
    }

    public String dumpData() {
        List<String> database = dbManager.getDatabaseList();
        return dataDump.dataBaseDumpTask(database);
    }

    public String rollBack(String file_name) {
        return dataDump.rollBack(file_name);
    }

    public response<List<JSONObject>> getDumpList(String dump_time) {
        List<JSONObject> data = new ArrayList<>();
        try {
            data = dataManage.getDumpList(dump_time);
        } catch (Exception e) {
            return new response<>(500, e.getCause().getMessage(), data);
        }
        return new response<>(data);
    }

    public response<Boolean> restoreData(String parma) {
        List<editEntity> columns = new ArrayList<>();
        try {
            JSONObject dataJSON = JSON.parseObject(parma);
            int form_id = dataJSON.getIntValue("form_id");
            Map<String, String> tb = dataManage.getTableByFormId(form_id);
            JSONObject data = dataJSON.getJSONObject("data");
            Set<String> keys = data.keySet();
            for (String key : keys) {
                editEntity c = new editEntity(key, data.get(key));
                columns.add(c);
            }
            dataManage.insertData(tb.get("db_name"), tb.get("tb_name"), columns);
            dataManage.deleteRecycleData(dataJSON.getIntValue("del_id"));
        } catch (Exception e) {
            return new response<>(500, e.getCause().getMessage(), false);
        }
        return new response<>(true);
    }

    public response<Boolean>restoreAllData(int form_id,HttpServletRequest request){
        try {
            String[] userCookie = Objects.requireNonNull(getCookieByName(request, "login_cookie")).split("_");
            List<recycleBin>data = dataManage.queryRecycleData(form_id,userCookie[1], new ArrayList<>());
            Map<String, String> tb = dataManage.getTableByFormId(form_id);
            for (recycleBin r: data) {
                JSONObject d=r.getData();
                Set<String>keys=d.keySet();
                List<editEntity> columns = new ArrayList<>();
                for (String key : keys) {
                    editEntity c = new editEntity(key, d.get(key));
                    columns.add(c);
                }
                dataManage.insertData(tb.get("db_name"), tb.get("tb_name"), columns);
                dataManage.deleteRecycleData(r.getDel_id());
            }
        }catch (Exception e){
            return new response<>(500,e.getCause().getMessage(),false);
        }
        return new response<>(true);
    }

    public response<Boolean>deleteRecycleData(int id){
        try {
            dataManage.deleteRecycleData(id);
        }catch (Exception e){
            return new response<>(500,e.getCause().getMessage(),false);
        }
        return new response<>(true);
    }

    public response<Boolean>clearRecycle(HttpServletRequest request,int form_id){
        try {
            String[] userCookie = Objects.requireNonNull(getCookieByName(request, "login_cookie")).split("_");
            dataManage.clearRecycle(userCookie[1], form_id);
        }catch (Exception e){
            return new response<>(500,e.getCause().getMessage(),false);
        }
        return new response<>(true);
    }

    public response<List<recycleBin>>queryRecycleData(String parma, HttpServletRequest request){
        List<recycleBin>data=new ArrayList<>();
        try {
            JSONObject query = JSON.parseObject(parma);
            int form_id = query.getIntValue("form_id");
            List<editEntity> columns = query.getJSONArray("columns").toJavaList(editEntity.class);
            String[] userCookie = Objects.requireNonNull(getCookieByName(request, "login_cookie")).split("_");
            data=dataManage.queryRecycleData(form_id,userCookie[1],columns);
        }catch (Exception e){
            return new response<>(500,e.getCause().getMessage(),data);
        }
        return new response<>(data);
    }
}
