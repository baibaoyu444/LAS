package cn.las.bean.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class DeclareVO {

    private Integer id;

    private Integer type;

    private String reason;

    private Integer userId;

    private Integer status;

    private List<Integer> weeks;

    private List<Integer> days;

    private Integer sectionenum;

    private List<Integer> classIds;

    private Integer tag;
}
