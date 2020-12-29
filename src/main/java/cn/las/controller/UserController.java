package cn.las.controller;

import cn.las.bean.entity.Message;
import cn.las.bean.entity.User;
import cn.las.service.RoleService;
import cn.las.service.UserService;
import cn.las.utils.AESUtil;
import cn.las.utils.MD5Utils;
import com.google.common.xml.XmlEscapers;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @爱出bug的代码小白
 *
 *　　┏┓　　　┏┓+ +
 *　┏┛┻━━━┛┻┓ + +
 *　┃　　　　　　　┃ 　
 *　┃　　　━　　　┃ ++ + + +
 * ████━████ ┃+
 *　┃　　　　　　　┃ +
 *　┃　　　┻　　　┃
 *　┃　　　　　　　┃ + +
 *　┗━┓　　　┏━┛
 *　　　┃　　　┃　　　　　　　　　　　
 *　　　┃　　　┃ + + + +
 *　　　┃　　　┃
 *　　　┃　　　┃ +  神兽保佑
 *　　　┃　　　┃    代码无bug　　
 *　　　┃　　　┃　　+　　　　　　　　　
 *　　　┃　 　　┗━━━┓ + +
 *　　　┃ 　　　　　　　┣┓
 *　　　┃ 　　　　　　　┏┛
 *　　　┗┓┓┏━┳┓┏┛ + + + +
 *　　　　┃┫┫　┃┫┫
 *　　　　┗┻┛　┗┻┛+ + + +
 */

