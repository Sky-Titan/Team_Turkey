package org.techtown.turkey_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class subject_change_write extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_change_write);
    }
    public void scw_add(View view)
    {
        EditText scw_title = (EditText)findViewById(R.id.scw_title);
        EditText scw_content = (EditText)findViewById(R.id.scw_content);
        EditText scw_subjectid = (EditText)findViewById(R.id.scw_subjectid);
        EditText scw_password = (EditText)findViewById(R.id.scw_password);
        String text4err="";
        if(scw_title.length()>0&&scw_content.length()>0&&scw_password.length()>0&&scw_subjectid.length()<10) {
            //서버에 추가하는 코드 필요
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
            if(scw_subjectid.length()<10) {
                if(text4err.length()>0)
                    text4err=text4err+", ";
                text4err = text4err + "과목코드";
            }
            text4err = text4err + "을(를) 확인하세요.";
            Toast.makeText(getApplicationContext(), text4err, Toast.LENGTH_SHORT).show();
        }
    }

}
