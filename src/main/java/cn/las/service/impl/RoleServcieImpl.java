package cn.las.service.impl;

import cn.las.dao.RoleDao;
import cn.las.bean.entity.Role;
import cn.las.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServcieImpl implements RoleService {

    @Autowired
    RoleDao roleDao;

    @Override
    public Role findById(Integer id) throws Exception {
        return roleDao.findById(id);
    }

    @Override
    public Role findByUserId(Integer userId) throws Exception {
        return roleDao.findByUserId(userId);
    }

    @Override
    public void insertUserRole(int userId, int roleId) throws Exception {
        roleDao.insertUserRole(userId, roleId);
    }
}
