package com.example.newloginapp;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkConfiguration
{
    private static Context context;

    NetworkConfiguration(Context context)
    {
        this.context = context;
    }

    public boolean isConnected()
    {
        boolean connected = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
        {
            //we are connected to a network
            connected = true;
        }
        else
        {
            connected = false;
        }

        return connected;
    }

    public void alertMessage(final String s)
    {

        // Create the object of
        // AlertDialog Builder class
        AlertDialog.Builder builder
                = new AlertDialog
                .Builder(context);

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
//                                finish();

                                try {
                                    context.startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
//                                    Intent intent = new Intent(Intent.ACTION_MAIN);
//                                    intent.setClassName("com.android.phone", "com.android.phone.NetworkSetting");
//                                    context.startActivity(intent);

//                                    Intent intent = new Intent(Intent.ACTION_MAIN);
//                                    intent.addCategory(WifiNetworkSpecifier.CONTENTS_FILE_DESCRIPTOR);
//                                    context.startActivity(intent);
                                }
                                catch (ActivityNotFoundException e)
                                {
                                    e.printStackTrace();
                                }

//                                try {
//                                    context.startActivity(Intent.getIntent(s));
//                                } catch (URISyntaxException e) {
//                                    e.printStackTrace();
//                                }
                            }
                        });


        // Create the Alert dialog
        AlertDialog alertDialog = builder.create();

        // Show the Alert Dialog box
        alertDialog.show();
    }

}
