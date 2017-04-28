package com.example.a10011284.dodgeproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class DodgeProject extends AppCompatActivity implements SensorEventListener {

    GameSurface gameSurface;
    int value = 0;
    int eValue = 5;
    boolean onScreen = false;
    int score =0;
    boolean intersect = false;
    int text;
    SoundPool soundPool;
    static SoundPool sound;
    int iSound;
    int pointSound;
    boolean gameOver = false;
    boolean faster = false;
    MediaPlayer m;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameSurface = new GameSurface(this);
        setContentView(gameSurface);
        SensorManager manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor mySensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        manager.registerListener(this,mySensor,SensorManager.SENSOR_DELAY_GAME);
        m = MediaPlayer.create(this,R.raw.back);
        m.setVolume(2,2);
        m.start();
        SoundPool.Builder builder = new SoundPool.Builder(); //creates builder
        builder.setMaxStreams(3);
        soundPool = builder.build();
        soundPool.setVolume(iSound,1,1);
        iSound = soundPool.load(this,R.raw.smash,1);
        pointSound = soundPool.load(this,R.raw.point,1);


        CountDownTimer c = new CountDownTimer(30000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                text = (int) millisUntilFinished/1000;
            }

            @Override
            public void onFinish() {
                Log.d("hello","finished");
                gameOver = true;
                //gameSurface.pause();
            }
        };
        c.start();


    }

    @Override
    protected void onPause(){
        super.onPause();
        gameSurface.pause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        gameSurface.resume();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.values[0] < -1.0){
            value = 5;
        }
        if (event.values[0] < -2.5){
            value = 13;
        }
        if (event.values[0] > -1.0 && event.values[0] < 1.0){
            value = 0;
        }
        if  (event.values[0] > 1.0 ){
            value = -5;
        }
        if (event.values[0] > 2.5){
            value = -13;
        }
        Log.d("hello",value+"");

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    //----------------------------GameSurface Below This Line--------------------------
    public class GameSurface extends SurfaceView implements Runnable,View.OnTouchListener{

        Thread gameThread;
        SurfaceHolder holder;
        volatile boolean running = false;
        Bitmap myImage;
        Bitmap enImage;
        Bitmap bImage;
        Bitmap background;
        Paint paintProperty;
        Enemy en;
        Player p;
        Canvas canvas;

        int screenWidth;
        int screenHeight;

        public GameSurface(Context context) {
            super(context);

            holder=getHolder();

            myImage = BitmapFactory.decodeResource(getResources(),R.drawable.spaceship);
            myImage = myImage.createScaledBitmap(myImage,83,150,false);
            bImage = BitmapFactory.decodeResource(getResources(),R.drawable.brokentwo);
            bImage = bImage.createScaledBitmap(bImage,83,150,false);
            enImage = BitmapFactory.decodeResource(getResources(),R.drawable.asteroid);
            enImage = enImage.createScaledBitmap(enImage,50,50,false);
            background = BitmapFactory.decodeResource(getResources(),R.drawable.back);


            Display screenDisplay = getWindowManager().getDefaultDisplay();
            Point sizeOfScreen = new Point();
            screenDisplay.getSize(sizeOfScreen);
            screenWidth=sizeOfScreen.x;
            screenHeight=sizeOfScreen.y;
            background = background.createScaledBitmap(background,screenWidth,screenHeight,false);
            p = new Player(100,screenHeight-myImage.getHeight()-140);

            paintProperty= new Paint();
            paintProperty.setTextSize(20);
            paintProperty.setColor(Color.WHITE);
            setOnTouchListener(this);

        }

        @Override
        public void run() {
            while (running == true){
                if (holder.getSurface().isValid() == false)
                    continue;
                canvas= holder.lockCanvas();
                canvas.drawBitmap(background,0,0,null);
                //Enemy e =  new Enemy(20,20);
                //canvas.drawBitmap( enImage,e.getX(),e.getY(),null);
                if (gameOver == false){
                    canvas.drawText("Score: "+score,30,30,paintProperty);
                    canvas.drawText("Time Remaining: " + text,screenWidth-300,30,paintProperty);
                    if (onScreen == false){
                        int x = (int) (Math.random()*(screenWidth-100))+50;
                        Log.d("hello",x+"");
                        en = new Enemy(x,20);
                        onScreen = true;
                    }
                    int temp = p.getX()+value;
                    if (temp < screenWidth-myImage.getWidth() && temp>0 ){
                        p.setX(value);
                    }
                    boolean col = p.getRectangle().intersect(en.getRectangle());
                    if (col == true){
                        //Log.d("hello","works");
                        if (intersect == false){score = score -1;}
                        soundPool.play(iSound,1,1,1,0,1);
                        intersect = true;
                    }
                    if (intersect == true){
                        canvas.drawBitmap( bImage,p.getX(), p.getY(),null);
                    }
                    else {
                        canvas.drawBitmap( myImage,p.getX(), p.getY(),null);
                    }

                    if (onScreen == true){
                        en.move(eValue);
                        canvas.drawBitmap( enImage,en.getX(),en.getY(),null);
                        //Log.d("hello",en.getX()+"");
                        //Log.d("hello",screenHeight-10+"");
                        if (en.getY() > screenHeight-10){
                            onScreen = false;
                            if (intersect == true){
                                intersect = false;
                            }
                            else {
                                score++;
                                soundPool.play(pointSound,1,1,1,0,1);
                            }
                        }
                    }
                }
                if (gameOver == true){
                    m.stop();
                    canvas.drawText("Game Over",350,400,paintProperty);
                    canvas.drawText("Score: "+score,350,420,paintProperty);
                }
                holder.unlockCanvasAndPost(canvas);
                //m.start();
                //value++;=
            }
        }

        public void resume(){
            running=true;
            gameThread=new Thread(this);
            gameThread.start();
        }

        public void pause() {
            running = false;
            while (true) {
                try {
                    gameThread.join();
                } catch (InterruptedException e) {
                }
            }
        }


        @Override
        public boolean onTouch(View v, MotionEvent event) {
            //Log.d("hello","whats");
            // do i need to make it go faster every time i touch screen?
            if (faster == false){
                eValue = 10;
                faster = true;
            }
            else {
                eValue = 5;
                faster = false;
            }
            return false;
        }
    }//GameSurface
}
