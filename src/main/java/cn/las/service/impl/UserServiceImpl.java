package cn.las.service.impl;

import cn.las.dao.ArrangeDao;
import cn.las.dao.RoleDao;
import cn.las.dao.UserDao;
import cn.las.bean.entity.User;
import cn.las.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserDao userDao;

    @Autowired
    ArrangeDao arrangeDao;

    @Autowired
    RoleDao roleDao;

    public User findByUsername(String username) throws Exception {
        return userDao.findByUsername(username);
    }

    public void addUser(User user) throws Exception {
        if(user.getUsername() == null) throw new IllegalArgumentException("用户名非空");
        if(user.getPassword() == null) throw new IllegalArgumentException("用户密码非空");
        if(user.getTeacher() == null) throw new IllegalArgumentException("教师名称非空");
        userDao.addUser(user);
    }

    public void deleteUserByUsername(String username) throws Exception {
        userDao.deleteUserByUsername(username);
    }

    public List<User> findAll() throws Exception {
        return userDao.findAll();
    }

    public void changePassword(String username, String password) throws Exception {
        userDao.changePassword(username, password);
    }

    @Override
    public User findUserInfoById(Integer id) throws Exception {
        return userDao.findUserInfoById(id);
    }

    @Override
    public void removeById(Integer userId) throws Exception {

        // 事先删除课表当中的数据
        arrangeDao.removeByUserId(userId);

        // 删除user_role当中的数据
        roleDao.removeByUserId(userId);

        // 之后删除用户数据
        userDao.removeById(userId);
    }

    @Override
    public void updateOne(User user) throws Exception {
        userDao.updateOne(user);
    }

    @Override
    public HashMap<Integer, Map<String, Object>> getUserInfo() throws Exception {
        return userDao.getUserInfo();
    }
}
