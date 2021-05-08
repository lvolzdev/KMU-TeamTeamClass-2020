package com.cookandroid.trashapptest;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentRanking extends Fragment {

    public static Context context;
    MainActivity mainActivity;
    Button rank, Total_rank;
    LinearLayout linear;
    ListView Total_rank_list;
    MyAdapter adapter;
    TextView point;
    String user[];

    View view;

    String[] ranking = new String[10];
    String[] info;
    String[] refresh;

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
        view = inflater.inflate(R.layout.fragment_ranking, container, false);

        rank = (Button) view.findViewById(R.id.rank);
        Total_rank = (Button) view.findViewById(R.id.Total_rank);

        linear = (LinearLayout) view.findViewById(R.id.linear);

        Total_rank_list = (ListView) view.findViewById(R.id.Total_rank_list);
        adapter = new MyAdapter();
        Total_rank_list.setAdapter(adapter);
        /*
        ListView는 사용자가 정의한 데이터 목록을 아이템 단위로 구성하여 화면에 출력하는 ViewGroup입니다.
        listView에 데이터를 추가하여 화면에 표시하기 위해서는 Adapter를 사용해야합니다.
        Adapter는 사용자가 정의한 데이터를 ListView에 출력하기 위해 사용하는 객체로,
        사용자 데이터와 화면 출력 View로 이루어진 두 개의 부분을 이어주는 객체입니다.
        Adpater를 생성 후 ListView에 지정해줍니다.
        */

        info = null;
        final String[] error = {"error"};
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
                }
            }
        }.start();

        while(info == null) Log.d("test", "mang");
        Log.d("test1", info[0]);
        if(info != error) {
            point = (TextView) view.findViewById(R.id.point);
            point.setText(info[2] + " P");
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

        rank.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Total_rank.setVisibility(View.VISIBLE);
            }
        });

        Total_rank.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                linear.setVisibility(View.VISIBLE);
            }
        });

        return view;
    }

    class MyAdapter extends BaseAdapter {
        @Override
        //데이터가 몇개나 있나?
        public int getCount() {
            return ranking.length;
        }

        @Override
        //아이템을 하나씩 받아옴
        public Object getItem(int position) {
            return ranking[position];
        }

        @Override
        //
        public long getItemId(int position) {
            return position;
        }
        //position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴.
        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {

            user = null;
            final String[] error = {"error"};
            new Thread(){
                public void run(){

                    Json json = new Json();
                    TokenStorage ts = new TokenStorage();
                    json.putJson("token", ts.getToken());
                    json.putJson("where", "all");
                    String msg = json.sendREST("http://10.0.2.2:8000/users/users", "PUT", json.getJson());
                    // arr 초기화
                    if(msg.equals("401")){
                        // 역시 토큰 에러죠?
                        user = error;
                    } else {
                        user = json.orderDict(msg);
                    }
                }
            }.start();
            while(user == null) Log.d("test", "mang");

            int count=0;
            try {
                for (int i = 0; i < user.length; i+=2) {
                    user[i] = user[i];
                    count++;
                    Log.d("test", Integer.toString(count));
                }
            } catch(Exception e){

            }
            Log.d("test", Integer.toString(count));

            for(int i=0; i<count*2; i+=2) user[i] = user[i].substring(1, user[i].length()-1);
            for(int i=0; i<count; i++){
                for(int j=i+1; j<count; j++){
                    if(Double.parseDouble(user[i*2+1]) < Double.parseDouble(user[j*2+1])){
                        String tmp = user[i*2];
                        user[i*2] = user[j*2];
                        user[j*2] = tmp;
                        tmp = user[i*2+1];
                        user[i*2+1] = user[j*2+1];
                        user[j*2+1] = tmp;
                    }
                }
            }

            for(int i=0; i<user.length/2; i++){
                ranking[i] = Integer.toString(i+1) + "  " + user[i*2] + "  " + user[i*2+1];
            }

            TextView v = new TextView(mainActivity.getApplicationContext());
            v.setText(ranking[position]);
            v.setTextSize(30);
            v.setTextColor(Color.BLACK);
            v.setGravity(Gravity.CENTER);

            return v;
        }
    }

}
