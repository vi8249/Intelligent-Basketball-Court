package com.example.vi8249.intelligentbasketballcourt;

/**
 * Created by vi8249 on 2017/6/4.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;

/**
 * View that can be displayed when selecting values in the chart. Extend this class to provide custom layouts for your
 * markers.
 *
 * @author Philipp Jahoda
 */
public class MyMarkerView2 extends MarkerView {
    private TextView tvContent, tvDate;
    private ArrayList<String> XAxisLabel;
    private MPPointF mOffset;

    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    public MyMarkerView2(Context context, int layoutResource, ArrayList<String> list) {
        super(context, layoutResource);
        XAxisLabel = list;
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvContent = (TextView) findViewById(R.id.tvContent);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        //Log.d("marker", "" + e.getX() + " " + e.getY());

        tvDate.setText("" + XAxisLabel.get((int) e.getX()));

        if (highlight.getDataSetIndex() == 1)
            tvContent.setText("" + e.getY() + " %");
        else
            tvContent.setText("" + e.getY() + " °C");

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        if (mOffset == null) {
            // center the marker horizontally and vertically
            mOffset = new MPPointF(-getWidth() / 2 + 225, -getHeight() - 30);
        }

        return mOffset;
    }

    @Override
    public void draw(Canvas canvas, float posX, float posY) {
        MPPointF offset = getOffset();
        Log.d("offset", Float.toString(getOffset().getX()));
        Log.d("offset", "draw: " + getWidth());

        if (posX - offset.x > getWidth())
            posX -= 450f;

        int saveId = canvas.save();
        // translate to the correct position and draw
        canvas.translate(posX + offset.x, posY + offset.y);
        draw(canvas);
        canvas.restoreToCount(saveId);
    }
}
