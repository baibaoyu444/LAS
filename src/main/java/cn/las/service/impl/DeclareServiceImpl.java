package cn.las.service.impl;

import cn.las.bean.entity.Declare;
import cn.las.mapper.DeclareMapper;
import cn.las.service.DeclareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeclareServiceImpl implements DeclareService {

    @Autowired
    DeclareMapper declareMapper;

    @Override
    public void insertDeclare(Declare declare) throws Exception {
        declareMapper.insertDeclare(declare);
    }

    @Override
    public void removeById(Integer id) throws Exception {
        declareMapper.removeById(id);
    }

    @Override
    public List<Declare> findAll() throws Exception {
        return declareMapper.findAll();
    }
}
