package cn.las.bean.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Declare {

    private Integer id;

    private Integer type;

    private String reason;

    private Integer userId;

    private Integer status;

    private String changeweeks;

    private String changedays;

    private Integer changesectionenum;

    private String changeclassids;

    private Integer tag;
}
