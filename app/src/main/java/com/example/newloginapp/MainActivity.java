package com.example.newloginapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
{
    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;
    private TextView textViewForgotPassword;
    private MaterialButton materialButtonLogin;
    private TextView textViewCreateAnAccount;

    private ProgressDialog progressDialog;

    public Toast toast;

    public static Activity fa;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        fa = this;

        NetworkConfiguration networkConfiguration = new NetworkConfiguration(this);

        if(!networkConfiguration.isConnected())
        {
            setContentView(R.layout.main_no_internet);
            getSupportActionBar().hide();
            alertMessage();
            return;
        }

        if(SharedPreferenceManager.getInstance(this).isLoggedIn())
        {
            startActivity(new Intent(this, HomePageActivity.class));
            finish();
            return;
        }

        if(SharedPreferenceManager.getInstance(this).getIsUserSetPassword() != null)
        {
            startActivity(new Intent(this, CreatePasswordActivity.class));
            finish();
            return;
        }

        if(SharedPreferenceManager.getInstance(this).getisEmailSend() != null)
        {
            startActivity(new Intent(this, EmailVerificationActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        textInputLayoutEmail = findViewById(R.id.idTextInputLayoutEmailLoginActivity);
        textInputLayoutPassword = findViewById(R.id.idTextInputLayoutPasswordLoginActivity);
        textViewForgotPassword = findViewById(R.id.idTextViewForgotPasswordLoginActivity);
        materialButtonLogin = findViewById(R.id.idButtonLoginLoginActivity);
        textViewCreateAnAccount = findViewById(R.id.idTextViewCreateAnAccountLoginActivity);

        textInputLayoutEmail.getEditText().setOnTouchListener(new View.OnTouchListener()
        {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                textInputLayoutEmail.setErrorEnabled(false);
                return false;
            }
        });

        textInputLayoutPassword.getEditText().setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                textInputLayoutPassword.setErrorEnabled(false);
                return false;
            }
        });

        textViewForgotPassword.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String email = textInputLayoutEmail.getEditText().getText().toString().trim();
                Intent intent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);
                toast.cancel();
            }
        });

        materialButtonLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                userLogin();
            }
        });

        textViewCreateAnAccount.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
//                finish();
                startActivity(new Intent(getApplicationContext(), NewAccountActivity.class));
            }
        });

        progressDialog = new ProgressDialog(this);
        toast = new Toast(this);
    }

    private void userLogin()
    {
        textInputLayoutEmail.setErrorEnabled(false);
        textInputLayoutPassword.setErrorEnabled(false);

        final String email = textInputLayoutEmail.getEditText().getText().toString().trim();
        final String password = textInputLayoutPassword.getEditText().getText().toString().trim();

        Log.d("TAG", "userLogin: "+email.length());

        if(email.length() == 0)
        {
            textInputLayoutEmail.setError("Email ID not blank");
            textInputLayoutEmail.requestFocus();
            textInputLayoutEmail.setErrorEnabled(false);
            return;
        }

        if(password.length() == 0)
        {
            textInputLayoutPassword.setError("Password not blank");
            textInputLayoutPassword.requestFocus();
            textInputLayoutPassword.setErrorEnabled(false);
            return;
        }


        progressDialog.setMessage("Please Wait...");
        progressDialog.show();


        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_LOGIN_USER,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        progressDialog.dismiss();
                        Log.d("TAG", "onResponse: 1");
                        try
                        {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            Log.d("TAG", "onResponse: 2");

                            if(!jsonObject.getBoolean("error"))
                            {
                                Log.d("TAG", "onResponse: 3");

                                SharedPreferenceManager.getInstance(getApplicationContext())
                                        .userInfo(
                                                jsonObject.getString("name"),
                                                jsonObject.getString("mobile"),
                                                jsonObject.getString("email")
                                        );

                                SharedPreferenceManager.getInstance(getApplicationContext())
                                        .userID(jsonObject.getString("id"));
                                Log.d("TAG", "onResponse: 4");

                                SharedPreferenceManager.getInstance(getApplicationContext())
                                        .userLogin("yes");

                                startActivity(new Intent(getApplicationContext(), HomePageActivity.class));
                                finish();

                            }
                            else
                            {
                                Log.d("TAG", "onResponse: 5");

                                toast = Toast.makeText(
                                        getApplicationContext(),
                                        jsonObject.getString("message"),
                                        Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                Log.d("TAG", "onResponse: 6");
                                progressDialog.dismiss();
                            }
                        }
                        catch (JSONException e)
                        {
                            progressDialog.dismiss();

                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Log.d("TAG", "onErrorResponse: "+error);
                        progressDialog.dismiss();
                    }
                }
        )
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);

                return params;
            }
        };

        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    public void alertMessage()
    {

        // Create the object of
        // AlertDialog Builder class
        AlertDialog.Builder builder
                = new AlertDialog
                .Builder(MainActivity.this);

        // Set the message show for the Alert time
        builder.setMessage("Please connect with to working internet connection");

        // Set Alert Title
        builder.setTitle("Network Error!");

        // Set Cancelable false// Set the Negative button with No name
        // OnClickListener method is use
        // of DialogInterface interface.

        // for when the user clicks on the outside
        // the Dialog Box then it will remain show
        builder.setCancelable(false);

        // Set the positive button with yes name
        // OnClickListener method is use of
        // DialogInterface interface.

        builder
                .setPositiveButton(
                        "Ok",
                        new DialogInterface
                                .OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which)
                            {

                                // When the user click yes button
                                // then app will close
                                finish();
                                startActivity(getIntent());
                            }
                        });


        // Create the Alert dialog
        AlertDialog alertDialog = builder.create();

        // Show the Alert Dialog box
        alertDialog.show();
    }
}