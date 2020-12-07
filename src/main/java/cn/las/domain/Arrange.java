package cn.las.domain;

import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Arrays;
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

    private int id;

    private int laboratoryId;

    private int userId;

    private int courseId;

    private int day;

    private int section;

    private int week;

    private int number;

    private int status;

    private List<Integer> weeks;

    private User user;

    private Laboratory laboratory;

    private Course course;

    private String classes;

    private List<String> classInfo;
}
