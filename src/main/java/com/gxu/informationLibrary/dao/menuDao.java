package com.gxu.informationLibrary.dao;

import com.gxu.informationLibrary.entity.menuInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface menuDao {
    void addMenu();

    void deleteMenu();

    List<menuInfo>query();

    List<menuInfo>getMenuById();

    List<Map<String,Object>>getMenuSelect();

    List<Map<String, Object>>getFormSelect();

    void editMenu();
}
