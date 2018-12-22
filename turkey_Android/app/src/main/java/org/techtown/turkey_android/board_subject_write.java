package org.techtown.turkey_android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class board_subject_write extends AppCompatActivity {
    EditText bsw_title;
    EditText bsw_content;
    EditText bsw_password;
    String number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_subject_write);

        bsw_title = (EditText)findViewById(R.id.bsw_title);
        bsw_content = (EditText)findViewById(R.id.bsw_content);
        bsw_password = (EditText)findViewById(R.id.bsw_password);
        // 과목코드는 intent로 받아온다.
        Intent intent_sw = getIntent();
        number = intent_sw.getExtras().getString("subjectNumber");// intent로 board_subject로부터 subjectNumber 값을 가져옴
    }

    public void scw_add(View view)
    {
        String text4err="";
        if(bsw_title.length()>0&&bsw_content.length()>0&&bsw_password.length()>0) {
            //서버에 추가하는 코드 필요
            String title = bsw_title.getText().toString();
            String content = bsw_content.getText().toString();
            String password = bsw_password.getText().toString();
            board_subject_write.InsertData task = new board_subject_write.InsertData();
            task.execute("http://119.201.56.98/insert_post_2.php",number,title,content,password);//php수정필요함
            Toast.makeText(getApplicationContext(), "추가했습니다.", Toast.LENGTH_SHORT).show();

            finish();
        }
        else
        {
            if(bsw_title.length()==0)
                text4err=text4err+"제목";
            if(bsw_content.length()==0) {
                if (text4err.length() > 0)
                    text4err = text4err + ", ";
                text4err = text4err + "내용";
            }
            if(bsw_password.length()==0) {
                if(text4err.length()>0)
                    text4err=text4err+", ";
                text4err = text4err + "비밀번호";
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
            progressDialog = ProgressDialog.show(board_subject_write.this,
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
