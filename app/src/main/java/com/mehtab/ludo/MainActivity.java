
package com.mehtab.ludo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.Path;
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

        // -1 = yard, 0-51 = track, 52-56 = home lane, 57 = finished
        int[][] pos = new int[4][4];

        int turn = 0;
        int dice = 0;
        boolean rolled = false;

        float cell, left, top;

        int[] start = {0, 13, 26, 39};

        int[] safeSquares = {
            0, 8, 13, 21, 26, 34, 39, 47
        };

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

        int[][][] homeLane = {
            {{7,1},{7,2},{7,3},{7,4},{7,5}},
            {{1,7},{2,7},{3,7},{4,7},{5,7}},
            {{7,13},{7,12},{7,11},{7,10},{7,9}},
            {{13,7},{12,7},{11,7},{10,7},{9,7}}
        };

        public LudoBoard() {
            super(MainActivity.this);

            for (int a = 0; a < 4; a++) {
                for (int b = 0; b < 4; b++) {
                    pos[a][b] = -1;
                }
            }
        }

        void fill(Canvas c, int color,
                  float x1, float y1, float x2, float y2) {
            p.setColor(color);
            p.setStyle(Paint.Style.FILL);
            c.drawRect(x1, y1, x2, y2, p);
        }

        void circle(Canvas c, int color,
                    float x, float y, float radius) {
            p.setColor(color);
            p.setStyle(Paint.Style.FILL);
            c.drawCircle(x, y, radius, p);
        }

        @Override
        protected void onDraw(Canvas c) {
            super.onDraw(c);

            float size = Math.min(getWidth(), getHeight());
            cell = size / 15f;

            left = (getWidth() - size) / 2f;
            top = (getHeight() - size) / 2f;

            c.save();
            c.translate(left, top);

            fill(c, Color.WHITE, 0, 0, size, size);

            drawBase(c, 0, 0, Color.RED);
            drawBase(c, 9, 0, Color.GREEN);
            drawBase(c, 0, 9, Color.YELLOW);
            drawBase(c, 9, 9, Color.BLUE);

            drawTrack(c);
            drawHome(c);
            drawTokens(c);

            p.setColor(Color.BLACK);
            p.setTextSize(cell * .58f);
            p.setTextAlign(Paint.Align.CENTER);
            c.drawText("MEHTAB LUDO",
                    cell * 7.5f, cell * 7.15f, p);

            p.setColor(colors[turn]);
            p.setTextSize(cell * .43f);
            c.drawText("PLAYER " + (turn + 1),
                    cell * 7.5f, cell * 8.0f, p);

            p.setColor(Color.BLACK);
            c.drawRoundRect(
                new RectF(cell * 5.3f, cell * 9.0f,
                          cell * 9.7f, cell * 11.0f),
                cell * .3f, cell * .3f, p
            );

            p.setColor(Color.WHITE);
            p.setTextSize(cell * .85f);
            String d = dice == 0 ? "ROLL" : String.valueOf(dice);
            c.drawText(d, cell * 7.5f, cell * 10.25f, p);

            p.setTextSize(cell * .32f);
            c.drawText("Tap dice, then tap your token",
                    cell * 7.5f, cell * 12.0f, p);

            p.setTextAlign(Paint.Align.LEFT);
            c.restore();
        }

        void drawBase(Canvas c, int x, int y, int color) {
            fill(c, color,
                    x * cell, y * cell,
                    (x + 6) * cell, (y + 6) * cell);

            p.setColor(Color.WHITE);
            c.drawRoundRect(
                new RectF((x + 1) * cell, (y + 1) * cell,
                          (x + 5) * cell, (y + 5) * cell),
                cell * .4f, cell * .4f, p
            );

            int[][] spots = {
                {2,2},{4,2},{2,4},{4,4}
            };

            for (int i = 0; i < 4; i++) {
                circle(c, color,
                    (x + spots[i][0]) * cell,
                    (y + spots[i][1]) * cell,
                    cell * .38f);
            }
        }

        void drawTrack(Canvas c) {
            for (int i = 0; i < path.length; i++) {
                int r = path[i][0];
                int col = path[i][1];

                int color = Color.WHITE;

                for (int a = 0; a < 4; a++) {
                    if (i == start[a]) color = colors[a];
                }

                if (isSafe(i)) color = Color.LTGRAY;

                fill(c, color,
                    col * cell, r * cell,
                    (col + 1) * cell, (r + 1) * cell);

                p.setColor(Color.DKGRAY);
                p.setStyle(Paint.Style.STROKE);
                p.setStrokeWidth(1);
                c.drawRect(
                    col * cell, r * cell,
                    (col + 1) * cell, (r + 1) * cell, p
                );
                p.setStyle(Paint.Style.FILL);

                if (isSafe(i)) {
                    p.setColor(Color.DKGRAY);
                    p.setTextSize(cell * .45f);
                    p.setTextAlign(Paint.Align.CENTER);
                    c.drawText("*",
                        (col + .5f) * cell,
                        (r + .75f) * cell, p);
                }
            }
        }

        void drawHome(Canvas c) {
            for (int a = 0; a < 4; a++) {
                for (int i = 0; i < 5; i++) {
                    int r = homeLane[a][i][0];
                    int col = homeLane[a][i][1];

                    fill(c, colors[a],
                        col * cell, r * cell,
                        (col + 1) * cell, (r + 1) * cell);

                    p.setColor(Color.WHITE);
                    p.setStyle(Paint.Style.STROKE);
                    c.drawRect(
                        col * cell, r * cell,
                        (col + 1) * cell, (r + 1) * cell, p
                    );
                    p.setStyle(Paint.Style.FILL);
                }
            }

            float cx = 7.5f * cell;
            float cy = 7.5f * cell;

            Path triangle = new Path();

            triangle.moveTo(6 * cell, 6 * cell);
            triangle.lineTo(9 * cell, 6 * cell);
            triangle.lineTo(cx, cy);
            triangle.close();
            p.setColor(Color.GREEN);
            c.drawPath(triangle, p);

            triangle.reset();
            triangle.moveTo(9 * cell, 6 * cell);
            triangle.lineTo(9 * cell, 9 * cell);
            triangle.lineTo(cx, cy);
            triangle.close();
            p.setColor(Color.BLUE);
            c.drawPath(triangle, p);

            triangle.reset();
            triangle.moveTo(9 * cell, 9 * cell);
            triangle.lineTo(6 * cell, 9 * cell);
            triangle.lineTo(cx, cy);
            triangle.close();
            p.setColor(Color.YELLOW);
            c.drawPath(triangle, p);

            triangle.reset();
            triangle.moveTo(6 * cell, 9 * cell);
            triangle.lineTo(6 * cell, 6 * cell);
            triangle.lineTo(cx, cy);
            triangle.close();
            p.setColor(Color.RED);
            c.drawPath(triangle, p);
        }

        void drawTokens(Canvas c) {
            for (int a = 0; a < 4; a++) {
                for (int b = 0; b < 4; b++) {
                    float[] xy = tokenPosition(a, b);

                    circle(c, Color.BLACK,
                        xy[0], xy[1], cell * .34f);

                    circle(c, colors[a],
                        xy[0], xy[1], cell * .29f);

                    p.setColor(Color.WHITE);
                    p.setTextSize(cell * .25f);
                    p.setTextAlign(Paint.Align.CENTER);

                    c.drawText(String.valueOf(b + 1),
                        xy[0], xy[1] + cell * .09f, p);
                }
            }

            p.setTextAlign(Paint.Align.LEFT);
        }

        float[] tokenPosition(int player, int token) {
            int value = pos[player][token];

            if (value == -1) {
                int bx = (player == 0 || player == 2) ? 0 : 9;
                int by = (player == 0 || player == 1) ? 0 : 9;

                int[][] spots = {
                    {2,2},{4,2},{2,4},{4,4}
                };

                return new float[] {
                    (bx + spots[token][0]) * cell,
                    (by + spots[token][1]) * cell
                };
            }

            if (value >= 52 && value <= 56) {
                int i = value - 52;

                return new float[] {
                    (homeLane[player][i][1] + .5f) * cell,
                    (homeLane[player][i][0] + .5f) * cell
                };
            }

            if (value == 57) {
                float[][] offsets = {
                    {-0.25f,-0.25f},{0.25f,-0.25f},
                    {-0.25f,0.25f},{0.25f,0.25f}
                };

                return new float[] {
                    (7.5f + offsets[token][0]) * cell,
                    (7.5f + offsets[token][1]) * cell
                };
            }

            int index = (start[player] + value) % 52;

            return new float[] {
                (path[index][1] + .5f) * cell,
                (path[index][0] + .5f) * cell
            };
        }

        boolean isSafe(int index) {
            for (int s : safeSquares) {
                if (s == index) return true;
            }
            return false;
        }

        boolean canMove(int player, int token) {
            int v = pos[player][token];

            if (v == 57) return false;

            if (v == -1) return dice == 6;

            return v + dice <= 57;
        }

        boolean hasMove(int player) {
            for (int b = 0; b < 4; b++) {
                if (canMove(player, b)) return true;
            }
            return false;
        }

        void nextTurn() {
            turn = (turn + 1) % 4;
            dice = 0;
            rolled = false;
        }

        void rollDice() {
            if (rolled) return;

            dice = random.nextInt(6) + 1;
            rolled = true;

            if (!hasMove(turn)) {
                Toast.makeText(MainActivity.this,
                    dice == 6 ? "6! Dobara roll karo" :
                    "Koi valid move nahi",
                    Toast.LENGTH_SHORT).show();

                if (dice != 6) {
                    nextTurn();
                } else {
                    dice = 0;
                    rolled = false;
                }
            }

            invalidate();
        }

        void moveToken(int token) {
            if (!canMove(turn, token)) return;

            if (pos[turn][token] == -1) {
                pos[turn][token] = 0;
            } else {
                pos[turn][token] += dice;
            }

            // Capture only on non-safe squares.
            if (pos[turn][token] <= 51) {
                int index = (start[turn] + pos[turn][token]) % 52;

                if (!isSafe(index)) {
                    for (int a = 0; a < 4; a++) {
                        if (a == turn) continue;

                        for (int b = 0; b < 4; b++) {
                            if (pos[a][b] >= 0 &&
                                pos[a][b] <= 51) {

                                int other =
                                    (start[a] + pos[a][b]) % 52;

                                if (index == other) {
                                    pos[a][b] = -1;
                                }
                            }
                        }
                    }
                }
            }

            boolean won = true;

            for (int b = 0; b < 4; b++) {
                if (pos[turn][b] != 57) {
                    won = false;
                    break;
                }
            }

            if (won) {
                Toast.makeText(MainActivity.this,
                    "PLAYER " + (turn + 1) + " WINNER!",
                    Toast.LENGTH_LONG).show();

                invalidate();
                return;
            }

            // No three-six cancellation rule.
            // Every six gives another roll.
            if (dice == 6) {
                dice = 0;
                rolled = false;
            } else {
                nextTurn();
            }

            invalidate();
        }

        @Override
        public boolean onTouchEvent(MotionEvent e) {
            if (e.getAction() != MotionEvent.ACTION_DOWN) {
                return true;
            }

            float x = e.getX() - left;
            float y = e.getY() - top;

            if (x > cell * 5 && x < cell * 10 &&
                y > cell * 9 && y < cell * 11) {

                rollDice();
                return true;
            }

            if (rolled) {
                for (int b = 0; b < 4; b++) {
                    float[] xy = tokenPosition(turn, b);

                    float dx = x - xy[0];
                    float dy = y - xy[1];

                    if (dx * dx + dy * dy < cell * cell &&
                        canMove(turn, b)) {

                        moveToken(b);
                        return true;
                    }
                }

                Toast.makeText(MainActivity.this,
                    "Apni valid goti par tap karo",
                    Toast.LENGTH_SHORT).show();
            }

            return true;
        }
    }
}


