package cn.las.service.impl;

import cn.las.bean.dto.ArrangeDTO;
import cn.las.bean.entity.Declare;
import cn.las.bean.enu.SectionEnum;
import cn.las.bean.vo.DeclareVO;
import cn.las.converter.DeclareConverter;
import cn.las.mapper.DeclareMapper;
import cn.las.service.ArrangeService;
import cn.las.service.DeclareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class DeclareServiceImpl implements DeclareService {

    @Autowired
    DeclareMapper declareMapper;

    @Autowired
    ArrangeService arrangeService;

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

    @Override
    public List<Declare> findByUserId(Integer userId) throws Exception {
        return declareMapper.findByUserId(userId);
    }

    // 管路员批准修改申请  的服务
    @Override
    public void confirmDeclare(Integer id, Integer status) throws Exception {
        // 首先更改申请的状态
        declareMapper.updateDeclareStatus(id, status);

        // 按照id查询排课申请的信息
        List<Declare> declares = declareMapper.findByUserId(id);
        Declare declare = declares.get(0);

        DeclareVO vo = new DeclareVO();
        DeclareConverter.entity2vo(declare, vo);

        // 进行排课信息的修改

        // 获取修改的时间数据
        if(vo.getWeeks() != null) {
            List<Integer> weeks = vo.getWeeks();
            List<Integer> days = vo.getDays();
            Integer sectionEnum = vo.getSectionenum();
            int [] sections = SectionEnum.parse(sectionEnum);
            Set<Integer> section = new HashSet<Integer>();
            for (Integer s : sections) {
                section.add(s);
            }

            // 查询课程的tag
            int tag =  declare.getTag();

            // 通过tag查询arrageDto信息
            ArrangeDTO dto = arrangeService.findArrangeDtoByTag(tag);

            // 获取dto的基本信息
            dto.setWeeks(new HashSet<Integer>(weeks));
            dto.setDays(new HashSet<Integer>(days));
            dto.setSections(section);

            // 删除tag的信息
            arrangeService.deleteByTag(tag);

            // 添加新的课程
            arrangeService.insertArrange(dto);
        } else if(vo.getClassIds() != null) {
            // 获取修改的班级信息
            List<Integer> classIds = vo.getClassIds();

            // 按照tag获取排课信息 除了周数之外
            Integer tag = declare.getTag();
            
            // 获取原来的dto数据
            ArrangeDTO dto = arrangeService.findArrangeDtoByTag(tag);

            // 删除原来的数据
            arrangeService.deleteByTag(tag);

            // 插入新的数据
            dto.setClassIds(new HashSet<Integer>(classIds));
            arrangeService.insertArrange(dto);
        }
    }

    @Override
    public void refuseDeclare(Integer id, Integer status) throws Exception {
        declareMapper.updateDeclareStatus(id, status);
    }
}
