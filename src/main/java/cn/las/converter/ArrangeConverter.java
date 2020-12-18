package cn.las.converter;

import cn.las.bean.dto.ArrangeDTO;
import cn.las.bean.entity.Arrange;
import cn.las.bean.vo.ArrangeVO;
import org.springframework.beans.BeanUtils;

import java.util.Arrays;
import java.util.List;

public class ArrangeConverter {

    public static void dtoToEntity(ArrangeDTO dto, Arrange entity) {
        BeanUtils.copyProperties(dto, entity);

        // 复制剩下的没复制的属性classes
        List<String> classList = dto.getClassList();
        if(classList == null || classList.size() == 0) {
            String s = classList.toString();
            String classes = s.substring(1, s.length() - 1).replaceAll(", ", ",").trim();
            entity.setClasses(classes);
        }
    }

    public static void entityToDto(Arrange entity, ArrangeDTO dto) {
        BeanUtils.copyProperties(entity, dto);

//        dto.setId(entity.getId());
//        dto.setLaboratoryId(entity.getLaboratoryId());
//        dto.setCourseId(entity.getCourseId());
//        dto.setUserId(entity.getUserId());
//        dto.setWeek(entity.getWeek());
//        dto.setSection(entity.getSection());
//        dto.setDay(entity.getDay());
//        dto.setNumber(entity.getNumber());
//        dto.setStatus(entity.getStatus());

        // 封装课程信息
        if(entity.getClasses() != null) {
            Arrays.asList(entity.getClasses().split(","));
        }
    }

    public static void dto2vo(ArrangeDTO dto, ArrangeVO vo) {
        BeanUtils.copyProperties( dto, vo);
        vo.setCourseName(dto.getCourse().getName());
        vo.setLabName(dto.getLaboratory().getName());
        vo.setUserName(dto.getUser().getTeacher());
    }


}
