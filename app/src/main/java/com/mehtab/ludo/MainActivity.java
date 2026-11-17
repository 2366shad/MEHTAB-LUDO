
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

        // Board/player indices: Red top-left, Green top-right,
        // Yellow bottom-left, Blue bottom-right.
        int[] colors = {
            Color.RED, Color.GREEN, Color.YELLOW, Color.BLUE
        };

        // Turn order requested: Green -> Blue -> Red -> Yellow.
        int[] turnOrder = {1, 3, 0, 2};

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

            // Four player dice sit beside their matching board quadrants.
            // Only the current player's dice is highlighted and tappable.
            drawPlayerDice(c);

            p.setTextAlign(Paint.Align.LEFT);
            c.restore();
        }

        float diceX(int player) {
            if (player == 0 || player == 2) return 6.78f * cell;
            return 8.22f * cell;
        }

        float diceY(int player) {
            if (player == 0 || player == 1) return 6.78f * cell;
            return 8.22f * cell;
        }

        void drawPlayerDice(Canvas c) {
            for (int player = 0; player < 4; player++) {
                float cx = diceX(player);
                float cy = diceY(player);
                float half = cell * .66f;
                boolean active = player == turn;

                p.setStyle(Paint.Style.FILL);
                p.setColor(active ? colors[player] : Color.rgb(205,205,205));
                c.drawRoundRect(new RectF(cx-half, cy-half, cx+half, cy+half),
                        cell * .18f, cell * .18f, p);

                p.setColor(Color.WHITE);
                c.drawRoundRect(new RectF(cx-half*.78f, cy-half*.78f,
                        cx+half*.78f, cy+half*.78f),
                        cell * .13f, cell * .13f, p);

                if (active && rolled) {
                    drawDiceFace(c, dice, cx, cy);
                } else {
                    p.setColor(active ? colors[player] : Color.GRAY);
                    p.setTextAlign(Paint.Align.CENTER);
                    p.setTextSize(cell * .22f);
                    c.drawText("ROLL", cx, cy + cell * .075f, p);
                }

                if (active) {
                    p.setColor(Color.BLACK);
                    p.setStyle(Paint.Style.STROKE);
                    p.setStrokeWidth(cell * .045f);
                    c.drawRoundRect(new RectF(cx-half, cy-half, cx+half, cy+half),
                            cell * .18f, cell * .18f, p);
                    p.setStyle(Paint.Style.FILL);
                }
            }
        }

        void drawDiceFace(Canvas c, int value, float cx, float cy) {
            float d = cell * .22f;
            float r = cell * .075f;
            int[][][] dots = {
                {},
                {{0,0}},
                {{-1,-1},{1,1}},
                {{-1,-1},{0,0},{1,1}},
                {{-1,-1},{1,-1},{-1,1},{1,1}},
                {{-1,-1},{1,-1},{0,0},{-1,1},{1,1}},
                {{-1,-1},{1,-1},{-1,0},{1,0},{-1,1},{1,1}}
            };
            p.setColor(Color.BLACK);
            p.setStyle(Paint.Style.FILL);
            for (int[] dot : dots[value]) {
                c.drawCircle(cx + dot[0] * d, cy + dot[1] * d, r, p);
            }
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
                    drawStar(c, (col + .5f) * cell, (r + .5f) * cell, cell * .30f);
                }
            }
        }

        void drawStar(Canvas c, float cx, float cy, float radius) {
            Path star = new Path();
            for (int i = 0; i < 10; i++) {
                double angle = -Math.PI / 2 + i * Math.PI / 5;
                float r = (i % 2 == 0) ? radius : radius * .43f;
                float x = cx + (float)Math.cos(angle) * r;
                float y = cy + (float)Math.sin(angle) * r;
                if (i == 0) star.moveTo(x, y); else star.lineTo(x, y);
            }
            star.close();
            p.setColor(Color.rgb(255, 190, 35));
            p.setStyle(Paint.Style.FILL);
            c.drawPath(star, p);
            p.setColor(Color.rgb(120, 75, 10));
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(cell * .025f);
            c.drawPath(star, p);
            p.setStyle(Paint.Style.FILL);
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
                    boolean movable = rolled && a == turn && canMove(a, b);
                    drawPawn(c, colors[a], xy[0], xy[1], movable);
                }
            }
        }

        void drawPawn(Canvas c, int color, float x, float y, boolean highlighted) {
            float s = cell;
            if (highlighted) {
                p.setColor(Color.argb(75, 255, 215, 0));
                c.drawCircle(x, y, s * .49f, p);
                p.setColor(Color.rgb(255, 190, 0));
                p.setStyle(Paint.Style.STROKE);
                p.setStrokeWidth(s * .055f);
                c.drawCircle(x, y, s * .43f, p);
                p.setStyle(Paint.Style.FILL);
            }

            // Soft shadow and broad pawn foot
            p.setColor(Color.argb(85, 0, 0, 0));
            c.drawOval(new RectF(x - s*.34f, y + s*.20f, x + s*.34f, y + s*.39f), p);

            p.setColor(darken(color));
            c.drawOval(new RectF(x - s*.34f, y + s*.10f, x + s*.34f, y + s*.34f), p);

            // Pawn body with a rounded shoulder and narrow neck
            Path body = new Path();
            body.moveTo(x - s*.25f, y + s*.18f);
            body.cubicTo(x - s*.24f, y - s*.02f, x - s*.12f, y - s*.07f, x - s*.12f, y - s*.19f);
            body.cubicTo(x - s*.12f, y - s*.28f, x - s*.22f, y - s*.30f, x - s*.22f, y - s*.39f);
            body.cubicTo(x - s*.22f, y - s*.56f, x + s*.22f, y - s*.56f, x + s*.22f, y - s*.39f);
            body.cubicTo(x + s*.22f, y - s*.30f, x + s*.12f, y - s*.28f, x + s*.12f, y - s*.19f);
            body.cubicTo(x + s*.12f, y - s*.07f, x + s*.24f, y - s*.02f, x + s*.25f, y + s*.18f);
            body.close();
            p.setColor(color);
            c.drawPath(body, p);

            // Gloss highlight and round head
            p.setColor(lighten(color));
            c.drawCircle(x - s*.075f, y - s*.40f, s*.105f, p);
            p.setColor(color);
            c.drawCircle(x, y - s*.43f, s*.19f, p);
            p.setColor(lighten(color));
            c.drawCircle(x - s*.055f, y - s*.49f, s*.065f, p);

            p.setColor(Color.argb(150, 255, 255, 255));
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(s*.025f);
            c.drawPath(body, p);
            c.drawCircle(x, y - s*.43f, s*.19f, p);
            p.setStyle(Paint.Style.FILL);
        }

        int darken(int color) {
            return Color.rgb((int)(Color.red(color)*.62f),
                    (int)(Color.green(color)*.62f), (int)(Color.blue(color)*.62f));
        }

        int lighten(int color) {
            return Color.rgb(Math.min(255, Color.red(color)+65),
                    Math.min(255, Color.green(color)+65),
                    Math.min(255, Color.blue(color)+65));
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
            int orderIndex = 0;
            for (int i = 0; i < turnOrder.length; i++) {
                if (turnOrder[i] == turn) {
                    orderIndex = i;
                    break;
                }
            }
            turn = turnOrder[(orderIndex + 1) % turnOrder.length];
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

            float activeDiceX = diceX(turn);
            float activeDiceY = diceY(turn);
            if (Math.abs(x - activeDiceX) < cell * .72f &&
                Math.abs(y - activeDiceY) < cell * .72f) {
                rollDice();
                invalidate();
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


