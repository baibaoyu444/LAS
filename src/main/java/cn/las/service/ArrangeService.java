package cn.las.service;

import cn.las.bean.dto.ArrangeDTO;
import cn.las.bean.entity.Arrange;
import jxl.biff.EmptyCell;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public interface ArrangeService {

    List<ArrangeDTO> findArrangeByUserId(@Param("userId") Integer userId) throws Exception;

    List<ArrangeDTO> findAll() throws Exception;

    //根据课程号删除排课
    void deleteArrangeByCourseId(@Param("courseId") int courseId)throws Exception;

    //通过实验室号查询排课情况
    List<ArrangeDTO> findArrangeByLaboratoryId(@Param("laboratoryId")int laboratory)throws Exception;

    //通过课程号找到排课情况
    List<ArrangeDTO> findArrangeByCourseId(@Param("courseId")int courseId)throws Exception;

    // 通过用户的id查询所有排课信息
    List<ArrangeDTO> findArrangeByUserId(Integer userId, Integer week) throws Exception;

    // 按照arrange查询排课
    List<ArrangeDTO> findByArrange(Arrange arrange) throws Exception;

    // 插入一个dto信息
    void insertArrange(ArrangeDTO dto) throws Exception;

    // 通过tag删除
    void deleteByTag(Integer tag) throws Exception;

    void updateByArrange(Arrange arrange) throws Exception;

    void updateByWeeksDaysAndSections(Set<Integer> weeks, Set<Integer> days, Integer sectionEnum, Integer tag) throws Exception;

    void updateByArrangeDTO(ArrangeDTO dto) throws Exception;

    // 通过tag查询一个arrangedto信息
    ArrangeDTO findArrangeDtoByTag(Integer tag) throws Exception;
}
