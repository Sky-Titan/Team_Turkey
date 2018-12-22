package org.techtown.turkey_android;
//fragment_c is for board

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentC extends Fragment{

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

    public FragmentC() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_c,container,false);

        editText=(EditText)view.findViewById(R.id.post_edit);
        Button button=(Button)view.findViewById(R.id.f_c_btn_write);
        Button button2=(Button)view.findViewById(R.id.f_c_btn_search);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),subject_change_write.class);
                startActivity(intent);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText().toString().equals(""))//검색창에 아무 것도 없을시
                {
                    adapter = new PostViewAdapter() ;
                    listview.setAdapter(adapter);
                    getData("http://119.201.56.98/select_post_2.php");

                }
                else//검색어가 존재 할 시
                {
                    getData2("http://119.201.56.98/select_post_2.php");//SEARCH 용도의 getdata
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

                            if(title.contains(editText.getText().toString()))//해당 교과목번호에 검색어가 포함이 되어있으면 추가
                            {
                                adapter.addItem(id,number,title,content,password);
                            }

                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }

                }
            }
        });

        adapter=new PostViewAdapter();
        listview = (ListView) view.findViewById(R.id.listview1);
        listview.setAdapter(adapter);
        getData("http://119.201.56.98/select_post_2.php");

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                PostViewItem  item = (PostViewItem)adapter.getItem(pos);
                String id=item.getId();
                Intent intent=new Intent(getActivity(), subject_change_read.class);
                intent.putExtra("id",id);
                startActivity(intent);

            }
        }) ;

        return view;
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
