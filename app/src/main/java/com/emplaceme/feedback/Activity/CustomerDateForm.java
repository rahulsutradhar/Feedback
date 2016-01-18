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
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
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
    static final int DATE_DIALOG_ID_DOB = 0;
    static final int DATE_DIALOG_ID_ANNIVERSARY = 1;

    private TextInputLayout textInputLayoutName;
    private TextInputLayout textInputLayoutDOB;
    private TextInputLayout textInputLayoutAnniversary;
    private RelativeLayout layoutDOB;
    private RelativeLayout layoutAnniversary;

    private int mDay, mYear, mMonth;

    float amount;
    int numberPeople;
    int food = 0;
    int ambience = 0;
    int service = 0;

    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    JSONObject jsonParams = null;
    JSONArray jsonArray = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_date_form);

        inputName = (EditText) findViewById(R.id.input_name);
        inputDob = (EditText) findViewById(R.id.input_dob);
        inputAnniversary = (EditText) findViewById(R.id.input_anniversary);
        submit = (Button) findViewById(R.id.submitButton);
        textInputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        textInputLayoutDOB = (TextInputLayout) findViewById(R.id.input_layout_dob);
        textInputLayoutAnniversary = (TextInputLayout) findViewById(R.id.input_layout_anniversary);

        layoutDOB = (RelativeLayout) findViewById(R.id.layoutFormTwo);
        layoutAnniversary = (RelativeLayout) findViewById(R.id.layoutFormThree);

        //get data from intent
        Intent i = getIntent();
        amount = i.getFloatExtra("amount_bill", 0.0f);
        numberPeople = i.getIntExtra("number_of_people", 0);
        food = i.getIntExtra("food_feedback",0);
        ambience = i.getIntExtra("ambience_feedback",0);
        service = i.getIntExtra("service_feedback",0);


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
                callServerToSendFeedbackData(getUrl());
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
                    String s = String.valueOf(dayOfMonth) + "/" + String.valueOf(monthOfYear + 1) +
                            "/" + String.valueOf(year);
                    inputDob.setText(s);

                }
            };

    private DatePickerDialog.OnDateSetListener mDateSetListenerAnniversary =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {
                    String s = String.valueOf(dayOfMonth) + "/" + String.valueOf(monthOfYear + 1) +
                            "/" + String.valueOf(year);
                    inputAnniversary.setText(s);

                }
            };


    public void callServerToSendFeedbackData(String URL) {

        try {
                jsonParams = new JSONObject();
                jsonArray = new JSONArray();

            //save data in shared preferance
            SharedPreferences userCredential = PreferenceManager.getDefaultSharedPreferences(this);

            jsonParams.put("bill_amount", String.valueOf(amount));
            jsonParams.put("no_of_people", String.valueOf(numberPeople));
            jsonParams.put("phone_no", userCredential.getString("user_phone", ""));
            jsonParams.put("email", userCredential.getString("user_email", ""));
            jsonParams.put("email", userCredential.getString("user_email", ""));
            jsonParams.put("country_id", userCredential.getString("outlet_company_id", ""));
            jsonParams.put("food", food);
            jsonParams.put("ambiance",ambience);
            jsonParams.put("service", service);
            jsonParams.put("name", userCredential.getString("user_name", ""));
            jsonParams.put("dob", userCredential.getString("user_dob", ""));
            jsonParams.put("anniversary", userCredential.getString("user_anniversary", ""));
            jsonParams.put("user_id", userCredential.getString("user_id", ""));
            jsonParams.put("branch_id", userCredential.getString("outlet_branch_id", ""));

            jsonArray.put(jsonParams);


        }catch (JSONException e)
        {

        }

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST,
                URL,
                jsonArray,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Get the server Response

                        Toast.makeText(getApplicationContext(), "Successfully done", Toast.LENGTH_LONG).show();
                    }
                }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    switch (response.statusCode) {
                        case 200:
                            Toast.makeText(getApplicationContext(), "Successfulll", Toast.LENGTH_LONG).show();
                            break;
                    }
                    //Additional cases
                }

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    //Toast.makeText(getApplicationContext(),"Invalid user Id", Toast.LENGTH_LONG).show();

                }
            }
        })
        ;
        requestQueue.add(request);

/*
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
            return jsonParams;
        }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> pars = new HashMap<String, String>();
            pars.put("Content-Type", "application/jsonArray");
            return pars;
        }


        }
        */

    }

    public String getUrl() {
        String URL = "";
        SharedPreferences accessTokenData = PreferenceManager.getDefaultSharedPreferences(this);
        String accessToken = accessTokenData.getString("access_token", "");

        URL = URL_FEEDBACK + "?token=".trim() + accessToken;

        return URL.trim();
    }

}
