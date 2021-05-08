package com.cookandroid.trashapptest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SignUpView extends AppCompatActivity {
    Button Btn_join;
    TextView checkid, checkpassword;
    TextView retextPw;
    String response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_view);

        Btn_join = (Button) findViewById(R.id.Btn_join);

        checkid = (TextView) findViewById(R.id.checkid);
        checkpassword = (TextView) findViewById(R.id.checkpassword);
        retextPw = (TextView) findViewById(R.id.recheckpassword);

        Btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                retextPw.setVisibility(View.INVISIBLE);
                checkid.setVisibility(View.INVISIBLE);

                response = null;
                new Thread(){
                    public void run(){
                        EditText textId = (EditText) findViewById(R.id.idText);
                        EditText textPw = (EditText) findViewById(R.id.passwordText);
                        EditText textName = (EditText) findViewById(R.id.Btn_name);
                        EditText RepasswordText = (EditText) findViewById(R.id.RepasswordText);
                        // id, pw가 조건에 맞는지 판단해야함
                        // 예를 들어 pw가 몇자리 이상 같은거
                        // 아 맞다 공백도 판단해서 처리해야합니다

                        if(!(textPw.getText().toString().equals(RepasswordText.getText().toString()))){
                            response = "402";
                            return;
                        } //입력한 패스워드와 재입력한 패스워드가 일치하는지 체크

                        Json json = new Json();
                        json.putJson("user_id", textId.getText().toString());
                        json.putJson("password", textPw.getText().toString());
                        json.putJson("name", textName.getText().toString());
                        response = json.sendREST("http://10.0.2.2:8000/signup", "POST", json.getJson());

                    }
                }.start();

                while(response == null) Log.d("test", "mang");

                if(response.equals("402")){
                    retextPw.setVisibility(View.VISIBLE);
                    // password repassword가 일치하지 않습니다
                } else if(response.equals("401")){
                    // 중복된 아이디가 존재
                    checkid.setVisibility(View.VISIBLE);
                } else if(response.equals("400")){
                    // 그냥 뭔가 잘못 됨
                } else {
                    // 회원가입 성공
                    Intent intent = new Intent(SignUpView.this, LoginActivity.class);
                    startActivity(intent);
                }

            }
        });
    }
}
