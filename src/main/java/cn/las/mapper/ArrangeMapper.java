package cn.las.mapper;

import cn.las.bean.entity.Arrange;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ArrangeMapper {
    List<Arrange> findByUserId(@Param("userId") int userId)throws Exception;

    void deleteById(@Param("courseId") int courseId)throws Exception;

    void updateArrangeById(@Param("id") int id, @Param("week") int week, @Param("day") int day, @Param("section") int section)throws Exception;

    void insertone(Arrange arrange)throws Exception;

    List<Arrange> findArrangeByLaboratoryId(@Param("laboratoryId") int laboratoryId)throws Exception;

    List<Arrange> findArrangeByCourseId(@Param("courseId") int courseId)throws Exception;

    List<Arrange> findArrangeByweek(@Param("weeks") int weeks)throws Exception;
}
