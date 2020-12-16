package cn.las.bean.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Course {

    private int id;

    private String name;

    private int time;

    private double score;
}
