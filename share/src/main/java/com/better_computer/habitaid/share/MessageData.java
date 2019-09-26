package com.better_computer.habitaid.share;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;

import java.io.Serializable;

public class MessageData implements Serializable {

    private String text1;
    private String text2;

    public String getText1() {
        return text1;
    }

    public void setText1(String text1) {
        this.text1 = text1;
    }

    public String getText2() {
        return text2;
    }

    public void setText2(String text2) {
        this.text2 = text2;
    }

    public String toJsonString() {
        try {
            String jsonString = new JSONStringer().object()
                    .key("text1").value(text1)
                    .key("text2").value(text2)
                    .endObject().toString();
            return jsonString;
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static MessageData toMessageData(String jsonString) {
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) new JSONTokener(jsonString).nextValue();
            MessageData messageData = new MessageData();
            messageData.setText1(jsonObject.getString("text1"));
            messageData.setText2(jsonObject.getString("text2"));

            return messageData;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
