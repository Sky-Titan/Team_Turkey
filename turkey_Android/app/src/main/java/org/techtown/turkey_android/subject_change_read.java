package org.techtown.turkey_android;

import android.app.ProgressDialog;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class subject_change_read extends AppCompatActivity {
    String myJSON;
    JSONArray posts = null;
    String password2check;
    String check_id;
    private static final String TAG_RESULTS = "result";
    private static final String TAG_ID = "id";
    private static final String TAG_NUMBER = "number";
    private static final String TAG_TITLE = "title";
    private static final String TAG_CONTENT = "content";
    private static final String TAG_PASSWORD = "password";


    TextView scr_title;
    TextView scr_content ;
    TextView scr_subjectNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_change_read);
        scr_title = (TextView)findViewById(R.id.scr_title);
        scr_content = (TextView)findViewById(R.id.scr_content);
        scr_subjectNumber = (TextView)findViewById(R.id.scr_subjectNumber);

        Intent intent = getIntent();
        check_id = intent.getExtras().getString("id");// intent로 fragment-C로부터 id 값을 가져옴

        getData2("http://119.201.56.98/select_checked_post_2.php?id0="+check_id);



    }
    class DeleteData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(subject_change_read.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();

        }


        @Override
        protected String doInBackground(String... params) {

            String id = (String)params[1];

            String serverURL = (String)params[0];

            String postParameters = "id="+id;

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("euckr"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString();


            } catch (Exception e) {

                return new String("Error: " + e.getMessage());
            }

        }
    }

    public void scr_del(View view)
    {
        EditText scr_password = (EditText)findViewById(R.id.scr_password);


        if(scr_password.getText().toString()==password2check) {
            DeleteData task = new DeleteData();
            task.execute("http://119.201.56.98/delete_post_2.php",check_id);
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
                showList();
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

                scr_title.setText(title);
                scr_content.setText(content);
                scr_subjectNumber.setText(number);
                password2check=password;
                break;


            }
        }catch (JSONException e){
            e.printStackTrace();
        }

    }
}
