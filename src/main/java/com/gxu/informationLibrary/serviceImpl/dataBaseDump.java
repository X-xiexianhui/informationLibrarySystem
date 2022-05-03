package com.gxu.informationLibrary.serviceImpl;

import com.gxu.informationLibrary.dao.dbManageDao;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class dataBaseDump {
    private final dbManageDao dbManager;

    public dataBaseDump(dbManageDao dbManager) {
        this.dbManager = dbManager;
    }

    //    每周五下午5点备份
    @Scheduled(cron = "0 0 17 ? * 6")
    public void dump(){
        log.info("备份数据库");
        List<String>database=dbManager.getDatabaseList();
        log.info(dataBaseDumpTask(database));
    }

    //mysqldump --database db.. > ./dump/back.sql
    //备份
    public static @NotNull String dataBaseDumpTask(@NotNull List<String>databaseList) {
        PrintWriter printWriter = null;
        BufferedReader bufferedReader = null;
        String backTime = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
        File file = new File("./dump");
        if (!file.exists()) {
            log.info("文件夹./dump创建："+file.mkdir());
        }
        StringBuilder newCmd = new StringBuilder("mysqldump --databases information_library");
        for (String database: databaseList) {
            newCmd.append(" ").append(database);
        }
        File datafile = new File(file + File.separator+"backup_"+backTime+ ".sql");
        newCmd.append(" >").append(datafile);
        if (datafile.exists()) {
            return "backup_"+backTime+ ".sql" + "文件名已存在";
        }
        try {
            printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(datafile), StandardCharsets.UTF_8));
            Process process;
            String property = System.getProperty("os.name");
            System.out.println(property);
            if (property.contains("Linux")) {
                // linux
                process = Runtime.getRuntime().exec(new String[]{"bash", "-c", newCmd.toString()});
            } else {
                // 本地win
                process = Runtime.getRuntime().exec("docker exec -it mysql"+ newCmd);
            }
            InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8);
            bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                printWriter.println(line);
            }
            // 此次会执行过长时间,直到备份完成
            printWriter.flush();
            printWriter.close();
            //0 表示线程正常终止。
            if (process.waitFor() == 0) {
                // 线程正常执行
                log.info("【备份数据库】SUCCESS，SQL文件：{}", datafile);
            }
        }catch (Exception e) {
            e.printStackTrace();
            return "【备份数据库】FAILURE";
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (printWriter != null) {
                    printWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "【备份数据库】--END";
    }
    public static String rollBack(String fileName){
        try {
            String newCmd="mysql<"+fileName;
            Process process;
            String property = System.getProperty("os.name");
            System.out.println(property);
            if (property.contains("Linux")) {
                // linux
                process = Runtime.getRuntime().exec(new String[]{"bash", "-c",newCmd});
            } else {
                // 本地win
                process = Runtime.getRuntime().exec(newCmd);
            }
            if (process.waitFor() == 0) {
                // 线程正常执行
                log.info("成功还原数据库");
            }
        }catch (Exception e){
            return e.getCause().getMessage();
        }
        return "成功还原数据库";
    }
}
