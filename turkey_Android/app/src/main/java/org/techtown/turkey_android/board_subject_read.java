package org.techtown.turkey_android;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
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

public class board_subject_read extends AppCompatActivity {
    String myJSON;
    String myJSON2;
    JSONArray posts = null;
    JSONArray comments = null;

    ListView listView;
    CommentViewAdapter adapter;

    String password2check;
    String check_id;
    private static final String TAG_RESULTS = "result";
    private static final String TAG_ID = "id";
    private static final String TAG__POST_ID = "post_id";
    private static final String TAG_NUMBER = "number";
    private static final String TAG_TITLE = "title";
    private static final String TAG_CONTENT = "content";
    private static final String TAG_PASSWORD = "password";

    EditText comment_edit;
    EditText comment_password;
    EditText pop_password;

    TextView bsr_title;
    TextView bsr_content ;
    TextView bsr_subjectNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_subject_read);
        bsr_title = (TextView)findViewById(R.id.bsr_title);
        bsr_content = (TextView)findViewById(R.id.bsr_content);
        bsr_subjectNumber = (TextView)findViewById(R.id.bsr_subjectNumber);

        comment_edit=(EditText)findViewById(R.id.comment_edit);
        comment_password=(EditText)findViewById(R.id.comment_pw_edit);
        pop_password=(EditText)findViewById(R.id.pop_password2);


        listView=(ListView)findViewById(R.id.listview_comments);
        adapter=new CommentViewAdapter();
        listView.setAdapter(adapter);

        String id;
        String password;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                CommentViewItem  item = (CommentViewItem)adapter.getItem(pos);


                final String id=item.getId();// 수정필요
                final String password=item.getPassword();// 수정필요
                String content=item.getContent();// 수정필요

                final EditText pop_password=new EditText(board_subject_read.this);
                pop_password.setInputType( InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD );
                pop_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                LinearLayout dialogView = (LinearLayout)View.inflate(board_subject_read.this,R.layout.dialog,null);
                final AlertDialog ad=new AlertDialog.Builder(board_subject_read.this)
                        .setTitle("댓글 삭제를 원하시면 비밀번호를 입력하세요")
                        .setMessage(content)
                        .setView(pop_password)
                        .setPositiveButton("취소",null)
                        .setNegativeButton("삭제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                board_subject_read.DeleteData2 task = new board_subject_read.DeleteData2();
                                if(password.equals(pop_password.getText().toString())) {
                                    task.execute("http://119.201.56.98/delete_comment.php", id);//php수정필요한부분
                                    Toast.makeText(getApplicationContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                    adapter=new CommentViewAdapter();
                                    listView.setAdapter(adapter);
                                    getData2("http://119.201.56.98/select_comment.php");
                                }
                                else
                                {
                                    Toast.makeText(getApplicationContext(), "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        });

        Intent intent_sr = getIntent();
        check_id = intent_sr.getExtras().getString("id");// intent로 board_subject로부터 id 값을 가져옴
        Log.i("board_subject_read",check_id);
        getData("http://119.201.56.98/select_checked_post.php?id0="+check_id);////php수정필요한부분
        getData2("http://119.201.56.98/select_comment.php");//댓글불러옴
    }

    //댓글작성버튼 클릭
    public void write_comment(View v){
        InsertData task = new InsertData();
        task.execute("http://119.201.56.98/insert_comment.php",check_id,comment_edit.getText().toString(),comment_password.getText().toString());

        Toast.makeText(getApplicationContext(),"댓글작성완료",Toast.LENGTH_SHORT).show();
        comment_edit.setText("");
        comment_password.setText("");
        adapter=new CommentViewAdapter();
        listView.setAdapter(adapter);
        getData2("http://119.201.56.98/select_comment.php");

    }
    class DeleteData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(board_subject_read.this,
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

    class DeleteData2 extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(board_subject_read.this,
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
    public void bsr_del(View view)
    {
        EditText bsr_password = (EditText)findViewById(R.id.bsr_password);

        if(bsr_password.getText().toString().equals(password2check)) {
            board_subject_read.DeleteData task = new board_subject_read.DeleteData();
            task.execute("http://119.201.56.98/delete_post.php",check_id);//php수정필요한부분
            Toast.makeText(getApplicationContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();
            finish();
            //sql문을 통한 삭제코드 추가할것
        }
        else
        {
            Toast.makeText(getApplicationContext(), "비밀번호를 확인하세요.", Toast.LENGTH_SHORT).show();
        }

    }
    public void bsr_close(View view)
    {
        finish();
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
    public void getData2(String url){//댓글 불러옴
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
                myJSON2 = result;
                showList2();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }
    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(board_subject_read.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();

        }


        @Override
        protected String doInBackground(String... params) {


            String post_id = (String)params[1];
            String content = (String)params[2];
            String password = (String)params[3];

            String serverURL = (String)params[0];

            String postParameters = "post_id=" + post_id + "&content="+content + "&password="+password;

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

                bsr_title.setText(title);
                bsr_content.setText(content);
                bsr_subjectNumber.setText(number);
                password2check=password;
                break;


            }
        }catch (JSONException e){
            e.printStackTrace();
        }

    }
    protected void showList2(){
        try{

            JSONObject jsonObj = new JSONObject(myJSON2);
            comments = jsonObj.getJSONArray(TAG_RESULTS);
            for(int i =0;i< comments.length();i++){
                JSONObject c = comments.getJSONObject(i);
                String id = c.getString(TAG_ID);
                String post_id=c.getString(TAG__POST_ID);
                String content = c.getString(TAG_CONTENT);
                String password = c.getString(TAG_PASSWORD);

                if(post_id.equals(check_id))
                    adapter.addItem(id,post_id,content,password);

            }
            listView.setAdapter(adapter);
        }catch (JSONException e){
            e.printStackTrace();
        }

    }
}
