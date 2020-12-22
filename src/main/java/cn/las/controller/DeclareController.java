package cn.las.controller;

import cn.las.bean.entity.Declare;
import cn.las.bean.entity.Message;
import cn.las.service.DeclareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
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
     * 改变排课教师
     * @param declare
     * @return
     */
    @RequestMapping(value = "/changeUser", method = RequestMethod.POST)
    @ResponseBody
    public Message changeUser(@RequestBody Declare declare) {
        try {
            declareService.insertDeclare(declare);
        } catch (Exception e) {
            e.printStackTrace();
            return new Message(500, "服务器错误");
        }
        return new Message(200, "申请成功");
    }

    /**
     * 更改排课的时间
     *
     * @param maps
     * @return
     */
    @RequestMapping(value = "/changeTime", method = RequestMethod.POST)
    @ResponseBody
    public Message changeTime(@RequestBody Map<String, Object> maps) {
        try {
            List<Integer> weeks = (List<Integer>) maps.get("weeks");
            List<Integer> days = (List<Integer>) maps.get("days");
            Object section = maps.get("section");
//            declareService.insertDeclare(declare);
        } catch (Exception e) {
            e.printStackTrace();
            return new Message(500, "服务器错误");
        }
        return new Message(200, "申请成功");
    }


//    @RequestMapping(value = "/changeClasses", method = RequestMethod.POST)
//    @ResponseBody
//    public Message changeClasses(@Re) {
//
//    }






//    void insertDeclare(Declare declare) throws Exception;
//
//    void removeById(Integer id) throws Exception;
//
//    List<Declare> findAll() throws Exception;
}
