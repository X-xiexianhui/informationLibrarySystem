package com.gxu.informationLibrary.controller;

import com.alibaba.fastjson.JSONObject;
import com.gxu.informationLibrary.entity.response;
import com.gxu.informationLibrary.serviceImpl.dataImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@ResponseBody
public class dataController {
    private final dataImpl dataManage;

    public dataController(dataImpl dataManage) {
        this.dataManage = dataManage;
    }
    @GetMapping("api/data/column")
    public response<List<Map<String,String>>>getTableColumn(@RequestParam("form_id")int form_id){
        return dataManage.getTableColumn(form_id);
    }
    @GetMapping("api/data/get")
    public response<List<JSONObject>>getTableData(@RequestParam("form_id")int form_id){
        return dataManage.getTableData(form_id);
    }
    @PostMapping("api/data/add")
    public response<String>insertData(@RequestBody String parma, HttpServletRequest request){
        return dataManage.insertData(parma, request);
    }
    @PostMapping("api/file/add")
    public response<String>uploadFile(@RequestParam("file") MultipartFile file){
        return dataManage.uploadFile(file);
    }
}
