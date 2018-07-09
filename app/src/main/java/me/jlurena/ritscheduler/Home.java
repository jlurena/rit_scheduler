package me.jlurena.ritscheduler;

import android.app.Activity;
import android.os.Bundle;

import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Util;

public class Home extends Activity {

    private BoomMenuButton boomMenuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initBoomButton();


    }

    private void initBoomButton() {
        boomMenuButton = findViewById(R.id.boom_menu_button);

        boomMenuButton.setButtonEnum(ButtonEnum.Ham);


        // Search for a course
        boomMenuButton.addBuilder(new HamButton.Builder()
                .normalImageRes(android.R.drawable.ic_menu_search)
                .addView(R.layout.search_dialogue)
                .normalColorRes(R.color.dark_gray)
                .highlightedColorRes(R.color.color_accent)
                .buttonHeight(Util.dp2px(80)));
    }
}
