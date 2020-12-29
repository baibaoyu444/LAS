package cn.las.dao;

import cn.las.bean.entity.Course;
import org.apache.ibatis.annotations.*;
import org.springframework.context.expression.MapAccessor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface CourseDao {

    @Insert("insert into course(name, time, score) values(#{name},#{time},#{score})")
    void addCourse(Course course) throws Exception;

    @Select("select * from course where id=#{id}")
    Course findCourseById(Integer id) throws Exception;

    @Select("select * from course where name=#{courseName}")
    Course findCourseByCourseName(String courseName) throws Exception;

    @Delete("delete from course where id=#{id}")
    void removeCourseById(Integer id) throws Exception;

    @Delete("delete from course where name=#{courseName}")
    void removeCourseByCourseName(String courseName) throws Exception;

    @Select("select * from course")
    List<Course> findAll() throws Exception;

    @Update("update course set name=#{name},time=#{time},score=#{score} where id=#{id}")
    void updateCourse(Course course) throws Exception;

    @Select("select name from course where id=#{id}")
    String selectNameById(Integer id) throws Exception;

    @Select("select id, name from course")
    @MapKey("id")
    HashMap<Integer, Map<String, Object>> getCourseInfo() throws Exception;
}
