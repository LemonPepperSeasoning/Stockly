package com.larkspur.stockly.Activities;

import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.larkspur.stockly.Models.IUser;
import com.larkspur.stockly.Models.User;
import com.larkspur.stockly.R;

public class SettingsActivity extends CoreActivity {

    private class ViewHolder {
        EditText _usernameTextField;

        public ViewHolder() {
            _usernameTextField = (EditText) findViewById(R.id.usernameTextfield);
        }
    }

    private ViewHolder _vh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        _vh =  new ViewHolder();
        _vh._usernameTextField.setText(_user.getUsername());
        _vh._usernameTextField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                String username = _vh._usernameTextField.getText().toString();
                _user.setUsername(username);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

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

    public void clickReturn(View view){
        Intent intent = this.getIntent();
        Class activity = (Class) intent.getExtras().getSerializable("Class");
        if(activity == StockActivity.class){
            Bundle bundle = intent.getExtras();
            intent.putExtras(bundle);
            redirectActivity(this,activity,bundle);
        }else {
            redirectActivity(this, activity);
        }
    }
}