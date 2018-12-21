package org.techtown.turkey_android;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class VacancyNotification extends Service {
    String myJSON;

    private static final String TAG_RESULTS = "result";
    private static final String TAG_NUMBER = "number";
    private static final String TAG_TITLE = "title";
    private static final String TAG_PROFESSOR = "professor";
    private static final String TAG_TOTAL = "total";
    private static final String TAG_APPLICANT = "applicant";

    Cursor cursor;
    static final int G_NOTIFY_NUM = 1;

    String checkedNumber="";
    String checkedApplicant="";
    JSONArray lectures = null;
    SQLiteDatabase db;
    NotificationManager m_NotiManager;

    @Override
    public IBinder onBind(Intent intent) {
        // Service 객체와 (화면단 Activity 사이에서)
        // 통신(데이터를 주고받을) 때 사용하는 메서드
        // 데이터를 전달할 필요가 없으면 return null;
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
       // m_NotiManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Thread thread=new Thread(new VacancyNotify());
        thread.setDaemon(false);
        thread.start();
        startForeground(1,new Notification());
        // 서비스에서 가장 먼저 호출됨(최초에 한번만)

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 서비스가 호출될 때마다 실행

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);

    }

    class VacancyNotify implements Runnable{
        String myJSON;

        private static final String TAG_RESULTS = "result";
        private static final String TAG_NUMBER = "number";
        private static final String TAG_TITLE = "title";
        private static final String TAG_PROFESSOR = "professor";
        private static final String TAG_TOTAL = "total";
        private static final String TAG_APPLICANT = "applicant";

        Cursor cursor;
        static final int G_NOTIFY_NUM = 1;

        String checkedNumber="";
        String checkedApplicant="";
        JSONArray lectures = null;
        SQLiteDatabase db;
        NotificationManager  m_NotiManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        @Override
        public void run() {
            while(true)
            {
                loadDB();
                try
                {
                    Thread.sleep(3000);
                }
                catch (Exception e){

                }
            }
        }
        public void loadDB(){
            db = openOrCreateDatabase(
                    "test.db",
                    SQLiteDatabase.CREATE_IF_NECESSARY,
                    null);

            db.execSQL("CREATE TABLE IF NOT EXISTS lecture"
                    +"(number TEXT,applicant TEXT);");
            cursor= db.rawQuery("SELECT * FROM lecture",null);
            String uri="http://119.201.56.98/select_checked_lecture.php?";
            int i=0;
            while(cursor.moveToNext()){
                checkedNumber=cursor.getString(0);
                uri+="number"+i+"="+checkedNumber;
                if(i+1!=cursor.getCount())
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
        public void showList(){
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
                    //Toast.makeText(getApplicationContext(),number,Toast.LENGTH_SHORT).show();
                    cursor.moveToPosition(-1);
                    while (cursor.moveToNext()){
                        if(cursor.getString(0).equals(number))
                        {
                            checkedApplicant=cursor.getString(1);
                            break;
                        }
                    }
                    //checkedApplicant=cursor.getString(1);
                    //Toast.makeText(getApplicationContext(),checkedApplicant,Toast.LENGTH_SHORT).show();
                    if(Integer.parseInt(applicant)<Integer.parseInt(checkedApplicant))//현재 지원자 수 줄었을 경우
                    {
                        String channelId = "channel";
                        String channelName = "Channel Name";

                        m_NotiManager
                                = (NotificationManager) getSystemService  (Context.NOTIFICATION_SERVICE);


                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                            int importance = NotificationManager.IMPORTANCE_HIGH;

                            NotificationChannel mChannel = new NotificationChannel(
                                    channelId, channelName, importance);

                            m_NotiManager.createNotificationChannel(mChannel);

                        }

                        NotificationCompat.Builder builder =
                                new NotificationCompat.Builder(getApplicationContext(), channelId);

                        int icon=VacancyNotification.this.getApplicationInfo().icon;

                        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        PendingIntent content =PendingIntent.getActivity(getApplicationContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

                        builder.setContentTitle("공석 알림 : "+number+" "+title)
                                .setContentText("정원 : "+total+"석     "+checkedApplicant+"석->"+applicant+"석 공석 발생! 서두르세요")
                                .setSmallIcon(icon)
                                .setDefaults(Notification.DEFAULT_ALL)
                                .setBadgeIconType(R.drawable.appicon)
                                .setContentIntent(content)
                                .setAutoCancel(true);
                        m_NotiManager.notify(0,builder.build());

                        String sql="UPDATE lecture SET applicant='"+applicant+"' WHERE number='"+number+"';";
                        db.close();
                        db = openOrCreateDatabase(
                                "test.db",
                                SQLiteDatabase.CREATE_IF_NECESSARY,
                                null);
                        db.execSQL(sql);

                    }
                    else if(Integer.parseInt(applicant)>Integer.parseInt(checkedApplicant))
                    {
                        String sql="UPDATE lecture SET applicant='"+applicant+"' WHERE number='"+number+"';";
                        db.close();
                        db = openOrCreateDatabase(
                                "test.db",
                                SQLiteDatabase.CREATE_IF_NECESSARY,
                                null);
                        db.execSQL(sql);
                    }
                }

            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

}

