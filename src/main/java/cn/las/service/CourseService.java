package cn.las.service;

import cn.las.bean.entity.Course;

import java.util.List;

public interface CourseService {
    void addCourse(Course course) throws Exception;

    Course findCourseById(Integer id) throws Exception;

    Course findCourseByCourseName(String courseName) throws Exception;

    void removeCourseById(Integer id) throws Exception;

    void removeCourseByCourseName(String courseName) throws Exception;

    List<Course> findAll() throws Exception;

    void updateCourse(Course course) throws Exception;

    List<Course> selectAll() throws Exception;
}
