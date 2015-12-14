package com.example.feihe.sudo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        View newButton=this.findViewById(R.id.new_button);
        newButton.setOnClickListener(this);
        View continueButton=this.findViewById(R.id.continue_button);
        continueButton.setOnClickListener(this);
        View aboutButton=this.findViewById(R.id.about_button);
        aboutButton.setOnClickListener(this);
        View exitButton=this.findViewById(R.id.exit_button);
        exitButton.setOnClickListener(this);
    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.new_button:
                //开始游戏
                openNewGameDiag();
                break;
            case R.id.continue_button:
                startGame(game.DIFFICULTY_CONTINUE);
                break;
            case R.id.about_button:
                //弹出关于窗口
                Intent intent=new Intent(this, about.class);
                startActivity(intent);
                break;
            case R.id.exit_button:
                //退出游戏
                finish();
                break;
            default:
                break;
        }
    }

    //询问难度，并开始新游戏
    private void openNewGameDiag(){
        //Builder builder= new AlertDialog.Builder(this);
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_difficulty)
                .setItems(R.array.difficulty, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startGame(which);
                    }
                }).show();
    }

    //游戏开始
    private void startGame(int difficulty){
        Intent intent=new Intent(MainActivity.this, game.class);
        intent.putExtra(game.KEY_DIFFICULTY, difficulty);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, settings.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
