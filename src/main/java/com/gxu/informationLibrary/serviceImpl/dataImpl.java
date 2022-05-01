package com.gxu.informationLibrary.serviceImpl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gxu.informationLibrary.dao.dataManageDao;
import com.gxu.informationLibrary.entity.editEntity;
import com.gxu.informationLibrary.entity.response;
import com.gxu.informationLibrary.server.dataServer;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class dataImpl implements dataServer {
    final dataManageDao dataManage;

    public dataImpl(dataManageDao dataManage) {
        this.dataManage = dataManage;
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
    public response<List<JSONObject>> getTableData(int form_id) {
        List<JSONObject> data = new ArrayList<>();
        try {
            Map<String, String> tb = dataManage.getTableByFormId(form_id);
            data = dataManage.getData(tb.get("db_name"), tb.get("tb_name"), false);
        } catch (Exception e) {
            return new response<>(500, e.getCause().getMessage(), data);
        }
        return new response<>(data);
    }

    @Override
    public response<String> insertData(String parma) {
        JSONObject insert = JSON.parseObject(parma);
        int form_id = insert.getInteger("form_id");
        Map<String, String> tb = dataManage.getTableByFormId(form_id);
        List<editEntity> columns = insert.getJSONArray("insert").toJavaList(editEntity.class);
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
            dataManage.deleteData(tb.get("db_name"), tb.get("tb_name"), record_id);
        }catch (Exception e){
            return new response<>(500,e.getCause().getMessage(),"");
        }

        return new response<>("");
    }

    @Override
    public response<List<JSONObject>> queryData(String parma) {
        JSONObject query = JSON.parseObject(parma);
        String db_name = query.getString("db_name");
        String tb_name = query.getString("tb_name");
        List<editEntity> columns = query.getJSONArray("columns").toJavaList(editEntity.class);
        return null;
    }

    @Override
    public response<String> updateData(String parma) {
        try {
            JSONObject updateJSON = JSON.parseObject(parma);
            int form_id = updateJSON.getIntValue("form_id");
            Map<String, String> tb = dataManage.getTableByFormId(form_id);
            int record_id = updateJSON.getIntValue("record_id");
            List<editEntity> updates = updateJSON.getJSONArray("update").toJavaList(editEntity.class);
            dataManage.updateData(tb.get("db_name"), tb.get("tb_name"), record_id, updates);
        } catch (Exception e) {
            return new response<>(500, e.getCause().getMessage(), "");
        }
        return new response<>("");
    }

    public response<String> uploadFile(@NotNull MultipartFile file) {
        response<String> res = new response<>("");
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
}
