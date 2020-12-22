package cn.las.service.impl;

import cn.las.dao.ArrangeDao;
import cn.las.dao.IClassDao;
import cn.las.bean.entity.IClass;
import cn.las.service.IClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class IClassServiceImpl implements IClassService {

    @Autowired
    IClassDao iClassDao;

    @Autowired
    ArrangeDao arrangeDao;

    public IClass findByClassName(String classname) throws Exception {
        return iClassDao.findByClassName(classname);
    }

    public IClass findById(Integer id) throws Exception {
        return iClassDao.findById(id);
    }

    public List<IClass> findAll() throws Exception {
        return iClassDao.findAll();
    }

    public void addClass(IClass iClass) throws Exception {
        if(iClass.getName() == null) throw new IllegalArgumentException("班级名称非空");
        if(iClass.getNumber() < 0) throw new IllegalArgumentException("班级人数不为负数");
        if(iClassDao.findByClassName(iClass.getName()) != null) throw new Exception("用户已存在");
        iClassDao.addClass(iClass);
    }

    public void updateClass(IClass iClass) throws Exception {
        if(iClass.getName() == null) throw new IllegalArgumentException("班级名称非空");
        if(iClass.getNumber() < 0) throw new IllegalArgumentException("班级人数不为负数");
        iClassDao.updateClass(iClass);
    }

    public void deleteByClassId(Integer id) throws Exception {

        if(id == null) throw new IllegalArgumentException("参数非空");
        // 删除班级的排课数据
        arrangeDao.removeByCourseId(id);
        // 删除班级数据
        iClassDao.deleteByClassId(id);
    }

    @Override
    public HashMap<Integer, String> getClassInfo() throws Exception {
        return iClassDao.getClassInfo();
    }
}
