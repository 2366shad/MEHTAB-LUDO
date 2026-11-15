
package com.mehtab.ludo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.widget.Toast;
import java.util.Random;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new LudoBoard());
    }

    class LudoBoard extends View {
        Paint p = new Paint(3);
        Random random = new Random();

        int[] colors = {
            Color.RED, Color.GREEN, Color.YELLOW, Color.BLUE
        };

        int[][] pos = new int[4][4];
        int turn = 0;
        int dice = 0;
        boolean rolled = false;
        int selected = -1;

        float cell;
        float left, top;

        int[][] path = {
            {6,1},{6,2},{6,3},{6,4},{6,5},
            {5,6},{4,6},{3,6},{2,6},{1,6},{0,6},
            {0,7},{0,8},{1,8},{2,8},{3,8},{4,8},{5,8},
            {6,9},{6,10},{6,11},{6,12},{6,13},
            {7,14},{8,14},{8,13},{8,12},{8,11},{8,10},{8,9},
            {9,8},{10,8},{11,8},{12,8},{13,8},
            {14,7},{14,6},{13,6},{12,6},{11,6},{10,6},{9,6},
            {8,5},{8,4},{8,3},{8,2},{8,1},
            {7,0},{6,0},{5,0},{4,0},{3,0}
        };

        int[][] home = {
            {7,1},{7,2},{7,3},{7,4},{7,5},
            {1,7},{2,7},{3,7},{4,7},{5,7},
            {7,13},{7,12},{7,11},{7,10},{7,9},
            {13,7},{12,7},{11,7},{10,7},{9,7}
        };

        int[] start = {0,13,26,39};

        public LudoBoard() {
            super(MainActivity.this);
            for (int a=0;a<4;a++)
                for (int b=0;b<4;b++)
                    pos[a][b] = -1;
        }

        void paintColor(Canvas c, int color, float x, float y,
                        float r) {
            p.setColor(color);
            c.drawCircle(x,y,r,p);
        }

        @Override
        protected void onDraw(Canvas c) {
            super.onDraw(c);

            float size = Math.min(getWidth(),getHeight());
            cell = size / 15f;
            left = (getWidth()-size)/2f;
            top = (getHeight()-size)/2f;

            c.save();
            c.translate(left,top);

            p.setColor(Color.WHITE);
            c.drawRect(0,0,size,size,p);

            drawBase(c,0,0,Color.RED);
            drawBase(c,9,0,Color.GREEN);
            drawBase(c,0,9,Color.YELLOW);
            drawBase(c,9,9,Color.BLUE);

            for (int i=0;i<path.length;i++) {
                int r=path[i][0], col=path[i][1];
                p.setColor(Color.LTGRAY);
                c.drawRect(col*cell,r*cell,
                    (col+1)*cell,(r+1)*cell,p);
                p.setColor(Color.DKGRAY);
                p.setStyle(Paint.Style.STROKE);
                c.drawRect(col*cell,r*cell,
                    (col+1)*cell,(r+1)*cell,p);
                p.setStyle(Paint.Style.FILL);
            }

            drawHome(c);

            for(int a=0;a<4;a++) {
                for(int b=0;b<4;b++) {
                    float[] xy = tokenPosition(a,b);
                    paintColor(c,colors[a],xy[0],xy[1],cell*0.32f);
                    p.setColor(Color.WHITE);
                    p.setTextSize(cell*0.3f);
                    c.drawText(""+(b+1),xy[0]-cell*.09f,
                        xy[1]+cell*.1f,p);
                }
            }

            p.setColor(Color.BLACK);
            p.setTextSize(cell*.65f);
            c.drawText("MEHTAB LUDO",cell*4.2f,cell*7.2f,p);

            p.setTextSize(cell*.48f);
            p.setColor(colors[turn]);
            c.drawText("PLAYER "+(turn+1),cell*5.1f,cell*8.1f,p);

            p.setColor(Color.BLACK);
            c.drawRoundRect(new RectF(cell*5.3f,cell*9.0f,
                cell*9.7f,cell*11.0f),cell*.3f,cell*.3f,p);

            p.setColor(Color.WHITE);
            p.setTextSize(cell*1.0f);
            String d = dice==0 ? "ROLL" : ""+dice;
            c.drawText(d,cell*6.5f,cell*10.35f,p);

            p.setTextSize(cell*.35f);
            c.drawText("Tap dice, then tap your token",
                cell*2.4f,cell*12.0f,p);

            c.restore();
        }

        void drawBase(Canvas c,int x,int y,int color) {
            p.setColor(color);
            c.drawRect(x*cell,y*cell,
                (x+6)*cell,(y+6)*cell,p);
            p.setColor(Color.WHITE);
            c.drawRoundRect(new RectF((x+1)*cell,(y+1)*cell,
                (x+5)*cell,(y+5)*cell),cell*.4f,cell*.4f,p);

            for(int i=0;i<4;i++) {
                float px=(x+(i%2==0?2:4))*cell;
                float py=(y+(i<2?2:4))*cell;
                paintColor(c,color,px,py,cell*.38f);
            }
        }

        void drawHome(Canvas c) {
            int[][] lanes = {
                {7,1},{1,7},{7,13},{13,7}
            };
            for(int a=0;a<4;a++) {
                for(int i=0;i<5;i++) {
                    int r=home[a][0]+(a==1?i:a==3?-i:0);
                    int col=home[a][1]+(a==0?i:a==2?-i:0);
                    if(r>=0&&r<15&&col>=0&&col<15) {
                        p.setColor(colors[a]);
                        c.drawRect(col*cell,r*cell,
                            (col+1)*cell,(r+1)*cell,p);
                    }
                }
            }
        }

        float[] tokenPosition(int player,int token) {
            int value=pos[player][token];

            if(value<0) {
                int bx=(player==0||player==2)?0:9;
                int by=(player==0||player==1)?0:9;
                int[][] spots={{2,2},{4,2},{2,4},{4,4}};
                return new float[]{
                    (bx+spots[token][0])*cell,
                    (by+spots[token][1])*cell
                };
            }

            if(value>=52) {
                int[][] finish={{7,7},{7,7},{7,7},{7,7}};
                return new float[]{7.5f*cell,7.5f*cell};
            }

            int index=(start[player]+value)%path.length;
            return new float[]{
                (path[index][1]+.5f)*cell,
                (path[index][0]+.5f)*cell
            };
        }

        void nextTurn() {
            turn=(turn+1)%4;
            dice=0;
            rolled=false;
            selected=-1;
        }

        boolean canMove(int player,int token) {
            int v=pos[player][token];
            if(v>=52) return false;
            if(v==-1) return dice==6;
            return v+dice<=52;
        }

        void moveToken(int token) {
            if(!canMove(turn,token)) return;

            if(pos[turn][token]==-1)
                pos[turn][token]=0;
            else
                pos[turn][token]+=dice;

            if(pos[turn][token]<52) {
                int index=(start[turn]+pos[turn][token])%52;

                for(int a=0;a<4;a++) {
                    if(a==turn) continue;
                    for(int b=0;b<4;b++) {
                        if(pos[a][b]>=0 && pos[a][b]<52) {
                            int other=(start[a]+pos[a][b])%52;
                            if(index==other) pos[a][b]=-1;
                        }
                    }
                }
            }

            if(dice!=6) nextTurn();
            else {
                dice=0;
                rolled=false;
            }

            invalidate();
        }

        @Override
        public boolean onTouchEvent(MotionEvent e) {
            if(e.getAction()!=MotionEvent.ACTION_DOWN)
                return true;

            float x=e.getX()-left;
            float y=e.getY()-top;

            if(x>cell*5 && x<cell*10 &&
               y>cell*9 && y<cell*11) {
                if(!rolled) {
                    dice=random.nextInt(6)+1;
                    rolled=true;
                    invalidate();
                }
                return true;
            }

            if(rolled) {
                for(int b=0;b<4;b++) {
                    float[] xy=tokenPosition(turn,b);
                    float dx=x-xy[0],dy=y-xy[1];
                    if(dx*dx+dy*dy<cell*cell &&
                       canMove(turn,b)) {
                        moveToken(b);
                        return true;
                    }
                }
                Toast.makeText(MainActivity.this,
                    "Apni goti par tap karo",Toast.LENGTH_SHORT).show();
            }

            return true;
        }
    }
}

