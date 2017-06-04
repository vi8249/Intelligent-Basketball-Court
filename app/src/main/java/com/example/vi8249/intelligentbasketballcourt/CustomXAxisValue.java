package com.example.vi8249.intelligentbasketballcourt;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;

/**
 * Created by vi8249 on 2017/6/3.
 */

public class CustomXAxisValue implements IAxisValueFormatter {
    private ArrayList<String> list;

    public CustomXAxisValue(ArrayList<String> list) {
        this.list = list;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return list.get((int)value);
    }
}
