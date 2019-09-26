package com.better_computer.habitaid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.wearable.activity.WearableActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.better_computer.habitaid.share.MessageData;
import com.better_computer.habitaid.share.WearMessage;

public class ActivityMessage extends WearableActivity {

    private TextView text1View;
    private TextView text2View;
    private String sText1 = "";
    private String sText2 = "";
    private String sKeyMsg1 = "";
    private String sFrqKeyMsg1 = "";
    private String sKeyMsg2 = "";
    private String sFrqKeyMsg2 = "";

    private SharedPreferences prefs;

    private Button btnCycl;
    private Button btnPost;
    private Button btnCncl;
    private Button btnRst;
    private Button btnAck1;
    private Button btnAck2;
    private long[] vibrationPattern;
    private GestureDetector mGestureDetector;

    final long FIVE_SECONDS = 1000*5;
    Handler handler = new Handler();

    final Runnable r = new Runnable() {
        public void run() {
            try {
                finishScreen();
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    };

    public static final void startActivity(Context context, MessageData messageData) {
        Intent intent = new Intent(context, ActivityMessage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("MessageData", messageData);
        context.startActivity(intent);
    }

    public static final void startActivity(Context context, MessageData messageData, long[] vibrationPattern) {
        Intent intent = new Intent(context, ActivityMessage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("MessageData", messageData);
        intent.putExtra("lxVibrationPattern", vibrationPattern);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sKeyMsg1 = prefs.getString("key_msg1", "key_msg_1");
        sFrqKeyMsg1 = prefs.getString("frq_key_msg1", "200;100");
        sKeyMsg2 = prefs.getString("key_msg2", "key_msg_2");
        sFrqKeyMsg2 = prefs.getString("frq_key_msg2", "200;100");

        text1View = (TextView) findViewById(R.id.text1);
        text2View = (TextView) findViewById(R.id.text2);
        btnCycl = (Button) findViewById(R.id.btnCycl);
        btnPost = (Button) findViewById(R.id.btnPost);
        btnCncl = (Button) findViewById(R.id.btnCncl);
        btnRst = (Button) findViewById(R.id.btnRst);
        btnAck1 = (Button) findViewById(R.id.btnAck1);
        btnAck2 = (Button) findViewById(R.id.btnAck2);

        Intent intent = getIntent();
        MessageData messageData = (MessageData) intent.getSerializableExtra("MessageData");

        if (messageData != null) {
            sText1 = messageData.getText1();
            sText2 = messageData.getText2();
        }

        if(sText1.equalsIgnoreCase(sKeyMsg1)) {
            text1View.setText(sText1);
            sText2 = sFrqKeyMsg1;
            text2View.setVisibility(View.GONE);
            btnCycl.setVisibility(View.VISIBLE);
            btnPost.setVisibility(View.GONE);
            btnCncl.setVisibility(View.GONE);
            btnRst.setVisibility(View.GONE);
            btnAck1.setVisibility(View.GONE);
            btnAck2.setVisibility(View.GONE);
        }
        else if (sText1.equalsIgnoreCase(sKeyMsg2)) {
            text1View.setText(sText1);
            sText2 = sFrqKeyMsg2;
            text2View.setVisibility(View.GONE);
            btnCycl.setVisibility(View.VISIBLE);
            btnPost.setVisibility(View.GONE);
            btnCncl.setVisibility(View.GONE);
            btnRst.setVisibility(View.GONE);
            btnAck1.setVisibility(View.GONE);
            btnAck2.setVisibility(View.GONE);
        }
        else {
            if(sText1.contains("!!!")) {
                text1View.setTextSize(30);
                text2View.setTextSize(30);

                text1View.setText(sText1);
                text2View.setText(sText2);
                btnPost.setVisibility(View.GONE);
                btnCncl.setVisibility(View.GONE);
                btnRst.setVisibility(View.GONE);
                btnCycl.setVisibility(View.GONE);
                btnAck1.setVisibility(View.GONE);
                btnAck2.setVisibility(View.GONE);
            }
            else if(sText2.equalsIgnoreCase("Toda")) {
                text1View.setText(sText1);
                text2View.setText("");

                btnPost.setVisibility(View.VISIBLE);
                btnCncl.setVisibility(View.VISIBLE);
                btnCycl.setVisibility(View.GONE);
                btnRst.setVisibility(View.GONE);
                btnAck1.setVisibility(View.GONE);
                btnAck2.setVisibility(View.GONE);
            }
            else if(sText2.equalsIgnoreCase("Prepare1")) {
                text1View.setText(sText1);
                text2View.setText("");

                btnAck1.setVisibility(View.VISIBLE);
                btnAck2.setVisibility(View.VISIBLE);
                btnPost.setVisibility(View.GONE);
                btnCncl.setVisibility(View.GONE);
                btnRst.setVisibility(View.GONE);
                btnCycl.setVisibility(View.GONE);
            }
            else if(sText2.equalsIgnoreCase("Prepare2")) {
                text1View.setText(sText1);
                text2View.setText("");

                btnAck1.setVisibility(View.GONE);
                btnAck2.setVisibility(View.VISIBLE);
                btnPost.setVisibility(View.GONE);
                btnCncl.setVisibility(View.GONE);
                btnRst.setVisibility(View.GONE);
                btnCycl.setVisibility(View.GONE);
            }
            else if(sText2.equalsIgnoreCase("Prepared")) {
                text1View.setText(sText1);
                text2View.setText("");

                btnPost.setVisibility(View.VISIBLE);
                btnCncl.setVisibility(View.VISIBLE);
                btnRst.setVisibility(View.VISIBLE);
                btnCycl.setVisibility(View.GONE);
                btnAck1.setVisibility(View.GONE);
                btnAck2.setVisibility(View.GONE);
            }
            else {
                text1View.setText(sText1);
                text2View.setText(sText2);

                btnCycl.setVisibility(View.GONE);
                btnPost.setVisibility(View.GONE);
                btnCncl.setVisibility(View.GONE);
                btnRst.setVisibility(View.GONE);
                btnAck1.setVisibility(View.GONE);
                btnAck2.setVisibility(View.GONE);
            }
        }

        if(intent.hasExtra("lxVibrationPattern")) {
            vibrationPattern = intent.getLongArrayExtra("lxVibrationPattern");
        }
        else {
            vibrationPattern = new long[]{0, 500, 50, 500, 50, 500};
        }

        mGestureDetector = new GestureDetector(ActivityMessage.this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                handler.removeCallbacks(r);
                handler.postDelayed(r, FIVE_SECONDS);

                return super.onSingleTapUp(e);
            }
        });

        btnCycl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageData messageData = new MessageData();
                messageData.setText1(sText1);
                messageData.setText2(sText2);

                WearMessage wearMessage = new WearMessage(getApplicationContext());
                wearMessage.sendData("/cycle", messageData.toJsonString());
            }
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageData messageData = new MessageData();
                messageData.setText1(sText1);
                messageData.setText2("");

                WearMessage wearMessage = new WearMessage(getApplicationContext());
                wearMessage.sendData("/postpone-sched", messageData.toJsonString());
            }
        });

        btnCncl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageData messageData = new MessageData();
                messageData.setText1(sText1);
                messageData.setText2("");

                WearMessage wearMessage = new WearMessage(getApplicationContext());
                wearMessage.sendData("/cancel-sched", messageData.toJsonString());
            }
        });

        btnRst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageData messageData = new MessageData();
                messageData.setText1(sText1);
                messageData.setText2("");

                WearMessage wearMessage = new WearMessage(getApplicationContext());
                wearMessage.sendData("/reset-sched", messageData.toJsonString());
            }
        });

        btnAck1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageData messageData = new MessageData();
                messageData.setText1(sText1);
                messageData.setText2("");

                WearMessage wearMessage = new WearMessage(getApplicationContext());
                wearMessage.sendData("/prepare-ack1", messageData.toJsonString());
            }
        });

        btnAck2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageData messageData = new MessageData();
                messageData.setText1(sText1);
                messageData.setText2("");

                WearMessage wearMessage = new WearMessage(getApplicationContext());
                wearMessage.sendData("/prepare-ack2", messageData.toJsonString());
            }
        });

        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        //-1 - don't repeat
        final int indexInPatternToRepeat = -1;
        vibrator.vibrate(vibrationPattern, indexInPatternToRepeat);

        handler.postDelayed(r, FIVE_SECONDS);

    }

    private void finishScreen() {
        finish();

        if(!isTaskRoot()) {
            overridePendingTransition(0, 0);
        }
    }

    @Override
    public boolean dispatchTouchEvent (MotionEvent e) {
        return mGestureDetector.onTouchEvent(e) || super.dispatchTouchEvent(e);
    }
}
