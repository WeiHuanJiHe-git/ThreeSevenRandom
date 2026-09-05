package com.fonuhuo.sevenrandom;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

public final class MainActivity extends Activity {
    private static final long ROLL_INTERVAL_MS = 60L;

    private final TextView[] numberViews = new TextView[3];
    private final TextView[] palaceViews = new TextView[3];
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private final Runnable rollingTick = new Runnable() {
        @Override
        public void run() {
            if (!resumed || rollingSession == null || !rollingSession.isRolling()) {
                return;
            }
            renderPreview(rollingSession.previewValues());
            mainHandler.postDelayed(this, ROLL_INTERVAL_MS);
        }
    };

    private TextView finalPalaceView;
    private TextView hintView;
    private RandomEngine.RollingSession rollingSession;
    private boolean resumed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RandomEngine randomEngine = new RandomEngine();
        rollingSession = new RandomEngine.RollingSession(randomEngine);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(dp(22), dp(28), dp(22), dp(22));
        root.setBackgroundColor(Color.rgb(248, 248, 246));
        root.setClickable(true);
        root.setFocusable(true);

        Space topSpace = new Space(this);
        root.addView(topSpace, new LinearLayout.LayoutParams(1, 0, 1f));

        TextView title = label("三个随机数", 16, Typeface.BOLD);
        title.setTextColor(Color.rgb(85, 85, 85));
        root.addView(title);

        LinearLayout numbers = valueRow(numberViews, 46, Typeface.BOLD);
        LinearLayout.LayoutParams numberParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        numberParams.setMargins(0, dp(10), 0, dp(34));
        root.addView(numbers, numberParams);

        TextView palaceLabel = label("连续起宫", 15, Typeface.NORMAL);
        root.addView(palaceLabel);

        LinearLayout palaces = valueRow(palaceViews, 27, Typeface.BOLD);
        LinearLayout.LayoutParams palaceParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        palaceParams.setMargins(0, dp(10), 0, dp(9));
        root.addView(palaces, palaceParams);

        TextView rule = label("大安起第一数 · 上一宫起下一数 · 本宫算 1", 13, Typeface.NORMAL);
        rule.setTextColor(Color.rgb(115, 115, 115));
        LinearLayout.LayoutParams ruleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        ruleParams.setMargins(0, 0, 0, dp(34));
        root.addView(rule, ruleParams);

        TextView finalLabel = label("最终落宫", 15, Typeface.NORMAL);
        root.addView(finalLabel);

        finalPalaceView = label("-", 48, Typeface.BOLD);
        finalPalaceView.setTextColor(Color.rgb(20, 20, 20));
        LinearLayout.LayoutParams finalParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        finalParams.setMargins(0, dp(8), 0, 0);
        root.addView(finalPalaceView, finalParams);

        Space bottomSpace = new Space(this);
        root.addView(bottomSpace, new LinearLayout.LayoutParams(1, 0, 1f));

        hintView = label("数字滚动中 · 单击停止", 14, Typeface.NORMAL);
        hintView.setTextColor(Color.rgb(115, 115, 115));
        root.addView(hintView);

        root.setOnClickListener(v -> { });
        root.setOnTouchListener((v, event) -> {
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                if (rollingSession.isRolling()) {
                    stopAtTouch(event);
                } else {
                    restartRolling();
                }
                return true;
            }
            if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                v.performClick();
                return true;
            }
            return true;
        });

        setContentView(root);
        clearFinalResult();
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumed = true;
        if (rollingSession != null && rollingSession.isRolling()) {
            mainHandler.post(rollingTick);
        }
    }

    @Override
    protected void onPause() {
        resumed = false;
        mainHandler.removeCallbacks(rollingTick);
        super.onPause();
    }

    private void stopAtTouch(MotionEvent event) {
        mainHandler.removeCallbacks(rollingTick);

        RandomEngine.RollResult result = rollingSession.stop(
                System.nanoTime(),
                event.getEventTime(),
                event.getX(),
                event.getY());

        int[] values = result.values();
        int[] palaces = result.palaces();
        for (int i = 0; i < values.length; i++) {
            numberViews[i].setText(Integer.toString(values[i]));
            palaceViews[i].setText(RandomEngine.palaceName(palaces[i]));
        }
        finalPalaceView.setText(RandomEngine.palaceName(palaces[2]));
        hintView.setText("已定 · 单击重新开始");
    }

    private void restartRolling() {
        rollingSession.restart();
        clearFinalResult();
        hintView.setText("数字滚动中 · 单击停止");
        if (resumed) {
            mainHandler.post(rollingTick);
        }
    }

    private void renderPreview(int[] values) {
        for (int i = 0; i < values.length; i++) {
            numberViews[i].setText(Integer.toString(values[i]));
        }
    }

    private void clearFinalResult() {
        for (TextView palaceView : palaceViews) {
            palaceView.setText("-");
        }
        finalPalaceView.setText("-");
    }

    private LinearLayout valueRow(TextView[] destination, int textSizeSp, int style) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER);
        for (int i = 0; i < destination.length; i++) {
            TextView value = label("-", textSizeSp, style);
            value.setTextColor(Color.rgb(20, 20, 20));
            destination[i] = value;
            row.addView(value, new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        }
        return row;
    }

    private TextView label(String text, int textSizeSp, int style) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextSize(textSizeSp);
        view.setGravity(Gravity.CENTER);
        view.setTypeface(Typeface.create(Typeface.DEFAULT, style));
        view.setTextColor(Color.rgb(80, 80, 80));
        view.setIncludeFontPadding(false);
        return view;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
