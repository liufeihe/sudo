package com.example.feihe.sudo;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

public class Keypad extends Dialog {

    private final View[] keys=new View[10];
    //private View keypad;

    private final int[] useds;
    private final PuzzleView puzzleView;

    public Keypad(Context context, int[] useds, PuzzleView puzzleView){
        super(context);
        this.useds=useds;
        this.puzzleView=puzzleView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keypad);

        findViews();
        for(int element:useds){
            //软键盘上已经使用过的数字标红
            if(element!=0)
                keys[element].setBackgroundColor(0x64ff0000);
                //keys[element].setVisibility(View.INVISIBLE);
        }
        setListeners();
    }

    //获取软键盘和各个按钮的视图
    private void findViews(){
        //keypad=findViewById(R.id.keypad);
        keys[0]=findViewById(R.id.keypad_0);
        keys[1]=findViewById(R.id.keypad_1);
        keys[2]=findViewById(R.id.keypad_2);
        keys[3]=findViewById(R.id.keypad_3);
        keys[4]=findViewById(R.id.keypad_4);
        keys[5]=findViewById(R.id.keypad_5);
        keys[6]=findViewById(R.id.keypad_6);
        keys[7]=findViewById(R.id.keypad_7);
        keys[8]=findViewById(R.id.keypad_8);
        keys[9]=findViewById(R.id.keypad_9);
    }
    //设置监听器
    private void setListeners(){
        for(int i=0;i<keys.length;i++){
            final int t=i;
            keys[i].setOnClickListener(new View.OnClickListener(){public void onClick(View v){returnResult(t);}
                    });
        }
        /*keypad.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                returnResult(0);
            }
        });*/
    }
    //将点击产生的数字输入数组
    private void returnResult(int tile){
        puzzleView.setSelectedTile(tile);
        dismiss();
    }

}
