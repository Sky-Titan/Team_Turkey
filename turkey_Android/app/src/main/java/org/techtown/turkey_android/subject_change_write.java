package org.techtown.turkey_android;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class subject_change_write extends AppCompatActivity {

    EditText scw_title;
    EditText scw_content;
    EditText scw_subjectNumber;
    EditText scw_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_change_write);

        scw_title = (EditText)findViewById(R.id.scw_title);
        scw_content = (EditText)findViewById(R.id.scw_content);
        scw_subjectNumber = (EditText)findViewById(R.id.scw_subjectNumber);
        scw_password = (EditText)findViewById(R.id.scw_password);
    }
    public void scw_add(View view)
    {
        String text4err="";
        if(scw_title.length()>0&&scw_content.length()>0&&scw_password.length()>0) {
            //서버에 추가하는 코드 필요

            String number = scw_subjectNumber.getText().toString();
            String title = scw_title.getText().toString();
            String content = scw_content.getText().toString();
            String password = scw_password.getText().toString();

            InsertData task = new InsertData();
            task.execute("http://119.201.56.98/insert_post_2.php",number,title,content,password);

            Toast.makeText(getApplicationContext(), "추가했습니다.", Toast.LENGTH_SHORT).show();


            finish();
        }
        else
        {
            if(scw_title.length()==0)
                text4err=text4err+"제목";
            if(scw_content.length()==0) {
                if (text4err.length() > 0)
                    text4err = text4err + ", ";
                text4err = text4err + "내용";
            }
            if(scw_password.length()==0) {
                if(text4err.length()>0)
                    text4err=text4err+", ";
                text4err = text4err + "비밀번호";
            }
            if(scw_subjectNumber.length()==0||scw_subjectNumber.length()>10) {
                if(text4err.length()>0)
                    text4err=text4err+", ";
                text4err = text4err + "과목코드";
            }
            text4err = text4err + "을(를) 확인하세요.";
            Toast.makeText(getApplicationContext(), text4err, Toast.LENGTH_SHORT).show();
        }
    }
    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(subject_change_write.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();

        }


        @Override
        protected String doInBackground(String... params) {

            String number = (String)params[1];
            String title = (String)params[2];
            String content = (String)params[3];
            String password = (String)params[4];

            String serverURL = (String)params[0];

            String postParameters = "number="+number+"&title=" + title + "&content="+content + "&password="+password;

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

}
