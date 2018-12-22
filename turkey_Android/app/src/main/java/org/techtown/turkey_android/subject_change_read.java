package org.techtown.turkey_android;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class subject_change_read extends AppCompatActivity {
    String myJSON;
    JSONArray posts = null;
    String password2check;
    private static final String TAG_RESULTS = "result";
    private static final String TAG_ID = "id";
    private static final String TAG_NUMBER = "number";
    private static final String TAG_TITLE = "title";
    private static final String TAG_CONTENT = "content";
    private static final String TAG_PASSWORD = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_change_read);
        TextView scr_title = (TextView)findViewById(R.id.scr_title);
        TextView scr_content = (TextView)findViewById(R.id.scr_content);

        Intent intent = getIntent();
        String check_id = intent.getExtras().getString("id");// intent로 fragment-C로부터 id 값을 가져옴

        getData2("http://119.201.56.98/select_post_2.php");
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

                if(check_id==id)//해당 id라면 스톱
                {
                    scr_title.setText(title);
                    scr_content.setText(content);

                    password2check=password;
                    break;
                }

            }
        }catch (JSONException e){
            e.printStackTrace();
        }


    }

    public void scr_del(View view)
    {
        EditText scr_password = (EditText)findViewById(R.id.scr_password);


        if(scr_password.getText().toString()==password2check) {
            Toast.makeText(getApplicationContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();
            finish();
            //sql문을 통한 삭제코드 추가할것
        }
        else
        {
            Toast.makeText(getApplicationContext(), "비밀번호를 확인하세요.", Toast.LENGTH_SHORT).show();
        }

    }
    public void scr_close(View view)
    {
        finish();
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

}
