package cn.las.mapper;

import cn.las.bean.dto.ArrangeDTO;
import cn.las.bean.entity.Arrange;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface ArrangeMapper {

    void deleteByCourseId(@Param("courseId") int courseId)throws Exception;

    List<ArrangeDTO> findArrangeByLaboratoryId(@Param("laboratoryId") int laboratoryId)throws Exception;

    List<ArrangeDTO> findArrangeByCourseId(@Param("courseId") int courseId)throws Exception;

    List<ArrangeDTO> findArrangeByUserId(@Param("userId") int userId)throws Exception;

    List<ArrangeDTO> findArrangeByweek(@Param("weeks") int weeks)throws Exception;

    List<ArrangeDTO> findAll() throws Exception;

    ArrangeDTO findArrangeByTag(Integer tag) throws Exception;

    Set<Integer> findWeeksByTag(@Param("tag") Integer tag) throws Exception;

    Set<Integer> findDaysByTag(@Param("tag") Integer tag) throws Exception;

    Set<Integer> findSectionsByTag(@Param("tag") Integer tag) throws Exception;

    // 通过arrange当中的条件查询dto数组
    List<ArrangeDTO> findByArrange(Arrange arrange) throws Exception;

    // 通过tag，查询某一组课班级集合
    Set<Integer> findClassIdsByTag(@Param("tag") Integer tag) throws Exception;

    // 通过一个arrange修改课程信息
    void updateByArrange(Arrange arrange) throws Exception;

    // 查询原始的arrange数据
    List<Arrange> selectOriginArrange(Arrange arrange) throws Exception;

}
