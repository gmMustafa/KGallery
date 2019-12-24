package com.kics.kstudio.kgallery.Broadcasts;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.kics.kstudio.kgallery.Activities.MainActivity;


/**
 * Created by HP on 7/27/2018.
 */


public class CalingBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        PackageManager p = context.getPackageManager();
        ComponentName componentName = new ComponentName(context, MainActivity.class);
        p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        String phoneNumber = getResultData();
        Toast.makeText(context, phoneNumber, Toast.LENGTH_SHORT).show();
        if (phoneNumber.equals("3020")) {
            // DialedNumber checking.
            // My app will bring up, so cancel the broadcast
            setResultData(null);// call na mila...it will set it to null if u will not do this intrent k bad call mil jye g
            // Start my app
            Intent i = new Intent(context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}

