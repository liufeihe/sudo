package com.example.feihe.sudo;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Toast;

public class game extends Activity {
    private static final String PREF_PUZZLE="puzzle";
    private static final String PREF_PUZZLE_FLAG="puzzleFlag";
    public static final String KEY_DIFFICULTY="difficulty";
    public static final int DIFFICULTY_CONTINUE=-1;
    public static final int DIFFICULTY_EASY=0;
    public static final int DIFFICULTY_MEDIUM=1;
    public static final int DIFFICULTY_HARD=2;

    private int[] puzzle=new int[9*9];
    private int[] puzzleFlag=new int[9*9];
    private PuzzleView puzzleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取难度,默认为简单
        int diff=getIntent().getIntExtra(KEY_DIFFICULTY, DIFFICULTY_EASY);
        int[] puz=getPuzzle(diff);
        for(int i=0;i<81;i++)
            puzzle[i]=puz[i];
        for(int i=81;i<81*2;i++)
            puzzleFlag[i-81]=puz[i];

        calculateUsedTiles();

        puzzleView=new PuzzleView(this);
        setContentView(puzzleView);
        puzzleView.requestFocus();
        getActionBar().setDisplayHomeAsUpEnabled(true);
        //如果活动重启，则从continue
        getIntent().putExtra(KEY_DIFFICULTY, DIFFICULTY_CONTINUE);
    }
    @Override
    protected void onPause(){
        super.onPause();
        //保存现在的状态
        getPreferences(MODE_PRIVATE).edit().putString(PREF_PUZZLE, toPuzzleString(puzzle)).commit();
        getPreferences(MODE_PRIVATE).edit().putString(PREF_PUZZLE_FLAG, toPuzzleString(puzzleFlag)).commit();
    }

    //设置三个初始字谜组
    private final String easyPuzzle=
            "360000000"+ "004230800"+"000004200"
            +"070460003"+"820000014"+"500013020"
            +"001900000"+"007048300"+"000000045";
    private final String mediumPuzzle=
            "650000070"+"000506000"+"014000005"
            +"007009000"+"002314700"+"000700800"
            +"500000630"+"000201000"+"030000097";
    private final String hardPuzzle=
            "009000000"+"080605020"+"501078000"
            +"000000700"+"706040102"+"004000000"
            +"000720903"+"090301080"+"000000600";
    //获得字谜数组
    private int[] getPuzzle(int diff){
        String puz;
        String puzFlag;
        switch (diff) {
            case DIFFICULTY_CONTINUE:
                puz=getPreferences(MODE_PRIVATE).getString(PREF_PUZZLE, easyPuzzle);
                puzFlag=getPreferences(MODE_PRIVATE).getString(PREF_PUZZLE_FLAG, easyPuzzle);
                break;
            case DIFFICULTY_HARD:
                puz=hardPuzzle;
                puzFlag=puz;
                break;
            case DIFFICULTY_MEDIUM:
                puz=mediumPuzzle;
                puzFlag=puz;
                break;
            case DIFFICULTY_EASY:
            default:
                puz=easyPuzzle;
                puzFlag=puz;
                break;
        }
        return fromPuzzleString(puz+puzFlag);
    }
    //把一个数组转化为字符
    static private String toPuzzleString(int[] puz){
        StringBuilder buf=new StringBuilder();
        for(int element:puz){
            buf.append(element);
        }
        return buf.toString();
    }
    //把字符转化为数组
    static protected int[] fromPuzzleString(String str){
        int[] puz=new int[str.length()];
        for(int i=0;i<puz.length;i++){
            puz[i]=str.charAt(i)-'0';
        }
        return puz;
    }
    //把数字设置进数组
    protected boolean setTileIfValid(int x,int y,int value){
        /*int[] tiles=getUsedTiles(x,y);
        if(value!=0){
            for(int tile:tiles){
                if(tile==value)
                    return false;
            }
        }*/
        setTile(x, y, value);
        calculateUsedTiles();
        return true;
    }

    //存储每格中已经使用过的数字
    private final int[][][] used=new int[9][9][];
    //获取某个小格的已经使用的数组
    protected int[] getUsedTiles(int x,int y){
        return used[x][y];
    }
    //计算所有小格中哪些数字已经被使用
    private void calculateUsedTiles(){
        for(int i=0;i<9;i++)
            for(int j=0;j<9;j++){
                used[i][j]=calculateUsedTiles(i,j);
            }
    }
    //计算某个小格中哪些数字已经被使用
    private int[] calculateUsedTiles(int x, int y){
        int[] c=new int[9];
        //查看本行上哪些数字已经被使用
        for(int i=0;i<9;i++){
            int t=getTile(x,i);
            if(t!=0)
                c[t-1]=t;
        }
        //查看本列上哪些数字已经被使用
        for(int i=0;i<9;i++){
            int t=getTile(i,y);
            if(t!=0)
                c[t-1]=t;
        }
        //查看本宫里哪些数字已经被使用
        int startx=(x/3)*3;
        int starty=(y/3)*3;
        for(int i=startx;i<startx+3;i++)
            for(int j=starty;j<starty+3;j++){
                int t=getTile(i,j);
                if(t!=0)
                    c[t-1]=t;
            }
        //压缩
        int total=0;
        for(int element:c){
            if(element!=0)
                total++;
        }
        int[] c1=new int[total];
        total=0;
        for(int element:c){
            if(element!=0)
                c1[total++]=element;
        }
        return c1;
    }

    //获取小格里的数字
    private int getTile(int x,int y){
        return puzzle[y*9+x];
    }
    //设置小格里的数字
    private void setTile(int x,int y, int value){
        puzzle[y*9+x]=value;
    }
    //获取小格数字的字符形式
    protected String getTileString(int x,int y){
        int v = getTile(x, y);
        if(v==0)
            return "";
        else
            return String.valueOf(v);
    }

    //显示软键盘
    protected void showKeypadOrError(int x,int y){
        int[] tiles=getUsedTiles(x, y);
        if(tiles.length==9){
            if(isGameOver()){
                Toast toast=Toast.makeText(this, R.string.win_label,Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
                return;
            }
            Toast toast=Toast.makeText(this, R.string.no_move_label,Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }

        Dialog v=new Keypad(this,tiles,puzzleView);
        v.setTitle("数字键盘");
        v.show();
    }

    //数字是否是原来就有的
    protected boolean isNewTile(int x,int y){
        if(puzzleFlag[x+y*9]==0)
            return true;
        return false;
    }

    //判断是否游戏结束
    protected boolean isGameOver(){
        for(int x=0;x<9;x++)
            for(int y=0;y<9;y++){
                if(used[x][y].length!=9)
                    return false;
            }
        return true;
    }
}
