package cn.las.bean.entity;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class Laboratory {

    private Integer id;

    private String name;

    private String type;

    private Integer size;

    private String location;

    private Integer status;

    private String limitpro;

//    private List<String> limits;

//    public List<String> getLimits() {
//        if(limitpro == null) return null;
//        String[] s = limitpro.split(" ");
//        limits.addAll(Arrays.asList(s));
//        return limits;
//    }
}
