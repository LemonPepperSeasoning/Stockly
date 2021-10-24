package com.larkspur.stockly.activities;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.larkspur.stockly.models.IPortfolio;
import com.larkspur.stockly.models.IUser;
import com.larkspur.stockly.models.IWatchlist;
import com.larkspur.stockly.models.User;
import com.larkspur.stockly.R;

/**
 * This activity handles the Settings screen. This screen allows users to change their name, change
 * the time zone, currency and etc.
 * Author: Takahiro
 */
public class SettingsActivity extends CoreActivity {

    /**
     * Represents every item in the screen and displays each one.
     */
    private class ViewHolder {
        EditText _usernameTextField;

        public ViewHolder() {
            _usernameTextField = (EditText) findViewById(R.id.usernameTextfield);
        }
    }

    private ViewHolder _vh;

    /**
     * Initialises all processes for the screen once screen is launched.
     *
     * @param savedInstanceState default input (Any saved stock or user information)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        _vh = new ViewHolder();
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
        ConstraintLayout portfolioCard = findViewById(R.id.clear_portfolio_view);
        ConstraintLayout watchlistCard = findViewById(R.id.clear_watchlist_view);

        portfolioCard.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.alert_dialog, null);
                PopupWindow popupWindow = new PopupWindow(popupView, DrawerLayout.LayoutParams.WRAP_CONTENT, DrawerLayout.LayoutParams.WRAP_CONTENT, true);
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                IPortfolio portfolio = User.getInstance().getPortfolio();

                Button noButton = (Button) popupView.findViewById(R.id.no_button_view);
                Button yesButton = (Button) popupView.findViewById(R.id.yes_button_view);

                noButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupWindow.dismiss();
                    }
                });
                yesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        IUser user = User.getInstance();
                        IPortfolio portfolio = user.getPortfolio();
                        portfolio.removeAllStocks();
                        Toast.makeText(view.getContext(), "Removed all stocks from Portfolio", Toast.LENGTH_SHORT).show();
                        popupWindow.dismiss();
                    }
                });

            }
        });

        watchlistCard.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.alert_dialog, null);
                PopupWindow popupWindow = new PopupWindow(popupView, DrawerLayout.LayoutParams.WRAP_CONTENT, DrawerLayout.LayoutParams.WRAP_CONTENT, true);
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                IPortfolio portfolio = User.getInstance().getPortfolio();

                Button noButton = (Button) popupView.findViewById(R.id.no_button_view);
                Button yesButton = (Button) popupView.findViewById(R.id.yes_button_view);

                noButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupWindow.dismiss();
                    }
                });
                yesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        IUser user = User.getInstance();
                        IWatchlist watchlist = user.getWatchlist();
                        watchlist.removeAllStocks();
                        Toast.makeText(view.getContext(), "Removed all stocks from Watchlist", Toast.LENGTH_SHORT).show();
                        popupWindow.dismiss();
                    }
                });

            }
        });

    }

    /**
     * Handles click functionality for return text
     *
     * @param view TextView
     */
    public void clickReturn(View view) {
        Intent intent = this.getIntent();
        Class activity = (Class) intent.getExtras().getSerializable("Class");
        if (activity == DetailsActivity.class) {
            Bundle bundle = intent.getExtras();
            intent.putExtras(bundle);
            redirectActivity(this, activity, bundle);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        } else {
            redirectActivity(this, activity);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        }
    }



}