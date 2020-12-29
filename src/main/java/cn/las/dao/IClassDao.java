package cn.las.dao;

import cn.las.bean.entity.IClass;
import org.apache.ibatis.annotations.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IClassDao {

    @Select("select * from iclass where name=#{classname}")
    IClass findByClassName(String classname) throws Exception;

    @Select("select * from iclass where id=#{id}")
    IClass findById(Integer id) throws Exception;

    @Select("select * from iclass")
    List<IClass> findAll() throws Exception;

    @Insert("insert into iclass(college, profession, name, number) values(#{college}, #{profession}, #{name}, #{number})")
    void addClass(IClass iClass) throws Exception;

    @Update("update iclass set college=#{college}, profession=#{profession}, name=#{name}, number=#{number} " +
            " where id=#{id}")
    void updateClass(IClass iClass) throws Exception;

    @Delete("delete from iclass where id=#{id}")
    void deleteByClassId(Integer id) throws Exception;

    @Select("select id, name from iclass")
    @MapKey("id")
    HashMap<Integer, Map<String, Object>> getClassInfo() throws Exception;
}
