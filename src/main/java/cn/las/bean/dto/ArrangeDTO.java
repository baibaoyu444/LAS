package cn.las.bean.dto;

import cn.las.bean.entity.Course;
import cn.las.bean.entity.Laboratory;
import cn.las.bean.entity.User;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Set;

@Data
@ToString
public class ArrangeDTO {

    private Integer laboratoryId;

    private Integer userId;

    private Integer courseId;

    private Set<Integer> weeks;

    private Integer day;

    private Set<Integer> sections;

    private Integer number;

    private Integer status;

    private String classes;

    private String type;

    private String tag;

    private Double period;

    private Laboratory laboratory;

    private User user;

    private Course course;

    private List<String> classList;

    private Integer sectionEnum;
}
