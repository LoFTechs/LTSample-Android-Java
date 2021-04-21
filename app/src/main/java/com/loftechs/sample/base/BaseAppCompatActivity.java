package com.loftechs.sample.base;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.loftechs.sample.utils.PermissionUtil;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class BaseAppCompatActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private static final int ALL_PERMISSION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        methodRequiresPermission();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    public void showShortToast(String toastContent) {
        Toast.makeText(getBaseContext(), toastContent, Toast.LENGTH_SHORT).show();
    }

    public void startActivity(Class<?> cls) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        startActivity(intent);
    }

    public void startActivity(Class<?> cls, Bundle intentBundle) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        intent.putExtras(intentBundle);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void setTitleBackButtonVisiable(boolean visiable) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(visiable);
    }

    @AfterPermissionGranted(ALL_PERMISSION)
    private void methodRequiresPermission() {
        if (EasyPermissions.hasPermissions(this, PermissionUtil.getVoicePerms())) {
            // Already have permission, do the thing
//            Toast.makeText(this, "permission allowed", Toast.LENGTH_SHORT).show();
        } else {
            // Do not have permissions, request them now
//            Toast.makeText(this, "not allowed", Toast.LENGTH_SHORT).show();
            EasyPermissions.requestPermissions(this, "permission",
                    ALL_PERMISSION, PermissionUtil.getVoicePerms());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        // Some permissions have been denied
        // LTLog.d("sibo", "onPermissionsDenied:" + requestCode + ":" + perms.size());
//        PermissionHelper.getInstance().setDeniedPerms(perms);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        // LTLog.d("sibo", "onPermissionsGranted:" + requestCode + ":" + perms.size());
//        PermissionHelper.getInstance().setGrantedPerms(perms);
    }
}
