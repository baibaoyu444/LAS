package cn.las.bean.entity;

import lombok.Data;
import lombok.ToString;
import org.omg.CORBA.INTERNAL;

import java.util.List;

/**
 * laboratory : 储存实验室对象
 * user : 储存教师对象
 * course ：储存课程对象
 *
 * 这些都可以在查询数据库"课程安排信息"的时候顺便查出来
 */
@Data
@ToString
public class Arrange {

    private Integer id;

    private Integer laboratoryId;

    private Integer userId;

    private Integer courseId;

    private Integer classId;

    private Integer week;

    private Integer day;

    private Integer section;

    private Integer number;

    private Integer status;

    private Integer tag;

    private Double period;

    private Integer sectionEnum;
}
