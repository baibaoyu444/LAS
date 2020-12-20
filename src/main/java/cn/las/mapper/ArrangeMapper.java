package cn.las.mapper;

import cn.las.bean.dto.ArrangeDTO;
import cn.las.bean.entity.Arrange;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface ArrangeMapper {
    List<Arrange> findByUserId(@Param("userId") int userId)throws Exception;

    void deleteById(@Param("courseId") int courseId)throws Exception;

    void updateArrangeById(@Param("id") int id, @Param("week") int week, @Param("day") int day, @Param("section") int section)throws Exception;

    List<Arrange> findArrangeByLaboratoryId(@Param("laboratoryId") int laboratoryId)throws Exception;

    List<Arrange> findArrangeByCourseId(@Param("courseId") int courseId)throws Exception;

    List<Arrange> findArrangeByweek(@Param("weeks") int weeks)throws Exception;

    List<ArrangeDTO> findAll() throws Exception;

    Set<Integer> findWeeksByTag(@Param("tag") Integer tag) throws Exception;

    Set<Integer> findSectionsByTag(@Param("tag") Integer tag) throws Exception;

    List<ArrangeDTO> findByArrange(Arrange arrange) throws Exception;
}
