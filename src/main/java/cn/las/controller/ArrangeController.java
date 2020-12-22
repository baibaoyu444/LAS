package cn.las.controller;

import cn.las.bean.dto.ArrangeDTO;
import cn.las.bean.entity.*;
import cn.las.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
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

    List<ArrangeDTO>[][] processMap(List<ArrangeDTO> dtos) {
        if(dtos == null) return null;
        List<ArrangeDTO>[][] lists = new ArrayList[6][7];
        for (ArrangeDTO dto : dtos) {
            Set<Integer> weeks = dto.getWeeks();
            Set<Integer> days = dto.getDays();
            Set<Integer> sections = dto.getSections();

            for (Integer section : sections) {
                for (Integer day :  days) {
                    if(lists[section - 1][day - 1] == null) {
                        lists[section - 1][day - 1] = new ArrayList<ArrangeDTO>();
                    }
                    lists[section - 1][day - 1].add(dto);
                }
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
    @RequestMapping(value = "/findArrangeByWDS", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(
            httpMethod = "GET",
            notes = "查询课程By周数|周几|时间段</br>"+
                    "输入JSON数据: {\"week\":1,\"day\":1,\"section\":1}",
            value = "查询课程By周数|周几|时间段"
    )
    public Message findArrangeByWDS(@RequestBody Map<String, Object> maps) {

        // 非空验证
        Integer week = (Integer) maps.get("week");
        Integer day = (Integer) maps.get("day");
        Integer section = (Integer) maps.get("section");

        Arrange arrange = new Arrange();
        arrange.setDay(day);
        arrange.setWeek(week);
        arrange.setSection(section);
        System.out.println("Arrange = " + arrange);

        // 获取教室安排信息
        List<ArrangeDTO> dtos = null;
        try {
            dtos = arrangeService.findByArrange(arrange);
        } catch (Exception e) {
            e.printStackTrace();
            return new Message(500, "服务器错误");
        }

        Message message = new Message(200, "获取教室安排成功");
        message.putData("arranges", processMap(dtos));
        return message;
    }

    /**
     * 通过教师的id查询
     * @param maps
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/findArrangeByUserId", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(
            httpMethod = "GET",
            notes = "通过老师ID查询某周排课</br>"+
                    "输入JSON数据: {\"userId\":11,\"week\":1}",
            value = "通过老师ID查询某周排课"
    )
    public Message findArrangeByUserId(@RequestBody Map<String, Object> maps) throws Exception {

        Integer userId = (Integer) maps.get("userId");
        Integer week = (Integer) maps.get("week");
        if(userId == null) return new Message(403, "参数不全");

        Arrange arrange = new Arrange();
        arrange.setUserId(userId);
        arrange.setWeek(week);
        List<ArrangeDTO> arranges = arrangeService.findByArrange(arrange);
        Message message = new Message(200, "请求数据成功");
        message.putData("arranges", processMap(arranges));
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
    @RequestMapping(value = "/findArrangeByLaboratoryId", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(
            httpMethod = "GET",
            notes = "输入json数据：{\"laboratoryId\":1,\"week\":1}",
            value = "查询实验室排课情况"
    )
    public Message findArrangeByLaboratoryId(@RequestBody Map<String, Object> maps)throws Exception{
        Integer laboratoryId = (Integer) maps.get("laboratoryId");
        Integer week = (Integer) maps.get("week");

        List<ArrangeDTO> all = null;
        try {
            Arrange arrange = new Arrange();
            arrange.setLaboratoryId(laboratoryId);
            arrange.setWeek(week);
            all = arrangeService.findByArrange(arrange);
        } catch (Exception e) {
            e.printStackTrace();
            return new Message(500, "服务器出错");
        }

        Message message = new Message(200, "获取排课信息成功");
        message.putData("arranges",processMap(all));
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
            List<Integer> cids = (List<Integer>) map.get("classIds");
            Set<Integer> classIds = new HashSet<Integer>(cids);
            List<Integer> wks = (List<Integer>) map.get("weeks");
            Set<Integer> weeks = new HashSet<Integer>(wks);
            List<Integer> dys = (List<Integer>) map.get("days");
            Set<Integer> days = new HashSet<Integer>(dys);
            Double period = (Double) map.get("time");
            Integer sectionEnum = (Integer) map.get("sectionEnum");

            // 新增课程封装
            ArrangeDTO dto = new ArrangeDTO();
            dto.setCourseId(courseId);
            dto.setUserId(userId);
            dto.setType(type);

            dto.setNumber(number);

            dto.setPeriod(period);
            dto.setDays(days);
            dto.setWeeks(weeks);
            dto.setClassIds(classIds);
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


    /**
     * 修改某一排课的教师
     *
     * @param maps
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/updateArrangeTeacher", method = RequestMethod.PUT)
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public Message updateArrangeUserIdByTag(@RequestBody Map<String, Object> maps) throws Exception {
        // 获取前端传输数据
        Integer tag = (Integer) maps.get("tag");
        Integer userId = (Integer) maps.get("userId");

        // 对数据进行封装
        Arrange arrange = new Arrange();
        arrange.setUserId(userId);
        arrange.setTag(tag);
        try {
            arrangeService.updateByArrange(arrange);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return new Message(403, e.getMessage());
        }

        Message message = new Message(200, "修改教室数据成功");
        return message;
    }

    /**
     * 修改教室的位置
     *
     * @param maps
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/updateArrangeLab", method = RequestMethod.PUT)
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public Message updateArrangeLaboratoryByTag(@RequestBody Map<String, Object> maps) throws Exception {
        // 获取前端传输数据
        Integer tag = (Integer) maps.get("tag");
        Integer laboratoryId = (Integer) maps.get("laboratoryId");

        // 对数据进行封装
        Arrange arrange = new Arrange();
        arrange.setLaboratoryId(laboratoryId);
        arrange.setTag(tag);

        // 检查教室是否冲突，并且抓住报错
        arrangeService.updateByArrange(arrange);

        return  new Message(200, "操作成功");
    }

    /**
     * 修改教室的位置
     *
     * @param maps
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/updateArrangeWDS", method = RequestMethod.PUT)
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public Message updateArrangeByTag(@RequestBody Map<String, Object> maps) {
        // 获取前端传输数据
        Integer tag = (Integer) maps.get("tag");
        Set<Integer> weeks = (Set<Integer>) maps.get("weeks");
        Set<Integer> days = (Set<Integer>) maps.get("days");
        Integer sectionEnum = (Integer) maps.get("sectionEnum");
        // 对数据进行封装


        // catch 冲突的时候的报错
        try {
            arrangeService.updateByWeeksDaysAndSections(weeks, days, sectionEnum, tag);
        } catch (IllegalArgumentException e) {
            // 当出现错误的时候，直接回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Message(501, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Message(200, "操作成功");
    }


    /**
     * 通过tag进行课程的删除
     * @param maps
     * @return
     * @throws Exception
     */
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







/**
 Set<Integer> weeks = (Set<Integer>) maps.get("weeks");
 Integer day = (Integer) maps.get("day");
 Integer sectionEnum = (Integer) maps.get("section");
 Integer laboratoryId = (Integer) maps.get("laboratoryId");
 Integer userId = (Integer) maps.get("userId");
 Integer courseId = (Integer) maps.get("courseId");
 String type = (String) maps.get("type");

 // 先查询，提取原有的课程信息，之后再删除原来的课程
 try {
 Arrange arrange = new Arrange();
 arrange.setTag(tag);
 List<ArrangeDTO> byArrange = arrangeService.findByArrange(arrange);
 // 检查byArrange是不是空的

 // 获取原始的dto数据
 ArrangeDTO dto = byArrange.get(0);
 //            String classes = dto.getClassList().toString().replaceAll(", ", ",");
 //            classes = classes.substring(1, classes.length() - 1);
 Double period = dto.getPeriod();
 Integer number = dto.getNumber();


 // 删除之前的所有tag排课数据
 arrangeService.deleteByTag(tag);

 // 之后进行排课修改

 } catch (Exception e) {
 e.printStackTrace();
 }

 // 插入新的排课数据
 ArrangeDTO dto = new ArrangeDTO();
 dto.setSectionEnum(sectionEnum);*/