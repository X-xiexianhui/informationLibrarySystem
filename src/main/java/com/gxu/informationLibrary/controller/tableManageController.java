package com.gxu.informationLibrary.controller;


import com.alibaba.fastjson.JSONObject;
import com.gxu.informationLibrary.entity.response;
import com.gxu.informationLibrary.serviceImpl.tbManageImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@Controller
@ResponseBody
public class tableManageController {
    final tbManageImpl tbManage;

    public tableManageController(tbManageImpl tbManage) {
        this.tbManage = tbManage;
    }

    @PostMapping("/tb/add")
    public response<List<JSONObject>> addTable(@RequestBody String Param){
        List<JSONObject>data=new ArrayList<>();
        try {
            data=tbManage.createTable(Param);
        } catch (Exception e){
            return new response<>(500, String.valueOf(e.getMessage()), data);
        }
        return new response<>(data);
    }
    @DeleteMapping("/tb/delete")
    public response<String>deleteTable(@RequestParam("db_name")String db_name, @RequestParam("tb_name")String tb_name){
        try {
            tbManage.deleteTable(db_name, tb_name);
        }catch (Exception e){
            return new response<>(500,e.getMessage(),"");
        }
        return new response<>("");
    }
    @GetMapping("/tb/test")
    public response<List<JSONObject>>test(@RequestParam("db_name")String db_name, @RequestParam("tb_name")String tb_name){
        List<JSONObject> data=new ArrayList<>();
        try {
            data=this.tbManage.test(db_name, tb_name);
        }catch (Exception e){
            return new response<>(500,e.getMessage(),data);
        }
        return new response<>(data);
    }
}