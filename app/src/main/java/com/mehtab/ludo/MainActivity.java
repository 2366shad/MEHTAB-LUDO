package com.mehtab.ludo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.content.Context;
import android.view.MotionEvent;
import java.util.Random;

public class MainActivity extends Activity {
    public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setStatusBarColor(Color.rgb(20,20,35));
        setContentView(new Game(this));
    }

    class Game extends View {
        Paint p = new Paint(3);
        Random random = new Random();
        int[] dice = {1,1,1,1};
        int turn = 0;

        int[] colors = {
            Color.rgb(230,45,55),
            Color.rgb(35,165,85),
            Color.rgb(245,190,35),
            Color.rgb(40,110,225)
        };

        int[][] homes = {{0,0},{9,0},{9,9},{0,9}};

        Game(Context c) {
            super(c);
        }

        void rect(Canvas c,float x,float y,float w,float h,int color) {
            p.setColor(color);
            p.setStyle(Paint.Style.FILL);
            c.drawRect(x,y,x+w,y+h,p);
        }

        void circle(Canvas c,float x,float y,float r,int color) {
            p.setColor(color);
            p.setStyle(Paint.Style.FILL);
            c.drawCircle(x,y,r,p);
        }

        void die(Canvas c,float x,float y,float s,int value,int color,boolean active) {
            float h=s/2;
            p.setColor(color);
            p.setStyle(Paint.Style.FILL);
            c.drawRoundRect(x-h,y-h,x+h,y+h,s*.17f,s*.17f,p);

            p.setColor(Color.WHITE);
            c.drawRoundRect(x-h+s*.09f,y-h+s*.09f,
                x+h-s*.09f,y+h-s*.09f,s*.13f,s*.13f,p);

            float a=s*.25f;
            float r=s*.055f;
            switch(value) {
                case 1:
                    circle(c,x,y,r,Color.BLACK);
                    break;
                case 2:
                    circle(c,x-a,y-a,r,Color.BLACK);
                    circle(c,x+a,y+a,r,Color.BLACK);
                    break;
                case 3:
                    circle(c,x-a,y-a,r,Color.BLACK);
                    circle(c,x,y,r,Color.BLACK);
                    circle(c,x+a,y+a,r,Color.BLACK);
                    break;
                case 4:
                    circle(c,x-a,y-a,r,Color.BLACK);
                    circle(c,x+a,y-a,r,Color.BLACK);
                    circle(c,x-a,y+a,r,Color.BLACK);
                    circle(c,x+a,y+a,r,Color.BLACK);
                    break;
                case 5:
                    circle(c,x-a,y-a,r,Color.BLACK);
                    circle(c,x+a,y-a,r,Color.BLACK);
                    circle(c,x,y,r,Color.BLACK);
                    circle(c,x-a,y+a,r,Color.BLACK);
                    circle(c,x+a,y+a,r,Color.BLACK);
                    break;
                case 6:
                    circle(c,x-a,y-a,r,Color.BLACK);
                    circle(c,x+a,y-a,r,Color.BLACK);
                    circle(c,x-a,y,r,Color.BLACK);
                    circle(c,x+a,y,r,Color.BLACK);
                    circle(c,x-a,y+a,r,Color.BLACK);
                    circle(c,x+a,y+a,r,Color.BLACK);
                    break;
            }

            if(active) {
                p.setColor(Color.WHITE);
                p.setStyle(Paint.Style.STROKE);
                p.setStrokeWidth(3);
                c.drawRoundRect(x-h-3,y-h-3,x+h+3,y+h+3,
                    s*.2f,s*.2f,p);
                p.setStyle(Paint.Style.FILL);
            }
        }

        protected void onDraw(Canvas c) {
            super.onDraw(c);
            c.drawColor(Color.rgb(20,20,35));

            float board=Math.min(getWidth()-20,getHeight()-100);
            float left=(getWidth()-board)/2;
            float top=15;
            float cell=board/15f;

            rect(c,left,top,board,board,Color.WHITE);

            for(int i=0;i<4;i++) {
                float x=left+homes[i][0]*cell;
                float y=top+homes[i][1]*cell;

                rect(c,x,y,6*cell,6*cell,colors[i]);
                rect(c,x+cell,y+cell,4*cell,4*cell,Color.WHITE);

                for(int a=0;a<2;a++)
                    for(int b=0;b<2;b++)
                        circle(c,x+(2+a*2)*cell,y+(2+b*2)*cell,
                            cell*.22f,colors[i]);
            }

            for(int i=6;i<=8;i++) {
                for(int j=0;j<15;j++) {
                    if(j>=6 && j<=8) continue;
                    rect(c,left+j*cell,top+i*cell,cell,cell,Color.rgb(245,245,245));
                    rect(c,left+i*cell,top+j*cell,cell,cell,Color.rgb(245,245,245));
                }
            }

            for(int i=1;i<=5;i++) {
                rect(c,left+7*cell,top+i*cell,cell,cell,colors[1]);
                rect(c,left+7*cell,top+(14-i)*cell,cell,cell,colors[3]);
                rect(c,left+i*cell,top+7*cell,cell,cell,colors[0]);
                rect(c,left+(14-i)*cell,top+7*cell,cell,cell,colors[2]);
            }

            float cx=left+7.5f*cell, cy=top+7.5f*cell;
            rect(c,cx-cell*1.5f,cy-cell*1.5f,cell*1.5f,cell*1.5f,colors[0]);
            rect(c,cx,cy-cell*1.5f,cell*1.5f,cell*1.5f,colors[1]);
            rect(c,cx-cell*1.5f,cy,cell*1.5f,cell*1.5f,colors[2]);
            rect(c,cx,cy,cell*1.5f,cell*1.5f,colors[3]);

            p.setColor(Color.DKGRAY);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(1);
            for(int i=0;i<=15;i++) {
                c.drawLine(left+i*cell,top,left+i*cell,top+board,p);
                c.drawLine(left,top+i*cell,left+board,top+i*cell,p);
            }
            p.setStyle(Paint.Style.FILL);

            float[][] centers={
                {left+3*cell,top+3*cell},
                {left+12*cell,top+3*cell},
                {left+12*cell,top+12*cell},
                {left+3*cell,top+12*cell}
            };

            for(int i=0;i<4;i++) {
                die(c,centers[i][0],centers[i][1],
                    cell*1.25f,dice[i],colors[i],turn==i);
            }

            p.setColor(Color.WHITE);
            p.setTextSize(17);
            p.setTextAlign(Paint.Align.CENTER);
            p.setFakeBoldText(true);
            c.drawText("MEHTAB LUDO",getWidth()/2f,getHeight()-18,p);
        }

        public boolean onTouchEvent(MotionEvent e) {
            if(e.getAction()==MotionEvent.ACTION_DOWN) {
                float board=Math.min(getWidth()-20,getHeight()-100);
                float left=(getWidth()-board)/2;
                float top=15;
                float cell=board/15f;

                float[][] centers={
                    {left+3*cell,top+3*cell},
                    {left+12*cell,top+3*cell},
                    {left+12*cell,top+12*cell},
                    {left+3*cell,top+12*cell}
                };

                for(int i=0;i<4;i++) {
                    float dx=e.getX()-centers[i][0];
                    float dy=e.getY()-centers[i][1];

                    if(dx*dx+dy*dy<cell*cell && i==turn) {
                        dice[i]=random.nextInt(6)+1;
                        if(dice[i]!=6) turn=(turn+1)%4;
                        invalidate();
                        break;
                    }
                }
                return true;
            }
            return true;
        }
    }
}
