package cn.las.dao;

import cn.las.bean.dto.ArrangeDTO;
import cn.las.bean.entity.Arrange;
import org.apache.ibatis.annotations.*;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 排课持久层
 */
public interface ArrangeDao {

    @Delete("delete from arrange where userId=#{userId}")
    void removeByUserId(Integer userId) throws Exception;

    @Delete("delete from arrange where courseId=#{courseId}")
    void removeByCourseId(Integer courseId) throws Exception;

    @Delete("delete from arrange where laboratoryId=#{laboratoryId}")
    void removeByLaboratoryId(Integer laboratoryId) throws Exception;

    @Insert("insert into arrange " +
            "(laboratoryId, userId, courseId, week, day, section, number, status, classId, tag, period, sectionEnum) " +
            "values(#{laboratoryId},#{userId},#{courseId},#{week},#{day},#{section}," +
            "#{number},#{status},#{classId},#{tag},#{period},#{sectionEnum})")
    void insertArrange(Arrange arrange) throws Exception;

    @Select("select MAX(tag) from arrange")
    Integer findMaxTag() throws Exception;

    @Delete("delete from arrange where tag=#{tag}")
    void removeByTag(Integer tag) throws Exception;
}
