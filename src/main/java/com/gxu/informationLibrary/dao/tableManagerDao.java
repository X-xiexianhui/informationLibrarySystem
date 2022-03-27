package com.gxu.informationLibrary.dao;


import com.alibaba.fastjson.JSONObject;
import com.gxu.informationLibrary.entity.table;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Mapper
@Repository
public interface tableManagerDao {
    void createTable(@Param("column") List column,@Param("pks") List pks, @Param("db_name") String db_name, @Param("tb_name") String tb_name);
    void deleteTable(@Param("db_name")String db_name, @Param("tb_name")String tb_name);
    List<table>searchTables(@Param("tb_name")String tb_name);
    void alterTable(@Param("column") List column, @Param("pks") List pks, @Param("db_name") String db_name, @Param("tb_name") String tb_name);
    List<JSONObject>getColumnList(@Param("db_name")String db_name,@Param("tb_name")String tb_name);
    List<JSONObject>test(@Param("db_name")String db_name, @Param("tb_name")String tb_name);
}