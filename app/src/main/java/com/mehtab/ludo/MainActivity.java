package com.mehtab.ludo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.content.Context;
import android.view.MotionEvent;
import java.util.Random;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.rgb(20,20,35));
        setContentView(new LudoView(this));
    }

    class LudoView extends View {
        Paint p = new Paint(3);
        Random random = new Random();
        int dice = 6;

        int red = Color.rgb(230,45,55);
        int green = Color.rgb(35,165,85);
        int yellow = Color.rgb(245,190,35);
        int blue = Color.rgb(40,110,225);

        LudoView(Context c) { super(c); }

        void rect(Canvas c, float x, float y, float w, float h, int color) {
            p.setColor(color);
            c.drawRect(x,y,x+w,y+h,p);
        }

        void circle(Canvas c,float x,float y,float r,int color) {
            p.setColor(color);
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

            float board = Math.min(getWidth()-24,getHeight()-145);
            float left = (getWidth()-board)/2;
            float top = 24;
            float cell = board/15f;

            rect(c,left,top,board,board,Color.WHITE);

            // Four home areas
            int[] colors={red,green,yellow,blue};
            int[][] homes={{0,0},{9,0},{9,9},{0,9}};

            for(int i=0;i<4;i++) {
                float x=left+homes[i][0]*cell;
                float y=top+homes[i][1]*cell;
                rect(c,x,y,6*cell,6*cell,colors[i]);
                rect(c,x+cell,y+cell,4*cell,4*cell,Color.WHITE);

                for(int a=0;a<2;a++) {
                    for(int b=0;b<2;b++) {
                        circle(c,x+(2+a*2)*cell,y+(2+b*2)*cell,
                            cell*.43f,colors[i]);
                        circle(c,x+(2+a*2)*cell,y+(2+b*2)*cell,
                            cell*.23f,Color.WHITE);
                    }
                }
            }

            // Playing tracks
            for(int i=6;i<=8;i++) {
                for(int j=0;j<15;j++) {
                    if(j>=6 && j<=8) continue;
                    rect(c,left+j*cell,top+i*cell,cell,cell,
                        Color.rgb(245,245,245));
                    rect(c,left+i*cell,top+j*cell,cell,cell,
                        Color.rgb(245,245,245));
                }
            }

            // Colored entry lanes
            for(int i=1;i<=5;i++) {
                rect(c,left+7*cell,top+i*cell,cell,cell,green);
                rect(c,left+7*cell,top+(14-i)*cell,cell,cell,blue);
                rect(c,left+i*cell,top+7*cell,cell,cell,red);
                rect(c,left+(14-i)*cell,top+7*cell,cell,cell,yellow);
            }

            // Center
            float cx=left+7.5f*cell;
            float cy=top+7.5f*cell;
            p.setColor(red);
            c.drawRect(cx-cell*1.5f,cy-cell*1.5f,cx,cy,p);
            p.setColor(green);
            c.drawRect(cx,cy-cell*1.5f,cx+cell*1.5f,cy,p);
            p.setColor(yellow);
            c.drawRect(cx-cell*1.5f,cy,cx,cy+cell*1.5f,p);
            p.setColor(blue);
            c.drawRect(cx,cy,cx+cell*1.5f,cy+cell*1.5f,p);

            // Board border and grid
            p.setColor(Color.DKGRAY);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(1);
            for(int i=0;i<=15;i++) {
                c.drawLine(left+i*cell,top,left+i*cell,top+board,p);
                c.drawLine(left,top+i*cell,left+board,top+i*cell,p);
            }
            p.setStyle(Paint.Style.FILL);

            // Dice
            float dy=top+board+48;
            p.setColor(Color.WHITE);
            c.drawRoundRect(left+board*.35f,dy-28,
                left+board*.65f,dy+28,14,14,p);
            text(c,""+dice,getWidth()/2f,dy+12,34,Color.BLACK);
            text(c,"TAP DICE TO ROLL",getWidth()/2f,dy+68,17,Color.WHITE);
            text(c,"MEHTAB LUDO",getWidth()/2f,getHeight()-15,
                19,Color.rgb(255,205,50));
        }

        @Override
        public boolean onTouchEvent(MotionEvent e) {
            if(e.getAction()==MotionEvent.ACTION_DOWN) {
                dice=random.nextInt(6)+1;
                invalidate();
                return true;
            }
            return true;
        }
    }
}
