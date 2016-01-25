package com.emplaceme.feedback.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.emplaceme.feedback.R;

public class CustomerFeedbackForm extends AppCompatActivity {

    private RadioGroup groupFood;
    private RadioGroup groupAmbience;
    private RadioGroup groupService;

    private TextView userNameView;
 String outletName = "";
    String userName = "";
    float amount;
    int numberPeople;

    int f = 0;
    int a = 0;
    int s = 0;
    int food = 0;
    int ambience = 0;
    int service = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_feedback_form);

        SharedPreferences userData = PreferenceManager.getDefaultSharedPreferences(this);
        outletName = userData.getString("client_name", "");
        userName = userData.getString("user_name","");


        if (outletName.isEmpty() == false) {
            getSupportActionBar().setTitle(outletName);
        } else {
            getSupportActionBar().setTitle("");
        }

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //get data from intent
        Intent i = getIntent();
        amount = i.getFloatExtra("amount_bill", 0.0f);
        numberPeople = i.getIntExtra("number_of_people", 0);


        userNameView = (TextView)findViewById(R.id.customerName);

        groupFood = (RadioGroup) findViewById(R.id.radioFood);
        groupAmbience = (RadioGroup) findViewById(R.id.radioAmbience);
        groupService = (RadioGroup) findViewById(R.id.radioService);


        if(userName.isEmpty() == false)
        {
            userNameView.setVisibility(View.VISIBLE);
            userNameView.setText("Hey! "+userName);
        }else {
            userNameView.setVisibility(View.GONE);
        }


        checkButtonClicked();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    public void checkButtonClicked()
    {
        //food group
        groupFood.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                f = 1;
                switch (checkedId)
                {
                    case R.id.foodPoor:
                        food = 25;
                        break;
                    case R.id.foodAverage:
                        food = 50;
                        break;
                    case R.id.foodGood:
                        food = 75;
                        break;
                    case R.id.foodAwsome:
                        food = 100;
                        break;
                }

                checkFeedbackProvided();

            }
        });


        //ambience group
        groupAmbience.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                a = 1;
                switch (checkedId)
                {
                    case R.id.ambiencePoor:
                        ambience = 25;
                        break;
                    case R.id.ambienceAverage:
                        ambience = 50;
                        break;
                    case R.id.ambienceGood:
                        ambience = 75;
                        break;
                    case R.id.ambienceAwsome:
                        ambience = 100;
                        break;
                }

                checkFeedbackProvided();

            }
        });

        //service group
        groupService.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                s = 1;
                switch (checkedId)
                {
                    case R.id.servicePoor:
                        service = 25;
                        break;
                    case R.id.serviceAverage:
                        service = 50;
                        break;
                    case R.id.serviceGood:
                        service = 75;
                        break;
                    case R.id.serviceAwsome:
                        service = 100;
                        break;
                }

                checkFeedbackProvided();

            }
        });


    }

    public void checkFeedbackProvided()
    {

        if( f == 1 && a == 1 && s == 1)
        {
            //open thank you page
            Intent screen = new Intent(CustomerFeedbackForm.this, CustomerDateForm.class);
            screen.putExtra("amount_bill", amount);
            screen.putExtra("number_of_people", numberPeople);
            screen.putExtra("food_feedback",food);
            screen.putExtra("ambience_feedback",ambience);
            screen.putExtra("service_feedback",service);
            startActivity(screen);

        }

    }

}
