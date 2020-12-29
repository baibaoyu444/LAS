package cn.las.service;

import cn.las.bean.entity.Declare;

import java.util.List;

public interface DeclareService {

    void insertDeclare(Declare declare) throws Exception;

    void removeById(Integer id) throws Exception;

    List<Declare> findAll() throws Exception;

    List<Declare> findByUserId(Integer userId) throws Exception;

    void confirmDeclare(Integer id, Integer status) throws Exception;

    void refuseDeclare(Integer id, Integer status) throws Exception;
}
