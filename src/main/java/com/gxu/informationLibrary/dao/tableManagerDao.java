package com.gxu.informationLibrary.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import org.springframework.stereotype.Repository;

import java.util.List;


@Mapper
@Repository
public interface tableManagerDao {
    void createTable(@Param("column") List column,@Param("pks") List pks, @Param("dbName") String dbName, @Param("tbName") String tbName);
    List<String>getColumns(@Param("dbName")String dbName,@Param("tbName")String tbName);
}