package cn.las.mapper;

import cn.las.bean.entity.Declare;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DeclareMapper {

    void insertDeclare(Declare declare) throws Exception;

    void removeById(Integer id) throws Exception;

    List<Declare> findAll() throws Exception;

    List<Declare> findByUserId(Integer userId) throws Exception;

    void updateDeclareStatus(@Param("id") Integer id, @Param("status") Integer status) throws Exception;
    
    Declare findById(@Param("id") Integer id) throws Exception;
}
