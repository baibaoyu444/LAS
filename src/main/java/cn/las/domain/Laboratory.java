package cn.las.domain;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Laboratory {

    private int id;

    private String name;

    private String type;

    private int size;

    private String location;

    private int status;
}
