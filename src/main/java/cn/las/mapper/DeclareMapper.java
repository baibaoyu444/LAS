package cn.las.mapper;

import cn.las.bean.entity.Declare;

import java.util.List;

public interface DeclareMapper {

    void insertDeclare(Declare declare) throws Exception;

    void removeById(Integer id) throws Exception;

    List<Declare> findAll() throws Exception;

}
