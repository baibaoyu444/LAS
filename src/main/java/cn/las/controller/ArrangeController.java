package cn.las.controller;

import cn.las.domain.*;
import cn.las.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import java.util.*;

/**
 * 关于选课的控制器
 *
 * 功能设计
 *
 * 查询模块
 * 1、查询所有排课信息 并且转换为前端容易显示的页面（1）
 * 2、按照一个arrange对象查询所有符合需求的排课信息  arrange对象当中可能有些属性不存在
 * 3、按照教师的id查询课程
 * 4、查询每个实验室的排课信息
 * 5、分页查询（整合到上面的功能当中，按照页号和页面大小查询数据）
 *
 * 增加模块
 * 1、增加一个数组类型的arrange 任何冲突都要考虑进去（时间冲突、实验室冲突、实验室和课程信息冲突  等等等...）
 *      返回申请的信息哪里有异常，必须详细设计，状态是0的排课信息不算在当中
 * 2、增加几条审核信息，排课状态是0-待审核 状态
 * 3、检查排课课时是否超出限制或者课时是否被安排完毕
 *
 * 删除模块
 * 1、按照一个arrange对象删除课程信息（主要是使用当中的课程id，实验室id，教师id，weeks，day，section）
 * 2、
 *
 *
 * 修改模块
 * 1、按照一个旧的arrange对象和新的arrange对象修改多条排课信息
 * 2、排课审核功能，修改审核通过的排课的状态信息
 *
 *
 *
 * 剩下其他没写到的功能，参考需求分析
 */
@Controller
@RequestMapping("arrange")
@Api(tags = "排课接口")
public class ArrangeController {

    // 注入arrange服务层对象
    @Autowired
    ArrangeService arrangeService;

    @Autowired
    LaboratoryService laboratoryService;

    @Autowired
    IClassService iClassService;

    @Autowired
    UserService userService;

    @Autowired
    CourseService courseService;


    /**
     * 处理课程表功能-白宝玉
     *
     * @param all
     * @return
     */
    private List<Arrange> process(List<Arrange> all) {
        // 进行排课数据的整理
        List<Arrange> arranges = new ArrayList<Arrange>();
        Arrange arrange = null;
        for (Arrange arr : all) {
            if (arrange == null) {
                arrange = arr;
                arr.setWeeks(new ArrayList<Integer>());
                arr.getWeeks().add(arr.getWeek());
                continue;
            }
            // 确保数据是一致的
            if (
                    arrange.getLaboratoryId() == arr.getLaboratoryId() &&
                            arrange.getUserId() == arr.getUserId() &&
                            arrange.getCourseId() == arr.getCourseId() &&
                            arrange.getDay() == arr.getDay() &&
                            arrange.getSection() == arr.getSection()
                          //  arrange.getClasses().equals(arr.getClasses())
            ) {
                arrange.getWeeks().add(arr.getWeek());
            } else {
                arranges.add(arrange);
                arr.setWeeks(new ArrayList<Integer>());
                arr.getWeeks().add(arr.getWeek());
                arrange = arr;
            }
        }
        arranges.add(arrange);
        return arranges;
    }

    /**
     * 获取课程表的二维数组-白宝玉
     *
     * @param list
     * @return
     */
    private List<Arrange>[][] getCourseList(List<Arrange> list) {

        // 对传入的list进行遍历
        for(Arrange arrange : list) {
            System.out.println(arrange);
        }

        List<Arrange> [][] arrs = new ArrayList[6][7];
        for (Arrange arr : list) {
            if (arr != null) {
                int day = arr.getDay() - 1;
                int section = arr.getSection() - 1;
                if (arrs[section][day] == null) {
                    arrs[section][day] = new ArrayList<Arrange>();
                    arrs[section][day].add(arr);
                    continue;
                }

                // 如果存在，直接插入
                arrs[section][day].add(arr);
            }
        }

        return arrs;
    }


