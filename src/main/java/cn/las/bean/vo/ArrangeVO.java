package cn.las.bean.vo;

import cn.las.bean.entity.Course;
import cn.las.bean.entity.Laboratory;
import cn.las.bean.entity.User;
import lombok.Data;

import java.util.List;

@Data
public class ArrangeVO {
    private Integer id;

    private Laboratory laboratory;

    private User user;

    private Course course;

    private Integer week;

    private Integer day;

    private Integer section;

    private Integer number;

    private Integer status;

    private List<String> classes;
}
