package com.onedaytrip;

/**
 * Created by tomek on 7/12/15.
 */
public class TestJavaAccess {
    private int intNum;
    private long longNum;
    private String stringVar;

    public TestJavaAccess(Integer intNum, Long longNum, String stringVar) {
        this.intNum = intNum;
        this.longNum = longNum;
        this.stringVar = stringVar;
    }

    public int getIntNum() {
        return intNum;
    }

    public long getLongNum() {
        return longNum;
    }

    public String getStringVar() {
        return stringVar;
    }

    @Override
    public String toString() {
        return "TestJavaAccess{" +
                "intNum=" + intNum +
                ", longNum=" + longNum +
                ", stringVar='" + stringVar + '\'' +
                '}';
    }
}
