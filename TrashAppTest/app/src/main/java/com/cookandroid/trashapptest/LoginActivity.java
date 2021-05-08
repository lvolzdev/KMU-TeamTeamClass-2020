package com.cookandroid.trashapptest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    Button login, sign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sign = (Button) findViewById(R.id.sign);
        login = (Button) findViewById(R.id.login);

        sign.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpView.class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(){
                    public void run(){
                        EditText textId = (EditText) findViewById(R.id.username);
                        EditText textPw = (EditText) findViewById(R.id.password);

                        // 공백 판단해서 처리해야합니다

                        Json json = new Json();
                        json.putJson("user_id", textId.getText().toString());
                        json.putJson("password", textPw.getText().toString());
                        Log.d("asdf", "asdf");
                        String msg = json.sendREST("http://10.0.2.2:8000/login", "PUT", json.getJson());

                        if(msg.equals("401")){
                            // 비밀번호가 잘못됨
                        } else if(msg.equals("400")){
                            // 해당하는 아이디가 없음
                        } else {
                            // 로그인 성공
                            String arr[] = json.orderDict(msg);
                            String token = arr[0];
                            String rtoken = arr[1];

                            TokenStorage ts = new TokenStorage();
                            ts.setToken(token);
                            ts.setrToken(rtoken);

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        }

                    }
                }.start();
            }
        });
    }
}
