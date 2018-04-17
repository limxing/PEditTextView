package me.leefeng.viewlibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by limxing on 2018/4/16.
 * PEditTextView 数字输入框，密码输入框
 */

public class PEditTextView extends ViewGroup {

    private boolean isPassword;
    private int textColor;
    private int inputColor;
    private EditText editText;
    private int boxCount = 6;
    private List<Box> list = new ArrayList<Box>();
    private float density;
    private Paint _paint;
    private RectF rectF = new RectF();
    private InputMethodManager imm;
    private float boxWandH;
    private Rect textRect = new Rect();
    private int inputingColor;
    private float boxMarginWidth;
    private PEditTextFinishListener listener;

    public PEditTextView(Context context) {
        super(context);
        init(context);
    }

    public void showKeyBoard(){
        editText.setFocusable(true);
        editText.requestFocus();
        imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
    }

    public void setListener(PEditTextFinishListener listener) {
        this.listener = listener;
    }

    private void init(Context context) {
        _paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        editText = new EditText(context);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(boxCount)});
        addView(editText);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                char[] text = s.toString().toCharArray();

                for (int i = 0; i < boxCount; i++) {
                    if (text.length <= i) {
                        list.get(i).text = "";
                    } else {
                        list.get(i).text = String.valueOf(text[i]);
                    }
                    invalidate();
                }
                if (text.length == boxCount && listener != null) {
                    listener.callBack(s.toString());

                }
            }
        });
        density = context.getResources().getDisplayMetrics().density;
        for (int i = 0; i < boxCount; i++) {
            Box box = new Box();
            list.add(box);
        }
        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                showKeyBoard();
                break;

        }
        return true;
    }

    public PEditTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PEditTextView);
        boxCount = typedArray.getInt(R.styleable.PEditTextView_length, 4);
        inputingColor = typedArray.getColor(R.styleable.PEditTextView_focus_color, Color.RED);
        inputColor = typedArray.getColor(R.styleable.PEditTextView_normal_color, Color.BLACK);
        textColor = typedArray.getColor(R.styleable.PEditTextView_text_color, Color.BLACK);
        boxMarginWidth = typedArray.getDimension(R.styleable.PEditTextView_box_padding, 5 * density);
        isPassword = typedArray.getBoolean(R.styleable.PEditTextView_is_password, false);
        typedArray.recycle();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = getWidth() - getPaddingLeft() - getPaddingRight();
        int height = getHeight() - getPaddingTop() - getPaddingBottom();
        float heightBoxWidth = height * boxCount + (boxCount - 1) * boxMarginWidth;
        float left = getPaddingLeft();
        float top = getPaddingTop();
        float bottom = 0;
        float right = 0;
        boxWandH = (width - boxMarginWidth * (boxCount - 1)) / boxCount;
        if (width > heightBoxWidth) {
            left += (width - heightBoxWidth) / 2;
            boxWandH -= 2 * left / 5;
        } else {
            top += (height - boxWandH) / 2;
        }
        bottom = top + boxWandH;
        for (int i = 0; i < boxCount; i++) {
            float cl = left + i * (boxWandH + boxMarginWidth);
            right = cl + boxWandH;
            list.get(i).left = cl;
            list.get(i).top = top;
            list.get(i).right = right;
            list.get(i).bottom = bottom;
        }
        editText.layout(0, 0, 1, 0);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        _paint.setStrokeWidth(3);
        _paint.setTextSize(boxWandH / 4 * 3);
        int length = editText.getText().toString().length();
        for (int i = 0; i < boxCount; i++) {
            Box box = list.get(i);
            rectF.left = box.left;
            rectF.top = box.top;
            rectF.right = box.right;
            rectF.bottom = box.bottom;
            _paint.setColor(inputColor);
            _paint.setStyle(Paint.Style.STROKE);
            if (length == i) {
                _paint.setColor(inputingColor);
                _paint.setShadowLayer(density / 2, 0, 0, Color.BLUE);
            } else {
                _paint.clearShadowLayer();
            }
            canvas.drawRoundRect(rectF, boxWandH / 10, boxWandH / 10, _paint);
//            String text = box.text;
            _paint.setStyle(Paint.Style.FILL);
            _paint.setColor(textColor);
            if (isPassword && !box.text.isEmpty()) {
                canvas.drawCircle(rectF.centerX(), rectF.centerY(), boxWandH / 5, _paint);
            } else {
                _paint.clearShadowLayer();
                _paint.getTextBounds(box.text, 0, box.text.length(), textRect);
                float x = rectF.centerX() - textRect.width() / 2 - density;
                if (box.text.equals("1")) {
                    x -= density * 3;
                }
                canvas.drawText(box.text, x,
                        box.bottom - (boxWandH - textRect.height()) / 2 - density, _paint);
            }
        }
    }

    private class Box {
        float left = 0;
        float top = 0;
        float right = 0;
        float bottom = 0;
        String text = "";
    }


    @Override
    protected void onDetachedFromWindow() {
        editText = null;
        _paint = null;
        list.clear();
        list = null;
        rectF = null;
        imm = null;
        textRect = null;
        super.onDetachedFromWindow();
    }

    public interface PEditTextFinishListener {
        void callBack(String result);
    }
}
