package org.techtown.turkey_android;


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
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentB extends Fragment {


    String myJSON;


    private static final String TAG_RESULTS = "result";
    private static final String TAG_NUMBER = "number";
    private static final String TAG_TITLE = "title";
    private static final String TAG_PROFESSOR = "professor";
    private static final String TAG_TOTAL = "total";
    private static final String TAG_APPLICANT = "applicant";

    EditText editText;

    JSONArray lectures = null;

    ListView listview;
    LectureViewAdapter adapter;
    public FragmentB() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_b,container,false);

        editText=(EditText)view.findViewById(R.id.lecture_edit);
        Button button=(Button)view.findViewById(R.id.lecture_search);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText().toString().equals(""))//검색창에 아무 것도 없을시
                {
                    adapter = new LectureViewAdapter() ;
                    listview.setAdapter(adapter);
                    getData("http://119.201.56.98/select_lecture.php");

                }
                else//검색어가 존재 할 시
                {
                    getData2("http://119.201.56.98/select_lecture.php");//SEARCH 용도의 getdata
                    adapter = new LectureViewAdapter() ;
                    listview.setAdapter(adapter);
                    try{
                        JSONObject jsonObj = new JSONObject(myJSON);
                        lectures = jsonObj.getJSONArray(TAG_RESULTS);

                        for(int i =0;i< lectures.length();i++){
                            JSONObject c = lectures.getJSONObject(i);
                            String number = c.getString(TAG_NUMBER);
                            String title = c.getString(TAG_TITLE);
                            String professor = c.getString(TAG_PROFESSOR);
                            String total = c.getString(TAG_TOTAL);
                            String applicant = c.getString(TAG_APPLICANT);

                            if(title.contains(editText.getText().toString()))//해당 교과목에 검색어가 포함이 되어있으면 추가
                            {
                                adapter.addItem(number,title,professor,total,applicant);
                            }
                            else if(number.contains(editText.getText().toString()))//
                            {
                                adapter.addItem(number,title,professor,total,applicant);
                            }

                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }

                }
            }
        });

        adapter=new LectureViewAdapter();
        listview = (ListView) view.findViewById(R.id.listview_lectures);
        listview.setAdapter(adapter);
        getData("http://119.201.56.98/select_lecture.php");

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                ListViewItem  item = (ListViewItem)adapter.getItem(pos);
                String number=item.getNumber();
                Intent intent=new Intent(getActivity(), board_subject.class);
                intent.putExtra("subjectNumber",number);
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
                    con.disconnect();
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
            lectures = jsonObj.getJSONArray(TAG_RESULTS);


            for(int i =0;i< lectures.length();i++){
                JSONObject c = lectures.getJSONObject(i);
                String number = c.getString(TAG_NUMBER);
                String title = c.getString(TAG_TITLE);
                String professor = c.getString(TAG_PROFESSOR);
                String total = c.getString(TAG_TOTAL);
                String applicant = c.getString(TAG_APPLICANT);
                adapter.addItem(number, title, professor, total, applicant);

            }
            listview.setAdapter(adapter);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}
