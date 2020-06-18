package com.example.newloginapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NewAccountActivity extends AppCompatActivity
{
    private TextInputLayout textInputLayoutName;
    private TextInputLayout textInputLayoutMobile;
    private TextInputLayout textInputLayoutEmail;
    private MaterialButton materialButtonSubmit;
    private TextView textViewGoToLogin;

    private boolean isCrateUser = true;

    private ProgressDialog progressDialog;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);

        getSupportActionBar().hide();

        textInputLayoutName = findViewById(R.id.idTextInputLayoutNameNewAccountActivity);
        textInputLayoutMobile = findViewById(R.id.idTextInputLayoutMobileNewAccountActivity);
        textInputLayoutEmail = findViewById(R.id.idTextInputLayoutEmailNewAccountActivity);
        materialButtonSubmit = findViewById(R.id.idButtonSubmitNewAccountActivity);
        textViewGoToLogin = findViewById(R.id.idTextViewGoToLoginNewAccountActivity);

        progressDialog = new ProgressDialog(this);


        textInputLayoutName.getEditText().setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                textInputLayoutName.setErrorEnabled(false);
                return false;
            }
        });

        textInputLayoutMobile.getEditText().setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                textInputLayoutMobile.setErrorEnabled(false);
                return false;
            }
        });

        textInputLayoutEmail.getEditText().setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                textInputLayoutEmail.setErrorEnabled(false);
                return false;
            }
        });



        materialButtonSubmit.setOnClickListener(new View.OnClickListener()
        {
            @RequiresApi(api = Build.VERSION_CODES.N_MR1)
            @Override
            public void onClick(View v)
            {
                if(isCrateUser)
                {
                    creatUser();
                }

            }
        });

        textViewGoToLogin.setOnClickListener(new View.OnClickListener()
        {
            @RequiresApi(api = Build.VERSION_CODES.N_MR1)
            @Override
            public void onClick(View v)
            {
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));

            }
        });

        progressDialog = new ProgressDialog(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    private void creatUser()
    {
        textInputLayoutName.setErrorEnabled(false);
        textInputLayoutMobile.setErrorEnabled(false);
        textInputLayoutEmail.setErrorEnabled(false);

        final String name = textInputLayoutName.getEditText().getText().toString().trim();
        final String mobile = textInputLayoutMobile.getEditText().getText().toString().trim();
        final String email = textInputLayoutEmail.getEditText().getText().toString().trim();

        if(name.length() == 0)
        {
            textInputLayoutName.setError("Name not blank");
            textInputLayoutName.requestFocus();
            textInputLayoutName.setErrorEnabled(false);
            return;
        }
        if(!name.matches("[a-zA-Z][a-zA-Z ]*"))
        {
            textInputLayoutName.setError("Name not contains other than characters");
            textInputLayoutName.requestFocus();
            return;
        }

        if(mobile.length() == 0)
        {
            textInputLayoutMobile.setError("Mobile number not blank");
            textInputLayoutMobile.requestFocus();
            textInputLayoutMobile.setErrorEnabled(false);
            return;
        }
        if(mobile.length() < 10 || mobile.length() > 10)
        {
            textInputLayoutMobile.setError("Mobile number must be 10 digit");
            textInputLayoutMobile.requestFocus();
            return;
        }

        if(email.length() == 0)
        {
            textInputLayoutEmail.setError("Email ID not blank");
            textInputLayoutEmail.requestFocus();
            textInputLayoutEmail.setErrorEnabled(false);
            return;
        }
        if(!isValidEmail(email))
        {
            textInputLayoutEmail.setError("Enter Valid Email ID");
            textInputLayoutEmail.requestFocus();
            return;
        }

        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String finalURL = Constants.URL_VALIDITY_USER+"?name="+name+"&mobile="+mobile+"&email="+email;

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                finalURL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        try
                        {
                            JSONObject jsonObject = new JSONObject(response);

                            if(!jsonObject.getBoolean("error"))
                            {
                                SharedPreferenceManager.getInstance(getApplicationContext())
                                        .userInfo(name, mobile, email);

                                if(isCrateUser)
                                {
                                    isCrateUser = false;
                                    sendEmailOTP();
                                }

                            }
                            else
                            {
                                progressDialog.dismiss();
                                Toast toast = Toast.makeText(
                                        getApplicationContext(),
                                        jsonObject.getString("message"),
                                        Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }


                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        progressDialog.dismiss();
                    }
                }
        );

//        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        if(isCrateUser)
        {
            RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
        }

    }

    public static boolean isValidEmail(CharSequence target)
    {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }


    private void sendEmailOTP()
    {
        final String email = SharedPreferenceManager.getInstance(this).getUserEmail();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_SEND_EMAIL_OTP,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        try
                        {
                            JSONObject jsonObject = new JSONObject(response);

                            if(!jsonObject.getBoolean("error"))
                            {
                                SharedPreferenceManager.getInstance(getApplicationContext())
                                        .userSendEmail("yes");

                                finish();
                                startActivity(new Intent(getApplicationContext(), EmailVerificationActivity.class));
                            }
                            else
                            {
                                Toast toast = Toast.makeText(
                                        getApplicationContext(),
                                        "Pleas Try Again..",
                                        Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                            progressDialog.dismiss();
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
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
                return params;
            }
        };

        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }
}