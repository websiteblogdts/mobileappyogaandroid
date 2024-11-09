package com.example.adminjava;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/** @noinspection ALL*/
public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            // Gọi phương thức đồng bộ dữ liệu khi có kết nối
            syncOfflineData(context);
        }
    }

    private void syncOfflineData(Context context) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        databaseHelper.syncOfflineDataToFirebase();
    }
}
