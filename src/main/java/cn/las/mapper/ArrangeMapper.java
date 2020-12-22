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

    List<ArrangeDTO> findArrangeByTag(Integer tag) throws Exception;

    Set<Integer> findWeeksByTag(@Param("tag") Integer tag) throws Exception;

    Set<Integer> findDaysByTag(@Param("tag") Integer tag) throws Exception;

    Set<Integer> findSectionsByTag(@Param("tag") Integer tag) throws Exception;

    List<ArrangeDTO> findByArrange(Arrange arrange) throws Exception;

    Set<Integer> findClassIdsByTag(@Param("tag") Integer tag) throws Exception;

    void updateByArrange(Arrange arrange) throws Exception;

    List<Arrange> selectOriginArrange(Arrange arrange) throws Exception;
}
