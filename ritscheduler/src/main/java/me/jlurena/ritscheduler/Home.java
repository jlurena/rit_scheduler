package me.jlurena.ritscheduler;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.nightonke.boommenu.BoomButtons.BoomButton;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.OnBoomListenerAdapter;
import com.nightonke.boommenu.Util;


public class Home extends Activity {

    private BoomMenuButton mBoomMenuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initBoomButton();
        initCalendar();

    }

    private void initBoomButton() {
        mBoomMenuButton = findViewById(R.id.boom_menu_button);
        final Context context = this.getBaseContext();

        mBoomMenuButton.setButtonEnum(ButtonEnum.Ham);


        // Search for a course
        mBoomMenuButton.addBuilder(new HamButton.Builder()
                .normalImageRes(android.R.drawable.ic_menu_search)
                .addView(R.layout.search_dialogue)
                .normalColorRes(R.color.dark_gray)
                .highlightedColorRes(R.color.color_accent)
                .buttonHeight(Util.dp2px(80)));
        mBoomMenuButton.setOnBoomListener(new OnBoomListenerAdapter() {

            @Override
            public void onClicked(int index, BoomButton boomButton) {
                // When clicked
            }
            @Override
            public void onBoomDidHide() {
                // When finished hiding
            }
        });
    }

    private void initCalendar() {

    }
}
