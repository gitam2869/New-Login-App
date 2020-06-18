package com.example.newloginapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alimuzaffar.lib.pin.PinEntryEditText;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EmailVerificationActivity extends AppCompatActivity
{

    private PinEntryEditText pinEntryEditTextEmail;
    private MaterialButton materialButtonBackToHome;
    private TextView textViewCheckEmail;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        getSupportActionBar().hide();

        MainActivity.fa.finish();

        pinEntryEditTextEmail = findViewById(R.id.idPinEntryEditTextEmail);
        materialButtonBackToHome = findViewById(R.id.idButtonBackToHomeEmailVerificationActivity);
        textViewCheckEmail = findViewById(R.id.idTextViewCheckEmailEmailVerificationActivity);

        textViewCheckEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                startActivity(intent);
            }
        });

        materialButtonBackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferenceManager.getInstance(getApplicationContext()).logout();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });


        progressDialog = new ProgressDialog(this);

        verifyEmail();

    }

    private void verifyEmail()
    {
        if (pinEntryEditTextEmail != null)
        {
            pinEntryEditTextEmail.setOnPinEnteredListener(new PinEntryEditText.OnPinEnteredListener()
            {
                @Override
                public void onPinEntered(CharSequence str)
                {
                    checkOtp(str);
                }
            });
        }
    }

    private void checkOtp(final CharSequence otp)
    {
        final String email = SharedPreferenceManager.getInstance(this).getUserEmail();
        final String otp1 = String.valueOf(otp);

        Log.d("TAG", "checkOtp: " + email + "    " + otp1);

        progressDialog.setMessage("Verifying OTP...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_VERIFY_EMAIL_OTP,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (!jsonObject.getBoolean("error"))
                            {
//                                insertUserData();
                                showAlertDialogOTP();
                            }
                            else
                            {
                                Toast toast = Toast.makeText(
                                        getApplicationContext(),
                                        "WRONG OTP, TRY AGAIN...",
                                        Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                pinEntryEditTextEmail.setText(null);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("otp", otp1);
                return params;
            }
        };

        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    }


    private void showAlertDialogOTP() {

        // Create the object of
        // AlertDialog Builder class
        AlertDialog.Builder builder
                = new AlertDialog
                .Builder(EmailVerificationActivity.this);

        // Set the message show for the Alert time
        builder.setIcon(R.drawable.ic_emailverification);
        builder.setMessage("VERIFIED SUCCESSFULLY");

        // Set Alert Title
        builder.setTitle("EMAIL ID");

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
                                                int which) {

                                // When the user click yes button
                                // then app will close
                                finish();
                                startActivity(new Intent(getApplicationContext(), CreatePasswordActivity.class));

                            }
                        });


        // Create the Alert dialog
        AlertDialog alertDialog = builder.create();

        // Show the Alert Dialog box
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        moveTaskToBack(true);

    }
}