    /**
     * 查询所有的排课信息
     *
     * @return 返回带有所有排课arranges的message
     */
    @RequestMapping(value = "findAll", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(
            httpMethod = "GET",
            notes = "查询所有排课",
            value = "查询所有排课"
    )
    public Message findAll() throws Exception {
        List<Arrange> all = null;
        try {
            all = arrangeService.findAll();
        }catch (Exception e){
            e.printStackTrace();
            return new Message(205, "获取排课信息失败");
        }

        if(all == null){
            return new Message(201, "不存在任何课程");
        }

        List<Arrange> process = process(all);
        List<Arrange>[][] courseList = getCourseList(process);

        Message message = new Message(200, "获取排课信息成功");
        message.putData("AllArrange",courseList);
        return message;
    }

    /**
     * 接口存在问题
     *
     * @param arrange 自动封装排课对象
     * @return  返回成功 | 失败信息
     * @throws Exception
     *
     * 添加选课信息
     */
    @RequestMapping(value = "addArrangeMapper", method = RequestMethod.POST)
    @ResponseBody
    @ApiIgnore
    public Message addArrangeMapper(@RequestBody Map<String,Object>maps) throws Exception {

        String username = (String)maps.get("username");
        User user = userService.findByUsername(username);
        Integer userId = user.getId();

        String coursename = (String)maps.get("coursename");
        Course course = courseService.findCourseByCourseName(coursename);
        Integer courseId = course.getId();

        List<Integer> weeks = (List<Integer>) maps.get("weeks");
        List<Integer> sections = (List<Integer>) maps.get("sections");
        Integer day = (Integer) maps.get("day");
        Integer number = (Integer) maps.get("number");
        String classes = (String) maps.get("classes");

        //验证信息是否为空
        if (userId == null || courseId == null || weeks == null || sections == null || day == null || number == null || classes == null){
            return new Message(403, "参数不能为空");
        }

        //验证排课是否冲突


        try {
            for (Integer i : sections){

            }
        }catch (Exception e){
            message.setCode(203);
            message.setMessage("增加排课信息失败");
            return message;
        }

        message.setCode(200);
        message.setMessage("增加排课成功");

        return message;
    }

    /**
     * @param courseId 课程id
     * @return  返回成功 | 失败信息
     * @throws Exception
     *
     * 删除选课信息根据课程id
     *
     * 接口存在问题 - 考虑不够详细或者存在错误
     */
//    @RequestMapping(value = "deleteArrangeByCourseId", method = RequestMethod.POST)
//    @ResponseBody
//    @ApiIgnore
//    public Message deleteArrangeById(@RequestParam int courseId)throws Exception{
//
//        List<Arrange> all = null;
//        try {
//            all = arrangeService.findArrangeByCourseId(courseId);
//        }catch (Exception e){
//            e.printStackTrace();
//            return new Message(207, "查询此课程的排课情况失败");
//        }
//
//        if(all == null){
//            return new Message(204, "没有此课程的排课情况");
//        }
//
//        try {
//            arrangeService.deleteArrangeByCourseId(courseId);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new Message(208, "删除此课程排课信息失败");
//        }
//
//        return new Message(200, "删除课程成功");
//    }

    /**
     * @param map1
     * {
     *     id:...,
     *     week:...,
     *     day:...,
     *     section:...
     * }
     **/
    @RequestMapping(value = "updateArrangeByCourseId", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(
            httpMethod = "GET",
            notes = "临时调课",
            value = "临时调课"
    )
    public Message updateArrangeById(@RequestBody Map<String, Object> map1)throws Exception {

        Integer id = (Integer) map1.get("id");
        Integer week = (Integer) map1.get("week");
        Integer day = (Integer) map1.get("day");
        Integer section = (Integer) map1.get("day");

        if(id == null || week == null || day == null || section == null){
            return new Message(403, "参数不能为空");
        }

        try {
            arrangeService.updateArrangeById(id,week,day,section);
        } catch (Exception e) {
            e.printStackTrace();
            return new Message(207, "修改排课失败");
        }

        return new Message(200, "修改排课成功");
    }


    /**
     * @return  返回带有某实验室排课arranges的message
     * @throws Exception
     *
     * 查询某实验室排课信息
     */
    @RequestMapping(value = "findArrangeByLaboratoryId", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(
            httpMethod = "POST",
            notes = "根据实验室id查看每周排课",
            value = "根据实验室id查看每周排课"
    )
    public Message findArrangeByLaboratoryId(@RequestBody Map<String, Object> maps)throws Exception{
        int id = (Integer) maps.get("laboratoryId");
        System.out.println(id);
        int week = (Integer) maps.get("week");
        System.out.println(week);
        List<Arrange> all = null;
        try {
            all = arrangeService.findArrangeByLaboratoryId(id,week);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new Message(208, "查询此实验室的排课情况失败");
        }

        if(all == null) {
            return new Message(202, "此实验室未安排课程");
        }


        List<Arrange> process = process(all);
        List<Arrange>[][] courseList = getCourseList(process);

        Message message = new Message(200, "获取排课信息成功");
        message.putData("ArrangeByLaboratoryId",courseList);
        return message;
    }


    /**
     * @param weeks 教学周数
     * @return  返回带有某实验室排课arranges的message
     * @throws Exception
     *
     * 根据教学周查询排课情况（可能不需要，先保留）
     */
//    @RequestMapping(value = "findArrangeByweek", method = RequestMethod.GET)
//    @ResponseBody
//    @ApiIgnore
//    public Message findArrangeByweek(@RequestParam int weeks)throws Exception{
//        Message message = new Message();
//
//        List<Arrange> all = null;
//        try {
//            all = arrangeService.findAll();
//        }catch (Exception e){
//            message.setCode(206);
//            message.setMessage("获取排课信息失败");
//            return message;
//        }
//        List<Arrange> some = new ArrayList<Arrange>();
//
//        for(Arrange s : all){
//            int week = s.getWeek();
//            if(Arrays.asList(week).contains(weeks))
//                some.add(s);
//        }
//
//        if (some.isEmpty()){
//            message.setCode(210);
//            message.setMessage("当前周没有任何排课");
//            return message;
//        }
//
//        message.setCode(200);
//        message.putData("findArrangeByweek",some);
//        message.setMessage("获取当前周排课成功");
//
//        return message;
//    }


    /**
     * @param maps
     * {
     *     type:...,
     *     weeks:...,
     *     day:...,
     *     sections:...
     * }
     * @return
     *
     * 1、按照指定的周数（weeks）、周几（day）、课程的节数（sections）和教室类型（type）进行选课
     *      要求前端给的数据是本课的排课周数，周几和第几节以及实验室的类型
     * 2、不指定教室的号，按照指定的信息获取合适的教室让教师自己选
     * 3、选好课之后再次提交，添加排课信息
     *
     * 周数可（weeks）多选，周几（days）可多选，课程的节数（section）可多选但是最多两节，教室的类型（type）单选
     *
     * 测试通过--白宝玉
     */
    @RequestMapping(value = "selectEnableLabByTWDS", method = RequestMethod.GET)
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    @ApiOperation(
            httpMethod = "GET",
            notes = "查询可用教室By类型|周数|周几|时间段</br>"+
                    "输入JSON数据: {</br>" +
                    "    \"weeks\":[1,2,3,4],</br>" +
                    "    \"day\":1,</br>" +
                    "    \"section\":3,</br>" +
                    "    \"type\":\"实训实验室\"</br>" +
                    "}",
            value = "查询可用教室By类型|周数|周几|时间段"
    )
    public Message findArrangeByTypeAndWeeksAndDayAndSection(@RequestBody Map<String, Object> maps) {
        //获取参数信息
        List<Integer> weeks = (List<Integer>) maps.get("weeks");
        Integer day = (Integer) maps.get("day");
        Integer section = (Integer) maps.get("section");
        String type = (String) maps.get("type");

        //非空验证
        if(weeks == null || section == null || type == null || day == null) {
            return new Message(403, "参数不能为空");
        }

        // 进行教室的查询

        List<Integer> laboratoriesid = null;
        try {
            /*
             * 首先查询某教室是否可用
             * 如果不可用使用返回错误信息和错误代码
             */

            laboratoriesid = arrangeService.isEnableByWeeksAndDayAndSection(weeks, day, section,type);

            System.out.println("arranges size = " + laboratoriesid.size());

            if(laboratoriesid.size() == 0) {
                Message message = new Message(502, "所选时间段冲突");
                message.putData("laboratoriesid", laboratoriesid);
                return message;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new Message(500, "查询空闲实验室失败-系统错误");
        }

        Message message = new Message(200, "获取空实验室信息成功");
        message.putData("laboratoryid", laboratoriesid);
        return message;
    }


    /**
     * @param maps
     * {
     *     userId:...,
     *     labId:...,
     *     courseId:...,
     *     classIds:...,
     *     weeks:...,
     *     days:...,
     *     sections:...
     * }
     * @return
     *
     * 1、根据给出的排课信息进行课程的增加
     * 2、需要考虑的情况
     *      1) 参数的非空验证
     *      2) 课程是否冲突查询 ==  如果冲突返回错误信息并且返回冲突数据
     * 3、生成排课信息
     *
     * 本接口可谓是最强接口
     *
     * 测试未通过--白宝玉
     *
     * 仍需改进
     */
//    @RequestMapping(value = "addArrange", method = RequestMethod.POST)
//    @ResponseBody
//    @Transactional(rollbackFor = Exception.class)
//    @ApiOperation(
//            httpMethod = "POST",
//            notes = "添加课程安排接口</br>"+
//                    "输入JSON数据: {</br>" +
//                    "    \"userId\": 3,</br>" +
//                    "    \"courseId\": 3,</br>" +
//                    "    \"labId\":4,</br>" +
//                    "    \"classIds\":[1,2],</br>" +
//                    "    \"weeks\":[1,2,3],</br>" +
//                    "    \"days\":[3,4,5],</br>" +
//                    "    \"sections\":[5]</br>" +
//                    "}",
//            value = "添加课程安排接口"
//    )
//    public Message addArrange(@RequestBody Map<String, Object> maps) {
//
//        //获取参数信息
//        List<Integer> weeks = (List<Integer>) maps.get("weeks");
//        List<Integer> days = (List<Integer>) maps.get("days");
//        List<Integer> sections = (List<Integer>) maps.get("sections");
//        Integer userId = (Integer) maps.get("userId");
//        Integer courseId = (Integer) maps.get("courseId");
//        Integer laboratoryId = (Integer) maps.get("labId");
//        Integer number = (Integer) maps.get("number");
//        Integer status = (Integer) maps.get("status");
//
//        //非空验证
//        if(
//                weeks == null || days == null || sections == null ||
//                userId == null || courseId == null || laboratoryId == null
//        ) {
//            return new Message(403, "参数不能为空");
//        }
//
//        // 检查教室是否可用
//        try {
//            Laboratory laboratory = laboratoryService.findById(laboratoryId);
//            if(laboratory == null) {
//                return new Message(601, "教室不存在");
//            }
//
//            if(laboratory.getStatus() == 0) {
//                return new Message(602, "教室不可用");
//            }
//
//            // 判断用户和课程的信息是否存在
//            Course course = courseService.findCourseById(courseId);
//            if(course == null) return new Message(604, "课程不存在");
//
//            // 检查用户是否存在
//            User user = userService.findUserInfoById(userId);
//            if(user == null) return new Message(605, "用户信息不存在");
//
//            // 进行课程的生成
//            List<Arrange> conflicts = new ArrayList<Arrange>();
//            List<Arrange> arranges = new ArrayList<Arrange>();
//
//            boolean permitAdd = true;
//            for (Integer week : weeks) {
//                for (Integer day : days) {
//                    for (Integer section : sections) {
//                        // 检查时间段是否可用
//                        List<Arrange> arr = arrangeService.findArrangeByWeekAndDayAndSection(week, day, section);
//                        if(arr != null) {
//                            conflicts.addAll(arr);
//                            permitAdd = false;
//                        }
//
//                        // 当不允许插入数据的时候，就不需要继续了
//                        if(!permitAdd) continue;
//                        Arrange arrange = new Arrange();
//                        arrange.setLaboratoryId(laboratoryId);
//                        arrange.setCourseId(courseId);
//                        arrange.setUserId(userId);
//                        arrange.setWeek(week);
//                        arrange.setDay(day);
//                        arrange.setSection(section);
//                        arranges.add(arrange);
//                        arrange.setNumber(number);
//                        arrange.setStatus(status);
//                    }
//                }
//            }
//
//            // 检查是否存在冲突
//            if(conflicts.size() != 0) {
//                Message message = new Message(606, "时间段存在冲突");
//                message.putData("conflicts", conflicts);
//                return message;
//            }
//
//            // 否则直接添加排课
//            for (Arrange arrange : arranges) {
//                arrangeService.insertArrange(arrange);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            // 基本这里的错误都是系统的错误了
//            return new Message(500, "服务器出错");
//        }
//
//        return new Message(200, "增加排课信息成功");
//    }

    /**
     * @param maps
     * {
     *     weeks:...,
     *     day:...
     * }
     * @return
     *
     * 查询某几周的某一天可用的时间段，查询全部的这几周可用时间段
     *
     * 测试已通过--白宝玉
     */
    @RequestMapping(value = "findSectionsByWD", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(
            httpMethod = "GET",
            notes = "查询可用时间段By周数|周几</br>"+
                    "输入JSON数据: {\"weeks\":[1,2,3,4],\"day\":1}",
            value = "查询可用时间段By周数|周几"
    )
    public Message findEnableSectionsByWeeksAndDay(@RequestBody Map<String, Object> maps) {


        // 获取数据
        List<Integer> weeks = (List<Integer>) maps.get("weeks");
        Integer day = (Integer) maps.get("day");
        String type = (String) maps.get("type");

        //非空验证
        if(weeks == null || day == null || type.equals("")) {
            return new Message(403, "参数非空");
        }

        // 进行可用时间段查询
        try {
            Set<Integer> sections = arrangeService.findSectionsByWeeksAndDay(weeks, day,type);
            Message message = new Message(200, "查询成功");
            message.putData("sections", sections);
        } catch (Exception e) {
            e.printStackTrace();
            return new Message(500, "获取空闲时间段错误");
        }


        return new Message(200, "查询成功");
    }

    /**
     * @param maps
     * {
     *     week:...,
     *     day:...,
     *     section:...
     * }
     * @return
     *
     * 按照周数和第几天和第几节课查找课程安排
     *
     * 测试通过--高义博
     */
    @RequestMapping(value = "findArrangeByWDS", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(
            httpMethod = "GET",
            notes = "查询课程By周数|周几|时间段</br>"+
                    "输入JSON数据: {\"week\":1,\"day\":1,\"section\":1}",
            value = "查询课程By周数|周几|时间段"
    )
    public Message findArrangeByWeekDayAndSection(@RequestBody Map<String, Object> maps) {

        // 进行非空验证
        Integer week = (Integer) maps.get("week");
        Integer day = (Integer) maps.get("day");
        Integer section = (Integer) maps.get("section");

        if(week == null || day == null || section == null) {
            return new Message(403, "参数不能为空");
        }

        // 获取教室安排信息
        List<Arrange> arrange = null;
        try {
            arrange = arrangeService.findArrangeByWeekAndDayAndSection(week, day, section);
        } catch (Exception e) {
            e.printStackTrace();
            return new Message(500, "查询排课数据失败");
        }

        Message message = new Message(200, "获取教室安排成功");
        message.putData("arranges", arrange);
        return message;
    }


    /**
     * @爱出bug的代码小白
     * 后面思考的
     *
     * 1、能否实现教师指定第几周到第几周上课，指定周几，教室的类型，之后返回可安排的教室
     * 2、能否实现教师指定第几周到第几周上课，指定教室，返回可安排的周数和可安排的节数
     * 3、能否实现教师指定第几周到第几周上课，指定周几，指定教室类型，返回可安排的教室和可安排的节数
     * 4、能否实现教师指定第几周到第几周上课，指定周几，指定教室类型，返回可安排的教室和可安排的节数
     */


    @RequestMapping(value = "findArrangeByUserId", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(
            httpMethod = "POST",
            notes = "通过老师ID查询某周排课</br>"+
                    "输入JSON数据: {\"userId\":11}",
            value = "通过老师ID查询某周排课"
    )
    public Message findArrangeByUserId(@RequestBody Map<String, Object> maps) throws Exception {
        Message message = new Message();

        Integer userId = (Integer) maps.get("userId");
        Integer week = (Integer) maps.get("week");
        if(userId == null|| week == null ) return new Message(403, "参数不全");


        List<Arrange> arranges = arrangeService.findArrangeByUserId(userId,week);
        List<Arrange> process = process(arranges);
        List<Arrange>[][] courseList = getCourseList(process);
        message.putData("arranges", courseList);
        return message;
    }
}
