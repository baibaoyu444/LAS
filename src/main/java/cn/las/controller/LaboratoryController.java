package cn.las.controller;

import cn.las.domain.Laboratory;
import cn.las.domain.Message;
import cn.las.mapper.LaboratoryMapper;
import cn.las.service.LaboratoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.swing.*;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("laboratory")
@Api(tags = "实验室接口")
public class LaboratoryController {

    @Autowired
    LaboratoryService laboratoryService;

    /**
     * 设置实验室状态
     *
     * @param maps
     * {
     *     id:...,
     *     status:...
     * }
     * @return
     */
    @RequestMapping(value = "updateStatus", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(
            httpMethod = "POST",
            notes = "修改实验室状态接口</br>"+
                    "输入JSON数据: {\"id\": 11,\"status\": 1}",
            value = "修改实验室状态"
    )
    public Message updateLaboratoryState(@RequestBody Map<String, Object> maps) {
        Message message = new Message();

        // 获取参数
        Integer id = (Integer) maps.get("id");
        Integer status = (Integer) maps.get("status");

        // 更改实验室的状态信息
        try {
            laboratoryService.updateLaboratoryStatus(id, status);
        } catch (Exception e) {
            e.printStackTrace();
            return new Message(501, e.getMessage());
        }

        return new Message(200, "实验室状态更新成功");
    }

    /**
     * 增加实验室信息
     *
     * @param lab
     * {
     *      name:...,
     *      type:...,
     *      size:...,
     *      location:..,
     *      status:...
     * }
     * @return
     *
     * 进行实验室的添加
     */
    @RequestMapping(value = "addLab", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(
            httpMethod = "POST",
            notes = "新增实验室接口</br>"+
                    "输入JSON数据: {\"name\": \"MAC上级实验室\",\"type\": \"mac教室\",\"size\": 60,\"location\": \"F区205\"}",
            value = "新增实验室"
    )
    public Message addLaboratory(@RequestBody Laboratory lab) {
        // 添加实验室信息
        try {
            laboratoryService.addLaboratory(lab);
        } catch (Exception e) {
            e.printStackTrace();
            return new Message(501, e.getMessage());
        }

        // 返回成功信息
        return new Message(200, "添加实验室成功");
    }


    /**
     * @param maps 前端传输的实验室新的类型信息
     * {
     *     id:...,
     *     type:...
     * }
     * @return
     */
    @RequestMapping(value = "updateType", method = RequestMethod.POST)
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    @ApiOperation(
            httpMethod = "POST",
            notes = "更新实验室大小接口</br>"+
                    "输入JSON数据: {\"id\":11,\"type\": \"xxx实验室\"}",
            value = "更新实验室大小"
    )
    public Message updateLaboratoryType(@RequestBody Map<String, Object> maps) {
        // 获取数据 + 非空验证
        String type = (String) maps.get("type");
        Integer id = (Integer) maps.get("id");

        try {
            laboratoryService.updateLaboratoryType(type, id);
        } catch (Exception e) {
            return new Message(501, e.getMessage());
        }

        // 返回操作信息
        return new Message(200, "修改实验室类型信息成功");
    }


    /**
     * @param maps 删除实验室信息
     * {
     *     id:...
     * }
     * @return
     */
    @RequestMapping(value = "deleteById", method = RequestMethod.POST)
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    @ApiOperation(
            httpMethod = "POST",
            notes = "删除实验室信息</br>"+
                    "输入JSON数据: {\"id\":11}",
            value = "删除实验室"
    )
    public Message deleteLaboratoryById(@RequestBody Map<String, Object> maps) {
        // 获取参数+非空验证
        Integer id = (Integer) maps.get("id");

        if(id == null) return new Message(403, "参数有误");

        try {
            laboratoryService.deleteById(id);
        } catch (Exception e) {
            return new Message(401, "删除实验室失败");
        }

        return new Message(200, "删除实验室成功");
    }


    @RequestMapping(value = "findAll", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(
            httpMethod = "GET",
            notes = "查询所有实验室接口",
            value = "查询所有实验室"
    )
    public Message findAll() {
        List<Laboratory> all = null;
        try {
            all = laboratoryService.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            return new Message(500, "获取实验室信息失败");
        }

        Message message = new Message(200, "获取实验室信息成功");
        message.putData("labs", all);
        return message;
    }


    /**
     * 修改实验室信息
     *
     * @param maps 前端传输的数据
     *          {
     *              id:...,
     *              size:...
     *          }
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "updateSize", method = RequestMethod.POST)
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    @ApiOperation(
            httpMethod = "POST",
            notes = "更新实验室大小接口</br>"+
                    "输入JSON数据: {\"id\":11,\"size\": 20}",
            value = "更新实验室大小"
    )
    public Message updateLaboratorySize(@RequestBody Map<String, Object> maps) {
        Message message = new Message();

        // 进行非空验证
        Integer id = (Integer) maps.get("id");
        Integer size = (Integer) maps.get("size");
        if (id == null || size == null) {
            message.setCode(403);
            message.setMessage("参数信息有误");
            return message;
        }

        try {
            if (laboratoryService.findById(id) == null) throw new RuntimeException("实验室不存在");
        } catch (Exception e) {
            e.printStackTrace();
            message.setMessage(e.getMessage());
            message.setCode(501);
            return message;
        }

        // 更改实验室的状态信息
        try {
            laboratoryService.updateLaboratoryPnum(id, size);
        } catch (Exception e) {
            e.printStackTrace();
            message.setMessage("更改实验室人数信息失败");
            message.setCode(501);
            return message;
        }

        message.setCode(200);
        message.setMessage("实验室人数更新成功");
        return message;
    }


    /**
     * 更新实验室数据
     * {
     *     id:...,
     *     name:...,
     *     type:...,
     *     size:...,
     *     location:...
     * }
     * @param lab
     * @return
     */
    @RequestMapping(value = "updateLab", method = RequestMethod.POST)
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    @ApiOperation(
            httpMethod = "POST",
            notes = "更新实验室</br>"+
                    "输入JSON数据: {\"id\":11,\"name\": \"test\",\"type\": \"test\",\"size\": 20,\"location\": \"xxx\"}",
            value = "更新实验室"
    )
    public Message updateLaboratory(@RequestBody Laboratory lab) {
        try {
            laboratoryService.updateLab(lab);
        } catch (Exception e) {
            e.printStackTrace();
            return new Message(501, e.getMessage());
        }
        return new Message(200, "更新实验室数据成功");
    }
}
