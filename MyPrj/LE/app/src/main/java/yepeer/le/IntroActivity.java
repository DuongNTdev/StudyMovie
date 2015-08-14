package yepeer.le;

import android.os.Bundle;
import android.view.View;

import com.github.paolorotolo.appintro.AppIntro;

import yepeer.le.fragments.SampleSlide;
import yepeer.le.save.UserData;

/**
 * Created by duongnt on 8/8/15.
 */
public class IntroActivity extends AppIntro {
    @Override
    public void init(Bundle savedInstanceState) {
        addSlide(SampleSlide.newInstance(R.layout.intro));
        addSlide(SampleSlide.newInstance(R.layout.intro2));
        addSlide(SampleSlide.newInstance(R.layout.intro3));

        setFadeAnimation();
    }

    private void loadMainActivity(){
        UserData.getInstance().setOpenedAppIntro(this);
       finish();
    }

    @Override
    public void onSkipPressed() {
        loadMainActivity();
    }

    @Override
    public void onDonePressed() {
        loadMainActivity();
    }

    public void getStarted(View v){
        loadMainActivity();
    }
}