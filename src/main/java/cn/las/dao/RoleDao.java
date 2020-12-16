package cn.las.dao;

import cn.las.bean.entity.Role;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface RoleDao {

    @Select("select * from role where role.id=#{id}")
    Role findById(Integer id) throws Exception;

    @Select("select * from role where id=(select roleId from user_role where userId=#{userId})")
    Role findByUserId(Integer userId) throws Exception;

    @Insert("insert into user_role values(#{userId},#{roleId})")
    void insertUserRole(@Param("userId") Integer userId, @Param("roleId") Integer roleId) throws Exception;

    @Delete("delete from user_role where userId=#{userId}")
    void removeByUserId(Integer userId) throws Exception;
}
