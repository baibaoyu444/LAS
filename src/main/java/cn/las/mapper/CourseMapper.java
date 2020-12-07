package cn.las.mapper;

import cn.las.domain.Course;

import java.util.List;

public interface CourseMapper {
    List<Course> selectAll() throws Exception;
}
