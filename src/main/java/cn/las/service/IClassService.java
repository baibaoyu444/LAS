package cn.las.service;

import cn.las.domain.IClass;

import java.util.List;

public interface IClassService {
    IClass findByClassName(String classname) throws Exception;

    IClass findById(Integer id) throws Exception;

    List<IClass> findAll() throws Exception;

    void addClass(IClass iClass) throws Exception;

    void updateClass(IClass iClass) throws Exception;

    void deleteByClassId(Integer id) throws Exception;
}
