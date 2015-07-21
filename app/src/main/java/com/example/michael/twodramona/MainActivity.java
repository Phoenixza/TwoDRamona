package com.example.michael.twodramona;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.michael.twodramona.tools.BaseGameActivity;
import com.example.michael.twodramona.tools.SimpleAnimationListener;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class MainActivity extends BaseGameActivity implements View.OnClickListener, Crap.OnBurstListener {

    private ScheduledExecutorService executor;
    private static final int CRAP_MAX = 13;
    private static final float V_MAX = 1f;
    private static final float SIZE_MAX = 128f;
    private Random rnd = new Random();
    private Drawable crapDrawable;
    private Set<Crap> crap = new HashSet<Crap>();
    private ViewGroup container;
    public static final String TYPEFACE_TITLE = "FantasticFont";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addTypeface(TYPEFACE_TITLE);
        container = (ViewGroup) findViewById(R.id.container);

        crapDrawable = getResources().getDrawable(R.drawable.papier);

    }


    private void showFragment() {
        container.removeAllViews();
        View start = getLayoutInflater().inflate(R.layout.start,null);
        setTypeface((TextView) start.findViewById(R.id.title), TYPEFACE_TITLE);
        start.findViewById(R.id.start).setOnClickListener(this);
        container.addView(start);
        Animation a = AnimationUtils.loadAnimation(this, R.anim.abc_fade_in);
        start.startAnimation(a);
    }

    @Override
    public void onClick(View view){
        if(view.getId() == R.id.start){
            Animation a = AnimationUtils.loadAnimation(this,R.anim.pulse);
            a.setAnimationListener(new SimpleAnimationListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    startGame();
                }
            });
            view.startAnimation(a);
        }
    }

    public void startGame(){
        container.removeAllViews();
        crap.clear();
        for(int i = 0; i>CRAP_MAX; i++){
            crap.add(new Crap((FrameLayout) container, scale(V_MAX), scale(SIZE_MAX), rnd, crapDrawable, this));
        }
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(moveRunnable, 0, 50, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void onPause(){
        super.onPause();
        executor.shutdown();
        crap.clear();
    }

    @Override
    protected void onResume(){
        super.onResume();
        showFragment();
    }

    private Runnable moveRunnable = new Runnable(){
        @Override
        public void run(){
            for(final  Crap c : crap) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        c.move();
                    }
                });
            }
        }
    };

    @Override
    public void onBurst(Crap c) {
        crap.remove(c);
        crap.add(new Crap((FrameLayout) container, scale(V_MAX), scale(SIZE_MAX), rnd, crapDrawable,this));
    }
}
