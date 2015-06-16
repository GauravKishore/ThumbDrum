package com.guava.thumbdrum;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;


public class MainActivity extends ActionBarActivity {
    private ImageView view;
    private Canvas canvas;
    private Paint paint;
    private SoundPool sp;

    private int BLUE = 0xff1a237e, BLUE2 = 0xff304ffe;
    private int RED = 0xffb71c1c, RED2 = 0xffff1744;
    private int GREEN = 0xff1b5e20, GREEN2 = 0xff00c853;
    private int YELLOW = 0xfff57f17, YELLOW2 = 0xffffd600;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view = (ImageView) this.findViewById(R.id.view);
        Bitmap bitmap = Bitmap.createBitmap(getWindowManager()
                .getDefaultDisplay().getWidth(), getWindowManager()
                .getDefaultDisplay().getHeight(), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        view.setImageBitmap(bitmap);
        paint = new Paint();
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);

        int bassX = width / 2, bassY = height * 5 / 4, bassRad = width * 4 / 5, bassCol = BLUE;
        int snareX = width / 4, snareY = height * 5 / 8, snareRad = width / 5, snareCol = RED;
        int tom1X = width / 6, tom1Y = height * 3 / 8, tom1Rad = width / 7, tom1Col = GREEN;
        int tom2X = width * 1 / 2, tom2Y = height * 2 / 5, tom2Rad = width / 6, tom2Col = GREEN;
        int tom3X = width * 3 / 4, tom3Y = height * 5 / 8, tom3Rad = width * 2 / 9, tom3Col = GREEN;
        int hatX = width * 5 / 6, hatY = height * 3 / 8, hatRad = width / 7, hatCol = YELLOW;
        int crashX = width * 2 / 3, crashY = height / 6, crashRad = width / 6, crashCol = YELLOW;
        int rideX = width / 4, rideY = height / 7, rideRad = width * 2 / 9, rideCol = YELLOW;

        final Drum bass = new Drum(bassX, bassY, bassRad, bassCol);
        bass.soundId = sp.load(this, R.raw.bass, 1);
        bass.drawDrum();

        final Drum snare = new Drum(snareX, snareY, snareRad, snareCol);
        snare.soundId = sp.load(this, R.raw.snare,1);
        snare.drawDrum();

        final Drum tom1 = new Drum(tom1X, tom1Y, tom1Rad, tom1Col);
        tom1.soundId = sp.load(this, R.raw.tom1,1);
        tom1.drawDrum();

        final Drum tom2 = new Drum(tom2X, tom2Y, tom2Rad, tom2Col);
        tom2.soundId = sp.load(this, R.raw.tom2,1);
        tom2.drawDrum();

        final Drum tom3 = new Drum(tom3X, tom3Y, tom3Rad, tom3Col);
        tom3.soundId = sp.load(this, R.raw.tom3,1);
        tom3.drawDrum();

        final Drum hat = new Drum(hatX, hatY, hatRad, hatCol);
        hat.soundId = sp.load(this, R.raw.hat,1);
        hat.drawDrum();

        final Drum crash = new Drum(crashX, crashY, crashRad, crashCol);
        crash.soundId = sp.load(this, R.raw.crash,1);
        crash.drawDrum();

        final Drum ride = new Drum(rideX, rideY, rideRad, rideCol);
        ride.soundId = sp.load(this, R.raw.ride,1);
        ride.drawDrum();

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_POINTER_DOWN:
                    case MotionEvent.ACTION_DOWN:
                        int pointIndex = event.getPointerCount() - 1;
                        float eventX = event.getX(pointIndex);
                        float eventY = event.getY(pointIndex);

                        if (bass.checkTouch(eventX, eventY)) {
                            bass.playDrum();
                        } else if (snare.checkTouch(eventX, eventY)) {
                            snare.playDrum();
                        } else if (tom1.checkTouch(eventX, eventY)) {
                            tom1.playDrum();
                        } else if (tom2.checkTouch(eventX, eventY)) {
                            tom2.playDrum();
                        } else if (tom3.checkTouch(eventX, eventY)) {
                            tom3.playDrum();
                        } else if (hat.checkTouch(eventX, eventY)) {
                            hat.playDrum();
                        } else if (crash.checkTouch(eventX, eventY)) {
                            crash.playDrum();
                        } else if (ride.checkTouch(eventX, eventY)) {
                            ride.playDrum();
                        }
                }
                return true;
            }
        });

    }

    public class Drum {
        private int x;
        private int y;
        private int rad;
        private int colour;
        private int colourAlt;
        private int soundId = -1;
        private int streamId = -1;

        Drum temp = this;
        Runnable run = new Runnable() {
            @Override
            public void run() {
                temp.drawDrum();
                view.invalidate();
            }
        };

        public Drum(int x, int y, int rad, int colour) {
            this.x = x;
            this.y = y;
            this.rad = rad;
            this.colour = colour;
            if (colour == BLUE) {
                this.colourAlt = BLUE2;
            } else if (colour == RED) {
                this.colourAlt = RED2;
            } else if (colour == GREEN) {
                this.colourAlt = GREEN2;
            } else {
                this.colourAlt = YELLOW2;
            }
        }

        public int drawDrum() {
            paint.setColor(this.colour);
            canvas.drawCircle(this.x, this.y, this.rad, paint);
            return this.colour;
        }

        public void playDrum() {
            view.removeCallbacks(run);

            sp.stop(this.streamId);
            this.streamId = sp.play(this.soundId, 1, 1, 0, 0, 1);
            paint.setColor(this.colourAlt);
            canvas.drawCircle(this.x, this.y, this.rad, paint);
            view.invalidate();

            sp.play(temp.soundId, 1, 1, 0, 0, 1);

            view.postDelayed(run, 200);

        }

        public boolean checkTouch(float touchX, float touchY) {
            return (touchY - this.y) * (touchY - this.y) + (touchX - this.x) * (touchX - this.x)
                    < this.rad * this.rad;
        }
    }
}
