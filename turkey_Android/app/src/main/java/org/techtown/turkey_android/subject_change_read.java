package org.techtown.turkey_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class subject_change_read extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_change_read);
        TextView scr_title = (TextView)findViewById(R.id.scr_title);
        TextView scr_content = (TextView)findViewById(R.id.scr_content);
        //sql을 이용한 read 로 텍스트 다바꿀것
        scr_title.setText("");
        scr_content.setText("");
    }

    public void scr_del(View view)
    {
        EditText scr_password = (EditText)findViewById(R.id.scr_password);


        if(true) {
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
}
