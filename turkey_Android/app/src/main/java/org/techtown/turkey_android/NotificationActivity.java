package org.techtown.turkey_android;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NotificationActivity extends AppCompatActivity {


    String myJSON;
    SQLiteDatabase db;

    private static final String TAG_RESULTS = "result";
    private static final String TAG_NUMBER = "number";
    private static final String TAG_TITLE = "title";
    private static final String TAG_PROFESSOR = "professor";
    private static final String TAG_TOTAL = "total";
    private static final String TAG_APPLICANT = "applicant";

    EditText editText;

    JSONArray lectures = null;

    ListView listview;
    CustomChoiceListViewAdapter adapter;

    //local db 불러오기
    public void loadDB(){
        db = openOrCreateDatabase(
                "test.db",
                SQLiteDatabase.CREATE_IF_NECESSARY,
                null);

        db.execSQL("CREATE TABLE IF NOT EXISTS lecture"
                +"(number TEXT,applicant TEXT);");

        if(db!=null){
            db.close();
        }
    }
    //알림예약버튼 클릭시 db에 insert
    public void InsertDB(View v){
        String number="";
        String applicant="";
        String sql;

        db = openOrCreateDatabase(
                "test.db",
                SQLiteDatabase.CREATE_IF_NECESSARY,
                null);

        db.execSQL("DROP TABLE IF EXISTS lecture;");
        db.execSQL("CREATE TABLE IF NOT EXISTS lecture"
                +"(number TEXT,applicant TEXT);");
        int count=0;

        for(int i=0;i<adapter.getCount();i++)//check된 item 찾아서 추가
        {

            if(listview.isItemChecked(i))//check되어있을때
            {
                ListViewItem item = (ListViewItem)adapter.getItem(i);
                number=item.getNumber();
                applicant=item.getApplicant();

                String uri="http://119.201.56.98/select_checked_lecture.php?number0="+number;
                getData2(uri);

                try{
                    JSONObject jsonObj = new JSONObject(myJSON);
                    lectures = jsonObj.getJSONArray(TAG_RESULTS);

                    JSONObject c = lectures.getJSONObject(0);
                    String applicant_new = c.getString(TAG_APPLICANT);
                    applicant=applicant_new;

                }catch (JSONException e){
                    e.printStackTrace();
                }
                sql="INSERT INTO lecture (number,applicant) VALUES('"+number+"','"+applicant+"');";
                db.execSQL(sql);
                count++;
            }
        }
        Toast.makeText(getApplicationContext(),""+count+"개의 강의 알림 예약이 완료 되었습니다.", Toast.LENGTH_SHORT).show();
        if(db!=null){
            db.close();
        }
        search(null);
    }
    //예약리스트 클릭시
    public void checkList(View v){
        InsertDB(null);
        //자동으로 local db에 insert후 intent로 새 bookingList액티비티 띄움
        Intent intent=new Intent(NotificationActivity.this,BookingList.class);
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        editText=(EditText)findViewById(R.id.editText1);
        // 코드 계속 ...
        // Adapter 생성
        adapter = new CustomChoiceListViewAdapter() ;

        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) findViewById(R.id.listview1);
        listview.setAdapter(adapter);

        loadDB();
        getData("http://119.201.56.98/select_lecture.php");//수정

    }

    //검색버튼 클릭시
    public void search(View v) {

        if (editText.getText().toString().equals(""))//검색창에 아무 것도 없을시
        {
            adapter = new CustomChoiceListViewAdapter() ;
            listview.setAdapter(adapter);
            getData("http://119.201.56.98/select_lecture.php");

        }
        else//검색어가 존재 할 시
        {
            getData2("http://119.201.56.98/select_lecture.php");//SEARCH 용도의 getdata
            adapter = new CustomChoiceListViewAdapter() ;
            listview.setAdapter(adapter);
            try{
                JSONObject jsonObj = new JSONObject(myJSON);
                lectures = jsonObj.getJSONArray(TAG_RESULTS);

                db = openOrCreateDatabase(
                        "test.db",
                        SQLiteDatabase.CREATE_IF_NECESSARY,
                        null);

                db.execSQL("CREATE TABLE IF NOT EXISTS lecture"
                        +"(number TEXT,applicant TEXT);");

                for(int i =0;i< lectures.length();i++){
                    JSONObject c = lectures.getJSONObject(i);
                    String number = c.getString(TAG_NUMBER);
                    String title = c.getString(TAG_TITLE);
                    String professor = c.getString(TAG_PROFESSOR);
                    String total = c.getString(TAG_TOTAL);
                    String applicant = c.getString(TAG_APPLICANT);

                    if(number.contains(editText.getText().toString()))//해당 교과목번호에 검색어가 포함이 되어있으면 추가
                    {
                        adapter.addItem(number,title,professor,total,applicant);
                        Cursor cursor= db.rawQuery("SELECT * FROM lecture where number='"+number+"'",null);
                        if(cursor.getCount()!=0) //check되어있다면
                        {
                            listview.setItemChecked(adapter.getCount()-1,true);
                        }
                    }

                }
                if(db!=null){
                    db.close();
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

            db = openOrCreateDatabase(
                    "test.db",
                    SQLiteDatabase.CREATE_IF_NECESSARY,
                    null);

            db.execSQL("CREATE TABLE IF NOT EXISTS lecture"
                    +"(number TEXT,applicant TEXT);");


            for(int i =0;i< lectures.length();i++){
                JSONObject c = lectures.getJSONObject(i);
                String number = c.getString(TAG_NUMBER);
                String title = c.getString(TAG_TITLE);
                String professor = c.getString(TAG_PROFESSOR);
                String total = c.getString(TAG_TOTAL);
                String applicant = c.getString(TAG_APPLICANT);
                adapter.addItem(number, title, professor, total, applicant);

                Cursor cursor= db.rawQuery("SELECT * FROM lecture where number='"+number+"'",null);
                if(cursor.getCount()!=0) //check되어있다면
                {
                    listview.setItemChecked(adapter.getCount()-1,true);
                }
            }
            if(db!=null){
                db.close();
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }


}
