package cn.las.bean.dto;

import cn.las.bean.entity.Course;
import cn.las.bean.entity.Laboratory;
import cn.las.bean.entity.User;
import lombok.Data;

import java.util.List;

@Data
public class ArrangeDTO {
    private Integer id;

    private Integer laboratoryId;

    private Integer userId;

    private Integer courseId;

    private Laboratory laboratory;

    private User user;

    private Course course;

    private Integer week;

    private Integer day;

    private Integer section;

    private Integer number;

    private Integer status;

    private List<String> classList;
}
