package cn.las.domain;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class User {

    private int id;

    private String username;

    private String password;

    private String teacher;

    private Role role;

    private Integer roleId;

    private String phone;

    private String email;
}
