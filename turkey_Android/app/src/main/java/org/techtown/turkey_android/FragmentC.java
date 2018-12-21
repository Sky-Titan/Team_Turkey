package org.techtown.turkey_android;
//fragment_c is for board

<<<<<<< HEAD
import android.content.Intent;
=======

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
>>>>>>> 6dbea34f0beaea3eeaefda1406949ede84ee1758
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
<<<<<<< HEAD
import android.widget.Button;
=======
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
>>>>>>> 6dbea34f0beaea3eeaefda1406949ede84ee1758


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentC extends Fragment {

    String myJSON;
    SQLiteDatabase db;

    private static final String TAG_RESULTS = "result";
    private static final String TAG_ID = "id";
    private static final String TAG_TITLE = "title";
    private static final String TAG_CONTENT = "content";
    private static final String TAG_PASSWORD = "password";


    ListView listview;

    JSONArray post=null;

    PostListViewAdapter adapter;
    public FragmentC() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
<<<<<<< HEAD
        View view=inflater.inflate(R.layout.fragment_c,container,false);
        Button button=(Button)view.findViewById(R.id.f_c_btn_write);
        Button button2=(Button)view.findViewById(R.id.f_c_btn_search);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),subject_change_write.class);
                startActivity(intent);
            }
        });
        return view;
=======

        getData("http://119.201.56.98/select_post.php");
        return inflater.inflate(R.layout.fragment_c, container, false);
>>>>>>> 6dbea34f0beaea3eeaefda1406949ede84ee1758
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
    protected void showList(){
        try{
            JSONObject jsonObj = new JSONObject(myJSON);
            post = jsonObj.getJSONArray(TAG_RESULTS);


            for(int i =0;i< post.length();i++){
                JSONObject c = post.getJSONObject(i);
                String id = c.getString(TAG_ID);
                String title = c.getString(TAG_TITLE);
                String content = c.getString(TAG_CONTENT);
                String password = c.getString(TAG_PASSWORD);
                adapter.addItem(id,title,content,password);

            }

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

}
