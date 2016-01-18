package com.emplaceme.feedback.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toolbar;

import com.emplaceme.feedback.R;

import java.nio.channels.FileLock;

public class Home extends AppCompatActivity {

    private Button newFeedback;
    float amount = 0.0f;
    int numPeolple = 0;
    String outletName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        //save data in shared preferance
        SharedPreferences userCredential = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = userCredential.edit();
        editor.putString("user_id","");
        editor.putString("user_name","");
        editor.putString("user_email","");
        editor.putString("country_id","");
        editor.putString("user_phone","");
        editor.putString("user_dob","");
        editor.putString("user_anniversary", "");
        editor.apply();

        outletName = userCredential.getString("outlet_name","");
        if(outletName.isEmpty() == false){
            getSupportActionBar().setTitle(outletName);
        }else {
            getSupportActionBar().setTitle("");
        }



        newFeedback = (Button) findViewById(R.id.new_feedback);

        newFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //open dialog
                openDialog();
            }
        });



    }

    public void openDialog() {
        final Dialog dialogAmount = new Dialog(this);
        dialogAmount.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAmount.setContentView(R.layout.dialog_feeback_amount);
        dialogAmount.setCanceledOnTouchOutside(true);

        final TextInputLayout amountLayout = (TextInputLayout) dialogAmount.findViewById(R.id.input_layout_amount);
        final TextInputLayout peopleLayout = (TextInputLayout) dialogAmount.findViewById(R.id.input_layout_people);
        final EditText amountInput = (EditText) dialogAmount.findViewById(R.id.input_amount);
        final EditText peopleInput = (EditText) dialogAmount.findViewById(R.id.input_people);

        if (amountInput.isFocused()) {
            amountInput.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);


        } else {

        }

        if (peopleInput.isFocused()) {
            peopleInput.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);


        }

        Button giveCustomer = (Button) dialogAmount.findViewById(R.id.give_customer);
        giveCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finish and open new activity


                int flag = checkForBlankData(amountInput.getText().toString(), peopleInput.getText().toString());

                if (flag == 0) {
                    amountLayout.setError("Enter Amount");
                    amountLayout.setErrorEnabled(true);
                    peopleLayout.setError("Enter number of People");
                    peopleLayout.setErrorEnabled(true);

                } else if (flag == 1) {
                    amountLayout.setError("Enter Amount");
                    amountLayout.setErrorEnabled(true);
                    peopleLayout.setErrorEnabled(false);

                } else if (flag == 2) {

                    peopleLayout.setError("Enter number of People");
                    peopleLayout.setErrorEnabled(true);
                    amountLayout.setErrorEnabled(false);

                } else if (flag == 3) {

                    amountLayout.setErrorEnabled(false);
                    peopleLayout.setErrorEnabled(false);

                    Intent screen = new Intent(Home.this, CustomerLogin.class);
                    screen.putExtra("amount_bill", Float.parseFloat(amountInput.getText().toString()));
                    screen.putExtra("number_of_people", Integer.parseInt(peopleInput.getText().toString()));
                    startActivity(screen);
                    dialogAmount.dismiss();
                }


            }
        });

        dialogAmount.show();

    }

    public int checkForBlankData(String amount, String numPeolple) {
        int flag = -1;
        int a = 0, p = 0;

        if (amount.isEmpty() == true) {
            a = 1;
        }
        if (numPeolple.isEmpty() == true) {
            p = 1;
        }

        //check for error
        if (a == 1 && p == 1) {
            flag = 0;
        } else if (a == 1 && p == 0) {
            flag = 1;
        } else if (a == 0 && p == 1) {
            flag = 2;
        } else if (a == 0 && p == 0) {
            flag = 3;
        }


        return flag;
    }

}
