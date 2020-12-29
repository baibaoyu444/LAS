package cn.las.controller;

import cn.las.bean.dto.ArrangeDTO;
import cn.las.bean.entity.*;
import cn.las.service.*;
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
 */
@Controller
@RequestMapping("arrange")
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
     * 查询所有的排课信息--列表形式
     *
     * @return 返回带有所有排课arranges的message
     */
    @RequestMapping(value = "/findAllList", method = RequestMethod.GET)
    @ResponseBody
    public Message findAllList() {
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
     * 查询所有的排课信息--图表形式
     *
     * @return 返回带有所有排课arranges的message
     */
    @RequestMapping(value = "/findAllMap", method = RequestMethod.GET)
    @ResponseBody
    public Message findAllMap() {
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
        message.putData("arranges", processMap(all));
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
     * 通过教师的id和周数查询排课信息--图表形式
     *
     * @param maps
     * {
     *     "userId" : 2,
     *     "week" : 2
     * }
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/findArrangeMapByUserId", method = RequestMethod.GET)
    @ResponseBody
    public Message findArrangeByUserId(@RequestBody Map<String, Object> maps) {

        Integer userId = (Integer) maps.get("userId");
        Integer week = (Integer) maps.get("week");
        if(userId == null || week == null) return new Message(403, "参数不全");

        Arrange arrange = new Arrange();
        arrange.setUserId(userId);
        arrange.setWeek(week);
        List<ArrangeDTO> arranges = null;
        try {
            arranges = arrangeService.findByArrange(arrange);
        } catch (Exception e) {
            e.printStackTrace();
            return new Message(500, "服务器错误");
        }
        Message message = new Message(200, "请求数据成功");
        message.putData("arranges", processMap(arranges));
        return message;
    }

    /**
     * 通过教师的id和周数查询排课信息--列表形式
     *
     * @param maps
     * {
     *     "userId" : 2,
     *     "week" : 2
     * }
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/findArrangeListByUserId", method = RequestMethod.GET)
    @ResponseBody
    public Message findArrangeListByUserId(@RequestBody Map<String, Object> maps) throws Exception {

        Integer userId = (Integer) maps.get("userId");
        if(userId == null) return new Message(403, "参数不全");

        Arrange arrange = new Arrange();
        arrange.setUserId(userId);
        List<ArrangeDTO> arranges = arrangeService.findByArrange(arrange);
        Message message = new Message(200, "请求数据成功");
        message.putData("arranges", arranges);
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
    @RequestMapping(value = "/findArrangeMapByLaboratoryId", method = RequestMethod.GET)
    @ResponseBody
    public Message findArrangeMapByLaboratoryId(@RequestBody Map<String, Object> maps)throws Exception{
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
     * 根据实验室的id和周数查询 此实验室排课信息
     * @param maps
     * {
     *     "laboratoryId":1,
     *     "week":1
     * }
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/findArrangeListByLaboratoryId", method = RequestMethod.GET)
    @ResponseBody
    public Message findArrangeListByLaboratoryId(@RequestBody Map<String, Object> maps)throws Exception{
        Integer laboratoryId = (Integer) maps.get("laboratoryId");

        List<ArrangeDTO> all = null;
        try {
            Arrange arrange = new Arrange();
            arrange.setLaboratoryId(laboratoryId);
            all = arrangeService.findByArrange(arrange);
        } catch (Exception e) {
            e.printStackTrace();
            return new Message(500, "服务器出错");
        }

        Message message = new Message(200, "获取排课信息成功");
        message.putData("arranges", all);
        return message;
    }

    /**
     * 增加排课信息列表--教师权限
     *
     * @param maps
     * {
     *     "courseId":1,
     *     "userId":1,
     *     "type":"web实验室",
     *     "plantime":2,
     *     "tableData":[
     *      {
     *          "studentNumber":1,
     *          "classIds":[1,2,3],
     *          "weeks":[1,2,3],
     *          "days":[1,2,3],
     *          "sectionEnum":1,
     *          "time":1
     *      },
     *      {
     *          ...
     *      }
     *     ]
     * }
     * @return
     */
    @RequestMapping(value = "/insertArranges", method = RequestMethod.POST)
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
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
                // 手动进行事务回滚
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return new Message(500, "服务器错误");
            }
            i++;
        }

        if (!errors.isEmpty()) {
            Message message = new Message(403, "存在冲突");
            message.putData("errors", errors);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return message;
        }

        Message message = new Message(200, "访问接口成功");
        return message;
    }


    /**
     * 修改某一排课的教师--管理员功能
     *
     * @param maps
     * {
     *     "tag":1,
     *     "userId":1
     * }
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/updateArrangeTeacher", method = RequestMethod.PUT)
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public Message updateArrangeUserIdByTag(@RequestBody Map<String, Object> maps) {
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
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Message(403, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Message(500, "服务器错误");
        }

        Message message = new Message(200, "修改教室数据成功");
        return message;
    }

    /**
     * 修改某一排课的实验室的位置
     *
     * @param maps
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/updateArrangeLab", method = RequestMethod.PUT)
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public Message updateArrangeLaboratoryByTag(@RequestBody Map<String, Object> maps) {
        // 获取前端传输数据
        Integer tag = (Integer) maps.get("tag");
        Integer laboratoryId = (Integer) maps.get("laboratoryId");

        // 对数据进行封装
        Arrange arrange = new Arrange();
        arrange.setLaboratoryId(laboratoryId);
        arrange.setTag(tag);

        // 检查教室是否冲突，并且抓住报错
        try {
            arrangeService.updateByArrange(arrange);
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Message(500, "服务器错误");
        }

        return  new Message(200, "操作成功");
    }

    /**
     * 更新一个教室的排课情况
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
        List<Integer> week = (List<Integer>) maps.get("weeks");
        Set<Integer> weeks = new HashSet<Integer>(week);
        List<Integer> day = (List<Integer>) maps.get("days");
        Set<Integer> days = new HashSet<Integer>(day);
        Integer sectionEnum = (Integer) maps.get("sectionEnum");
        // 对数据进行封装


        // catch 冲突的时候的报错
        try {
            arrangeService.updateByWeeksDaysAndSections(weeks, days, sectionEnum, tag);
        } catch (IllegalArgumentException e) {
            // 当出现错误的时候，直接回滚
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Message(501, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Message(500, "服务器错误");
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
    public Message deleteByTag(@RequestBody Map<String, Object> maps) {
        Integer tag = (Integer) maps.get("tag");
        if(tag == null) return new Message(403, "参数不全");
        try {
            arrangeService.deleteByTag(tag);
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Message(500, "服务器错误");
        }
        return new Message(200, "操作成功");
    }
}