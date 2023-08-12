package atms.app.agiskclient.GPTfdisk;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class DiskUsageView extends View {
    private long totalBytes;
    private long outerStart;
    private long outerEnd;
    private long innerStart;
    private long innerEnd;

    public DiskUsageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setData(long totalBytes, long outerStart, long outerEnd, long innerStart, long innerEnd) {
        this.totalBytes = totalBytes;
        this.outerStart = outerStart;
        this.outerEnd = outerEnd;
        this.innerStart = innerStart;
        this.innerEnd = innerEnd;
        invalidate(); // Trigger a redraw
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (totalBytes <= 0) {
            return;
        }

        float widthFactor = getWidth() / (float) totalBytes;
        float outerLeft = outerStart * widthFactor;
        float outerRight = outerEnd * widthFactor;
        float innerLeft = innerStart * widthFactor;
        float innerRight = innerEnd * widthFactor;

        // Draw outer chunk (container)
        Paint outerChunkPaint = new Paint();
        outerChunkPaint.setColor(Color.GREEN);
        canvas.drawRect(outerLeft, 0, outerRight, getHeight(), outerChunkPaint);

        // Draw inner chunk (contained) within the outer chunk
        Paint innerChunkPaint = new Paint();
        innerChunkPaint.setColor(Color.BLUE);
        canvas.drawRect(innerLeft, 0, innerRight, getHeight(), innerChunkPaint);
    }
}
