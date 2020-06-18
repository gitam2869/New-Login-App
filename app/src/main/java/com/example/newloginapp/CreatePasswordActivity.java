package com.example.newloginapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
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

public class CreatePasswordActivity extends AppCompatActivity
{
    private TextInputLayout textInputLayoutPassword;
    private TextInputLayout textInputLayoutConfirmPassword;
    private MaterialButton materialButtonSubmit;

    private ProgressDialog progressDialog;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_password);

        getSupportActionBar().hide();

        textInputLayoutPassword = findViewById(R.id.idTextInputLayoutPasswordCreatePasswordActivity);
        textInputLayoutConfirmPassword = findViewById(R.id.idTextInputLayoutConfirmPasswordCreatePasswordActivity);
        materialButtonSubmit = findViewById(R.id.idButtonSubmitCreatePasswordActivity);

        textInputLayoutPassword.getEditText().setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                textInputLayoutPassword.setErrorEnabled(false);
                return false;
            }
        });

        textInputLayoutConfirmPassword.getEditText().setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                textInputLayoutConfirmPassword.setErrorEnabled(false);
                return false;
            }
        });

        materialButtonSubmit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                insertUserData();
            }
        });

        progressDialog = new ProgressDialog(this);

        SharedPreferenceManager.getInstance(getApplicationContext())
                .userSetPassword("yes");
    }

    private void insertUserData()
    {
        textInputLayoutPassword.setErrorEnabled(false);
        textInputLayoutConfirmPassword.setErrorEnabled(false);

        final String password = textInputLayoutPassword.getEditText().getText().toString().trim();
        final String confirmPassword = textInputLayoutConfirmPassword.getEditText().getText().toString().trim();

        final String name = SharedPreferenceManager.getInstance(this).getUserName();
        final String mobile = SharedPreferenceManager.getInstance(this).getUserMobileNumber();
        final String email = SharedPreferenceManager.getInstance(this).getUserEmail();

        if(password.length() == 0)
        {
            textInputLayoutPassword.setError("Password not blank");
            textInputLayoutPassword.requestFocus();
            textInputLayoutPassword.setErrorEnabled(false);
            return;

        }

        if(password.length() < 8)
        {
            textInputLayoutPassword.setError("Enter Atleast 8 Characters");
            textInputLayoutPassword.requestFocus();
            return;
        }

        if(!password.equals(confirmPassword))
        {
            textInputLayoutConfirmPassword.setError("Password not match");
            textInputLayoutConfirmPassword.requestFocus();

            return;
        }

        textInputLayoutPassword.setHelperTextEnabled(false);
        textInputLayoutConfirmPassword.setHelperTextEnabled(false);

        progressDialog.setMessage("Please Wait...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_REGISTER_USER,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        progressDialog.dismiss();

                        try
                        {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);

                            if(!jsonObject.getBoolean("error"))
                            {
                                SharedPreferenceManager.getInstance(getApplicationContext())
                                        .userID(jsonObject.getString("id"));

                                SharedPreferenceManager.getInstance(getApplicationContext())
                                        .userLogin("yes");


                                finish();
                                startActivity(new Intent(getApplicationContext(), HomePageActivity.class));
                            }
                            else
                            {
                                Toast toast = Toast.makeText(
                                        getApplicationContext(),
                                        "PLEASE, TRY AGAIN...",
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
        )
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("mobile", mobile);
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };

        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    }
}