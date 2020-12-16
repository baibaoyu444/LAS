package cn.las.converter;

import cn.las.bean.dto.ArrangeDTO;
import cn.las.bean.entity.Arrange;
import org.springframework.beans.BeanUtils;

public class ArrangeConverter {

    public static void dtoToEntity(ArrangeDTO dto, Arrange entity) {
        BeanUtils.copyProperties(dto, entity);

        // 复制剩下的没复制的属性classes
        StringBuffer buffer = new StringBuffer("");
        for (String s : dto.getClassList()) {
            buffer.append(s).append(" ");
        }
        entity.setClasses(buffer.toString().trim());
    }

    public static void entityToDto(Arrange entity, ArrangeDTO dto) {

    }


}
