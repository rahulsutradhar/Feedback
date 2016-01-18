package com.emplaceme.feedback.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.emplaceme.feedback.Adapter.CustomSpinnerAdapter;
import com.emplaceme.feedback.Network.VolleySingleton;
import com.emplaceme.feedback.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.emplaceme.feedback.Extras.Keys.EndpointsOutlet.KEY_ID;
import static com.emplaceme.feedback.Extras.Keys.EndpointsOutlet.KEY_USER_ANNIVERSARY;
import static com.emplaceme.feedback.Extras.Keys.EndpointsOutlet.KEY_USER_COUNTRY_ID;
import static com.emplaceme.feedback.Extras.Keys.EndpointsOutlet.KEY_USER_DOB;
import static com.emplaceme.feedback.Extras.Keys.EndpointsOutlet.KEY_USER_EMAIL;
import static com.emplaceme.feedback.Extras.Keys.EndpointsOutlet.KEY_USER_NAME;
import static com.emplaceme.feedback.Extras.Keys.EndpointsOutlet.KEY_USER_PHONE;
import static com.emplaceme.feedback.Extras.LinkDetails.URL.URL_EXISTING_USER;
import static com.emplaceme.feedback.Extras.Keys.EndpointsOutlet.KEY_DATA;

public class CustomerLogin extends AppCompatActivity implements TextWatcher {

    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    Map<String, String> params = new HashMap<String, String>();

    private Button continueButton;
    private EditText inputPhone;
    private EditText inputEmail;
    private TextInputLayout inputPhoneLayout;
    private TextInputLayout inputEmailLayout;
    private Spinner spinner;
    private TextView outletNameView;

