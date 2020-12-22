package cn.las.service;

import cn.las.bean.entity.User;

import java.util.HashMap;
import java.util.List;

public interface UserService {

    User findByUsername(String username) throws Exception;

    void addUser(User user) throws Exception;

    void deleteUserByUsername(String username) throws Exception;

    List<User> findAll() throws Exception;

    void changePassword(String username, String password) throws Exception;

    User findUserInfoById(Integer id) throws Exception;

    void removeById(Integer userId) throws Exception;

    void updateOne(User user) throws Exception;

    HashMap<Integer, String> getUserInfo() throws Exception;
}
