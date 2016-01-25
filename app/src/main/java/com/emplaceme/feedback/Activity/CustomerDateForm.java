package com.emplaceme.feedback.Activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.emplaceme.feedback.Network.VolleySingleton;
import com.emplaceme.feedback.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.emplaceme.feedback.Extras.LinkDetails.URL.URL_EXISTING_USER;
import static com.emplaceme.feedback.Extras.LinkDetails.URL.URL_FEEDBACK;
import static com.emplaceme.feedback.Extras.LinkDetails.URL.URL_OUTLET_LOGIN;

public class CustomerDateForm extends AppCompatActivity {

    private EditText inputName;
    private EditText inputDob;
    private EditText inputAnniversary;
    private Button submit;
    private Button submitDone;
    static final int DATE_DIALOG_ID_DOB = 0;
    static final int DATE_DIALOG_ID_ANNIVERSARY = 1;

    private TextInputLayout textInputLayoutName;
    private TextInputLayout textInputLayoutDOB;
    private TextInputLayout textInputLayoutAnniversary;

    private RelativeLayout layoutDOB;
    private RelativeLayout layoutAnniversary;
    private RelativeLayout layoutName;
    private RelativeLayout layoutForm;
    private RelativeLayout layoutUpper;

    private TextView userNameView;
    private TextView messageDeals;
    private TextView messageFeedback;

    private int mDay, mYear, mMonth;

    float amount;
    int numberPeople;
    int food = 0;
    int ambience = 0;
    int service = 0;

    boolean flagName = false;
    boolean flagDob = false;
    boolean flagAnniversary = false;

    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    Map<String, String> jsonParams = new HashMap<String, String>();

    String name = "";
    String dob = "";
    String anniversary = "";

    String dobNew = "";
    String anniversaryNew = "";

