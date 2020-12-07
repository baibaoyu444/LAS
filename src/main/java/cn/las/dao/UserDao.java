package cn.las.dao;

import cn.las.domain.Role;
import cn.las.domain.User;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface UserDao {

    @Select("select * from user where username=#{username}")
    @Results({
            @Result(id = true, column = "id", property = "id"),
            @Result(column = "username", property = "username"),
            @Result(column = "password", property = "password"),
            @Result(column = "teacher", property = "teacher"),
            @Result(column = "id", property = "role", javaType = Role.class,
                    one = @One(select = "cn.las.dao.RoleDao.findByUserId"))
    })
    User findByUsername(String username) throws Exception;

    @Insert("insert into user(username, password, teacher) values(#{username},#{password},#{teacher})")
    @Options(useGeneratedKeys = true, keyProperty = "user.id", keyColumn = "id")
    void addUser(User user) throws Exception;

    @Delete("delete from user where username=#{username}")
    void deleteUserByUsername(String username) throws Exception;

    @Select("select * from user")
    List<User> findAll() throws Exception;

    @Update("update user set user.password=#{password} where user.username=#{username}")
    void changePassword(@Param("username") String username, @Param("password") String password) throws Exception;

    @Select("select username, teacher from user where id=#{id}")
    User findUserInfoById(Integer id) throws Exception;

    @Delete("delete from user where id=#{userId}")
    void removeById(Integer userId) throws Exception;
}
