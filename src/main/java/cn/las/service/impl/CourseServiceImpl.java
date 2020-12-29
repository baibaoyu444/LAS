package cn.las.service.impl;

import cn.las.dao.ArrangeDao;
import cn.las.dao.CourseDao;
import cn.las.bean.entity.Course;
import cn.las.mapper.CourseMapper;
import cn.las.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    CourseDao courseDao;

    @Autowired
    CourseMapper courseMapper;

    @Autowired
    ArrangeDao arrangeDao;

    public void addCourse(Course course) throws Exception {
        if(course.getName() == null) throw new IllegalArgumentException("课程名不为空");
        if(course.getTime() <= 0) throw new IllegalArgumentException("课时不得为空");
        if(courseDao.findCourseByCourseName(course.getName()) != null)
            throw new Exception("课程已存在");
        courseDao.addCourse(course);
    }

    public Course findCourseById(Integer id) throws Exception {
        return courseDao.findCourseById(id);
    }

    public Course findCourseByCourseName(String courseName) throws Exception {
        return courseDao.findCourseByCourseName(courseName);
    }

    public void removeCourseById(Integer id) throws Exception {

        // 删除课程的排课数据
        arrangeDao.removeByCourseId(id);
        // 删除课程数据
        courseDao.removeCourseById(id);
    }

    public void removeCourseByCourseName(String courseName) throws Exception {
        courseDao.removeCourseByCourseName(courseName);
    }

    public List<Course> findAll() throws Exception {
        return courseDao.findAll();
    }

    public void updateCourse(Course course) throws Exception {
        if(course.getId() == 0) throw new IllegalArgumentException("待修改课程id非空");
        if(course.getName() == null) throw new IllegalArgumentException("课程名非空");
        if(course.getTime() <= 0) throw new IllegalArgumentException("课时不符合要求");
        courseDao.updateCourse(course);
    }
    
    public List<Course> selectAll() throws Exception {
        return courseMapper.selectAll();
    }

    @Override
    public HashMap<Integer, Map<String, Object>> getCourseInfo() throws Exception {
        return courseDao.getCourseInfo();
    }
}
