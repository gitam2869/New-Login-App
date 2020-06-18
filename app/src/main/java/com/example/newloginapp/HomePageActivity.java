package com.example.newloginapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;

public class HomePageActivity extends AppCompatActivity
{
    private TextView textViewUserName;
    private TextView textViewUserEmail;
    private TextView textViewUserPhoneNumber;

    private MaterialCardView materialCardViewSignOut;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        textViewUserName = findViewById(R.id.idTextViewUserNameAccountActivity);
        textViewUserEmail = findViewById(R.id.idTextViewUserEmailAccountActivity);
        textViewUserPhoneNumber = findViewById(R.id.idTextViewUserPhoneNumberAccountActivity);
        materialCardViewSignOut = findViewById(R.id.idCardviewSignOutAccountActivity);

        textViewUserName.setText(SharedPreferenceManager.getInstance(this).getUserName());
        textViewUserEmail.setText(SharedPreferenceManager.getInstance(this).getUserEmail());
        textViewUserPhoneNumber.setText(SharedPreferenceManager.getInstance(this).getUserMobileNumber());
        materialCardViewSignOut.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                signOutUser();
            }
        });
    }

    private void signOutUser()
    {
// Create the object of
        // AlertDialog Builder class
        AlertDialog.Builder builder
                = new AlertDialog
                .Builder(HomePageActivity.this);

        // Set the message show for the Alert time
        builder.setMessage("Are you sure to Sign Out?");

        // Set Alert Title
        builder.setTitle("Alert !");

        // Set Cancelable false
        // for when the user clicks on the outside
        // the Dialog Box then it will remain show
        builder.setCancelable(true);

        // Set the positive button with yes name
        // OnClickListener method is use of
        // DialogInterface interface.

        builder
                .setPositiveButton(
                        "Yes",
                        new DialogInterface
                                .OnClickListener()
                        {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which)
                            {

                                // When the user click yes button
                                // then app will close
//                                finish();
                                SharedPreferenceManager.getInstance(getApplicationContext()).logout();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            }
                        });

        // Set the Negative button with No name
        // OnClickListener method is use
        // of DialogInterface interface.
        builder
                .setNegativeButton(
                        "No",
                        new DialogInterface
                                .OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which)
                            {

                                // If user click no
                                // then dialog box is canceled.
                                dialog.dismiss();
                            }
                        });

        // Create the Alert dialog
        AlertDialog alertDialog = builder.create();

        // Show the Alert Dialog box
        alertDialog.show();
    }
}