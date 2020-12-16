package cn.las.controller;

import cn.las.bean.entity.IClass;
import cn.las.bean.entity.Message;
import cn.las.service.IClassService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("class")
@Api(tags = "班级接口")
/**
 *
 *
 *
 *
 * 这个接口目前是废弃的，还在等待发掘
 *
 *
 *
 *
 *
 */
public class ClassCotroller {

    @Autowired
    IClassService iClassService;

    /**
     * 查询所有的班级信息
     *
     * @return
     */
    @RequestMapping(value = "findAll", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(
            httpMethod = "GET",
            notes = "查询所有班级信息接口",
            value = "查询所有班级信息"
    )
    public Message findAll() {
        List<IClass> all = null;
        try {
            all = iClassService.findAll();
        } catch (Exception e) {
            return new Message(500, "查询失败");
        }
        Message message = new Message(200, "查询班级信息成功");
        message.putData("classes", all);
        return message;
    }

    /**
     * 按照班级的id删除班级信息
     *
     * @param maps
     * {
     *     classId: ...
     * }
     * @return
     */
    @RequestMapping(value = "removeById", method = RequestMethod.POST)
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    @ApiOperation(
            httpMethod = "POST",
            notes = "删除班级信息接口</br>"+
                    "输入JSON数据: {\"classId\":11}",
            value = "删除班级信息"
    )
    public Message removeById(@RequestBody Map<String, Object> maps) {
        Integer classId = (Integer) maps.get("classId");

        try {
            iClassService.deleteByClassId(classId);
        } catch (Exception e) {
            return new Message(403, e.getMessage());
        }

        return new Message(200, "删除班级信息成功");
    }

    /**
     * 增加班级信息
     *
     * @param iClass
     * {
     *     name:...,
     *     number:...
     * }
     * @return
     */
    @RequestMapping(value = "addClass", method = RequestMethod.POST)
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    @ApiOperation(
            httpMethod = "POST",
            notes = "增加班级信息接口</br>"+
                    "输入JSON数据: {\"name\":\"计科226班\",\"number\":39}",
            value = "增加班级信息"
    )
    public Message addIClass(@RequestBody IClass iClass) {
        try {
            iClassService.addClass(iClass);
        } catch (Exception e) {
            return new Message(501, e.getMessage());
        }
        return new Message(200, "添加班级成功");
    }

    /**
     * 更新班级信息
     *
     * @param iClass
     * {
     *     id:..,
     *     name:...,
     *     number:...
     * }
     * @return
     */
    @RequestMapping(value = "updateClass", method = RequestMethod.POST)
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    @ApiOperation(
            httpMethod = "POST",
            notes = "修改班级信息接口</br>"+
                    "输入JSON数据: {\"id\":11,\"name\":\"计科226班\",\"number\":30}",
            value = "修改班级信息"
    )
    public Message updateClassById(@RequestBody IClass iClass) {
        try {
            iClassService.updateClass(iClass);
        } catch (Exception e) {
            return new Message(501, e.getMessage());
        }
        return new Message(200, "修改班级信息成功");
    }
}
