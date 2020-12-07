package cn.las.mapper;

import cn.las.domain.Arrange;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ArrangeMapper {
    List<Arrange> selectAll(@Param("userId") int userId)throws Exception;

    void deleteById(@Param("scourseId") int courseId)throws Exception;

    void updateArrangeById(@Param("courseId") int courseId)throws Exception;

    void insertone(Arrange arrange)throws Exception;

    List<Arrange> findArrangeByLaboratoryId(@Param("laboratoryId") int laboratoryId)throws Exception;

    List<Arrange> findArrangeByCourseId(@Param("courseId") int courseId)throws Exception;

    List<Arrange> findArrangeByweek(@Param("weeks") int weeks)throws Exception;
}
