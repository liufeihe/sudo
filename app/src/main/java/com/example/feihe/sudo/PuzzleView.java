package com.example.feihe.sudo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class PuzzleView extends View {

    private static final String tag ="sudo";
    private static final String SELFX="selfx";
    private static final String SELFY="selfy";
    private static final String VIEW_STATE="viewState";
    private static final int ID=82;

    private final game gam;
    public PuzzleView(Context context){
        super(context);
        this.gam=(game)context;
        setFocusable(true);
        setFocusableInTouchMode(true);
        //自定义的view，所以手动给指定个id
        setId(ID);
    }
    /*标准的视图会自动保存其状态，但puzzleview是我们自己定义的。
    * 在程序运行时如果改变了屏幕的方向，程序会忘记光标的位置。
    * 为了避免这个问题，我们专门处理下*/
    @Override
    protected Parcelable onSaveInstanceState(){
        Parcelable p=super.onSaveInstanceState();
        Bundle bundle=new Bundle();
        bundle.putInt(SELFX, selfx);
        bundle.putInt(SELFY, selfy);
        bundle.putParcelable(VIEW_STATE, p);
        return bundle;
    }
    @Override
    protected void onRestoreInstanceState(Parcelable state){
        Bundle bundle=(Bundle)state;
        select(bundle.getInt(SELFX),bundle.getInt(SELFY));
        super.onRestoreInstanceState(bundle.getParcelable(VIEW_STATE));
        return;
    }

    private float width;//每小格的宽度
    private float height;
    private int selfx;
    private int selfy;
    private final Rect selfRect=new Rect();
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        width=w/9f;
        height=h/9f;
        getRect(selfx, selfy, selfRect);
        super.onSizeChanged(w,h, oldw, oldh);
    }
    @Override
    protected void onDraw(Canvas canvas){
        width=getWidth()/9f;
        height=getHeight()/9f;

        //画背景
        Paint background=new Paint();
        background.setColor(getResources().getColor(R.color.puzzle_background));
        Rect rect=new Rect();
        rect.left=0;
        rect.top=0;
        rect.right=getWidth();
        rect.bottom=getHeight();
        canvas.drawRect(rect, background);

        //画面板
        Paint dark=new Paint();
        dark.setStrokeWidth(5);
        dark.setColor(getResources().getColor(R.color.puzzle_dark));
        Paint light=new Paint();
        light.setStrokeWidth(5);
        light.setColor(getResources().getColor(R.color.puzzle_light));
        for(int i=0;i<=9;i++){
            //每个小格之间的用浅颜色的线隔开
            canvas.drawLine(0,i*height,getWidth(),i*height,light);
            canvas.drawLine(i*width,0,i*width,getHeight(),light);
        }
       for(int i=0;i<=9;i++){
            if(i%3!=0)
                continue;
            //每个宫之间用深颜色的线隔开
            canvas.drawLine(0,i*height,getWidth(),i*height,dark);
            canvas.drawLine(i*width,0,i*width,getHeight(),dark);
        }

        //画数字
        Paint foreground=new Paint();
        foreground.setTextSize(height * 0.75f);
        foreground.setTextScaleX(width / height);
        foreground.setTextAlign(Paint.Align.CENTER);
        Paint.FontMetrics fm=foreground.getFontMetrics();
        float x=width/2;
        float y=height/2-(fm.ascent+fm.descent)/2;
        for(int i=0;i<9;i++)
        for(int j=0;j<9;j++){
            foreground.setColor(getResources().getColor(R.color.puzzle_foreground_old));
            if(this.gam.isNewTile(i,j))
                foreground.setColor(getResources().getColor(R.color.puzzle_foreground_new));

            canvas.drawText(this.gam.getTileString(i,j),x+i*width,y+j*height,foreground);
        }

        //画选中的矩形
        Paint selected=new Paint();
        selected.setColor(getResources().getColor(R.color.puzzle_selected));
        canvas.drawRect(selfRect,selected);

    }

    //选择的数组位置的矩形区域
    private void select(int x,int y){
        invalidate(selfRect);//将这块矩形区域标记为过期，窗口管理器后在之后调用ondraw重画这块区域
        selfx=Math.min(Math.max(x,0),8);
        selfy=Math.min(Math.max(y,0),8);
        getRect(selfx,selfy,selfRect);
        invalidate(selfRect);
    }
    //获取数组位置在画布的对应矩形区域
    private void getRect(int x,int y,Rect rect){
        rect.set((int) (x * width), (int) (y * height), (int) (x * width + width), (int) (y * height + height));
    }

    //点击屏幕时弹出软键盘
    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(event.getAction()!=MotionEvent.ACTION_DOWN)
            return super.onTouchEvent(event);
        select((int)(event.getX()/width),(int)(event.getY()/height));
        //如果是原来的数字则不显示软键盘
        if(gam.isNewTile(selfx, selfy))
            gam.showKeypadOrError(selfx, selfy);
        return true;
    }

    //把从软键盘中产生的数字输入
    public void setSelectedTile(int tile){
        if(gam.setTileIfValid(selfx,selfy,tile))
            invalidate();
        else
            Log.d(tag,"setSelectdTile failed, tile:"+tile+"x:"+selfx+"y:"+selfy);
    }
}
