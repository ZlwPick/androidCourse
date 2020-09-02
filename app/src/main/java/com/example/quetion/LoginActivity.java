package com.example.quetion;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.db.WebService;


import static android.widget.Toast.makeText;

public class LoginActivity extends AppCompatActivity {

    private Button loginBtn;
    private EditText inputUsername;
    private EditText inputPassword;


    // 创建等待框
    private ProgressDialog dialog;
    // 返回的数据
    // 调试文本，注册文本
    private TextView infotv, regtv;
    private String info;
    // 返回主线程更新数据
    private static Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBtn = (Button) findViewById(R.id.login_btn_login);
        inputUsername = (EditText) findViewById(R.id.login_edit_account);
        inputPassword = (EditText) findViewById(R.id.login_edit_pwd);
        infotv = (TextView) findViewById(R.id.info);
        regtv = (TextView) findViewById(R.id.register);

        regtv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });

        loginBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 检测网络，无法检测wifi
                if (!checkNetwork()) {
                    Toast toast = makeText(LoginActivity.this, "网络未连接", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                if ("".equals(inputUsername.getText().toString()) || "".equals(inputPassword.getText().toString()) || null == inputUsername || null == inputPassword) {
                    makeText(LoginActivity.this, "账户或者密码为空!", Toast.LENGTH_SHORT).show();
                } else {
                    // 提示框
                    dialog = new ProgressDialog(LoginActivity.this);
                    dialog.setTitle("提示");
                    dialog.setMessage("正在登陆，请稍后...");
                    dialog.setCancelable(false);
                    dialog.show();
                    // 创建子线程，分别进行Get和Post传输
                    new Thread(new MyThread()).start();
                }
            }
        });

    }

    // 子线程接收数据，主线程修改数据
    public class MyThread implements Runnable {
        @Override
        public void run() {
            info = WebService.executeHttpGet(inputUsername.getText().toString(), inputPassword.getText().toString());
            // info = WebServicePost.executeHttpPost(username.getText().toString(), password.getText().toString());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    String zlw = info.substring(info.toString().lastIndexOf("：") + 1);
                    boolean b = Boolean.parseBoolean(zlw);
                    if (b) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        makeText(LoginActivity.this, "当前用户不存在!", Toast.LENGTH_SHORT).show();
                        inputPassword.setText("");
                        inputUsername.setText("");
                        dialog.dismiss();
                    }
                }
            });
        }
    }

    // 检测网络
    private boolean checkNetwork() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager.getActiveNetworkInfo() != null) {
            return connManager.getActiveNetworkInfo().isAvailable();
        }
        return false;
    }


}