    int n = 0;
    int d = 0;
    int a = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_date_form);

        inputName = (EditText) findViewById(R.id.input_name);
        inputDob = (EditText) findViewById(R.id.input_dob);
        inputAnniversary = (EditText) findViewById(R.id.input_anniversary);

        submit = (Button) findViewById(R.id.submitButton);
        submitDone = (Button) findViewById(R.id.doneButton);

        userNameView = (TextView) findViewById(R.id.userName);
        messageDeals = (TextView) findViewById(R.id.messageDeals);
        messageFeedback = (TextView) findViewById(R.id.messageFeedback);


        textInputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        textInputLayoutDOB = (TextInputLayout) findViewById(R.id.input_layout_dob);
        textInputLayoutAnniversary = (TextInputLayout) findViewById(R.id.input_layout_anniversary);

        layoutUpper = (RelativeLayout) findViewById(R.id.upperLayout);
        layoutName = (RelativeLayout) findViewById(R.id.layoutFormOne);
        layoutDOB = (RelativeLayout) findViewById(R.id.layoutFormTwo);
        layoutAnniversary = (RelativeLayout) findViewById(R.id.layoutFormThree);
        layoutForm = (RelativeLayout) findViewById(R.id.layoutFour);

        //get data from intent
        Intent i = getIntent();
        amount = i.getFloatExtra("amount_bill", 0.0f);
        numberPeople = i.getIntExtra("number_of_people", 0);
        food = i.getIntExtra("food_feedback", 0);
        ambience = i.getIntExtra("ambience_feedback", 0);
        service = i.getIntExtra("service_feedback", 0);


        //check if data is available or not
        //check if its an existing user or not
        SharedPreferences userCredential = PreferenceManager.getDefaultSharedPreferences(this);
        name = userCredential.getString("user_name", "");
        dob = userCredential.getString("user_dob", "");
        anniversary = userCredential.getString("user_anniversary", "");
        checkForUserData();

        volleySingleton = VolleySingleton.getsInstance();
        requestQueue = volleySingleton.getRequestQueue();

        inputDob.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {
                    showDialog(DATE_DIALOG_ID_DOB);
                    hideKeyboard(v);
                }
            }
        });

        inputDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputDob.requestFocus();
                showDialog(DATE_DIALOG_ID_DOB);
                hideKeyboard(v);
            }
        });

        inputAnniversary.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {
                    showDialog(DATE_DIALOG_ID_ANNIVERSARY);
                    hideKeyboard(v);
                }
            }
        });
        inputAnniversary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputAnniversary.requestFocus();
                showDialog(DATE_DIALOG_ID_ANNIVERSARY);
                hideKeyboard(v);
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //build the json and send server request
                checkForInputs();

            }
        });

        submitDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //build the json and send server request
                checkForInputs();

            }
        });


        final Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);


    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID_DOB:
                return new DatePickerDialog(this,
                        mDateSetListenerDOB,
                        mYear, mMonth, mDay);
            case DATE_DIALOG_ID_ANNIVERSARY:
                return new DatePickerDialog(this,
                        mDateSetListenerAnniversary,
                        mYear, mMonth, mDay);


        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener mDateSetListenerDOB =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {

                    //Toast.makeText(CustomerDateForm.this,"DOb " +String.valueOf(year),Toast.LENGTH_SHORT).show();
                    String s = String.valueOf(year) + "-" + String.valueOf(monthOfYear + 1)
                            + "-" + String.valueOf(dayOfMonth);

                    inputDob.setText(s);
                    dobNew = s + " " + "00:00:00";

                }
            };

    private DatePickerDialog.OnDateSetListener mDateSetListenerAnniversary =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {

                  // Toast.makeText(CustomerDateForm.this,"Anni " +String.valueOf(year),Toast.LENGTH_SHORT).show();

                    String s = String.valueOf(year) + "-" + String.valueOf(monthOfYear + 1)
                            + "-" + String.valueOf(dayOfMonth);

                    inputAnniversary.setText(s);
                    anniversaryNew = s + " " + "00:00:00";
                }
            };

    public void checkForInputs() {
        SharedPreferences userCredential = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = userCredential.edit();

        //check for name
        if (flagName == true) {
            //check if user give any name
            if (inputName.getText().toString().isEmpty() == false)
                editor.putString("user_name", inputName.getText().toString().trim());

        }
        //check for dob
        if (flagDob == true) {
            //check if user give dob or not
            if (dobNew.isEmpty() == false) {

                editor.putString("user_dob", dobNew.trim());
               // Toast.makeText(this,"DOB Input = "+inputDob.getText().toString().trim(),Toast.LENGTH_SHORT).show();
            }
        }

        //check for anniversary
        if (flagAnniversary == true) {
            //check if user give anniversary or not
            if (anniversaryNew.isEmpty() == false) {
                editor.putString("user_anniversary", anniversaryNew.toString().trim());
                //Toast.makeText(this,"Anni Input = "+inputAnniversary.getText().toString().trim(),Toast.LENGTH_SHORT).show();
            }

        }

        editor.apply();

        //now call server
        callServerToSendFeedbackData(getUrl());


    }

    public void callServerToSendFeedbackData(String URL) {


        //save data in shared preferance
        SharedPreferences userCredential = PreferenceManager.getDefaultSharedPreferences(this);

        jsonParams.put("bill_amount", String.valueOf(amount));
        jsonParams.put("no_of_people", String.valueOf(numberPeople));
        jsonParams.put("phone_no", userCredential.getString("user_phone", ""));
        jsonParams.put("email", userCredential.getString("user_email", ""));
        jsonParams.put("country_id", userCredential.getString("company_id", ""));
        jsonParams.put("food", String.valueOf(food));
        jsonParams.put("ambiance", String.valueOf(ambience));
        jsonParams.put("service", String.valueOf(service));
        jsonParams.put("name", userCredential.getString("user_name", ""));
        jsonParams.put("dob", userCredential.getString("user_dob", ""));
        jsonParams.put("comment", "");
        jsonParams.put("anniversary", userCredential.getString("user_anniversary", ""));
        jsonParams.put("user_id", userCredential.getString("user_id", ""));
        jsonParams.put("branch_id", userCredential.getString("branch_id", ""));


        final StringRequest request = new StringRequest(Request.Method.POST,
                URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Get the server Response

                        //Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();
                        //Log.i("Response", response.toString());

                        Intent screen = new Intent(CustomerDateForm.this,ThanxGiving.class);
                        startActivity(screen);

                    }
                }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    switch (response.statusCode) {
                        case 201:
                            Toast.makeText(getApplicationContext(), "Successfulll", Toast.LENGTH_LONG).show();
                            break;
                    }
                    //Additional cases
                }

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), "No connection", Toast.LENGTH_LONG).show();

                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return jsonParams;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> pars = new HashMap<String, String>();
                pars.put("Content-Type", "application/x-www-form-urlencoded");
                return pars;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);
            }
        };
        requestQueue.add(request);


    }

    public String getUrl() {
        String URL = "";
        SharedPreferences accessTokenData = PreferenceManager.getDefaultSharedPreferences(this);
        String accessToken = accessTokenData.getString("access_token", "");

        URL = URL_FEEDBACK + "?token=".trim() + accessToken;

        return URL.trim();
    }

    public void checkForUserData() {

        if (name.isEmpty() == true) {
            n = 1;
            layoutName.setVisibility(View.VISIBLE);
            userNameView.setVisibility(View.GONE);
            flagName = true;

        } else {
            layoutName.setVisibility(View.GONE);
            userNameView.setVisibility(View.VISIBLE);
            userNameView.setText(name);
            flagName = false;
        }

       // Toast.makeText(this,"DOB = "+dob,Toast.LENGTH_SHORT).show();

        if (dob.trim().isEmpty() == true || dob.equalsIgnoreCase("0000-00-00 00:00:00") == true) {
            d = 1;
            layoutDOB.setVisibility(View.VISIBLE);
            flagDob = true;
        } else {
            layoutDOB.setVisibility(View.GONE);
            flagDob = false;
        }
      //  Toast.makeText(this,"ANni = "+anniversary,Toast.LENGTH_SHORT).show();

        if (anniversary.trim().isEmpty() == true || anniversary.equalsIgnoreCase("0000-00-00 00:00:00") == true) {
            a = 1;
            layoutAnniversary.setVisibility(View.VISIBLE);
            flagAnniversary = true;
        } else {
            layoutAnniversary.setVisibility(View.GONE);
            flagAnniversary = false;
        }


        if (n == 0 && d == 0 && a == 0) {
            //filled form
            layoutForm.setVisibility(View.GONE);

            userNameView.setVisibility(View.VISIBLE);
            userNameView.setText(name);

            messageDeals.setVisibility(View.GONE);
            messageFeedback.setVisibility(View.VISIBLE);

            submitDone.setVisibility(View.VISIBLE);
            submit.setVisibility(View.GONE);

            layoutUpper.setGravity(RelativeLayout.CENTER_IN_PARENT);



        } else {
            //atleast one field is empty
            layoutForm.setVisibility(View.VISIBLE);

            messageDeals.setVisibility(View.VISIBLE);
            messageFeedback.setVisibility(View.GONE);

            submitDone.setVisibility(View.GONE);


        }
    }


}