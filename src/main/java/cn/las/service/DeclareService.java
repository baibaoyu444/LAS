package cn.las.service;

import cn.las.bean.entity.Declare;

import java.util.List;

public interface DeclareService {

    void insertDeclare(Declare declare) throws Exception;

    void removeById(Integer id) throws Exception;

    List<Declare> findAll() throws Exception;
}
