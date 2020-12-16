package cn.las.bean.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Role {
    private int id;

    private String roleName;

    private String roleDesc;
}
