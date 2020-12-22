package cn.las.service.impl;

import cn.las.bean.dto.ArrangeDTO;
import cn.las.bean.enu.SectionEnum;
import cn.las.dao.ArrangeDao;
import cn.las.bean.entity.Arrange;
import cn.las.bean.entity.Laboratory;
import cn.las.dao.CourseDao;
import cn.las.dao.LaboratoryDao;
import cn.las.dao.UserDao;
import cn.las.mapper.ArrangeMapper;
import cn.las.service.ArrangeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class ArrangeServiceImpl implements ArrangeService {

    // 注入持久层对象
    @Autowired
    ArrangeDao arrangeDao;

    @Autowired
    ArrangeMapper arrangeMapper;

    @Autowired
    CourseDao courseDao;

    @Autowired
    UserDao userDao;

    @Autowired
    LaboratoryDao laboratoryDao;

    @Override
    public List<ArrangeDTO> findAll() throws Exception {
        return arrangeMapper.findAll();
    }

    @Override
    public void deleteArrangeByCourseId(int courseId)throws Exception{
        arrangeMapper.deleteByCourseId(courseId);
    }

    @Override
    public List<ArrangeDTO> findArrangeByLaboratoryId(int laboratoryId)throws Exception{
        return arrangeMapper.findArrangeByLaboratoryId(laboratoryId);
    }

    @Override
    public List<ArrangeDTO> findArrangeByUserId(Integer userId, Integer week) throws Exception {
        return null;
    }

    @Override
    public List<ArrangeDTO> findArrangeByCourseId(int courseId)throws Exception{
        return arrangeMapper.findArrangeByCourseId(courseId);
    }

    @Override
    public List<ArrangeDTO> findArrangeByUserId(Integer userId) throws Exception {
        return arrangeMapper.findArrangeByUserId(userId);
    }

    /**
     * @author 白宝玉
     *
     * 按照Arrange查询排课信息
     *
     * @param arrange
     * @return
     * @throws Exception
     */
    @Override
    public List<ArrangeDTO> findByArrange(Arrange arrange) throws Exception {
        List<ArrangeDTO> arranges = arrangeMapper.findByArrange(arrange);
        return arranges;
    }

    @Override
    public void insertArrange(ArrangeDTO dto) throws Exception {

        // 提取dto数据
        Set<Integer> weeks = dto.getWeeks();
        Set<Integer> days = dto.getDays();
        Integer sectionEnum = dto.getSectionEnum();
        int[] sections = SectionEnum.parse(sectionEnum);
        Set<Integer> classIds = dto.getClassIds();

        String type = dto.getType();
        Integer userId = dto.getUserId();
        Integer courseId = dto.getCourseId();
        Double period = dto.getPeriod();
        Integer number = dto.getNumber();

        // 检查tag是否存在 如果存在-修改数据 如果不存在-申请排课
        Integer tag = null;
        if(dto.getTag() == null) {
            tag = arrangeDao.findMaxTag() + 1;
        } else {
            tag = dto.getTag();
        }

        // 遍历集合数据
        Arrange entity = new Arrange();
        for (int week : weeks) {
            for (int section : sections) {
                for (int day : days) {
                    // arrange对象的封装a

                    // 封装其他属性（周数，星期，节次，实验室id）用于检验冲突
                    entity.setWeek(week);
                    entity.setSection(section);
                    entity.setDay(day);

                    /**
                     * 课程冲突检查流程
                     * 周次 + 星期 + 时间 + 教室 是否冲突
                     * - 如果冲突
                     *      - 教师的userId是否相同
                     *          - 不相同 - 教室该时间段被占用
                     *          - 相同 - 教师课程已存在
                     * - 没有冲突
                     *      - 进行教室的安排
                     *
                     * 一个教师 同一时间段不能安排多场课--等待考虑
                     */
                    // 插入之前检查课程是不是存在，如果不存在，直接插入
                    List<Laboratory> labs = null;
                    if(type == null) {
                        labs = laboratoryDao.findAll();
                    } else {
                        labs = laboratoryDao.findByType(type);
                    }

                    // 匹配实验室
                    boolean isInsert = false;
                    for (Laboratory lab : labs) {
//                        System.out.println("room status = " + lab);
                        // 如果实验室是禁用的  直接跳过
                        if(lab.getStatus() == 0) {
                            continue;
                        }

                        // 如果实验室是可用的
                        entity.setLaboratoryId(lab.getId());
                        List<ArrangeDTO> byArrange = arrangeMapper.findByArrange(entity);

                        // 按照条件查询的课程信息是空的 说明课程有剩余  可以直接插入数据
                        if(byArrange == null || byArrange.size() == 0) {

                            // 遍历班级信息
                            for (Integer classId : classIds) {
                                entity.setCourseId(courseId);
                                entity.setUserId(userId);
                                entity.setNumber(number);
                                entity.setStatus(1);
                                entity.setClassId(classId);
                                entity.setTag(tag);
                                entity.setPeriod(period);
                                entity.setSectionEnum(sectionEnum);

                                System.out.println(dto);
                                System.out.println(entity);
                                arrangeDao.insertArrange(entity);
                                isInsert = true;
                            }
                            break;
                        }

                        // 如果此时间段没有时间--查询冲突课程是不是教师课程
                        for (ArrangeDTO arrange : byArrange) {
                            if(arrange.getUserId().equals(userId)) {
                                StringBuffer buffer = new StringBuffer("HAVING-信息已存在 : ");
                                buffer.append("课程: ").append(arrange.getCourseName()).append(" ");
                                buffer.append("周数: ").append(arrange.getWeeks()).append(" ");
                                buffer.append("星期: ").append(arrange.getDays()).append(" ");
                                buffer.append("节次: ").append(arrange.getSections()).append(" 该教师此信息已存在");
                                throw new IllegalArgumentException(buffer.toString());
                            }
                        }


                        /**
                         * 提示信息
                         * 1、此类教室该时间段无空闲
                         * 2、教师课程已存在
                         */
                        StringBuffer buffer = new StringBuffer("EXIST-存在错误: ");
                        buffer.append("课程: ").append(courseId).append(" ");
                        buffer.append("周数: ").append(week).append(" ");
                        buffer.append("星期: ").append(day).append(" ");
                        buffer.append("节次: ").append(section).append(" 此类型教师此时间段无空闲, 请另选时段或更换教室类型");
                        throw new IllegalArgumentException(buffer.toString());
                    }

                    // 如果没插入课程 -- 只存在教室不可用一种情况
                    if(!isInsert) {
                        throw new IllegalArgumentException("FAULT-该类教室不可用");
                    }
                }
            }
        }
    }

    @Override
    public void deleteByTag(Integer tag) throws Exception {
        arrangeDao.removeByTag(tag);
    }

    @Override
    public void updateByArrange(Arrange param) throws Exception {
        // 检验新修改的是否冲突

        Arrange last = new Arrange();
        BeanUtils.copyProperties(param, last);


        /**
         * 1、如果修改的是教师，考虑到教师时间冲突
         * 2、如果修改的是实验室，考虑实验室是否冲突
         *
         *
         */








        // 检查实验室是否发生冲突
        if(param.getLaboratoryId() != null) {

        }

        // 检查用户的id是否发生冲突
        if(param.getUserId() != null) {

        }
//        // 卸下标记
//        param.setTag(null);

//        // 先查询这个教室在这个时间段是不是存在课程 如果存在--直接报错
//        List<Arrange> arranges = arrangeMapper.selectOriginArrange(param);
//        for (Arrange arrange : arranges) {
//            if(param.getLaboratoryId() == null)
//                param.setLaboratoryId(arrange.getLaboratoryId());
//            if(param.getWeek() == null)
//                param.setWeek(arrange.getWeek());
//            if(param.getDay() == null)
//                param.setDay(arrange.getDay());
//            if(param.getSection() == null)
//                param.setSection(arrange.getSection());
//            List<Arrange> byArrange = arrangeMapper.selectOriginArrange(param);
//            if(byArrange != null || byArrange.size() != 0) {
//                throw new IllegalArgumentException("该时间段该实验室无空闲时间");
//            }
//        }

//        arrangeMapper.updateByArrange(last);




        arrangeMapper.updateByArrange(param);
    }

    @Override
    public void updateByWeeksDaysAndSections(Set<Integer> weeks, Set<Integer> days, Integer sectionEnum, Integer tag) throws Exception {
        Arrange param = new Arrange();

        List<ArrangeDTO> dtos = arrangeMapper.findArrangeByTag(tag);
        if(dtos == null || dtos.size() == 0) {
            throw new Exception("数据不存在");
        }
        ArrangeDTO dto = dtos.get(0);

        // 获取之前的基本数据
        Integer laboratoryId = dto.getLaboratoryId();
        Integer courseId = dto.getCourseId();
        Integer userId = dto.getUserId();
        Integer number = dto.getNumber();
        Set<Integer> classIds = dto.getClassIds();
        Double period = dto.getPeriod();

        Arrange last = new Arrange();
        last.setLaboratoryId(laboratoryId);
        last.setCourseId(courseId);
        last.setUserId(userId);
        last.setNumber(number);
        last.setPeriod(period);
        last.setTag(tag);
        last.setSectionEnum(sectionEnum);

        // 需要事先删除 之后一个个插入
        arrangeDao.removeByTag(tag);

        for (Integer week : weeks) {
            for (Integer day : days) {
                for (Integer section : SectionEnum.parse(sectionEnum)) {
                    for (Integer classId : classIds) {
                        param.setWeek(week);
                        param.setDay(day);
                        param.setSection(section);
                        param.setClassId(classId);

                        List<Arrange> arranges = arrangeMapper.selectOriginArrange(param);
                        if(arranges != null || arranges.size() != 0) {
                            throw new IllegalArgumentException("该时间段该实验室无空闲时间");
                        }

                        // 如果没查询到，说明没冲突，直接插入数据
                        last.setWeek(week);
                        last.setDay(day);
                        last.setSection(section);
                        last.setClassId(classId);
                        arrangeDao.insertArrange(last);
                    }
                }
            }
        }
    }

    @Override
    public void updateByArrangeDTO(ArrangeDTO dto) throws Exception {

    }
}
