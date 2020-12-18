package cn.las.service.impl;

import cn.las.bean.dto.ArrangeDTO;
import cn.las.bean.entity.Course;
import cn.las.bean.entity.User;
import cn.las.controller.ArrangeController;
import cn.las.converter.ArrangeConverter;
import cn.las.dao.ArrangeDao;
import cn.las.bean.entity.Arrange;
import cn.las.bean.entity.Laboratory;
import cn.las.dao.CourseDao;
import cn.las.dao.LaboratoryDao;
import cn.las.dao.UserDao;
import cn.las.mapper.ArrangeMapper;
import cn.las.service.ArrangeService;
import com.fasterxml.jackson.databind.util.BeanUtil;
import org.springframework.beans.BeanUtils;
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

    public List<Arrange> findAll() throws Exception {
        return arrangeDao.findAll();
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
     * @param arrange
     * @return
     * @throws Exception
     */
    @Override
    public List<ArrangeDTO> findByArrange(Arrange arrange) throws Exception {
        // 使用bean utils进行属性的复制
        List<Arrange> arranges = arrangeDao.findByArrange(arrange);
        List<ArrangeDTO> dtos = new ArrayList<ArrangeDTO>();
        for (Arrange entity : arranges) {
            // 进行属性的复制
            ArrangeDTO arrangeDTO = new ArrangeDTO();
            ArrangeConverter.entityToDto(entity, arrangeDTO);

            System.out.println(arrangeDTO);
            System.out.println(entity);

            // 封装其他信息
            Course course = courseDao.findCourseById(entity.getId());
            Laboratory laboratory = laboratoryDao.findById(entity.getLaboratoryId());
            User user = userDao.findUserInfoById(entity.getUserId());

            arrangeDTO.setLaboratory(laboratory);
            arrangeDTO.setUser(user);
            arrangeDTO.setCourse(course);

            dtos.add(arrangeDTO);
        }
        return dtos;
    }

    @Override
    public void insertArrange(ArrangeDTO dto) throws Exception {
        // 进行对象的转换和检查
        List<Integer> weeks = dto.getWeeks();
        int[] sections = dto.getSections();
        String type = dto.getType();

        for (int week : weeks) {
            for (int section : sections) {
                // arrange对象的封装a
                Arrange entity = new Arrange();
                ArrangeConverter.dtoToEntity(dto, entity);

                // 封装其他属性（教室信息一会儿查询得到）
                entity.setWeek(week);
                entity.setSection(section);
                List<Arrange> exist = arrangeDao.findByArrange(entity);

                /**
                 * 提示信息
                 * 1、此类教室该时间段无空闲
                 */

                // 插入之前检查课程是不是存在，如果不存在，直接插入
                if(type != null) {
                    List<Laboratory> byType = laboratoryDao.findByType(type);
                    for (Laboratory lab : byType) {
                        entity.setLaboratoryId(lab.getId());
                        List<Arrange> byArrange = arrangeDao.findByArrange(entity);
                        if(byArrange == null || byArrange.size() == 0) {
                            // 如果查询到有空闲课程，直接插入
                            arrangeDao.insertArrange(entity);
                            return;
                        }
                    }
                }
                // 如果存在，抛出异常给上级捕捉
                throw new IllegalArgumentException("该时间段该类实验室无空闲");
            }
        }
    }
}
