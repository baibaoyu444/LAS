package cn.las.service;

import cn.las.bean.entity.Laboratory;

import java.util.HashMap;
import java.util.List;

public interface LaboratoryService {

    List<Laboratory> findAll() throws Exception;

    Laboratory findById(Integer id) throws Exception;

    void deleteById(Integer id) throws Exception;

    void addLaboratory(Laboratory laboratory) throws Exception;

    List<Laboratory> findByType(String type) throws Exception;

    boolean isEnable(Integer id) throws Exception;

    Laboratory findByLaboratoryName(String labName) throws Exception;

    void updateLab(Laboratory lab) throws Exception;

    HashMap<Integer, String> getLabInfo() throws Exception;
}
