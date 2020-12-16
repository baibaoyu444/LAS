package cn.las.mapper;

import cn.las.bean.entity.Course;

import java.util.List;

public interface CourseMapper {
    List<Course> selectAll() throws Exception;
}
