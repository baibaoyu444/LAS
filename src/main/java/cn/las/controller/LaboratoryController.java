package cn.las.controller;

import cn.las.bean.entity.Laboratory;
import cn.las.bean.entity.Message;
import cn.las.service.LaboratoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("laboratory")
public class LaboratoryController {

    @Autowired
    LaboratoryService laboratoryService;


    /**
     * 查询实验室基本信息
     *
     * 测试通过--白宝玉
     */
    @RequestMapping(value = "getLabInfo", method = RequestMethod.GET)
    @ResponseBody
    public Message getLabInfo() throws Exception {
        // 添加实验室信息
        HashMap<Integer, String> labInfo = laboratoryService.getLabInfo();

        // 返回成功信息
        Message message = new Message(200, "获取实验室信息成功");
        message.putData("labInfo", labInfo);
        return message;
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
     *
     * 测试通过--白宝玉
     */
    @RequestMapping(value = "addLab", method = RequestMethod.POST)
    @ResponseBody
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
     * @param maps 删除实验室信息
     * {
     *     id:...
     * }
     * @return
     *
     * 通过实验室id删除实验室信息
     *
     * 测试通过--白宝玉
     */
    @RequestMapping(value = "deleteById", method = RequestMethod.POST)
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
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


    /**
     * 查询全部的实验室信息
     * @return
     *
     * 测试通过--白宝玉
     */
    @RequestMapping(value = "findAll", method = RequestMethod.GET)
    @ResponseBody
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
     *
     * 测试通过--白宝玉
     */
    @RequestMapping(value = "updateLab", method = RequestMethod.POST)
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
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
