package cn.las.service;

import cn.las.domain.Laboratory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LaboratoryService {
    void updateLaboratoryStatus(@Param("id") int id, @Param("state") Integer state)throws Exception;

    void updateLaboratoryPnum(@Param("id") int id, @Param("size") Integer size)throws Exception;

    void updateLaboratoryType(@Param("type") String type, @Param("id") Integer id) throws Exception;

    List<Laboratory> findAll() throws Exception;

    Laboratory findById(Integer id) throws Exception;

    void deleteById(Integer id) throws Exception;

    void addLaboratory(Laboratory laboratory) throws Exception;

    List<Laboratory> findByType(String type) throws Exception;

    boolean isEnable(Integer id) throws Exception;

    Laboratory findByLaboratoryName(String labName) throws Exception;

    void updateLab(Laboratory lab) throws Exception;
}
