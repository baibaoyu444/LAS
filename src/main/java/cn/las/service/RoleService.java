package cn.las.service;

import cn.las.bean.entity.Role;
import org.apache.ibatis.annotations.Param;

public interface RoleService {

    Role findById(Integer id) throws Exception;

    Role findByUserId(Integer userId) throws Exception;

    void insertUserRole(@Param("userId") int userId, @Param("roleId") int roleId) throws Exception;
}
