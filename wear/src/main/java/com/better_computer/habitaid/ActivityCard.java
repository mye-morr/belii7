package com.better_computer.habitaid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.CardScrollView;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.TextView;

import com.better_computer.habitaid.share.MessageData;

public class ActivityCard extends WearableActivity {

    private TextView text1View;
    private TextView text2View;
    private long[] vibrationPattern;

    public static final void startActivity(Context context, MessageData messageData) {
        Intent intent = new Intent(context, ActivityCard.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("MessageData", messageData);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        CardScrollView cardScrollView = (CardScrollView)
                findViewById(R.id.card_scroll_view);
        cardScrollView.setCardGravity(Gravity.BOTTOM);

        text1View = (TextView) findViewById(R.id.text1);
        text2View = (TextView) findViewById(R.id.text2);

        Intent intent = getIntent();
        MessageData messageData = (MessageData) intent.getSerializableExtra("MessageData");

        String sText1 = messageData.getText1();
        String sText2 = messageData.getText2();

        text1View.setText(sText1);
        text2View.setText(sText2);

        vibrationPattern = new long[]{0, 4500};
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        //-1 - don't repeat
        final int indexInPatternToRepeat = -1;
        vibrator.vibrate(vibrationPattern, indexInPatternToRepeat);
    }
}
