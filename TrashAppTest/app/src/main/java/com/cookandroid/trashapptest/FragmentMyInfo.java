package com.cookandroid.trashapptest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentMyInfo extends Fragment {

    MainActivity mainActivity;
    Button btnLogout;
    View view;
    TextView textId, textEmail;

    String info[];
    String refresh[];

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity)getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mainActivity = null;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_myinfo, container, false);

        btnLogout = (Button) view.findViewById(R.id.btnLogout);
        textId = (TextView) view.findViewById(R.id.textId);
        textEmail = (TextView) view.findViewById(R.id.textEmail);
        final String error[] = {"error"};

        info = null;
        new Thread(){
            public void run(){

                Json json = new Json();
                TokenStorage ts = new TokenStorage();
                json.putJson("token", ts.getToken());
                json.putJson("where", "me");
                String msg = json.sendREST("http://10.0.2.2:8000/users/users", "PUT", json.getJson());
                if(msg.equals("401")){
                    // 역시 토큰 에러죠?
                    info = error;
                } else {
                    info = json.orderDict(msg);
                    Log.d("test", info[0]);
                    Log.d("test", info[1]);
                }
            }
        }.start();

        while(info == null) Log.d("test", "mang123");
        Log.d("test1", info[0]);
        if(info != error) {
            // ' 삭제해야함;;
            textId.setText("ID: " + info[0]);
            textEmail.setText("Name: " + info[1]);
        }

        refresh = null;
        new Thread(){
            public void run(){

                Json json = new Json();
                TokenStorage ts = new TokenStorage();
                json.putJson("token", ts.getToken());
                json.putJson("rtoken", ts.getrToken());
                String msg = json.sendREST("http://10.0.2.2:8000/refresh", "PUT", json.getJson());
                if(msg.equals("401")){
                    refresh = error;
                } else {
                    refresh = json.orderDict(msg);
                    ts.setToken(refresh[0]);
                    ts.setrToken(refresh[1]);
                }
            }
        }.start();

        while(refresh == null) Log.d("test", "mang");

        Log.d("test", "middle");
        btnLogout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                new Thread(){
                    public void run(){

                        TokenStorage ts = new TokenStorage();
                        String token = ts.getToken();
                        Json json = new Json();
                        json.putJson("token", token);
                        String msg = json.sendREST("http://10.0.2.2:8000/logout", "PUT", json.getJson());
                        if(msg.equals(401)){
                            // token 만료됨
                            // 근데 로그아웃이므로 그냥 로그인 화면으로 넘기면 됨
                        } else{
                            // 정상적으로 로그아웃 완료
                        }

                        Log.d("test", ts.getToken());
                        ts.setToken("");
                        ts.setrToken("");
                        Log.d("test", "info");
                        Intent intent=new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);

                    }
                }.start();
            }
        });
        Log.d("test", "finish");
        return view;
    }

}
