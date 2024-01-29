package com.qdreamer.x_android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.qdreamer.x_android.R;
import com.qdreamer.x_android.listener.PermissionResultCallback;
import com.qdreamer.x_android.utils.PermissionUtil;

/**
 * @author XJun
 * @date 2023/12/6 11:18
 **/
public class PermissionActivity extends AppCompatActivity {

    private static PermissionResultCallback mCallback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        Intent intent = getIntent();
        if(intent == null){
            this.finish();
            return;
        }

        String[] permissions = getIntent().getStringArrayExtra("permission");
        int code = getIntent().getIntExtra("code",-99);

        if(permissions == null || code == -99 || mCallback == null){
            this.finish();
            return;
        }

        //权限申请 --- 判断当前所有的权限是否都申请通过了
        if(PermissionUtil.hasPermissions(this,permissions)){
            mCallback.onPermissionGranted();
            this.finish();
            return;
        }

        //申请权限
        ActivityCompat.requestPermissions(this,permissions,code);
    }

    public static void openActivity(Context context,String[] permission,int requestCode,PermissionResultCallback callback){
        mCallback = callback;
        Intent intent = new Intent();
        intent.putExtra("permission",permission);
        intent.putExtra("code",requestCode);
        intent.setClass(context,PermissionActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(PermissionUtil.hasPermissions(this,permissions)){
            mCallback.onPermissionGranted();
            this.finish();
            return;
        }

        if(!PermissionUtil.hasPermissions(this,permissions)){
            mCallback.onPermissionReject();
            this.finish();
            return;
        }
        mCallback.onPermissionDenied();
        this.finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,0);
    }
}
