package com.example.newloginapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
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

public class ForgotPasswordActivity extends AppCompatActivity
{

    private TextView textViewEnterMessage;
    private TextView textViewSendMessage;
    private TextInputLayout textInputLayoutEmail;
    private MaterialButton materialButtonSendLink;
    private MaterialButton materialButtonBackToLogin;
    private TextView textViewCheckEmail;

    private ProgressDialog progressDialog;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        getSupportActionBar().hide();


        textViewEnterMessage = findViewById(R.id.idTextViewEnterMessageForgotPasswordActivity);
        textViewSendMessage = findViewById(R.id.idTextViewSendMessageForgotPasswordActivity);
        textInputLayoutEmail = findViewById(R.id.idTextInputLayoutEmailForgotPasswordActivity);
        materialButtonSendLink = findViewById(R.id.idButtonSendLinkForgotPasswordActivity);
        materialButtonBackToLogin = findViewById(R.id.idButtonBackToLoginForgotPasswordActivity);
        textViewCheckEmail = findViewById(R.id.idTextViewCheckEmailForgotPasswordActivity);

        String email = getIntent().getStringExtra("email");
        textInputLayoutEmail.getEditText().setText(email);

        textInputLayoutEmail.getEditText().setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                textInputLayoutEmail.setErrorEnabled(false);
                return false;
            }
        });

        materialButtonSendLink.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                sendResetPasswordLink();
            }
        });

        materialButtonBackToLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                MainActivity.fa.finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });

        textViewCheckEmail.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                startActivity(intent);
            }
        });

        progressDialog = new ProgressDialog(this);
    }

    private void sendResetPasswordLink()
    {
        final String email = textInputLayoutEmail.getEditText().getText().toString().trim();

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
            return;
        }

        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_SEND_EMAIL_RESET_PASSWORD_LINK,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        try
                        {
                            progressDialog.dismiss();
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);

                            if(!jsonObject.getBoolean("error"))
                            {
//                                Toast toast = Toast.makeText(getApplicationContext(),
//                                        jsonObject.getString("message"),
//                                        Toast.LENGTH_LONG);
//                                toast.setGravity(Gravity.CENTER,0,0);
//                                toast.show();

                                textViewEnterMessage.setVisibility(View.GONE);
                                textInputLayoutEmail.setVisibility(View.GONE);
                                materialButtonSendLink.setVisibility(View.GONE);

                                textViewSendMessage.setVisibility(View.VISIBLE);
                                materialButtonBackToLogin.setVisibility(View.VISIBLE);
                                textViewCheckEmail.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        jsonObject.getString("message"),
                                        Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER,0,0);
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
//                        Toast.makeText(
//                                getApplicationContext(),
//                                error.getMessage(),
//                                Toast.LENGTH_LONG
//                        ).show();
                    }
                }
        )
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<>();
                params.put("email",email);
                return params;
            }
        };

        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    }

    public static boolean isValidEmail(CharSequence target)
    {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

}
