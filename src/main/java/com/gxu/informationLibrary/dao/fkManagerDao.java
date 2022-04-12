package com.gxu.informationLibrary.dao;

import com.gxu.informationLibrary.entity.fk;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface fkManagerDao {
    void addFk(String db_name,String tb_name,String fk_name,String fk_column,String ref_table,String ref_column);
    void deleteFk();
    void insertFkInfo();
    void deleteFkInfo();
    List<String>getRefTable();
    List<String>getRefColumn(@Param("ref_table") String ref_table);
    List<fk>getFk();
}