    float amount;
    int numberPeople;
    String countryCode = "";
    String phoneNumber = "";
    String outletName = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login);

        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences userData = PreferenceManager.getDefaultSharedPreferences(this);
        outletName = userData.getString("outlet_name", "");

        //get data from intent
        Intent i = getIntent();
        amount = i.getFloatExtra("amount_bill", 0.0f);
        numberPeople = i.getIntExtra("number_of_people", 0);

        volleySingleton = VolleySingleton.getsInstance();
        requestQueue = volleySingleton.getRequestQueue();


        inputPhone = (EditText) findViewById(R.id.input_phone);
        inputEmail = (EditText) findViewById(R.id.input_email);

        outletNameView = (TextView)findViewById(R.id.outletName);
        inputPhoneLayout = (TextInputLayout) findViewById(R.id.input_layout_phone);
        inputEmailLayout = (TextInputLayout) findViewById(R.id.input_layout_email);

        continueButton = (Button) findViewById(R.id.continueButton);
        spinner = (Spinner) findViewById(R.id.countryCode);


        inputPhone.addTextChangedListener(this);

        if(outletName.isEmpty() == false)
        {
            outletNameView.setText(outletName);
        }else {
            outletNameView.setText("");
        }

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //now check the digits in phone number and email validation then proces
                if(checkValidationOfInputData() == true)
                {
                    //open the feedback form
                    Intent screen = new Intent(CustomerLogin.this, CustomerFeedbackForm.class);
                    screen.putExtra("amount_bill", amount);
                    screen.putExtra("number_of_people", numberPeople);
                    startActivity(screen);


                }

            }
        });

        addDataToSpinner();


    }


    public void addDataToSpinner() {

        final ArrayList<String> list = new ArrayList<String>();
        list.add("+91");
        list.add("+44");
        list.add("+61");
        list.add("+64");
        list.add("+1");

        // Custom ArrayAdapter with spinner item layout to set popup background

        CustomSpinnerAdapter spinAdapter = new CustomSpinnerAdapter(
                getApplicationContext(), list);

        spinner.setAdapter(spinAdapter);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                countryCode = list.get(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });

    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

        if (s.length() < 10) {
            //error message to give 10 digit phone number
            inputPhoneLayout.setError("Enter 10 digit mobile number");
            inputPhoneLayout.setErrorEnabled(true);
        } else {
            //request server
            inputPhoneLayout.setErrorEnabled(false);

            callServerValidateUser();
        }

    }

    public void callServerValidateUser() {
        String URL;
        phoneNumber = inputPhone.getText().toString().trim();
        // phoneNumber = countryCode.trim() + phoneNumber;

        URL = getUrl(phoneNumber);

        //get request to the server
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                URL,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        //parse he user JSON data
                        parseUserJsonData(response);

                    }
                }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                NetworkResponse response = error.networkResponse;
                if(response != null && response.data != null){
                    switch(response.statusCode){
                        case 404:
                            //put user id = 0
                            //save data in shared preferance
                            SharedPreferences userCredential = PreferenceManager.getDefaultSharedPreferences(CustomerLogin.this);
                            SharedPreferences.Editor editor = userCredential.edit();
                            editor.putString("user_id","0");
                            editor.putString("user_name","");
                            editor.putString("user_email", "");
                            editor.putString("country_id","");
                            editor.putString("user_phone",phoneNumber);
                            editor.putString("user_dob","");
                            editor.putString("user_anniversary","");
                            editor.apply();

                            break;
                    }
                    //Additional cases
                }

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    //Toast.makeText(getApplicationContext(),"Invalid user Id", Toast.LENGTH_LONG).show();

                }
            }
        });

        requestQueue.add(request);

    }

    public String getUrl(String phone) {
        String URL = "";
        SharedPreferences accessTokenData = PreferenceManager.getDefaultSharedPreferences(this);
        String accessToken = accessTokenData.getString("access_token", "");

        URL = URL_EXISTING_USER + "?token=".trim() + accessToken +
                "&phone=".trim() + phone;

        return URL.trim();
    }

    public void parseUserJsonData(JSONObject response) {

        String userId = "";
        String userName = "";
        String userEmail = "";
        String countryId = "";
        String userPhone = "";
        String userDob = "";
        String userAnniversary = "";


        if (response != null && response.length() > 0) {
            try {

                if (response.has(KEY_DATA) && !response.isNull(KEY_DATA)) {
                    JSONArray jsonArray = response.getJSONArray(KEY_DATA);

                    for (int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        //user id
                        if(jsonObject.has(KEY_ID) && !jsonObject.isNull(KEY_ID))
                        {
                            userId = jsonObject.getString(KEY_ID);
                        }

                        //user name
                        if (jsonObject.has(KEY_USER_NAME) && !jsonObject.isNull(KEY_USER_NAME))
                        {
                            userName = jsonObject.getString(KEY_USER_NAME);
                        }

                        //email id
                        if (jsonObject.has(KEY_USER_EMAIL) && !jsonObject.isNull(KEY_USER_EMAIL))
                        {
                            userEmail = jsonObject.getString(KEY_USER_EMAIL);
                        }

                        //country id
                        if (jsonObject.has(KEY_USER_COUNTRY_ID) && !jsonObject.isNull(KEY_USER_COUNTRY_ID))
                        {
                            countryId = jsonObject.getString(KEY_USER_COUNTRY_ID);
                        }

                        //phone
                        if(jsonObject.has(KEY_USER_PHONE) && !jsonObject.isNull(KEY_USER_PHONE))
                        {
                            userPhone = jsonObject.getString(KEY_USER_PHONE);
                        }

                        //dob
                        if (jsonObject.has(KEY_USER_DOB) && !jsonObject.isNull(KEY_USER_DOB))
                        {
                            userDob = jsonObject.getString(KEY_USER_DOB);
                        }

                        //Anniversary
                        if (jsonObject.has(KEY_USER_ANNIVERSARY) && !jsonObject.isNull(KEY_USER_ANNIVERSARY))
                        {
                            userAnniversary = jsonObject.getString(KEY_USER_ANNIVERSARY);
                        }


                    }

                }

                //if email exist then display in the edit text box
                if(userEmail.isEmpty() == false)
                {
                    inputEmail.setText("");
                    inputEmail.setText(userEmail);
                    inputEmailLayout.setErrorEnabled(false);

                    //hide keyboard
                    if(inputEmail.isFocused() == true || inputPhone.isFocused())
                    {
                        //hide the keyboard
                        //((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(inputEmail.getWindowToken(), 0);
                        ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
                                .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);

                    }
                }

                //save data in shared preferance
                SharedPreferences userCredential = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = userCredential.edit();
                editor.putString("user_id",userId);
                editor.putString("user_name",userName);
                editor.putString("user_email", userEmail);
                editor.putString("country_id",countryId);
                editor.putString("user_phone",userPhone);
                editor.putString("user_dob",userDob);
                editor.putString("user_anniversary",userAnniversary);
                editor.apply();




            } catch (JSONException e) {

            }


        } else {
            Toast.makeText(getApplicationContext(), "No data found", Toast.LENGTH_LONG).show();
        }
    }

    public boolean checkValidationOfInputData()
    {
        int e = 0;
        int p = 0;

        String phone = "";
        String email = "";

        phone = inputPhone.getText().toString();
        email = inputEmail.getText().toString();

        if(phone.length()<10)
        {
            inputPhoneLayout.setErrorEnabled(true);
            inputPhoneLayout.setError("Enter 10 digit mobile number");
            p = 1;
        }
        else {
            inputPhoneLayout.setErrorEnabled(false);
            p = 0;
        }

        if (checkEmailValidation(email) == false)
        {
            inputEmailLayout.setErrorEnabled(true);
            inputEmailLayout.setError("Invalid email id");
            e = 1;
        }else {
            e = 0;
            inputEmailLayout.setErrorEnabled(false);
        }

        if(p == 0 && e == 0)
        {
            return true;
        }else {
            return false;
        }

    }

    public boolean checkEmailValidation(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();

    }


}
