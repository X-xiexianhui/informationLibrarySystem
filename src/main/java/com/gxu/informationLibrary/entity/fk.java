package com.gxu.informationLibrary.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class fk {
    String fk_name;
    String fk_column;
    String ref_table;
    String ref_column;
}
