package cn.las.controller;

import cn.las.bean.dto.ArrangeDTO;
import cn.las.bean.entity.*;
import cn.las.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
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

    List<ArrangeDTO>[][] processList(List<ArrangeDTO> dtos) {
        if(dtos == null) return null;
        List<ArrangeDTO>[][] lists = new ArrayList[6][7];
        for (ArrangeDTO dto : dtos) {
            Set<Integer> weeks = dto.getWeeks();
            Integer day = dto.getDay();
            Set<Integer> sections = dto.getSections();

            for (Integer section : sections) {
                if(lists[section - 1][day - 1] == null) {
                    lists[section - 1][day - 1] = new ArrayList<ArrangeDTO>();
                }
                lists[section - 1][day - 1].add(dto);
            }
        }
        return lists;
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
            notes = "查询所有排课信息",
            value = "查询所有排课信息"
    )
    public Message findAll() throws Exception {
        List<ArrangeDTO> all = null;
        try {
            all = arrangeService.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            return new Message(205, "获取排课信息失败");
        }

        if(all == null) {
            return new Message(201, "不存在任何课程");
        }

        Message message = new Message(200, "获取排课信息成功");
        message.putData("arranges", all);
        return message;
    }

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
            notes = "",
            value = "临时调课"
    )
    @ApiIgnore
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
    @ApiIgnore
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
     * @param arrange
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
    public Message findArrangeByWeekDayAndSection(@RequestBody Arrange arrange) {

        // 获取教室安排信息
        List<ArrangeDTO> dtos = null;
        try {
            dtos = arrangeService.findByArrange(arrange);
        } catch (Exception e) {
            e.printStackTrace();
            return new Message(500, "服务器错误");
        }

        Message message = new Message(200, "获取教室安排成功");
        message.putData("arranges", processList(dtos));
        return message;
    }

    /**
     * 通过教师的id查询
     * @param maps
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "findArrangeByUserId", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(
            httpMethod = "POST",
            notes = "通过老师ID查询某周排课</br>"+
                    "输入JSON数据: {\"userId\":11,\"week\":1}",
            value = "通过老师ID查询某周排课"
    )
    @ApiIgnore
    public Message findArrangeByUserId(@RequestBody Map<String, Object> maps) throws Exception {
        Message message = new Message();

        Integer userId = (Integer) maps.get("userId");
        Integer week = (Integer) maps.get("week");
        if(userId == null) return new Message(403, "参数不全");

        Arrange arrange = new Arrange();
        arrange.setUserId(userId);
        arrange.setWeek(week);
        List<ArrangeDTO> arranges = arrangeService.findByArrange(arrange);
        message.putData("arranges", processList(arranges));
        return message;
    }

    /**
     * @return  返回带有某实验室排课arranges的message
     * @throws Exception
     *
     * 查询某实验室排课信息
     *
     * 测试通过--高义博
     */
    @RequestMapping(value = "/findArrangeByLaboratoryId", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(
            httpMethod = "POST",
            notes = "输入json数据：{\"laboratoryId\":1,\"week\":1}",
            value = "查询实验室排课情况"
    )
    public Message findArrangeByLaboratoryId(@RequestBody Map<String, Object> maps)throws Exception{
        int laboratoryId = (Integer) maps.get("laboratoryId");
        Integer week = (Integer) maps.get("week");

        List<ArrangeDTO> all = null;
        try {
            Arrange arrange = new Arrange();
            arrange.setLaboratoryId(laboratoryId);
            arrange.setWeek(week);
            all = arrangeService.findByArrange(arrange);
        } catch (Exception e) {
            return new Message(500, "服务器出错");
        }

        Message message = new Message(200, "获取排课信息成功");
        message.putData("arranges",processList(all));
        return message;
    }


    /**
     * 增加排课信息列表
     * @param maps
     * @return
     */
    @RequestMapping(value = "/insertArranges", method = RequestMethod.POST)
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    @ApiOperation(
            httpMethod = "POST",
            notes = "增加排课信息功能",
            value = "增加排课信息功能"
    )
    public Message insertManyArrange(@RequestBody Map<String, Object> maps) {

        // 遍历传递数据  并且进行数据的检验

        // 获取共有的数据
        Integer courseId = (Integer) maps.get("courseId");
        Integer userId = (Integer) maps.get("userId");
        String type = (String) maps.get("type");
        Double planTime = (Double) maps.get("plantime");

        // 获取表中数据
        List<Map<String, Object>> tableData = (List<Map<String, Object>>) maps.get("tableData");


        int i = 0;
        Map<Integer, Object> errors = new HashMap<Integer, Object>();
        for (Map<String, Object> map : tableData) {

            /*
             获取关键参数
             参数约束：
                课程名称|当前用户id|人数|多个班级
                实验室类型（[其中包含无限制，也就是任何一个教室都行]
                多个周次|周几|第几节课|安排课时
             */
            Integer number = (Integer) map.get("studentNumber");
            List<String> classes = (List<String>) map.get("classes");
            Set<Integer> weeks = (Set<Integer>) map.get("week");
            Integer day = (Integer) map.get("day");
            Set<Integer> sections = (Set<Integer>) map.get("section");
            Double period = (Double) map.get("time");
            Integer sectionEnum = (Integer) maps.get("sectionEnum");

            // 新增课程封装
            ArrangeDTO dto = new ArrangeDTO();
            dto.setCourseId(courseId);
            dto.setUserId(userId);
            dto.setType(type);

            dto.setNumber(number);
            dto.setClassList(classes);
            dto.setPeriod(period);
            dto.setDay(day);
            dto.setWeeks(weeks);
            dto.setSections(sections);
            dto.setSectionEnum(sectionEnum);

            // 新增课程校验
            try {
                arrangeService.insertArrange(dto);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                errors.put(i, e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                return new Message(500, "服务器错误");
            }
            i++;
        }

        if (!errors.isEmpty()) {
            Message message = new Message(403, "存在冲突");
            message.putData("errors", errors);
            return message;
        }

        Message message = new Message(200, "访问接口成功");
        return message;
    }


    @RequestMapping(value = "/updateArrangeBy", method = RequestMethod.PUT)
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    @ApiIgnore
    public Message updateArrangeBy(@RequestBody Map<String, Object> maps) {

        return null;
    }

    @RequestMapping(value = "/deleteByTag", method = RequestMethod.DELETE)
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    @ApiOperation(
            httpMethod = "DELETE",
            notes = "通过tag删除课程信息</br>"+
                    "输入JSON数据: {\"tag\":11}",
            value = "通过tag删除课程信息"
    )
    public Message deleteByTag(@RequestBody Map<String, Object> maps) throws Exception {
        Integer tag = (Integer) maps.get("tag");
        if(tag == null) return new Message(403, "参数不全");
        arrangeService.deleteByTag(tag);
        return new Message(200, "操作成功");
    }
}
