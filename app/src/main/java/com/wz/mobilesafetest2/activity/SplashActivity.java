package com.wz.mobilesafetest2.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

import com.wz.mobilesafetest2.R;
import com.wz.mobilesafetest2.utils.SafeConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class SplashActivity extends Activity {

    private static final int SHOW_UPDATE_DIALOG = 1;
    private static final int ERROR = 2;
    private static final int LOAD_MAIN = 3;
    private static final int GOTOINSTALL = 5;
    private static final int DOWNERROR = 0;
    private String localVersionName;
    private int localVersionCode;
    private int serverVersion;
    private Handler mHanlder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_UPDATE_DIALOG:
                    showUpdateDialog();
                    break;
                case ERROR:
                    Toast.makeText(SplashActivity.this, "获取版本信息错误:" + msg.arg1, Toast.LENGTH_SHORT).show();
                    jump2Main();
                    break;
                case LOAD_MAIN:
                    jump2Main();
                    break;
                case DOWNERROR:
                    Toast.makeText(SplashActivity.this, "下载错误:" + msg.arg1, Toast.LENGTH_SHORT).show();
                    jump2Main();
                    break;
                case GOTOINSTALL:
                    installApk();
                    break;
            }
            super.handleMessage(msg);
        }
    };
    private long startTime;

    /*
    Intent intent = new Intent(Intent.ACTION_VIEW);
    File file = new File(Environment.getExternalStorageDirectory().getPath(), "xx.apk");
    intent.addCategory(Intent.CATEGORY_DEFAULT);
    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
    startActivityForResult(intent, 0);
    */

    private void installApk() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "xxx.apk");
        intent.addCategory(intent.CATEGORY_DEFAULT);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        jump2Main();
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String newVersionUrl;
    private String desc;
    private AlertDialog dialog;

    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("有新版本,特性如下:" + desc);

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                jump2Main();
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downloadApk();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                jump2Main();
                dialog.dismiss();
            }
        });

        dialog = builder.show();
    }

    private void downloadApk() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
        new Thread() {

            @Override
            public void run() {
                int downErrorCode = -1;
                HttpURLConnection conn = null;
                FileOutputStream fos = null;
                try {
                    URL url = new URL(newVersionUrl);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setReadTimeout(2000);
                    conn.setConnectTimeout(2000);
                    int length = conn.getContentLength();
                    int code = conn.getResponseCode();
                    if (code == 200) {
                        InputStream in = conn.getInputStream();
                        byte[] buf = new byte[1024 * 10];
                        File file = new File(Environment.getExternalStorageDirectory().getPath(), "xxx.apk");
                        file.delete();
                        fos = new FileOutputStream(file);
                        int len = 0;
                        progressDialog.setMax(length);
                        int progress = 0;
                        while ((len = in.read(buf)) != -1) {
                            fos.write(buf, 0, len);
                            progress += len;
                            progressDialog.setProgress(progress);
                        }
                        progressDialog.dismiss();
                    } else {
                        downErrorCode = code;
                    }
                } catch (MalformedURLException e) {
                    downErrorCode = 4001;
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    downErrorCode = 4002;
                    e.printStackTrace();
                } catch (IOException e) {
                    downErrorCode = 4003;
                    e.printStackTrace();
                } finally {
                    Message msg = Message.obtain();
                    if (downErrorCode == -1) {
                        msg.what = GOTOINSTALL;
                        mHanlder.sendMessage(msg);
                    } else {
                        msg.what = DOWNERROR;
                        msg.arg1 = downErrorCode;
                        mHanlder.sendMessage(msg);
                    }
                    if (conn == null || fos == null) {
                        return;
                    }
                    conn.disconnect();
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

        initData();
    }

    private void initData() {
        startTime = System.currentTimeMillis();
        new Thread() {

            private int errorCode;

            @Override
            public void run() {
                HttpURLConnection conn = null;
                BufferedReader br = null;
                try {
                    errorCode = -1;
                    URL url = new URL(SafeConstants.UPDATEURL);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setReadTimeout(2000);
                    conn.setConnectTimeout(2000);

                    int code = conn.getResponseCode();
                    if (code == 200) {
                        InputStream in = conn.getInputStream();
                        br = new BufferedReader(new InputStreamReader(in));
                        String line = null;
                        StringBuilder sb = new StringBuilder();
                        while ((line = br.readLine()) != null) {
                            sb.append(line);
                        }
                        JSONObject jsonObject = new JSONObject(sb.toString());
                        serverVersion = jsonObject.getInt("version");
                        newVersionUrl = jsonObject.getString("url");
                        desc = jsonObject.getString("desc");
                    } else {
                        errorCode = code;
                    }
                } catch (MalformedURLException e) {
                    errorCode = 4001;
                } catch (IOException e) {
                    errorCode = 4002;
                    e.printStackTrace();
                } catch (JSONException e) {
                    errorCode = 4003;
                } finally {
                    Message msg = Message.obtain();
                    if (errorCode == -1) {
                        boolean isNewVersion = isNewVersion();
                        if (isNewVersion) {
                            msg.what = SHOW_UPDATE_DIALOG;
                        } else {
                            msg.what = LOAD_MAIN;
                        }
                    } else {
                        msg.what = ERROR;
                        msg.arg1 = errorCode;
                    }
                    mHanlder.sendMessage(msg);

                    if (br == null || conn == null) {
                        return;
                    }
                    conn.disconnect();
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private boolean isNewVersion() {
        if (serverVersion > localVersionCode) {
            //需要更新
            return true;
        } else {
            //不需要更新
            jump2Main();
            return false;
        }
    }

    private void jump2Main() {
        mHanlder.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000 - (System.currentTimeMillis() - startTime));
    }

    private void initView() {
        setContentView(R.layout.activity_splash);

        try {
            PackageManager manager = getPackageManager();
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            localVersionCode = info.versionCode;
            localVersionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        TextView tv_versionName = (TextView) findViewById(R.id.tv_splash_versionName);
        tv_versionName.setText("版本号:" + localVersionName);
    }
}
