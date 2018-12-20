package org.techtown.turkey_android;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BookingList extends AppCompatActivity {

    String myJSON;
    private static final String TAG_RESULTS = "result";
    private static final String TAG_NUMBER = "number";
    private static final String TAG_TITLE = "title";
    private static final String TAG_PROFESSOR = "professor";
    private static final String TAG_TOTAL = "total";
    private static final String TAG_APPLICANT = "applicant";

    JSONArray lectures = null;
    ListView listView;
    BookingListViewAdapter adapter;

    String checkedNumber="";

    public void loadDB(){
        SQLiteDatabase db = openOrCreateDatabase(
                "test.db",
                SQLiteDatabase.CREATE_IF_NECESSARY,
                null);

        db.execSQL("CREATE TABLE IF NOT EXISTS lecture"
                +"(number TEXT,applicant TEXT);");
        Cursor c= db.rawQuery("SELECT * FROM lecture",null);
        String uri="http://119.201.56.98/select_checked_lecture.php?";
        int i=0;
        while(c.moveToNext()){
            checkedNumber=c.getString(0);
            uri+="number"+i+"="+checkedNumber;
            if(i+1!=c.getCount())
                uri+="&";
                i++;
        }
        getData(uri);//db에서 데이터 가져옴

        if(db!=null){
            db.close();
        }

    }

    public void getData(String url){
        class GetDataJSON extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params){
                String uri = params[0];//"http://119.201.56.98/select_checked_lecture.php?number="+checkedNumber;
                String postParameters = "number="+checkedNumber;
                BufferedReader bufferedReader = null;
                try{

                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection)url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while((json = bufferedReader.readLine()) != null){
                        sb.append(json+"\n");
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
          //      Toast.makeText(getApplicationContext(),""+myJSON,Toast.LENGTH_SHORT).show();
                showList();
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
           //     Toast.makeText(getApplicationContext(),""+c.getString(TAG_TITLE),Toast.LENGTH_SHORT).show();
                String professor = c.getString(TAG_PROFESSOR);
                String total = c.getString(TAG_TOTAL);
                String applicant = c.getString(TAG_APPLICANT);

                adapter.addItem(number, title, professor, total, applicant);

          //  Toast.makeText(getApplicationContext(),""+adapter.getCount(),Toast.LENGTH_SHORT).show();
            }
            listView.setAdapter(adapter);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_list);
        adapter= new BookingListViewAdapter();
        listView=(ListView)findViewById(R.id.listview2);
        listView.setAdapter(adapter);

        loadDB();

    }


}
