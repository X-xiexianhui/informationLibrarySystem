package com.gxu.informationLibrary.entity;

import lombok.Data;

@Data
public class menuInfo {
    int menu_id;
    String menu_name;
    String menu_level;
    int father_menu;
    int context_form;
}
