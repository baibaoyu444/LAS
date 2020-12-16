package cn.las.bean.enu;

public enum SectionEnum {

    First(1,new int[]{1}),
    Second(2,new int[]{2}),
    Third(3,new int[]{3}),
    Forth(4,new int[]{4}),
    Fifth(5,new int[]{5}),
    Sixth(6,new int[]{6}),
    Seventh(7,new int[]{1,2}),
    eighth(8,new int[]{3,4}),
    ninth(9,new int[]{1,2,3,4});


    private int code;

    private int [] sections;

    SectionEnum(int code, int[] sections) {
        this.code = code;
        this.sections = sections;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int[] getSections() {
        return sections;
    }

    public void setSections(int[] sections) {
        this.sections = sections;
    }

    public int [] parse(int code) {
        for (SectionEnum sectionEnum: SectionEnum.values()) {
            if(sectionEnum.code == code) {
                return sectionEnum.sections;
            }
        }
        return null;
    }
}