@Controller
@RequestMapping("user")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    RoleService roleService;

    /**
     * 查询用户基本信息
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getUserInfo", method = RequestMethod.GET)
    @ResponseBody
    public Message getUserInfo() throws Exception {
        HashMap<Integer, String> userInfo = userService.getUserInfo();
        Message message = new Message(200, "获取用户数据成功");
        message.putData("userInfo", userInfo);
        return message;
    }


    /**
     * @param user
     * {
     *     username:...,
     *     password:...,
     *     roleId:...
     * }
     * @return 返回登陆成功 | 失败信息
     * @throws Exception
     *
     * 1、非空验证
     * 2、查询用户信息是否存在
     * 3、检验密码是否正确
     * 4、检查用户的身份信息
     *
     * 测试通过-白宝玉
     */
    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ResponseBody
    public Message login(@RequestBody User user) throws Exception {
        // 进行用户信息查询
        User usr = userService.findByUsername(user.getUsername());
        if(usr == null) {
            return  new Message(404, "用户不存在");
        }

        if(usr.getRole() == null) return new Message(403, "用户信息不存在");
        // 进行身份信息的验证
        if(usr.getRole().getId() != user.getRoleId()) {
            return  new Message(405, "身份信息不正确");
        }

        // 进行密码的验证
        String encrypt = MD5Utils.MD5Encode(user.getPassword());
        if(!usr.getPassword().equals(encrypt)) {
            return  new Message(403, "登录失败");
        }

        // 返回当前用户的信息
        Message message = new Message(200, "登录成功");
        message.putData("userInfo", usr);
        return message;
    }

    /**
     * @return  返回带有所有用户信息的数据 users
     * @throws Exception
     *
     * 测试通过-白宝玉
     */
    @RequestMapping(value = "findAll", method = RequestMethod.GET)
    @ResponseBody
    public Message findAll() throws Exception {
        Message message = new Message(200, "查询成功");
        List<User> all = userService.findAll();
        message.putData("users", all);
        return message;
    }

    /**
     * @param user
     * {
     *     username:...,
     *     password:...,
     *     teacher:...
     * }
     * @return 返回操作成功 | 失败数据
     * @throws Exception
     *
     * 1、非空验证
     * 2、添加用户
     *
     * 当出现添加用户过程当中抛出错误的时候，进行事务回滚
     *
     * 测试通过-白宝玉
     */
    @RequestMapping(value = "addUser", method = RequestMethod.POST)
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public Message addUser(@RequestBody User user) throws Exception {
        // 对密码进行加密
        user.setPassword(MD5Utils.MD5Encode(user.getPassword()));

        try {
            // 增加用户
            userService.addUser(user);

            // 生成用户的role信息，身份信息是user
            User usr = userService.findByUsername(user.getUsername());
            roleService.insertUserRole(usr.getId(), 2);
        } catch (Exception e) {
            return new Message(403, "账户已存在");
        }

        return new Message(200, "添加用户信息成功");
    }

    /**
     * 修改密码接口
     *
     * @param datas
     * {
     *     username:...,
     *     new_password:...,
     *     old_password:...
     * }
     * @return
     * @throws Exception
     *
     * 当抛出错误的的时候进行事务回滚
     *
     * 测试通过-白宝玉
     */
    @RequestMapping(value = "changePassword", method = RequestMethod.POST)
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public Message changePassword(@RequestBody Map<String, Object> datas) throws Exception {
        Message message = new Message();

        // 获取参数
        String oldPassword = (String) datas.get("old_password");
        String username = (String) datas.get("account");
        String newPassword = (String) datas.get("new_password");

        if(username == null || oldPassword == null || newPassword == null) {
            message.setCode(501);
            message.setMessage("参数不能为空");
            return message;
        }

        User user = userService.findByUsername(username);

        // 验证当前用户是否存在
        if(user == null) {
            message.setCode(500);
            message.setMessage("用户不存在");
            return message;
        }

        String secretOldPswd = AESUtil.encrypt(oldPassword);
        if(!secretOldPswd.equals(user.getPassword())) {
            message.setCode(502);
            message.setMessage("旧密码输入错误");
            return message;
        }

        // 验证通过开始修改当前账户的密码
        userService.changePassword(username, AESUtil.encrypt(newPassword));

        message.setCode(200);
        message.setMessage("修改密码成功");
        return message;
    }


    /**
     * 通过用户的Id删除用户
     *
     * @param maps 用户的参数数据
     * @return  返回操作结果
     * @throws Exception
     *
     * 测试通过-白宝玉
     */
    @RequestMapping(value = "removeById", method = RequestMethod.POST)
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public Message removeById(@RequestBody Map<String, Object> maps) {
        Integer userId = (Integer) maps.get("userId");
        if(userId == null) {
            return new Message(403, "参数不能为空");
        }

        try {
            userService.removeById(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return new Message(500, "删除用户信息失败");
        }
        return new Message(200, "删除成功!");
    }


    /**
     * 按照id获取账户信息功能
     *
     * @param maps
     * @return
     * @throws Exception
     *
     * 测试通过-白宝玉
     */
    @RequestMapping(value = "findById", method = RequestMethod.GET)
    @ResponseBody
    public Message findById(@RequestBody Map<String, Object> maps) throws Exception {
        User userId = userService.findUserInfoById((Integer) maps.get("userId"));
        Message message = new Message(200, "查询成功");
        message.putData("user", userId);
        return message;
    }

    /**
     * 按照id修改用户基本信息功能
     * @param user
     * @return
     * @throws Exception
     *
     * 测试通过-白宝玉
     */
    @RequestMapping(value = "/", method = RequestMethod.PUT)
    @ResponseBody
    @ApiOperation(
            httpMethod = "PUT",
            notes = "按照id查询用户功能</br>" +
                    "输入JSON数据:{\"userId\":2,\"username\":\"111\", \"teacher\":\"xxx\", \"email\":\"xxx\", \"phone\":\"xxx\"}",
            value = "查询用户BY用户id"
    )
    @Transactional(rollbackFor = Throwable.class)
    public Message updateOne(@RequestBody User user) throws Exception {
        userService.updateOne(user);
        Message message = new Message(200, "修改成功");
        return message;
    }
}
