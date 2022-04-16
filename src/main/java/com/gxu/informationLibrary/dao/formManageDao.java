package com.gxu.informationLibrary.dao;

import com.gxu.informationLibrary.entity.formStruct;
import com.gxu.informationLibrary.entity.formTable;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface formManageDao {
    List<formTable>query(@Param("tb_name")String tb_name);

    void renameForm(@Param("old_name") String old_name,@Param("new_name") String new_name);

    void editForm(@Param("formStructList") List<formStruct>formStructList);

    List<formStruct> getFormStruct(@Param("form_id") int form_id);
}
