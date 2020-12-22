package cn.las.bean.dto;

import lombok.Data;
import lombok.ToString;

import java.util.Map;
import java.util.Set;

@Data
@ToString
public class ArrangeDTO {

    private Integer laboratoryId;

    private Integer userId;

    private Integer courseId;

    private Set<Integer> weeks;

    private Set<Integer> days;

    private Set<Integer> sections;

    private Integer sectionEnum;

    private Integer number;

    private Integer status;

    private Integer tag;

    private Double period;

    private String type;

    private String labName;

    private String userName;

    private String courseName;

    private Map<Integer, String> classes;

    private Set<Integer> classIds;
}
