package com.larkspur.stockly.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.cardview.widget.CardView;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.larkspur.stockly.R;

public class HelpActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        setButtonOnClick(findViewById(R.id.base_expandableCard_1),
                findViewById(R.id.expand_button_1),
                findViewById(R.id.hidden_layout_1));
        setButtonOnClick(findViewById(R.id.base_expandableCard_2),
                findViewById(R.id.expand_button_2),
                findViewById(R.id.hidden_layout_2));
        setButtonOnClick(findViewById(R.id.base_expandableCard_3),
                findViewById(R.id.expand_button_3),
                findViewById(R.id.hidden_layout_3));
        setButtonOnClick(findViewById(R.id.base_expandableCard_4),
                findViewById(R.id.expand_button_4),
                findViewById(R.id.hidden_layout_4));
        setButtonOnClick(findViewById(R.id.base_expandableCard_5),
                findViewById(R.id.expand_button_5),
                findViewById(R.id.hidden_layout_5));
        setButtonOnClick(findViewById(R.id.base_expandableCard_6),
                findViewById(R.id.expand_button_6),
                findViewById(R.id.hidden_layout_6));

    }

    private void setButtonOnClick(CardView card,ImageButton button,ConstraintLayout hiddenLayout){
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
}
