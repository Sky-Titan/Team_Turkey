package org.techtown.turkey_android;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class board_subject extends AppCompatActivity {
    String number;
    String myJSON;
    JSONArray posts = null;

    EditText editText;

    ListView listview;
    PostViewAdapter adapter;

    private static final String TAG_RESULTS = "result";
    private static final String TAG_ID = "id";
    private static final String TAG_NUMBER = "number";
    private static final String TAG_TITLE = "title";
    private static final String TAG_CONTENT = "content";
    private static final String TAG_PASSWORD = "password";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_subject);
        editText=(EditText)findViewById(R.id.b_s_edit);
        Intent intent_bs = getIntent();
        number = intent_bs.getExtras().getString("subjectNumber");// intent로 board_subject로부터 subjectNumber 값을 가져옴
        adapter=new PostViewAdapter();
        listview = (ListView)findViewById(R.id.listview1);
        listview.setAdapter(adapter);
        getData("http://119.201.56.98/select_post_2.php");

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                PostViewItem  item = (PostViewItem)adapter.getItem(pos);
                String id=item.getId();
                Intent intent_sr=new Intent(board_subject.this, board_subject_read.class);
                intent_sr.putExtra("id",id);
                startActivity(intent_sr);
            }
        });
    }



    public void b_s_write(View view)
    {
        Intent intent_sw = new Intent(this,board_subject_write.class);
        intent_sw.putExtra("subjectNumber",number);
        startActivity(intent_sw);
    }

    public void b_s_search(View view)
    {
                if (editText.getText().toString().equals(""))//검색창에 아무 것도 없을시
                {
                    adapter = new PostViewAdapter() ;
                    listview.setAdapter(adapter);
                    getData("http://119.201.56.98/select_post_2.php");//php 수정필요

                }
                else//검색어가 존재 할 시
                {
                    getData2("http://119.201.56.98/select_post_2.php");//php 수정필요
                    adapter = new PostViewAdapter() ;
                    listview.setAdapter(adapter);
                    try{
                        JSONObject jsonObj = new JSONObject(myJSON);
                        posts = jsonObj.getJSONArray(TAG_RESULTS);

                        for(int i =0;i< posts.length();i++){
                            JSONObject c = posts.getJSONObject(i);
                            String id = c.getString(TAG_ID);
                            String number = c.getString(TAG_NUMBER);
                            String title = c.getString(TAG_TITLE);
                            String content = c.getString(TAG_CONTENT);
                            String password = c.getString(TAG_PASSWORD);

                            if(title.contains(editText.getText().toString())||content.contains(editText.getText().toString()))//검색칸에 타이틀이 또는 내용이 포함된다면 추가
                            {
                                adapter.addItem(id,number,title,content,password);
                            }

                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }

                }

    }




    public void getData(String url){
        class GetDataJSON extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params){
                String uri = params[0];
                BufferedReader bufferedReader = null;
                try{
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection)url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while((json = bufferedReader.readLine()) != null){
                        sb.append(json + "\n");
                    }

                    return sb.toString().trim();
                }catch (Exception e){
                    return null;
                }
            }
            @Override
            protected void onPostExecute(String result){
                myJSON = result;
                showList();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }

    public void getData2(String url){
        class GetDataJSON extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params){
                String uri = params[0];
                BufferedReader bufferedReader = null;
                try{
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection)url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while((json = bufferedReader.readLine()) != null){
                        sb.append(json + "\n");
                    }

                    return sb.toString().trim();
                }catch (Exception e){
                    return null;
                }
            }
            @Override
            protected void onPostExecute(String result){
                myJSON = result;
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }
    protected void showList(){
        try{
            JSONObject jsonObj = new JSONObject(myJSON);
            posts = jsonObj.getJSONArray(TAG_RESULTS);

            for(int i =0;i< posts.length();i++){
                JSONObject c = posts.getJSONObject(i);
                String id = c.getString(TAG_ID);
                String number = c.getString(TAG_NUMBER);
                String title = c.getString(TAG_TITLE);
                String content = c.getString(TAG_CONTENT);
                String password = c.getString(TAG_PASSWORD);

                adapter.addItem(id,number,title,content,password);

            }
            listview.setAdapter(adapter);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }


}
