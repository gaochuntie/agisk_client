package atms.app.agiskclient.UIcomponents;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import atms.app.agiskclient.Tools.DebugUtils;

public class LineSegmentRatioProgressBar extends ProgressBar {

    private final Paint paint = new Paint();
    private final List<Range> ranges = new ArrayList<>();

    public LineSegmentRatioProgressBar(Context context) {
        super(context);
        init();
    }

    public LineSegmentRatioProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LineSegmentRatioProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setWillNotDraw(false);
    }

    public void addRange(float startPercent, float endPercent, int color) {
        DebugUtils.functionHit("addRange:"+startPercent+" "+endPercent+" "+color+" ");
        ranges.add(new Range(startPercent, endPercent, color));
    }
    public void clearRanges() {
        ranges.clear();
        invalidate();
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        Rect rect = new Rect(0, 0, width, height);

        for (Range range : ranges) {
            int start = (int) (width * range.getStartPercent());
            int end = (int) (width * range.getEndPercent());

            rect.left = start;
            rect.right = end;

            paint.setColor(range.getColor());
            canvas.drawRect(rect, paint);
        }
    }

    private static class Range {
        private final float startPercent;
        private final float endPercent;
        private final int color;

        public Range(float startPercent, float endPercent, int color) {
            this.startPercent = startPercent;
            this.endPercent = endPercent;
            this.color = color;
        }

        public float getStartPercent() {
            return startPercent;
        }

        public float getEndPercent() {
            return endPercent;
        }

        public int getColor() {
            return color;
        }
    }
}
