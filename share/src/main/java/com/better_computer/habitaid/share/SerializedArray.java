package com.better_computer.habitaid.share;

public class SerializedArray {

    private String[] sxArr;
    private int iSize;

    // specifying size helps
    // when using java split function
    // which is 'optimized' .. :-\
    public SerializedArray(String[] sxArr, int iSize) {
        this.iSize = iSize;
        this.sxArr = new String[iSize];

        int len = sxArr.length;
        for (int i=0; i<iSize; i++) {
            if(i < len) {
                this.sxArr[i] = sxArr[i];
            }
            else {
                this.sxArr[i] = "";
            }
        }
    }

    public SerializedArray(String[] sxArr) {
        this.sxArr = sxArr;
        this.iSize = sxArr.length;
    }

    public String[] getArray() {
        return sxArr;
    }

    public int getSize() {
        return iSize;
    }

    public String getSerialString(String sDelim) {
        String sRet = "";

        for(int i=0; i<iSize; i++) {
            sRet += sDelim + sxArr[i];
        }

        return sRet.substring(1);
    }
}
