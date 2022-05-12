package com.gxu.informationLibrary.serviceImpl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gxu.informationLibrary.dao.formManageDao;
import com.gxu.informationLibrary.dao.menuDao;
import com.gxu.informationLibrary.entity.menuInfo;
import com.gxu.informationLibrary.entity.response;
import com.gxu.informationLibrary.server.menuServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class menuImpl implements menuServer {
    final menuDao menu;
    final formManageDao formManage;
    private final PlatformTransactionManager platformTransactionManager;
    public menuImpl(menuDao menu, formManageDao formManage, PlatformTransactionManager platformTransactionManager) {
        this.menu = menu;
        this.formManage = formManage;
        this.platformTransactionManager = platformTransactionManager;
    }

    @Override
    public List<menuInfo> addMenu(String param) {
        menuInfo menuObject=JSON.parseObject(param).toJavaObject(menuInfo.class);
        menu.addMenu(menuObject);
        return menu.query("");
    }

    @Override
    public List<menuInfo> deleteMenu(String param) {
        int menu_id=JSON.parseObject(param).getIntValue("menu_id");

        menu.deleteMenu(menu_id);
        return menu.query("");
    }

    @Override
    public List<menuInfo> queryMenu(String menu_name) {
        return menu.query(menu_name);
    }

    public menuInfo queryMenu(int menu_id) {
        return menu.getMenuById(menu_id);
    }

    public List<Map<String, Object>> queryMenu() {
        return menu.getMenuSelect();
    }

    @Override
    public response<List<menuInfo>> editMenu(String param) {
        List<menuInfo>data=new ArrayList<>();
        try {
            JSONObject menuJSON=JSON.parseObject(param);
            JSONArray update = menuJSON.getJSONArray("update");
            int menu_id = menuJSON.getIntValue("menu_id");
            int submenu = menu.countSubMenu(menu_id);
            for (int i = 0; i < update.size(); i++) {
                JSONObject up = update.getJSONObject(i);
                String col_name = up.getString("col_name");
                if (col_name.equals("menu_level")&&submenu>0){
                    platformTransactionManager.rollback(TransactionAspectSupport.currentTransactionStatus());
                    return new response<>(500,"该菜单有子菜单不可更改为二级菜单",menu.query(""));
                }
                menu.editMenu(col_name, up.getString("value"),menu_id);
            }
            data=menu.query("");
        }catch (Exception e){
            return new response<>(500,e.getCause().getMessage(),data);
        }


        return new response<>(data);
    }
    public List<Map<String,Object>> initMenu(){
        return menu.initMenu();
    }
}
