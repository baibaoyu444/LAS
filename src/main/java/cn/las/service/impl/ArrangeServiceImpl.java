package cn.las.service.impl;

import cn.las.bean.dto.ArrangeDTO;
import cn.las.bean.entity.Course;
import cn.las.bean.entity.User;
import cn.las.dao.ArrangeDao;
import cn.las.bean.entity.Arrange;
import cn.las.bean.entity.Laboratory;
import cn.las.dao.CourseDao;
import cn.las.dao.LaboratoryDao;
import cn.las.dao.UserDao;
import cn.las.mapper.ArrangeMapper;
import cn.las.service.ArrangeService;
import com.sun.org.apache.bcel.internal.generic.FSUB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
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

    public List<ArrangeDTO> findAll() throws Exception {
        return arrangeMapper.findAll();
    }

    public void deleteArrangeByCourseId(int courseId)throws Exception{
        arrangeMapper.deleteById(courseId);
    }

    public void insertone(Arrange arrange)throws Exception{
        arrangeMapper.insertone(arrange);
    }

    public void updateArrangeById(int id, int week, int day, int section)throws Exception{
        arrangeMapper.updateArrangeById(id,week,day,section);
    }

    public List<Arrange> findArrangeByLaboratoryId(int laboratoryId,int week)throws Exception{
        return arrangeDao.findByLaboratoryId(laboratoryId,week);
    }

    public List<Arrange> findArrangeByCourseId(int courseId)throws Exception{
        return arrangeMapper.findArrangeByCourseId(courseId);
    }

    //可能不需要（先保留）
    public List<Arrange> findArrangeByweek(int weeks)throws Exception{
        return arrangeMapper.findArrangeByweek(weeks);
    }

    @Override
    public List<Laboratory> findEmptyLabByTypeAndWeeksAndDayAndSections(String type, List<Integer> weeks, Integer day, List<Integer> sections) throws Exception {
        return arrangeDao.findEmptyLabByTypeAndWeeksAndDayAndSections(type, weeks, day, sections);
    }

    /**
     * @param laboratoryId 教室id
     * @param weeks 指定的周数
     * @param day 指定周几
     * @return
     * @throws Exception
     *
     * 查找可用的节数按照
     */
    @Override
    public List<Integer> findEmptySectionsByLabIdAndWeeksAndDay(Integer laboratoryId, List<Integer> weeks, Integer day) throws Exception {
        // 查询这几周在这一天可用的节数
        //        return arrangeDao.findEmptyLabByLabIdAndWeeksAndDay(laboratoryId, weeks, day);
        return null;
    }

    /**
     * @param weeks  指定周数
     * @param day  指定某一天
     * @return
     * @throws Exception
     *
     * 1、查找可用的节数 按照周数和周几
     * 2、返回集合当中包含节数信息
     */
    @Override
    public Set<Integer> findSectionsByWeeksAndDay(List<Integer> weeks, Integer day,String type) throws Exception {
        Set<Integer> sets = new HashSet<Integer>();
        Set<Integer> empty = arrangeDao.findSectionsByWeeksAndDay(weeks, day ,type);
        for (int i = 1; i <= 5; i++) {
            if(empty.contains(i)) sets.add(i);
        }
        return sets;
    }

    @Override
    public List<Arrange> findArrangeByWeekAndDayAndSection(Integer week, Integer day, Integer section) throws Exception {
        return arrangeDao.findArrangeByWeekAndDayAndSection(week, day, section);
    }

    @Override
    public List<Integer> isEnableByWeeksAndDayAndSection(List<Integer> weeks, Integer day, Integer section, String type) throws Exception {
        return arrangeDao.isEnableByWeeksAndDayAndSection(weeks, day, section, type);
    }

    @Override
    public void insertArrange(Arrange arrange) throws Exception {
        arrangeDao.addArrange(arrange);
    }

    @Override
    public void addArrange(Arrange arrange) throws Exception {
        arrangeDao.addArrange(arrange);
    }

    @Override
    public List<Arrange> findArrangeByUserId(Integer userId,Integer week) throws Exception {
        return arrangeDao.findArrangeByUserId(userId,week);
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
        // 使用bean utils进行属性的复制
        List<ArrangeDTO> arranges = arrangeMapper.findByArrange(arrange);
        return arranges;
    }

    @Override
    public void insertArrange(ArrangeDTO dto) throws Exception {
        // 提取dto数据
        List<Integer> weeks = dto.getWeeks();
        List<Integer> sections = dto.getSections();
        Integer day = dto.getDay();
        String type = dto.getType();
        Integer userId = dto.getUserId();
        Integer courseId = dto.getCourseId();
        String classes = dto.getClassList().toString().replaceAll(", ", ",");
        classes = classes.substring(1, classes.length() - 1);
        Double period = dto.getPeriod();
        Integer number = dto.getNumber();
        Integer tag = arrangeDao.findMaxTag() + 1;

        for (int week : weeks) {
            for (int section : sections) {
                // arrange对象的封装a
                Arrange entity = new Arrange();

                // 封装其他属性（周数，星期，节次，实验室id）用于检验冲突
                entity.setWeek(week);
                entity.setSection(section);
                entity.setDay(day);

                /**
                 * 提示信息
                 * 1、此类教室该时间段无空闲
                 * 2、教师课程已存在
                 */

                /**
                 * 课程冲突检查流程
                 * 周次 + 星期 + 时间 + 教室 是否冲突
                 * 如果冲突
                 *      教师的userId是否相同
                 *          不相同 - 教室该时间段被占用
                 *          相同 - 教师课程已存在
                 * 没有冲突
                 *      进行教室的安排
                 *
                 * 一个教师 同一时间段不能安排多场课--等待考虑
                 */
                // 插入之前检查课程是不是存在，如果不存在，直接插入
                List<Laboratory> byType = null;
                if(type == null) {
                    byType = laboratoryDao.findAll();
                } else {
                    byType = laboratoryDao.findByType(type);
                }

                // 匹配实验室
                for (Laboratory lab : byType) {
                    // 如果实验室是不可用的
                    if(lab.getStatus() == 0) continue;

                    // 如果实验室是可用的
                    entity.setLaboratoryId(lab.getId());
                    List<ArrangeDTO> byArrange = arrangeMapper.findByArrange(entity);
                    if(byArrange == null || byArrange.size() == 0) {
                        // 如果查询到有空闲课程，直接插入


                        entity.setCourseId(courseId);
                        entity.setUserId(userId);
                        entity.setNumber(number);
                        entity.setStatus(1);
                        entity.setClasses(classes);
                        entity.setTag(tag);
                        entity.setPeriod(period);

                        System.out.println(dto);
                        System.out.println(entity);
                        arrangeDao.insertArrange(entity);
                        break;
                    }

                    // 查询冲突课程是不是教师课程
                    boolean belongUser = false;
                    for (ArrangeDTO arrange : byArrange) {
                        if(arrange.getUserId().equals(userId)) {
                            belongUser = true;
                            break;
                        }
                    }

                    if(!belongUser) {
                        StringBuffer buffer = new StringBuffer("存在错误: ");
                        buffer.append("课程: ").append(courseId).append(" ");
                        buffer.append("周数: ").append(week).append(" ");
                        buffer.append("星期: ").append(day).append(" ");
                        buffer.append("节次: ").append(section).append(" 此类型教师此时间段无空闲, 请另选时段或更换教室类型");
                        throw new IllegalArgumentException(buffer.toString());
                    }
                }
            }
        }
    }
}
