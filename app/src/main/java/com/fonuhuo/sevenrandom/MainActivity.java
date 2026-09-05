package com.fonuhuo.sevenrandom;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

public final class MainActivity extends Activity {
    private final TextView[] numberViews = new TextView[3];
    private final TextView[] quotientViews = new TextView[3];
    private final TextView[] remainderViews = new TextView[3];
    private RandomEngine randomEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        randomEngine = new RandomEngine();

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

        LinearLayout numbers = valueRow(numberViews, 48, Typeface.BOLD);
        LinearLayout.LayoutParams numberParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        numberParams.setMargins(0, dp(10), 0, dp(34));
        root.addView(numbers, numberParams);

        TextView quotientLabel = label("超过 6：÷6 的商", 15, Typeface.NORMAL);
        root.addView(quotientLabel);
        LinearLayout quotients = valueRow(quotientViews, 31, Typeface.NORMAL);
        LinearLayout.LayoutParams quotientParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        quotientParams.setMargins(0, dp(8), 0, dp(30));
        root.addView(quotients, quotientParams);

        TextView remainderLabel = label("余数（0 记 6）", 15, Typeface.NORMAL);
        root.addView(remainderLabel);
        LinearLayout remainders = valueRow(remainderViews, 35, Typeface.BOLD);
        LinearLayout.LayoutParams remainderParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        remainderParams.setMargins(0, dp(8), 0, 0);
        root.addView(remainders, remainderParams);

        Space bottomSpace = new Space(this);
        root.addView(bottomSpace, new LinearLayout.LayoutParams(1, 0, 1f));

        TextView hint = label("点任意位置 · 再随机一次", 14, Typeface.NORMAL);
        hint.setTextColor(Color.rgb(115, 115, 115));
        root.addView(hint);

        root.setOnClickListener(v -> roll());
        setContentView(root);
        roll();
    }

    private void roll() {
        int[] values = randomEngine.generateThree();
        for (int i = 0; i < values.length; i++) {
            int value = values[i];
            numberViews[i].setText(Integer.toString(value));

            RandomEngine.Analysis analysis = RandomEngine.analyze(value);
            if (analysis.hasDivision()) {
                quotientViews[i].setText(Integer.toString(analysis.getQuotient()));
                remainderViews[i].setText(Integer.toString(analysis.getRemainder()));
            } else {
                quotientViews[i].setText("-");
                remainderViews[i].setText("-");
            }
        }
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
