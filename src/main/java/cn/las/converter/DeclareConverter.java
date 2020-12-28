package cn.las.converter;

import cn.las.bean.entity.Declare;
import cn.las.bean.vo.DeclareVO;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

public class DeclareConverter {

    // List转换为字符串形式
    private static String list2string(List<Integer> array) {
        if(array == null || array.size() == 0) return null;
        StringBuffer buffer = new StringBuffer();
        for (Integer arr : array) {
            buffer.append(arr).append(",");
        }
        buffer.deleteCharAt(buffer.length() - 1);
        return buffer.toString();
    }

    // 数组转换为字符串的形式
    private static String array2string(int [] array) {
        if(array == null || array.length == 0) return null;
        StringBuffer buffer = new StringBuffer();
        for (Integer arr : array) {
            buffer.append(arr).append(",");
        }
        buffer.deleteCharAt(buffer.length() - 1);
        return buffer.toString();
    }

    private static List<Integer> string2list(String data) {
        if(data == null) return null;
        List<Integer> list = new ArrayList<Integer>();
        String[] split = data.split(",");
        for (String string : split) {
            list.add(Integer.parseInt(string));
        }
        return list;
    }

    private static int [] string2array(String data) {
        if(data == null) return null;
        String[] split = data.split(",");
        int [] array = new int[split.length];
        for (int i = 0; i < array.length; i++) {
            array[i] = Integer.parseInt(split[i]);
        }
        return array;
    }

    public static void vo2entity(DeclareVO vo, Declare entity) {
        BeanUtils.copyProperties(vo, entity);
        // 更改其他数据--周数、天数、星期、班级数
        entity.setChangeweeks(list2string(vo.getWeeks()));
        entity.setChangedays(list2string(vo.getDays()));
        entity.setChangesectionenum(vo.getSectionenum());
        entity.setChangeclassids(list2string(vo.getClassIds()));
    }

    public static void entity2vo(Declare entity, DeclareVO vo) {
        BeanUtils.copyProperties(entity, vo);
        vo.setWeeks(string2list(entity.getChangeweeks()));
        vo.setDays(string2list(entity.getChangedays()));
        vo.setSectionenum(entity.getChangesectionenum());
        vo.setClassIds(string2list(entity.getChangeclassids()));
    }
}
