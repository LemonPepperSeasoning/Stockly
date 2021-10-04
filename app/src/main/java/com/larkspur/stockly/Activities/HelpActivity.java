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

    ImageButton expandButton;
    CardView cardView;
    ConstraintLayout hiddenView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        cardView = findViewById(R.id.base_expandableCard);
        expandButton = findViewById(R.id.expand_button);
        hiddenView = findViewById(R.id.hidden_laypout);

        expandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // If the CardView is already expanded, set its visibility
                //  to gone and change the expand less icon to expand more.
                if (hiddenView.getVisibility() == View.VISIBLE) {

                    // The transition of the hiddenView is carried out
                    //  by the TransitionManager class.
                    // Here we use an object of the AutoTransition
                    // Class to create a default transition.
                    TransitionManager.beginDelayedTransition(cardView,
                            new AutoTransition());
                    hiddenView.setVisibility(View.GONE);
                    expandButton.setImageResource(R.drawable.unexpand_button);
                }

                // If the CardView is not expanded, set its visibility
                // to visible and change the expand more icon to expand less.
                else {

                    TransitionManager.beginDelayedTransition(cardView,
                            new AutoTransition());
                    hiddenView.setVisibility(View.VISIBLE);
                    expandButton.setImageResource(R.drawable.expand_button);
                }
            }
        });
    }
}
