package cn.las.mapper;

import cn.las.bean.dto.ArrangeDTO;
import cn.las.bean.entity.Arrange;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface ArrangeMapper {

    // 通过课程id删除数据
    void deleteByCourseId(@Param("courseId") int courseId)throws Exception;

    // 通过实验室的id查询排课信息
    List<ArrangeDTO> findArrangeByLaboratoryId(@Param("laboratoryId") int laboratoryId)throws Exception;

    // 通过课程的id查询排课信息
    List<ArrangeDTO> findArrangeByCourseId(@Param("courseId") int courseId)throws Exception;

    // 通过用户id查询排课信息
    List<ArrangeDTO> findArrangeByUserId(@Param("userId") int userId)throws Exception;

    List<ArrangeDTO> findArrangeByWeek(@Param("week") int week)throws Exception;

    // 查询所有的排课信息
    List<ArrangeDTO> findAll() throws Exception;

    // 通过tag查询排课信息
    ArrangeDTO findArrangeByTag(Integer tag) throws Exception;

    // 按照tag查询weeks集合
    Set<Integer> findWeeksByTag(@Param("tag") Integer tag) throws Exception;

    // 按照tag查询days集合
    Set<Integer> findDaysByTag(@Param("tag") Integer tag) throws Exception;

    // 按照tag查询sections信息
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
