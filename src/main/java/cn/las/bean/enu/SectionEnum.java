package cn.las.bean.enu;

import java.util.Arrays;

public enum SectionEnum {

    First(1,new int[]{1}),
    Second(2,new int[]{2}),
    Third(3,new int[]{3}),
    Forth(4,new int[]{4}),
    Fifth(5,new int[]{5}),
    Sixth(6,new int[]{6}),
    Seventh(7,new int[]{1,2}),
    eighth(8,new int[]{3,4}),
    ninth(9,new int[]{5,6}),
    tenth(10,new int[]{1,2,3,4}),
    eleventh(11,new int[]{3,4,5,6}),
    twelveth(12, new int[]{1,2,3,4,5,6});



    private int code;

    private int [] sections;

    SectionEnum(int code, int[] sections) {
        this.code = code;
        this.sections = sections;
    }

    public static int [] parse(int code) {
        for (SectionEnum sectionEnum: SectionEnum.values()) {
            if(sectionEnum.code == code) {
                return sectionEnum.sections;
            }
        }
        return null;
    }
}
