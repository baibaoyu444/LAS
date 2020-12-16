package cn.las.mapper;

import cn.las.bean.entity.Laboratory;
import org.apache.ibatis.annotations.Param;

public interface LaboratoryMapper {
    void addLaboratory(Laboratory laboratory) throws Exception;

    void updateLaboratoryStatus(@Param("status") Integer status, @Param("id") Integer id) throws Exception;

    void updateLaboratoryPnum(@Param("size") Integer size, @Param("id") Integer id) throws Exception;

    void updateLaboratoryType(@Param("type") String type, @Param("id") Integer id) throws Exception;
}
