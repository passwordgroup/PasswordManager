package sunny.com.tools;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.ProgressBar;

/**
 * Created by sunnys on 2015/11/15.
 */
public class Progress extends ProgressBar{
    public Progress(Context context) {
        super(context);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
