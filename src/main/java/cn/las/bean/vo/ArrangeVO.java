package cn.las.bean.vo;

import lombok.Data;

import java.util.List;

@Data
public class ArrangeVO {
    private Integer id;

    private Integer laboratoryId;

    private Integer userId;

    private Integer courseId;

    private String courseName;

    private String labName;

    private String userName;

    private Integer week;

    private Integer day;

    private Integer section;

    private Integer number;

    private Integer status;

    private List<String> classList;
}
