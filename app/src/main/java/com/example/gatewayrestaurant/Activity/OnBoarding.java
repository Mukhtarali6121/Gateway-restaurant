package com.example.gatewayrestaurant.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gatewayrestaurant.Adapter.SliderAdapter;
import com.example.gatewayrestaurant.R;
import com.example.gatewayrestaurant.Utils.AppSettingsPref;

public class OnBoarding extends AppCompatActivity {

    ViewPager viewPager;
    LinearLayout dotsLayout;
    SliderAdapter sliderAdapter;
    Button letsGetstarted,nextbtn;
    TextView[] dots;
    Animation animation;
    int currentPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);

        init();

    }

    private void init()
    {
        viewPager = findViewById(R.id.slider);
        dotsLayout = findViewById(R.id.dots);
        letsGetstarted = findViewById(R.id.get_started_btn);
        nextbtn = findViewById(R.id.next_btn);


        sliderAdapter = new SliderAdapter(this);
        viewPager.setAdapter(sliderAdapter);
        changeStatusBarColor();

        //dots
        addDots(0);
        viewPager.addOnPageChangeListener(changeListener);


        letsGetstarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppSettingsPref.saveBoolean(getApplicationContext(), AppSettingsPref.HAS_ON_BOARDED, true);
                Intent intent = new Intent(getApplicationContext(), HomePageActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    //skip button
    public void skip(View view) {
        AppSettingsPref.saveBoolean(getApplicationContext(), AppSettingsPref.HAS_ON_BOARDED, true);

        Boolean hasOnBoard  =
                AppSettingsPref.getBooleanValue(this, AppSettingsPref.HAS_ON_BOARDED, false);

        Log.e("asfag",hasOnBoard.toString());

        startActivity(new Intent(getApplicationContext(), HomePageActivity.class));
        finish();
    }

    //next button
    public void next(View view) {
        viewPager.setCurrentItem(currentPos + 1);
//        if (currentPos == 0) {
//            nextbtn.setVisibility(View.VISIBLE);
//        } else if (currentPos == 1) {
//            nextbtn.setVisibility(View.VISIBLE);
//        } else {
//            nextbtn.setVisibility(View.INVISIBLE);
//        }

//        if(currentPos == 0){
//            nextbtn.setEnabled(true);
//        }else if(currentPos == 1){
//            nextbtn.setEnabled(true);
//        }else {
//            nextbtn.setEnabled(false);
//        }
    }


    //adding dots
    private void addDots(int position) {
        dots = new TextView[3];
        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(35);
            dotsLayout.addView(dots[i]);
        }
        if (dots.length > 0) {
            dots[position].setTextColor(getResources().getColor(R.color.colorPrimary));
        }
    }


    ViewPager.OnPageChangeListener changeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }


        //making visble and invisble till the last page comes(GET STARTED  button)
        @Override
        public void onPageSelected(int position) {
            addDots(position);
            currentPos = position;
            if (position == 0) {
                letsGetstarted.setVisibility(View.INVISIBLE);
            } else if (position == 1) {
                letsGetstarted.setVisibility(View.INVISIBLE);
            } else {
                animation = AnimationUtils.loadAnimation(OnBoarding.this, R.anim.bottom_animation);
                letsGetstarted.setAnimation(animation);
                letsGetstarted.setVisibility(View.VISIBLE);
                nextbtn.setVisibility(View.GONE);
            }
        }


        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    private void changeStatusBarColor() {
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        }
    }
}