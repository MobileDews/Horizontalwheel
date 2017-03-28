package com.techdew.horizontalwheeltext;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.techdew.lib.HorizontalWheel.AbstractWheel;
import com.techdew.lib.HorizontalWheel.ArrayWheelAdapter;
import com.techdew.lib.HorizontalWheel.OnWheelScrollListener;

public class MainActivity extends AppCompatActivity implements OnWheelScrollListener {

    String[] values;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AbstractWheel abstractWheel = (AbstractWheel) findViewById(R.id.HorizontalView);
        values = new String[100];
        for (int i = 0; i < 100; i++) {
            values[i] = String.valueOf(25 * (i));
        }

        ArrayWheelAdapter<String> ampmAdapter = new ArrayWheelAdapter<String>(MainActivity.this, values);
        ampmAdapter.setItemResource(R.layout.horizontal_wheel_text_centered);
        ampmAdapter.setItemTextResource(R.id.text);
        abstractWheel.setViewAdapter(ampmAdapter);
        abstractWheel.addScrollingListener(this);
    }

    @Override
    public void onScrollingStarted(AbstractWheel wheel) {

    }

    @Override
    public void onScrollingFinished(AbstractWheel wheel) {

        Toast.makeText(getApplicationContext(),""+values[wheel.getCurrentItem()],Toast.LENGTH_LONG).show();
    }
}
