package com.example.game1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    TextView scoreView;
    ScrollView scrollView;
    LinearLayout showView;
    LinearLayout maxHigh;
    View.OnTouchListener click=new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if(motionEvent.getAction()!=MotionEvent.ACTION_DOWN)return false;
            Tag tag=(Tag)view.getTag();
            if(tag.step>=refreshStep-2)return false;
            if(tag.death==true)stop(2);
            if(tag.step==step){
                if(running){
                    score+=10;
                    refreshScore();
                    ((Tag) view.getTag()).death=true;
                    view.setBackgroundColor(Color.WHITE);
                    step++;
                }
            }
            return false;
        }
    };
    int score=0;
    boolean running=false;
    int refreshStep=1;
    int step=1;
    int maxHighInt;
    int scrollInt=0;
    Queue<View> list=new LinkedList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scrollView=findViewById(R.id.scroll);
        showView=findViewById(R.id.show_list);
        maxHigh=findViewById(R.id.max_High);
        scoreView=findViewById(R.id.score);
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        new Thread(new ChuShiHuaXianCheng(this)).start();
    }
    public void setHighAndChuShiHua(int maxHighInt){
        this.maxHighInt=maxHighInt;
        ChuShiHua();
    }
    private void ChuShiHua(){
        System.out.println("初始化");
        list.clear();
        score=0;
        refreshScore();
        scrollInt=1;
        refreshStep=1;
        step=1;
        LinearLayout title1=(LinearLayout)(getLayoutInflater().inflate(R.layout.box,null));
        title1.setMinimumHeight(maxHighInt);
        title1.setTag(-1);
        LinearLayout title2=(LinearLayout)(getLayoutInflater().inflate(R.layout.title,null));
        title2.setMinimumHeight(maxHighInt);
        title2.setTag(-1);
        LinearLayout title3=(LinearLayout)(getLayoutInflater().inflate(R.layout.box,null));
        title3.setMinimumHeight(maxHighInt);
        title3.setTag(-1);
        LinearLayout title4=(LinearLayout)(getLayoutInflater().inflate(R.layout.button,null));
        Button bt=title4.findViewById(R.id.startBt);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start();
            }
        });
        title4.setMinimumHeight(maxHighInt);
        title4.setTag(-1);
        LinearLayout title5=(LinearLayout)(getLayoutInflater().inflate(R.layout.box,null));
        title5.setMinimumHeight(maxHighInt);
        title5.setTag(-1);
        list.offer(title5);
        list.offer(title4);
        list.offer(title3);
        list.offer(title2);
        list.offer(title1);
        refresh();
    }
    private void start(){
        if(running)return;
        running=true;
        new Thread(new Time(this)).start();
    }
    public void refreshScore(){
        scoreView.setText(score+"");
    }
    public void refresh(){
        showView.removeAllViews();
        for(int i=0;i<5;i++){
            showView.addView((View)(((LinkedList)list).get(4-i)));
        }
        refreshScrollView();
    }
    public void refreshScrollView(){
        scrollView.setScrollY(scrollInt);
    }
    public void eat(){
        View v=list.poll();
        list.offer(getNewBox());
        if((int)v.getTag()!=-1){
            boolean v1=((Tag)v.findViewById(R.id.part1).getTag()).death;
            boolean v2=((Tag)v.findViewById(R.id.part2).getTag()).death;
            boolean v3=((Tag)v.findViewById(R.id.part3).getTag()).death;
            boolean v4=((Tag)v.findViewById(R.id.part4).getTag()).death;
            if(v1&&v2&&v3&&v4==false)stop(1);
        }
    }
    private LinearLayout getNewBox(){
        LinearLayout temp=(LinearLayout)(getLayoutInflater().inflate(R.layout.box,null));
        temp.setTag(1);
        temp.setMinimumHeight(maxHighInt);
        int n=new Random().nextInt(4);
        View v1=temp.findViewById(R.id.part1);
        v1.setTag(new Tag(true,refreshStep));
        v1.setOnTouchListener(click);
        v1.setBackgroundColor(Color.WHITE);
        View v2=temp.findViewById(R.id.part2);
        v2.setTag(new Tag(true,refreshStep));
        v2.setOnTouchListener(click);
        v2.setBackgroundColor(Color.WHITE);
        View v3=temp.findViewById(R.id.part3);
        v3.setTag(new Tag(true,refreshStep));
        v3.setOnTouchListener(click);
        v3.setBackgroundColor(Color.WHITE);
        View v4=temp.findViewById(R.id.part4);
        v4.setTag(new Tag(true,refreshStep));
        v4.setOnTouchListener(click);
        v4.setBackgroundColor(Color.WHITE);
        View v=null;
        switch (n){
            case 0:
                v=v1;
                break;
            case 1:
                v=v2;
                break;
            case 2:
                v=v3;
                break;
            case 3:
                v=v4;
                break;
            default:
                break;
        }
        if(v!=null){
            v.setBackgroundColor(Color.BLACK);
            ((Tag)v.getTag()).death=false;
        }
        refreshStep++;
        return temp;
    }
    private void stop(int i){//1:漏黑//2:点白
        // TODO: 2019/12/9
        running=false;
        String show="";
        if(i==1)show="您漏掉了黑块！";
        else if(i==2)show="您踩到了白块！";
        AlertDialog.Builder dialog=new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("游戏结束：");
        dialog.setMessage("您的得分："+score+"\n"+show);
        dialog.setPositiveButton("再来一局", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ChuShiHua();
            }
        });
        AlertDialog dialogD=dialog.show();
        dialogD.setCanceledOnTouchOutside(false);
    }
}
class ChuShiHuaXianCheng implements Runnable{//取高
    MainActivity mainActivity;
    LinearLayout linearLayout;
    Handler handler=new Handler();
    ChuShiHuaXianCheng(MainActivity mainActivity){
        this.mainActivity=mainActivity;
        this.linearLayout=mainActivity.maxHigh;
    }
    @Override
    public void run() {
        if(mainActivity==null)return;
        if(linearLayout==null)return;
        int high=linearLayout.getHeight();
        if(high!=0){
            mainActivity.setHighAndChuShiHua(high/4);
        }else handler.postDelayed(this,50);
    }
}
class Time implements Runnable{
    MainActivity mainActivity;
    Handler handler=new Handler();
    int speed=1;
    Time(MainActivity mainActivity){
        this.mainActivity=mainActivity;
    }
    @Override
    public void run() {
        if(!mainActivity.running)return;
        int scrollInt=mainActivity.scrollInt;
        int maxhighInt=mainActivity.maxHighInt;
        if(scrollInt<=0){
            mainActivity.eat();
            mainActivity.scrollInt=maxhighInt;
            refreshSpeed();
            mainActivity.refresh();
        }else{
            if(scrollInt>=speed)mainActivity.scrollInt=scrollInt-speed;
            else mainActivity.scrollInt=0;
            mainActivity.refreshScrollView();
        }
        handler.postDelayed(this,10);
    }
    private void refreshSpeed(){
        int score=mainActivity.score;
        if(score<100)speed=5;
        else if(score>=100)speed=8;
        else if(score>=500)speed=11;
        else if(score>=1000)speed=15;
        else speed=20;
    }
}
class Tag{
    Tag(boolean death,int step){
        this.death=death;
        this.step=step;
    }
    boolean death;
    int step;
}