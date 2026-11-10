package com.mehtab.ludo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.content.Context;
import android.view.MotionEvent;
import java.util.Random;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setStatusBarColor(Color.rgb(20,20,35));
        setContentView(new LudoView(this));
    }

    class LudoView extends View {
        Paint p = new Paint(3);
        Random random = new Random();

        int[] dice = {1,1,1,1};
        int currentPlayer = 0;

        int[] colors = {
            Color.rgb(230,45,55),
            Color.rgb(35,165,85),
            Color.rgb(245,190,35),
            Color.rgb(40,110,225)
        };

        String[] names = {"RED","GREEN","YELLOW","BLUE"};

        LudoView(Context c) {
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

        void text(Canvas c,String s,float x,float y,float size,int color) {
            p.setColor(color);
            p.setTextSize(size);
            p.setTypeface(Typeface.DEFAULT_BOLD);
            p.setTextAlign(Paint.Align.CENTER);
            c.drawText(s,x,y,p);
        }

        @Override
        protected void onDraw(Canvas c) {
            super.onDraw(c);
            c.drawColor(Color.rgb(20,20,35));

            float board = Math.min(getWidth()-24,getHeight()-190);
            float left = (getWidth()-board)/2;
            float top = 24;
            float cell = board/15f;

            rect(c,left,top,board,board,Color.WHITE);

            int[][] homes = {
                {0,0},{9,0},{9,9},{0,9}
            };

            for(int i=0;i<4;i++) {
                float x=left+homes[i][0]*cell;
                float y=top+homes[i][1]*cell;

                rect(c,x,y,6*cell,6*cell,colors[i]);
                rect(c,x+cell,y+cell,4*cell,4*cell,Color.WHITE);

                for(int a=0;a<2;a++) {
                    for(int b=0;b<2;b++) {
                        circle(c,x+(2+a*2)*cell,
                            y+(2+b*2)*cell,cell*.43f,colors[i]);
                        circle(c,x+(2+a*2)*cell,
                            y+(2+b*2)*cell,cell*.23f,Color.WHITE);
                    }
                }
            }

            for(int i=6;i<=8;i++) {
                for(int j=0;j<15;j++) {
                    if(j>=6 && j<=8) continue;

                    rect(c,left+j*cell,top+i*cell,
                        cell,cell,Color.rgb(245,245,245));

                    rect(c,left+i*cell,top+j*cell,
                        cell,cell,Color.rgb(245,245,245));
                }
            }

            for(int i=1;i<=5;i++) {
                rect(c,left+7*cell,top+i*cell,cell,cell,colors[1]);
                rect(c,left+7*cell,top+(14-i)*cell,cell,cell,colors[3]);
                rect(c,left+i*cell,top+7*cell,cell,cell,colors[0]);
                rect(c,left+(14-i)*cell,top+7*cell,cell,cell,colors[2]);
            }

            float cx=left+7.5f*cell;
            float cy=top+7.5f*cell;

            rect(c,cx-cell*1.5f,cy-cell*1.5f,
                cell*1.5f,cell*1.5f,colors[0]);
            rect(c,cx,cy-cell*1.5f,
                cell*1.5f,cell*1.5f,colors[1]);
            rect(c,cx-cell*1.5f,cy,
                cell*1.5f,cell*1.5f,colors[2]);
            rect(c,cx,cy,cell*1.5f,cell*1.5f,colors[3]);

            p.setColor(Color.DKGRAY);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(1);

            for(int i=0;i<=15;i++) {
                c.drawLine(left+i*cell,top,
                    left+i*cell,top+board,p);
                c.drawLine(left,top+i*cell,
                    left+board,top+i*cell,p);
            }

            p.setStyle(Paint.Style.FILL);

            float dy=top+board+35;
            float boxW=(getWidth()-40)/4f;

            for(int i=0;i<4;i++) {
                float x=20+i*boxW;

                p.setColor(i==currentPlayer ?
                    Color.WHITE : Color.rgb(65,65,80));

                c.drawRoundRect(x+3,dy-25,
                    x+boxW-3,dy+25,10,10,p);

                text(c,""+dice[i],x+boxW/2,
                    dy+12,29,i==currentPlayer ?
                    Color.BLACK : Color.WHITE);

                text(c,names[i],x+boxW/2,
                    dy+47,10,colors[i]);
            }

            text(c,names[currentPlayer]+" PLAYER TURN",
                getWidth()/2f,dy+77,16,Color.WHITE);

            text(c,"TAP YOUR DICE TO ROLL",
                getWidth()/2f,dy+100,12,Color.LTGRAY);

            text(c,"MEHTAB LUDO",
                getWidth()/2f,getHeight()-15,
                19,Color.rgb(255,205,50));
        }

        @Override
        public boolean onTouchEvent(MotionEvent e) {
            if(e.getAction()==MotionEvent.ACTION_DOWN) {
                float board=Math.min(getWidth()-24,getHeight()-190);
                float dy=24+board+35;
                float boxW=(getWidth()-40)/4f;

                if(e.getY()>=dy-30 && e.getY()<=dy+30) {
                    int player=(int)((e.getX()-20)/boxW);

                    if(player==currentPlayer && player>=0 && player<4) {
                        dice[player]=random.nextInt(6)+1;

                        if(dice[player]!=6) {
                            currentPlayer=(currentPlayer+1)%4;
                        }

                        invalidate();
                    }
                }
                return true;
            }
            return true;
        }
    }
}
