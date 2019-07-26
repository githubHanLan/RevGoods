package kkkj.android.revgoods.conn.classicbt;

import android.Manifest;
import android.arch.lifecycle.OnLifecycleEvent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import kkkj.android.revgoods.MainActivity;
import kkkj.android.revgoods.R;
import kkkj.android.revgoods.conn.classicbt.listener.BluetoothPermissionCallBack;

/**
 * @author AllenLiu
 * @version 1.0
 * @date 2019/5/8

 */
public  class BluetoothPermissionHandler {
    private static final int REQUEST_CODE_OPEN_GPS = 0x1;
    private static final int REQUEST_CODE_ENABLE_BLUETOOTH = 0x2;
    private static final int REQUEST_BLUETOOTH=0x3;
    private final String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN};
    private BluetoothPermissionCallBack bluetoothPermissionCallBack;

   private AppCompatActivity context;

    public BluetoothPermissionHandler(AppCompatActivity context, BluetoothPermissionCallBack bluetoothPermissionCallBack) {
        this.bluetoothPermissionCallBack = bluetoothPermissionCallBack;
        this.context = context;
    }

    protected void afterGrantedPermissions() {
        if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkGPSIsOpen()) {
                new AlertDialog.Builder(context)
                        .setTitle(R.string.notifyTitle)
                        .setMessage(R.string.gpsNotifyMsg)
                        .setNegativeButton(R.string.cancel,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        context.finish();
                                    }
                                })
                        .setPositiveButton(R.string.setting,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                        context.startActivityForResult(intent, REQUEST_CODE_OPEN_GPS);
                                    }
                                })

                        .setCancelable(false)
                        .show();
            } else {
                if (bluetoothPermissionCallBack!=null) {
                    bluetoothPermissionCallBack.onBlueToothEnabled();
                }

            }
        } else {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);//需要BLUETOOTH权限
            context.startActivityForResult(intent, REQUEST_CODE_ENABLE_BLUETOOTH);
        }


    }

    public  void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == REQUEST_CODE_OPEN_GPS) {
            afterGrantedPermissions();
        }else if(requestCode==REQUEST_CODE_ENABLE_BLUETOOTH){
            afterGrantedPermissions();
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        if (requestCode == REQUEST_BLUETOOTH) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                afterGrantedPermissions();
                // permission was granted, yay! Do the
                // contacts-related task you need to do.
            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                if (bluetoothPermissionCallBack!=null)
                    bluetoothPermissionCallBack.permissionFailed();
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    private boolean checkGPSIsOpen() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null)
            return false;
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public void start(){
        checkPermissions(permissions);
    }

    private void checkPermissions(String[] permissions) {
        boolean permissionAllGranted=true;

        if (Build.VERSION.SDK_INT >= 23) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(context,permission)!= PackageManager.PERMISSION_GRANTED) {
                    permissionAllGranted = false;

                }
            }
        }
        else{
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context,permission)!= PackageManager.PERMISSION_GRANTED) {
                    permissionAllGranted = false;
                }
            }
        }

//        for (String permission : permissions) {
//            if (ContextCompat.checkSelfPermission(context,permission)!= PackageManager.PERMISSION_GRANTED) {
//                permissionAllGranted=false;
//            }
//        }
        if (!permissionAllGranted) {
            ActivityCompat.requestPermissions(context,permissions,REQUEST_BLUETOOTH);
        }else{
            afterGrantedPermissions();
        }
    }





}
