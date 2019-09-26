package com.better_computer.habitaid.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Pair;

import com.better_computer.habitaid.data.DatabaseHelper;
import com.better_computer.habitaid.data.core.Content;
import com.better_computer.habitaid.data.core.ContentLog;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DynaArray {

    private InternalItem[] internalArray = {};
    private InternalItemNew[] internalArrayNew = {};

    private volatile int lenInternalArray = 0;
    private volatile double totalWight = 0;
    private Random rand = new Random();

    public void init() {
        this.internalArray = new InternalItem[] {};
        this.internalArrayNew = new InternalItemNew[] {};

        lenInternalArray = 0;
        totalWight = 0;
    }

    private static class ContributingArray {
        //List<Content> array;
        String arrayId;
        int weight;
        int numRepeats;
        int len;
        double wtAvg;
        double wtExtinguish;
        double wtRemove;
    }

    public static class InternalItem {
        String name;
        double originalWeight;
        double calWeight;
        ContributingArray contributingArray;
        Content content;

        public String getName() {
            return name;
        }

        public String get_state() {
            return content.get_state();
        }

        public String getArrayId() {
            return content.getPlayerid();
        }
    }

    private static class ContributingArrayNew {
        //List<Content> array;
        String arrayId;
        double weight;
        int numRepeats;
        int len;
    }

    public static class InternalItemNew {
        String _state;
        String parent_id;
        String content;
        double originalWeight;
        double calWeight;
        int repeated;
        ContributingArrayNew contributingArray;

        public String get_state() {
            return _state;
        }

        public String getArrayId() {
            return parent_id;
        }
    }

    public String[] currentStringArray() {
        String[] result = new String[lenInternalArray];
        for (int i = 0 ; i < lenInternalArray ; i++) {
            InternalItem item = internalArray[i];
            result[i] = item.name;
        }
        return result;
    }

    public String[] currentStringArrayNew() {
        String[] result = new String[lenInternalArray];
        for (int i = 0 ; i < lenInternalArray ; i++) {
            InternalItemNew item = internalArrayNew[i];
            result[i] = item.content;
        }
        return result;
    }

    public InternalItem[] currentInternalItemArray() {
        InternalItem[] result = new InternalItem[lenInternalArray];
        for (int i = 0 ; i < lenInternalArray ; i++) {
            InternalItem item = internalArray[i];
            result[i] = item;
        }
        return result;
    }

    public InternalItemNew[] currentInternalItemArrayNew() {
        InternalItemNew[] result = new InternalItemNew[lenInternalArray];
        for (int i = 0 ; i < lenInternalArray ; i++) {
            InternalItemNew item = internalArrayNew[i];
            result[i] = item;
        }
        return result;
    }

    public void addContributingArray(List<Content> listContent, String arrayId, int weight, int numRepeats) {
        ContributingArray contributingArray = new ContributingArray();
        //contributingArray.array = listContent;
        contributingArray.arrayId = arrayId;
        contributingArray.weight = weight;
        contributingArray.numRepeats = numRepeats;

        int iTotWeightArray = 0;

        int lenContent = listContent.size();
        InternalItem[] tempInternalArray = new InternalItem[lenContent];

        for (int i=0 ; i<lenContent ; i++) {
            Content content = listContent.get(i);
            InternalItem item = new InternalItem();
            item.contributingArray = contributingArray;
            item.content = content;
            tempInternalArray[i] = item;

            item.name = (String) content.getContent();
            item.originalWeight = (double) content.getWeight();
            item.calWeight = item.originalWeight * item.contributingArray.weight;
            iTotWeightArray += item.calWeight;
        }

        contributingArray.len = lenContent;
        double dWtAvg = iTotWeightArray / (1.0 * lenContent);
        contributingArray.wtAvg = dWtAvg;
        contributingArray.wtExtinguish = dWtAvg / (lenContent + 1);
        contributingArray.wtRemove = dWtAvg / numRepeats;

        totalWight += iTotWeightArray;
        internalArray = concat(internalArray, lenInternalArray, tempInternalArray, tempInternalArray.length);
        lenInternalArray = internalArray.length;
    }

    public void addContributingArrayNew(
        List<Pair> listWtContent, String arrayId, double weight, int numRepeats) {
        ContributingArrayNew contributingArray = new ContributingArrayNew();
        //contributingArray.array = listContent;
        contributingArray.arrayId = arrayId;
        contributingArray.weight = weight;
        contributingArray.numRepeats = numRepeats;

        double dTotWeightArray = 0;

        int lenContent = listWtContent.size();
        InternalItemNew[] tempInternalArray = new InternalItemNew[lenContent];

        for (int i=0 ; i<lenContent ; i++) {
            InternalItemNew item = new InternalItemNew();
            item._state = "active";
            item.parent_id = arrayId;
            item.contributingArray = contributingArray;
            item.originalWeight = (double)((int)listWtContent.get(i).first / 1.0);
            item.content = (String)(listWtContent.get(i).second);
            item.calWeight = item.originalWeight * item.contributingArray.weight;

            tempInternalArray[i] = item;

            dTotWeightArray += item.calWeight;
        }

        contributingArray.len = lenContent;
        double dWtAvg = dTotWeightArray / (1.0 * lenContent);

        totalWight += dTotWeightArray;
        internalArrayNew = concatNew(internalArrayNew, lenInternalArray, tempInternalArray, tempInternalArray.length);
        lenInternalArray = internalArrayNew.length;
    }

    public boolean containsContributingArray(String arrayId) {
        if (lenInternalArray == 0) {
            return false;
        }

        int i = 0;
        while (i < lenInternalArray) {
            InternalItem item = internalArray[i];
            if (item.contributingArray.arrayId.equals(arrayId)) {
                return true;
            }
            i++;
        }
        return false;
    }

    public void removeContributingArray(String arrayId) {
        if (lenInternalArray == 0) {
            return;
        }

        int i = 0;
        while (i < lenInternalArray) {
            InternalItem item = internalArray[i];
            if (item.contributingArray.arrayId.equals(arrayId)) {
                swapWithLastItem(i);
            } else {
                i++;
            }
        }
    }

    public void removeContributingArrayNew(String arrayId) {
        if (lenInternalArray == 0) {
            return;
        }

        int i = 0;
        while (i < lenInternalArray) {
            InternalItemNew item = internalArrayNew[i];
            if (item.parent_id.equals(arrayId)) {
                swapWithLastItemNew(i);
            }
            else {
                i++;
            }
        }
    }

    public void removeContributingArrayStartWith(String arrayId) {
        if (lenInternalArray == 0) {
            return;
        }

        int i = 0;
        while (i < lenInternalArray) {
            InternalItem item = internalArray[i];
            if (item.contributingArray.arrayId.startsWith(arrayId)) {
                swapWithLastItem(i);
            } else {
                i++;
            }
        }
    }

    public void removeArrayItem(String itemName) {
        int i = 0;
        while (i < lenInternalArray) {
            InternalItem item = internalArray[i];
            if (item.name.equals(itemName)) {

                // weighted average was sigma product weights / len

                // if item removed, modify contributing array
                item.contributingArray.wtAvg =
                        ((item.contributingArray.wtAvg * item.contributingArray.len)
                        - item.originalWeight * item.contributingArray.weight) / (item.contributingArray.len - 1);

                item.contributingArray.len--;

                item.contributingArray.wtExtinguish =
                        item.contributingArray.wtAvg / (item.contributingArray.len + 1);

                item.contributingArray.wtRemove =
                        item.contributingArray.wtAvg / item.contributingArray.numRepeats;

                swapWithLastItem(i);
            } else {
                i++;
            }
        }
    }

    public String sItemDetails(String itemName) {
        String sRet = "";
        DecimalFormat df = new DecimalFormat("#.##");

        int i = 0;
        while (i < lenInternalArray) {
            InternalItem item = internalArray[i];
            if (item.name.equals(itemName)) {
                sRet = "len: " + item.contributingArray.len
                        + "\n" + "wt: " + item.contributingArray.weight
                        + "\n" + "wtavg: " + item.contributingArray.wtAvg
                        + "\n" + "wtext: " + item.contributingArray.wtExtinguish
                        + "\n" + "wtthr: " + df.format(item.contributingArray.wtRemove)
                        + "\n" + "itemOrigWeight: " + item.originalWeight
                        + "\n" + "itemCalWeight: " + df.format(item.calWeight)
                        + "\n" + "totalWight: " + df.format(totalWight)
                        + "\n" + "lenInternalArray: " + lenInternalArray;

                return sRet;
            }
            else {
                i++;
            }
        }

        return sRet;
    }

    public String getRandomElement() {
        if (lenInternalArray == 0) {
            return "";
        }

        double dRand = rand.nextDouble();
        dRand *= totalWight;

        double fSum = 0;

        for (int i = 0; i < lenInternalArray; i++) {
            InternalItem item = internalArray[i];
            fSum += (double) item.calWeight;
            if(dRand < fSum
                    && item.calWeight >= item.contributingArray.wtRemove) {

                String sResult = (String) item.name;

                // apply wtExtinguish
                double newWeight = item.calWeight - item.contributingArray.wtExtinguish;
                totalWight -= (item.calWeight - newWeight);
                // assign new cal weight
                item.calWeight = newWeight;

                return sResult;
            }
        }

        // may have changed!
        // since some items have been removed
        totalWight = 0;

        for (int i = 0; i < lenInternalArray; i++) {
            InternalItem item = internalArray[i];
            item.calWeight = item.originalWeight * item.contributingArray.weight;
            totalWight += item.calWeight;
        }
        return getRandomElement();
    }

    public String getRandomElementNew() {
        if (lenInternalArray == 0) {
            // bingo: the re-seed?
            //return "";

            SQLiteDatabase database = DatabaseHelper.getInstance().getReadableDatabase();

            String sql = "SELECT DISTINCT (cat || ';' || subcat || ';' || wtcat) as foo FROM core_tbl_nonsched WHERE _state='active' ORDER BY cat,subcat";

            List<String> listCatSubcat = new ArrayList<String>();
            try {
                Cursor cursor = database.rawQuery(sql, new String[0]);
                if (cursor.moveToFirst()) {
                    do {
                        listCatSubcat.add(cursor.getString(0));
                    } while (cursor.moveToNext());
                }

                //fix - android.database.CursorWindowAllocationException Start
                cursor.close();
                //fix - android.database.CursorWindowAllocationException End
            } catch (Exception e) {
                e.printStackTrace();
            }

            int iCount = 0;
            for (String s : listCatSubcat) {
                String sCat = "";
                String sSubcat = "";
                String sWtcat = "";

                String[] sxTokens = s.split(";");
                sCat = sxTokens[0];
                sSubcat = sxTokens[1];
                sWtcat = sxTokens[2];

                iCount = 0;
                List<Pair> listWtContent = new ArrayList<Pair>();

                sql = "SELECT (name || '-=' || content) as foo FROM core_tbl_nonsched "
                        + "WHERE cat='" + sCat + "' AND subcat='" + sSubcat + "' AND _state='active'";
                try {
                    Cursor cursor = database.rawQuery(sql, new String[0]);
                    if (cursor.moveToFirst()) {
                        do {
                            listWtContent.add(new Pair(2, cursor.getString(0)));
                            iCount++;
                        } while (cursor.moveToNext());
                    }

                    //fix - android.database.CursorWindowAllocationException Start
                    cursor.close();
                    //fix - android.database.CursorWindowAllocationException End
                } catch (Exception e) {
                    e.printStackTrace();
                }

                addContributingArrayNew(
                        listWtContent, s, Double.parseDouble(sWtcat) / iCount, 2);
            }
        }

        double dRand = rand.nextDouble();
        dRand *= totalWight;

        double fSum = 0;
        int numRepeats = 0;
        for (int i = 0; i < lenInternalArray; i++) {
            InternalItemNew item = internalArrayNew[i];
            fSum += (double) item.calWeight;
            numRepeats = item.contributingArray.numRepeats;
            if(dRand < fSum
                    && item.repeated < numRepeats) {

                // so calWeight <= 200 (using 100-scale * 2 standard)
                // lets say if its less than 20, aka category < 10
                // then we reserve right to skip over 50% items
                if(item.calWeight <= 20) {
                    if(rand.nextDouble() < 0.5) {
                        // flag as repeated w/o repeating
                        item.repeated++;
                    }
                    else {
                        String sResult = item.content;

                        // apply wtExtinguish
                        item.repeated++;
                        double newWeight =
                                ((numRepeats - item.repeated) * item.calWeight)
                                        / numRepeats;
                        totalWight -= (item.calWeight - newWeight);
                        // assign new cal weight
                        item.calWeight = newWeight;

                        return sResult;
                    }
                }
                else {
                    String sResult = item.content;

                    // apply wtExtinguish
                    item.repeated++;
                    double newWeight =
                            ((numRepeats - item.repeated) * item.calWeight)
                                    / numRepeats;
                    totalWight -= (item.calWeight - newWeight);
                    // assign new cal weight
                    item.calWeight = newWeight;

                    return sResult;
                }
            }
        }

        // may have changed!
        // since some items have been removed
        totalWight = 0;

        for (int i = 0; i < lenInternalArray; i++) {
            InternalItemNew item = internalArrayNew[i];
            item.calWeight = item.originalWeight * item.contributingArray.weight;
            item.repeated = 0;
            totalWight += item.calWeight;
        }
        return getRandomElementNew();
    }

    public ContentLog getRandomElementNewLog() {

        if (lenInternalArray == 0) {
            // bingo: the re-seed?
            //return "";

            SQLiteDatabase database = DatabaseHelper.getInstance().getReadableDatabase();

            String sql = "SELECT DISTINCT (cat || ';' || subcat || ';' || wtcat) as foo FROM core_tbl_nonsched WHERE _state='active' ORDER BY cat,subcat";

            List<String> listCatSubcat = new ArrayList<String>();
            try {
                Cursor cursor = database.rawQuery(sql, new String[0]);
                if (cursor.moveToFirst()) {
                    do {
                        listCatSubcat.add(cursor.getString(0));
                    } while (cursor.moveToNext());
                }

                //fix - android.database.CursorWindowAllocationException Start
                cursor.close();
                //fix - android.database.CursorWindowAllocationException End
            } catch (Exception e) {
                e.printStackTrace();
            }

            int iCount = 0;
            for (String s : listCatSubcat) {
                String sCat = "";
                String sSubcat = "";
                String sWtcat = "";

                String[] sxTokens = s.split(";");
                sCat = sxTokens[0];
                sSubcat = sxTokens[1];
                sWtcat = sxTokens[2];

                iCount = 0;
                List<Pair> listWtContent = new ArrayList<Pair>();

                sql = "SELECT (name || '-=' || content) as foo FROM core_tbl_nonsched "
                        + "WHERE cat='" + sCat + "' AND subcat='" + sSubcat + "' AND _state='active'";
                try {
                    Cursor cursor = database.rawQuery(sql, new String[0]);
                    if (cursor.moveToFirst()) {
                        do {
                            listWtContent.add(new Pair(2, cursor.getString(0)));
                            iCount++;
                        } while (cursor.moveToNext());
                    }

                    //fix - android.database.CursorWindowAllocationException Start
                    cursor.close();
                    //fix - android.database.CursorWindowAllocationException End
                } catch (Exception e) {
                    e.printStackTrace();
                }

                addContributingArrayNew(
                        listWtContent, s, Double.parseDouble(sWtcat) / iCount, 2);
            }
        }

        ContentLog retContentLog = new ContentLog();

        double dRand = rand.nextDouble();
        dRand *= totalWight;

        double fSum = 0;
        String sContent = "";
        int iLenContent = 0;
        int numRepeats = 0;
        for (int i = 0; i < lenInternalArray; i++) {
            InternalItemNew item = internalArrayNew[i];
            fSum += (double) item.calWeight;
            numRepeats = item.contributingArray.numRepeats;

            if(dRand < fSum
                    && item.repeated < numRepeats) {

                retContentLog.setPlayerid(item.parent_id);

                // so calWeight <= 200 (using 100-scale * 2 standard)
                // lets say if its less than 20, aka category < 10
                // then we reserve right to skip over 50% items
                if(item.calWeight <= 20) {
                    if(rand.nextDouble() < 0.5) {
                        // flag as repeated w/o repeating
                        item.repeated++;
                        sContent = sContent + " -> ";
                    }
                    else {
                        retContentLog.setContent(sContent + item.content);
                        retContentLog.setWt(item.calWeight);
                        retContentLog.setWtArray(totalWight);

                        // apply wtExtinguish
                        item.repeated++;
                        double newWeight =
                                ((numRepeats - item.repeated) * item.calWeight)
                                        / numRepeats;
                        totalWight -= (item.calWeight - newWeight);
                        // assign new cal weight
                        item.calWeight = newWeight;

                        retContentLog.setWtNew(newWeight);
                        retContentLog.setWtArrayNew(totalWight);

                        sContent = "";
                        return retContentLog;
                    }
                }
                else {
                    retContentLog.setContent(sContent + item.content);
                    retContentLog.setWt(item.calWeight);
                    retContentLog.setWtArray(totalWight);

                    // apply wtExtinguish
                    item.repeated++;
                    double newWeight =
                            ((numRepeats - item.repeated) * item.calWeight)
                                    / numRepeats;
                    totalWight -= (item.calWeight - newWeight);
                    // assign new cal weight
                    item.calWeight = newWeight;

                    retContentLog.setWtNew(newWeight);
                    retContentLog.setWtArrayNew(totalWight);

                    sContent = "";
                    return retContentLog;
                }
            }
        }

        // may have changed!
        // since some items have been removed
        totalWight = 0;

        for (int i = 0; i < lenInternalArray; i++) {
            InternalItemNew item = internalArrayNew[i];
            item.calWeight = item.originalWeight * item.contributingArray.weight;
            item.repeated = 0;
            totalWight += item.calWeight;
        }
        return getRandomElementNewLog();
    }

    private InternalItem[] concat(InternalItem[] a, int aLen, InternalItem[] b, int bLen) {
        InternalItem[] c= new InternalItem[aLen+bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }

    private InternalItemNew[] concatNew(InternalItemNew[] a, int aLen, InternalItemNew[] b, int bLen) {
        InternalItemNew[] c= new InternalItemNew[aLen+bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }

    private void swapWithLastItem(int index) {
        InternalItem item = internalArray[index];
        swap(internalArray, index, lenInternalArray - 1);
        // decrease totalWight
        totalWight -= item.calWeight;
        lenInternalArray--;
    }

    private void swapWithLastItemNew(int index) {
        InternalItemNew item = internalArrayNew[index];
        swap(internalArrayNew, index, lenInternalArray - 1);
        // decrease totalWight
        totalWight -= item.calWeight;
        lenInternalArray--;
    }

    private void swap(Object[] array, int index1, int index2) {
        Object temp = array[index1];
        array[index1] = array[index2];
        array[index2] = temp;
    }


    public static void main(String[] args) {
        System.out.println("Test");

        //Object[][] array1 = {{"aaa", 0.1}, {"bbb", 0.4}, {"ccc", 0.5}};
        //Object[][] array2 = {{"xxx", 0.2}, {"yyy", 0.3}, {"zzz", 0.5}};
        //Object[][] array3 = {{"111", 0.1}, {"222", 0.4}, {"333", 0.1}};

        DynaArray dynaArray = new DynaArray();

        List<Content> listContent1 = new ArrayList<Content>();

        Content c1_1 = new Content();
        c1_1.setPlayerid("1");
        c1_1.setWeight(1);
        c1_1.setContent("aaa");

        Content c1_2 = new Content();
        c1_2.setPlayerid("1");
        c1_2.setWeight(4);
        c1_2.setContent("bbb");

        Content c1_3 = new Content();
        c1_3.setPlayerid("1");
        c1_3.setWeight(5);
        c1_3.setContent("ccc");

        listContent1.add(c1_1);
        listContent1.add(c1_2);
        listContent1.add(c1_3);

        dynaArray.addContributingArray(listContent1, "ID1", 3, 2);

        List<Content> listContent2 = new ArrayList<Content>();

        Content c2_1 = new Content();
        c2_1.setPlayerid("1");
        c2_1.setWeight(2);
        c2_1.setContent("xxx");

        Content c2_2 = new Content();
        c2_2.setPlayerid("1");
        c2_2.setWeight(3);
        c2_2.setContent("yyy");

        Content c2_3 = new Content();
        c2_3.setPlayerid("1");
        c2_3.setWeight(5);
        c2_3.setContent("zzz");

        listContent2.add(c2_1);
        listContent2.add(c2_2);
        listContent2.add(c2_3);

        dynaArray.addContributingArray(listContent2, "ID2", 5, 1);

        //dynaArray.addContributingArray(array2, 5, "ID2", 0.6, 0.05);
        String[] foo;
        foo = dynaArray.currentStringArray();

        String o1 = dynaArray.getRandomElement();
        System.out.print("1: " + o1 + "\n");

        String o2 = dynaArray.getRandomElement();
        System.out.print("2: " + o2 + "\n");

        String o3 = dynaArray.getRandomElement();
        System.out.print("3: " + o3 + "\n");

//        dynaArray.addContributingArray(array3, 6, "ID3", 0.7, 0.1);

        String o4 = dynaArray.getRandomElement();
        System.out.print("4: " + o4 + "\n");

        String o5 = dynaArray.getRandomElement();
        System.out.print("5: " + o5 + "\n");

        String o6 = dynaArray.getRandomElement();
        System.out.print("6: " + o6 + "\n");

        String o7 = dynaArray.getRandomElement();
        System.out.print("7: " + o7 + "\n");

        String o8 = dynaArray.getRandomElement();
        System.out.print("8: " + o8 + "\n");

        String o9 = dynaArray.getRandomElement();
        System.out.print("9: " + o9 + "\n");

        dynaArray.removeContributingArray("ID2");

        foo = dynaArray.currentStringArray();

        dynaArray.removeArrayItem("111");

        dynaArray.removeContributingArrayStartWith("ID");
    }
}
