package cn.las.dao;

import cn.las.bean.entity.IClass;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.HashMap;
import java.util.List;

public interface IClassDao {

    @Select("select * from iclass where name=#{classname}")
    IClass findByClassName(String classname) throws Exception;

    @Select("select * from iclass where id=#{id}")
    IClass findById(Integer id) throws Exception;

    @Select("select * from iclass")
    List<IClass> findAll() throws Exception;

    @Insert("insert into iclass(name, number) values(#{name}, #{number})")
    void addClass(IClass iClass) throws Exception;

    @Update("update iclass set name=#{name}, number=#{number} " +
            " where id=#{id}")
    void updateClass(IClass iClass) throws Exception;

    @Delete("delete from iclass where id=#{id}")
    void deleteByClassId(Integer id) throws Exception;

    @Select("select id, name from iclass")
    HashMap<Integer, String> getClassInfo() throws Exception;
}
