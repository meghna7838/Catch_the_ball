package meghna.tinder.catch_the_ball;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private FrameLayout gameFrame;
    private int frameHeight,frameWidthh,initialFrameWidth;
    private LinearLayout startLayout;
    //Images
    private ImageView box,black,orange,pink;
    private Drawable imageBoxRight,imageBoxLeft;
    //Size
    private int boxSize;

    //position
    private float boxX,boxY;
    private float blackX,blackY;
    private float pinkX,pinkY;
    private float orangeX,orangeY;

    //Score
    private TextView scoreLabel,highScoreLabel;
    private int score,highScore,timeCount;
    private SharedPreferences settings;

    //Class
    private Timer timer;
    private Handler handler = new Handler();
    private SoundPlayer soundPlayer;

    //Status
    private boolean start_flg = false;
    private boolean action_flg = false;
    private boolean pink_flg = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        soundPlayer = new SoundPlayer(this);

        gameFrame = findViewById(R.id.gameFrame);
        startLayout =(LinearLayout) findViewById(R.id.startLayout);
        box = findViewById(R.id.box);
        black = findViewById(R.id.black);
        orange = findViewById(R.id.orange);
        pink = findViewById(R.id.pink);
        scoreLabel = findViewById(R.id.scoreLabel);
        highScoreLabel = findViewById(R.id.highScoreLabel);

        imageBoxLeft = getResources().getDrawable(R.drawable.box_left);
        imageBoxRight= getResources().getDrawable(R.drawable.box_right);

        //High Score

        settings = getSharedPreferences("GAME_DATA", Context.MODE_PRIVATE);
        highScore = settings.getInt("HIGH_SCORE",0);
        highScoreLabel.setText("High Score : " + highScore);

    }

    public void changePos()
    {
        //add time Count
        timeCount +=20;
        //orange
        orangeY+=12;

        float orangeCenterX = orangeX +orange.getWidth()/2;
        float orangeCenterY = orangeY +orange.getHeight()/2;

        if(hitCheck(orangeCenterX , orangeCenterY))
        {
            orangeY = frameHeight +100;
            score +=10;
            soundPlayer.playHitOrangeSound();
        }

        if (orangeY >frameHeight)
        {
            orangeY = -100;
            orangeX = (float) Math.floor(Math.random()*(frameWidthh - orange.getWidth()));
        }
        orange.setX(orangeX);
        orange.setY(orangeY);

        //pink
        if(!pink_flg && timeCount%10000 ==0)
        {
            pink_flg = true;
            pinkY = -20;
            pinkX = (float) Math.floor(Math.random()*(frameWidthh - pink.getWidth()));
        }

        if(pink_flg)
        {
            pinkY+=20;

            float pinkCenterX = pinkX + pink.getWidth()/2;
            float pinkCenterY = pinkY + pink.getHeight()/2;

            if(hitCheck(pinkCenterX,pinkCenterY))
            {
                pinkY =frameHeight + 30;
                score += 30;
                // change Frame width
                if(initialFrameWidth > frameWidthh *110 /100)
                {
                    frameWidthh = frameWidthh * 110 /100;
                    changeFrameWidth(frameWidthh);
                }
                soundPlayer.playHitPinkSound();
            }
            if(pinkY > frameHeight) pink_flg =  false;
            pink.setX(pinkX);
            pink.setY(pinkY);

        }

        //black

        blackY+= 18;

        float blackCenterX = blackX + black.getWidth()/2;
        float blackCenterY = blackY + black.getHeight()/2;

        if(hitCheck(blackCenterX,blackCenterY))
        {
            blackY = frameHeight +100;

            //change framewidth

            frameWidthh = frameWidthh*80/100;
            changeFrameWidth(frameWidthh);//chANging frame width
            soundPlayer.playHitBlackSound();
            if(frameWidthh <= boxSize)
            {
                //game over
                gameOver();
            }

        }
        if(blackY >frameHeight)
        {
            blackY = -100;
            blackX = (float) Math.floor(Math.random()*(frameWidthh- black.getWidth()));
        }

        black.setX(blackX);
        black.setY(blackY);

        //Move Box
        if(action_flg)
        {
            //touching
            boxX+=14;
            box.setImageDrawable(imageBoxRight);
        }else
        {
            boxX-=14;
            box.setImageDrawable(imageBoxLeft);
        }

        //checkBoxPosition
        if(boxX <0)
        {
            boxX = 0;
            box.setImageDrawable(imageBoxRight);
        }
        if(frameWidthh - boxSize <boxX)
        {
            boxX = frameWidthh - boxSize;
            box.setImageDrawable(imageBoxLeft);
        }
    box.setX(boxX);

        scoreLabel.setText("Score: "+score);
    }

    public boolean hitCheck(float x,float y)
    {
        if (boxX <= x && x<= boxX + boxSize && boxY <= y && y<= frameHeight)
        {
            return true;
        }
        return false;
    }

    public void changeFrameWidth(int frameWidthh){
        ViewGroup.LayoutParams params = gameFrame.getLayoutParams();
        params.width = frameWidthh;
        gameFrame.setLayoutParams(params);
    }

    public  void gameOver()
    {
        //stop timer
        timer.cancel();
        timer = null;
        start_flg = false;

        //Before showing startLayout ,sleep 1 second.
        try {
            TimeUnit.SECONDS.sleep(1);
             }catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        changeFrameWidth(initialFrameWidth);
        startLayout.setVisibility(View.VISIBLE);
        box.setVisibility(View.INVISIBLE);
        black.setVisibility(View.INVISIBLE);
        orange.setVisibility(View.INVISIBLE);
        pink.setVisibility(View.INVISIBLE);

        //Update High Score
        if(score > highScore)
        {
            highScore = score;
            highScoreLabel.setText("High Score : "+ highScore);

            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("HIGH SCORE",highScore);
            editor.commit();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(start_flg){
            if(event.getAction() == MotionEvent.ACTION_DOWN)
            {
                action_flg = true;
            }else if(event.getAction() == MotionEvent.ACTION_UP)
            {
                action_flg = false;
            }
        }
        return true;
    }

    public void startGame(View view)
    {
        start_flg = true;
        startLayout.setVisibility(View.INVISIBLE);

        if(frameHeight==0)
        {
            frameHeight = gameFrame.getHeight();
            frameWidthh = gameFrame.getWidth();
            initialFrameWidth = frameWidthh;

            boxSize = box.getHeight();
            boxX = box.getX();
            boxY = box.getY();
        }

        frameWidthh = initialFrameWidth;

        box.setX(0.0f);
        black.setY(3000.0f);
        orange.setY(3000.0f);
        pink.setY(3000.0f);

        blackY= black.getY();
        orangeY = orange.getY();
        pinkY = pink.getY();

        box.setVisibility(View.VISIBLE);
        black.setVisibility(View.VISIBLE);
        orange.setVisibility(View.VISIBLE);
        pink.setVisibility(View.VISIBLE);

        timeCount = 0;
        score=0;
        scoreLabel.setText("Score: 0");

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(start_flg){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            changePos();
                        }
                    });
                }
            }
        },0,20);
    }
    public void quitGame(View view)
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            finishAndRemoveTask();
        }else{
            finish();
        }

    }
}