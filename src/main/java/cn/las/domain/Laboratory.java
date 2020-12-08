package cn.las.domain;

import lombok.Data;
import lombok.ToString;

import java.util.Arrays;
import java.util.List;

@Data
@ToString
public class Laboratory {

    private int id;

    private String name;

    private String type;

    private int size;

    private String location;

    private int status;

    private String limitpro;

    private List<String> limits;

    public List<String> getLimits() {
        String[] s = limitpro.split(" ");
        limits.addAll(Arrays.asList(s));
        return limits;
    }
}
