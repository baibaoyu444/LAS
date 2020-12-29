package cn.las.service;

import cn.las.bean.entity.IClass;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IClassService {
    IClass findByClassName(String classname) throws Exception;

    IClass findById(Integer id) throws Exception;

    List<IClass> findAll() throws Exception;

    void addClass(IClass iClass) throws Exception;

    void updateClass(IClass iClass) throws Exception;

    void deleteByClassId(Integer id) throws Exception;

    HashMap<Integer, Map<String, Object>> getClassInfo() throws Exception;
}
