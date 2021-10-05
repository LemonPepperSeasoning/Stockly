package com.larkspur.stockly.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.ImageButton;

import com.larkspur.stockly.R;

public class SettingsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        ConstraintLayout card = findViewById(R.id.base_expandablelayout);
        ImageButton button = findViewById(R.id.expand_button_5);
        ConstraintLayout hiddenLayout = findViewById(R.id.hidden_layout_5);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // If the CardView is already expanded, set its visibility
                //  to gone and change the expand less icon to expand more.
                if (hiddenLayout.getVisibility() == View.VISIBLE) {


                    //TODO: Fix this transition. This transition works but its glitchy.
//                    TransitionManager.beginDelayedTransition(card,
//                            new AutoTransition());
                    hiddenLayout.setVisibility(View.GONE);
                    button.setImageResource(R.drawable.expand_button);
                }

                // If the CardView is not expanded, set its visibility
                // to visible and change the expand more icon to expand less.
                else {
                    TransitionManager.beginDelayedTransition(card,
                            new AutoTransition());
                    hiddenLayout.setVisibility(View.VISIBLE);
                    button.setImageResource(R.drawable.unexpand_button);
                }
            }
        });
    }


    public void clickBack(View view){
        MainActivity.redirectActivity(this,MainActivity.class);
    }
}