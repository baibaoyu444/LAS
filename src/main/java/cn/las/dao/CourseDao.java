package cn.las.dao;

import cn.las.domain.Course;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface CourseDao {

    @Insert("insert into course(name, time) values(#{name},#{time})")
    void addCourse(Course course) throws Exception;

    @Select("select * from course where course.id=#{id}")
    Course findCourseById(Integer id) throws Exception;

    @Select("select * from course where course.name=#{courseName}")
    Course findCourseByCourseName(String courseName) throws Exception;

    @Delete("delete from course where course.id=#{id}")
    void removeCourseById(Integer id) throws Exception;

    @Delete("delete from course where course.name=#{courseName}")
    void removeCourseByCourseName(String courseName) throws Exception;

    @Select("select * from course")
    List<Course> findAll() throws Exception;

    @Update("update course set course.name=#{name},course.time=#{time} where course.id=#{id}")
    void updateCourse(Course course) throws Exception;

}
