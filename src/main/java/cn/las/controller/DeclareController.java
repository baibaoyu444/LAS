package cn.las.controller;

import cn.las.bean.entity.Declare;
import cn.las.bean.entity.Message;
import cn.las.bean.vo.DeclareVO;
import cn.las.converter.DeclareConverter;
import cn.las.service.DeclareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/declare")
public class DeclareController {

    @Autowired
    DeclareService declareService;

    /**
     * 申请修改排课的信息--信息包括修改的周次，星期，节次，
     *
     * @param maps
     * @return
     */
    @RequestMapping(value = "/changeTime", method = RequestMethod.POST)
    @ResponseBody
    public Message changeTime(@RequestBody Map<String, Object> maps) {
        // 获取必要数据和班级ids数据
        Integer userId = (Integer) maps.get("userId");
        Integer tag = (Integer) maps.get("tag");
        Integer type = (Integer) maps.get("type");
        String reason = (String) maps.get("reason");
        List<Integer> weeks = (List<Integer>) maps.get("weeks");
        List<Integer> days = (List<Integer>) maps.get("days");
        Integer sectionEnum = (Integer) maps.get("sectionEnum");

        DeclareVO vo = new DeclareVO();

        vo.setWeeks(weeks);
        vo.setDays(days);
        vo.setSectionenum(sectionEnum);
        vo.setTag(tag);
        vo.setUserId(userId);
        vo.setType(type);
        vo.setReason(reason);
        vo.setStatus(0);

        Declare declare = new Declare();
        DeclareConverter.vo2entity(vo, declare);
        try {
            declareService.insertDeclare(declare);
        } catch (Exception e) {
            e.printStackTrace();
            return new Message(500, "服务器错误");
        }
        return new Message(200, "操作成功");
    }


    /**
     * 更改班级信息 + 申请用户的基本信息 + 原始客表的信息
     *
     * @param maps
     * @return
     */
    @RequestMapping(value = "/changeClasses", method = RequestMethod.POST)
    @ResponseBody
    public Message changeClasses(@RequestBody Map<String, Object> maps) {
        // 获取必要数据和班级ids数据
        Integer userId = (Integer) maps.get("userId");
        Integer tag = (Integer) maps.get("tag");
        Integer type = (Integer) maps.get("type");
        String reason = (String) maps.get("reason");
        List<Integer> classIds = (List<Integer>) maps.get("classIds");

        DeclareVO vo = new DeclareVO();

        vo.setClassIds(classIds);
        vo.setTag(tag);
        vo.setUserId(userId);
        vo.setType(type);
        vo.setReason(reason);
        vo.setStatus(0);

        Declare declare = new Declare();
        DeclareConverter.vo2entity(vo, declare);
        try {
            declareService.insertDeclare(declare);
        } catch (Exception e) {
            e.printStackTrace();
            return new Message(500, "服务器错误");
        }
        return new Message(200, "操作成功");
    }


    /**
     * 通过userId查询教师的排课修改申请信息
     *
     * @param maps
     * @return
     */
    @RequestMapping(value = "/findByUserId", method = RequestMethod.GET)
    @ResponseBody
    public Message findByUserId(@RequestBody Map<String, Object> maps) {
        Integer userId = (Integer) maps.get("id");
        try {
            List<Declare> byUserId = declareService.findByUserId(userId);
            if(byUserId == null) return new Message(10001, "不存在申请排课信息");
        } catch (Exception e) {
            e.printStackTrace();
            return new Message(500, "服务器错误");
        }
        Message message = new Message(200, "查询成功");
        return message;
    }

    /**
     * 管理员确认课程修改信息
     *
     * @param maps
     * @return
     */
    @RequestMapping(value = "/confirmDeclare", method = RequestMethod.POST)
    @ResponseBody
    public Message confirmDeclare(@RequestBody Map<String, Object> maps) {

        Integer id = (Integer) maps.get("id");
        Integer status = (Integer) maps.get("status");

        try {
            declareService.confirmDeclare(id, status);
        } catch (Exception e) {
            e.printStackTrace();
            return new Message(500, "服务器错误");
        }
        return new Message(200, "操作成功");
    }

    @RequestMapping(value = "refuseDeclare", method = RequestMethod.POST)
    @ResponseBody
    public Message confuseDeclare(@RequestBody Map<String, Object> maps) {
        Integer id = (Integer) maps.get("id");
        Integer status = (Integer) maps.get("status");

        try {
            declareService.refuseDeclare(id, status);
        } catch (Exception e) {
            e.printStackTrace();
            return new Message(500, "服务器错误");
        }
        return new Message(200, "操作成功");
    }
}
