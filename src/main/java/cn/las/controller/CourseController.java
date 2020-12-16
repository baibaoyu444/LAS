package cn.las.controller;

import cn.las.service.LaboratoryService;
import cn.las.bean.entity.Course;
import cn.las.bean.entity.Message;
import cn.las.service.CourseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 课程管理说明
 *
 * 1、课程添加和修改的权限都只有管理员权限可以更改
 * 2、...
 */
@Controller
@RequestMapping("course")
@Api(tags = "课程接口")
public class CourseController {

    @Autowired
    CourseService courseService;

    @Autowired
    LaboratoryService laboratoryService;

    /**
     * 查询所有的课程信息
     *
     * @return 返回带有所有course的 message
     * @throws Exception
     */
    @RequestMapping("findAll")
    @ResponseBody
    @ApiOperation(
            httpMethod = "GET",
            notes = "查询所有课程接口",
            value = "查询所有课程"
    )
    public Message findAll() {
        List<Course> all = null;
        try {
            all = courseService.findAll();
        } catch (Exception e) {
            return new Message(404, "数据获取失败");
        }
        Message message = new Message(200, "获取课程成功");
        message.putData("courses", all);
        return message;
    }

    /**
     * 增加课程功能
     *
     * @param course
     * {
     *     name:...,
     *     time:...
     * }
     * @return 操作成功与否
     */
    @RequestMapping(value = "addCourse", method = RequestMethod.POST)
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    @ApiOperation(
            httpMethod = "POST",
            notes = "新增课程接口</br>"+
                    "输入JSON数据: {\"name\": \"数据科学导论\",\"time\": 20,\"score\": 2.5}",
            value = "新增课程"
    )
    public Message addCourse(@RequestBody Course course) {

        // 这里抛出的错误和上面的有重复
        try {
            courseService.addCourse(course);
        } catch (Exception e) {
            e.printStackTrace();
            return new Message(501, e.getMessage());
        }

        return new Message(200, "课程添加成功");
    }

    /**
     * @param maps
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "findCourseByName", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(
            httpMethod = "GET",
            notes = "查询课程接口</br>"+
                    "输入JSON数据: {\"courseName\": \"数据科学导论\"}",
            value = "查询课程BY课程名"
    )
    public Message findCourseByCourseName(@RequestBody Map<String, Object> maps) {
        String cname = (String) maps.get("courseName");

        if(cname == null) return new Message(403, "课程名称非空");

        Course course = null;
        try {
            course = courseService.findCourseByCourseName(cname);
        } catch (Exception e) {
            e.printStackTrace();
            return new Message(500, "系统错误");
        }

        Message message = new Message(200, "课程查询成功");
        message.putData("course", course);
        return message;
    }

    /**
     *  按照id删除课程信息
     *
     * @param maps
     * {
     *     courseId:...
     * }
     * @return
     */
    @RequestMapping(value = "deleteById", method = RequestMethod.POST)
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    @ApiOperation(
            httpMethod = "POST",
            notes = "删除课程接口</br>"+
                    "输入JSON数据: {\"courseId\": 11}",
            value = "删除课程"
    )
    public Message deleteById(@RequestBody Map<String, Object> maps) {
        Integer courseId = (Integer) maps.get("courseId");
        if(courseId == null) return new Message(500, "删除课程ID不能为空");

        //之后删除课程信息
        try {
            courseService.removeCourseById(courseId);
        } catch (Exception e) {
            e.printStackTrace();
            return new Message(501, "课程删除失败");
        }
        return new Message(200, "删除课程成功");
    }

    /**
     * 按照课程的id修改课程信息
     *
     * @param course
     * {
     *     id:...,
     *     name:...,
     *     time:...
     * }
     * @return
     */
    @RequestMapping(value = "updateCourse", method = RequestMethod.POST)
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    @ApiOperation(
            httpMethod = "POST",
            notes = "修改课程接口</br>"+
                    "输入JSON数据: {\"id\": 5,\"name\": \"Linux入门\",\"time\": 30}",
            value = "修改课程"
    )
    public Message updateCourse(@RequestBody Course course) {
        // 更新课程信息
        try {
            courseService.updateCourse(course);
        } catch (Exception e) {
            e.printStackTrace();
            return new Message(501, e.getMessage());
        }
        return new Message(200, "修改课程信息成功");
    }

    @RequestMapping(value = "selectAll", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(
            httpMethod = "GET",
            notes = "查询所有课程接口",
            value = "查询所有课程"
    )
    public Message selectAll() throws Exception {
        List<Course> courses = courseService.selectAll();
        Message message = new Message(200, "获取课程信息成功");
        message.putData("course", courses);
        return message;
    }
}
