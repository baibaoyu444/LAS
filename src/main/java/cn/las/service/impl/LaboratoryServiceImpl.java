package cn.las.service.impl;

import cn.las.dao.ArrangeDao;
import cn.las.dao.LaboratoryDao;
import cn.las.bean.entity.Laboratory;
import cn.las.mapper.LaboratoryMapper;
import cn.las.service.LaboratoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LaboratoryServiceImpl implements LaboratoryService {

    @Autowired
    LaboratoryDao laboratoryDao;

    @Autowired
    LaboratoryMapper laboratoryMapper;

    @Autowired
    ArrangeDao arrangeDao;

    @Override
    public List<Laboratory> findAll() throws Exception {
        return laboratoryDao.findAll();
    }

    @Override
    public Laboratory findById(Integer id) throws Exception {
        return laboratoryDao.findById(id);
    }

    @Override
    public void deleteById(Integer id) throws Exception {

        // 首先删除实验室的排课数据
        arrangeDao.removeByLaboratoryId(id);
        // 删除实验室数据
        laboratoryDao.deleteById(id);
    }

    @Override
    public void addLaboratory(Laboratory laboratory) throws Exception {
        // 进行参数检验
        if(laboratory.getName() == null) throw new Exception("实验室名非空");
        if(laboratory.getSize() <= 0) throw new Exception("实验室大小非空");
        if(laboratory.getLocation() == null) throw new Exception("实验室位置非空");
        if(laboratory.getType() == null) throw new Exception("实验室类型非空");

        try {
            laboratoryMapper.addLaboratory(laboratory);
        } catch (Exception e) {
            throw new Exception("实验室已存在");
        }
    }

    @Override
    public List<Laboratory> findByType(String type) throws Exception {
        return laboratoryDao.findByType(type);
    }

    @Override
    public boolean isEnable(Integer id) throws Exception {
        return laboratoryDao.findLaboratoryStatusById(id) == 0 ? false : true;
    }

    @Override
    public Laboratory findByLaboratoryName(String labName) throws Exception {
        return laboratoryDao.findByLaboratoryName(labName);
    }

    @Override
    public void updateLab(Laboratory lab) throws Exception {
        if(lab.getName() == null) throw new Exception("实验室名非空");
        if(lab.getSize() <= 0) throw new Exception("实验室大小非空");
        if(lab.getLocation() == null) throw new Exception("实验室位置非空");
        if(lab.getType() == null) throw new Exception("实验室类型非空");

        laboratoryDao.updateLab(lab);
    }

    @Override
    public HashMap<Integer, Map<String, Object>> getLabInfo() throws Exception {
        return laboratoryDao.getLabInfo();
    }
}
