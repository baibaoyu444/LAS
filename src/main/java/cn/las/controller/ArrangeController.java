package cn.las.controller;

import cn.las.bean.dto.ArrangeDTO;
import cn.las.bean.entity.Arrange;
import cn.las.bean.entity.Message;
import cn.las.bean.enu.SectionEnum;
import cn.las.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
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


    /**
     * 处理课程表功能-白宝玉
     *
     * @param all
     * @return
     */
    private List<ArrangeDTO> process(List<ArrangeDTO> all) {
        // 进行排课数据的整理
        List<ArrangeDTO> dtos = new ArrayList<ArrangeDTO>();
        ArrangeDTO arrangeDTO = null;
        for (ArrangeDTO dto : all) {
            if (arrangeDTO == null) {
                arrangeDTO = dto;
                arrangeDTO.setWeeks(new ArrayList<Integer>());
                arrangeDTO.getWeeks().add(dto.getWeek());
                continue;
            }
            // 确保数据是一致的
            if (
                    arrangeDTO.getLaboratoryId() == dto.getLaboratoryId() &&
                            arrangeDTO.getUserId() == dto.getUserId() &&
                            arrangeDTO.getCourseId() == dto.getCourseId() &&
                            arrangeDTO.getDay() == dto.getDay() &&
                            arrangeDTO.getSection() == dto.getSection()
                          //  arrange.getClasses().equals(arr.getClasses())
            ) {
                arrangeDTO.getWeeks().add(dto.getWeek());
            } else {
                dtos.add(dto);
                dto.setWeeks(new ArrayList<Integer>());
                dto.getWeeks().add(dto.getWeek());
                arrangeDTO = dto;
            }
        }
        dtos.add(arrangeDTO);
        return dtos;
    }

    /**
     * 获取课程表的二维数组-白宝玉
     *
     * @param list
     * @return
     */
    private List<ArrangeDTO>[][] getCourseList(List<ArrangeDTO> list) {

        // 对传入的list进行遍历
        for(ArrangeDTO dto : list) {
            System.out.println(dto);
        }

        List<ArrangeDTO> [][] arrs = new ArrayList[6][7];
        for (ArrangeDTO dto : list) {
            if (dto != null) {
                int day = dto.getDay() - 1;
                int section = dto.getSection() - 1;
                if (arrs[section][day] == null) {
                    arrs[section][day] = new ArrayList<ArrangeDTO>();
                    arrs[section][day].add(dto);
                    continue;
                }

                // 如果存在，直接插入
                arrs[section][day].add(dto);
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
        List<ArrangeDTO> all = null;
        try {
            all = arrangeService.findByArrange(new Arrange());
        } catch (Exception e) {
            e.printStackTrace();
            return new Message(205, "获取排课信息失败");
        }

        if(all == null) {
            return new Message(201, "不存在任何课程");
        }

        List<ArrangeDTO> process = process(all);
        List<ArrangeDTO>[][] courseList = getCourseList(process);

        Message message = new Message(200, "获取排课信息成功");
        message.putData("arranges", courseList);
        return message;
    }

    /**
     * 接口存在问题
     *
     * @param maps 自动封装排课对象
     * @return  返回成功 | 失败信息
     * @throws Exception
     *
     * 添加选课信息
     */
    @RequestMapping(value = "addArrangeMapper", method = RequestMethod.POST)
    @ResponseBody
    @ApiIgnore
    public Message addArrangeMapper(@RequestBody Map<String,Object> maps) throws Exception {

        Message message = null;
        Integer userId = (Integer) maps.get("userId");
        Integer courseId = (Integer) maps.get("courseId");

        List<Integer> weeks = (List<Integer>) maps.get("weeks");
        List<Integer> sections = (List<Integer>) maps.get("sections");
        Integer day = (Integer) maps.get("day");
        Integer number = (Integer) maps.get("number");
        String classes = (String) maps.get("classes");

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
            notes = "",
            value = "查询实验室排课情况"
    )
    public Message findArrangeByLaboratoryId(@RequestBody Map<String, Object> maps)throws Exception{
        int id = (Integer) maps.get("laboratoryId");
        int week = (Integer) maps.get("week");
        List<ArrangeDTO> all = null;
        try {
//            all = arrangeService.findArrangeByLaboratoryId(id,week);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new Message(205, "查询此实验室的排课情况失败");
        }

        if(all == null) {
            return new Message(202, "此实验室未安排课程");
        }


        List<ArrangeDTO> process = process(all);
        List<ArrangeDTO>[][] courseList = getCourseList(process);

        Message message = new Message(200, "获取排课信息成功");
        message.putData("arranges",courseList);
        return message;
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


//        List<ArrangeDTO> arranges = arrangeService.findArrangeByUserId(userId,week);
//        List<ArrangeDTO> process = process(arranges);
//        List<ArrangeDTO>[][] courseList = getCourseList(process);
//        message.putData("arranges", courseList);
        return message;
    }


    /**
     * 按照用户id和周数week获取个人排课信息
     *
     * @param arrange
     * @return
     */
    @RequestMapping(value = "",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(
            httpMethod = "POST",
            notes = "按照用户id和周数week获取个人排课信息</br>"+
                    "输入JSON数据: {\"userId\":11, \"week\":1}",
            value = "按照用户id和周数week获取个人排课信息"
    )
    public Message find(@RequestBody Arrange arrange) {

        List<ArrangeDTO> list = null;
        try {
            list = arrangeService.findByArrange(arrange);
        } catch (Exception e) {
            e.printStackTrace();
            return new Message(500, "服务器错误");
        }
        Message message = new Message(200, "获取数据成功");
        message.putData("arranges", list);
        return message;
    }


    /**
     * 按照用户id和周数week获取个人排课信息
     *
     * @param arrange
     * @return
     *
     * 测试通过--白宝玉
     */
    @RequestMapping(value = "/findByIdAndWeek",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(
            httpMethod = "GET",
            notes = "按照用户id和周数week获取个人排课信息</br>"+
                    "输入JSON数据: {\"userId\":11, \"week\":1}",
            value = "按照用户id和周数week获取个人排课信息"
    )
    public Message findByIdAndWeek(@RequestBody Arrange arrange) {

        System.out.println(arrange);
        List<ArrangeDTO> list = null;
        try {
            list = arrangeService.findByArrange(arrange);
        } catch (Exception e) {
            e.printStackTrace();
            return new Message(500, "服务器错误");
        }

        // 进行数据封装
        List<ArrangeDTO> process = process(list);
        List<ArrangeDTO>[][] courseList = getCourseList(process);
        Message message = new Message(200, "获取数据成功");
        message.putData("arranges", courseList);
        return message;
    }


    /**
     * 增加排课信息列表
     * @param maps
     * @return
     */
    @RequestMapping(value = "/insertManyArrange", method = RequestMethod.POST)
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    @ApiOperation(
            httpMethod = "POST",
            notes = "按照用户id和周数week获取个人排课信息</br>"+
                    "输入JSON数据: {\"userId\":11, \"week\":1}",
            value = "按照用户id和周数week获取个人排课信息"
    )
    public Message insertManyArrange(@RequestBody Map<String, Map<String, Object>> maps) {

        // 遍历传递数据
        int i = 0;
        Map<Integer, Object> errors = new HashMap<Integer, Object>();
        for (String key : maps.keySet()) {
            Map<String, Object> map = maps.get(key);

            /*
             获取关键参数
             参数约束：
                课程名称|当前用户id|人数|多个班级
                实验室类型（[其中包含无限制，也就是任何一个教室都行]
                多个周次|周几|第几节课|安排课时
             */
            Integer courseId = (Integer) map.get("courseId");
            Integer userId = (Integer) map.get("userId");
            Integer number = (Integer) map.get("number");
            String classes = (String) map.get("classes");
            String type = (String) map.get("type");
            List<Integer> weeks = (List<Integer>) map.get("weeks");
            Integer day = (Integer) map.get("day");
            Integer section = (Integer) map.get("section");

            // 新增课程封装
            ArrangeDTO dto = new ArrangeDTO();
            dto.setCourseId(courseId);
            dto.setUserId(userId);
            dto.setNumber(number);
            dto.setClassList(Arrays.asList(classes.split(" ")));
            dto.setWeeks(weeks);
            dto.setDay(day);
            dto.setType(type);

            // 封装节数
            int [] sections = SectionEnum.parse(section);
            dto.setSections(sections);

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
    public Message updateArrangeBy(@RequestBody Map<String, Object> maps) {

        return null;
    }
}
