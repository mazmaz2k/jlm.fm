package fm.jlm.seproj.com.jlmfm;
import android.view.View;
import android.view.Menu;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.widget.Button;
import android.widget.ImageView;
import java.io.IOException;

import android.view.MenuItem;


public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
   // private Toolbar toolbar;
    private final String stream = "http://uk6.internet-radio.com:8418/live";
    private Button play;
    private ImageView re;
    private ImageView bl;
    private Button mute;

   
    private boolean prepared;
    private boolean isPressed;
    private boolean started;

    private MediaPlayer radio;
    private static final int noficationID = 583321;
    private NotificationCompat.Builder notification;
    private Uri soundURI;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        Button exitApp;

        soundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        prepared = false;
        started = true;
        isPressed =true;
        play = (Button) findViewById(R.id.playBtn);
        exitApp = (Button) findViewById(R.id.exitBtn);
        play.setEnabled(false);
        exitApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDestroy();
                finish();
                //System.exit(0);
            }
        });
        //play.setText("LOADING..");
        mute=(Button)  findViewById(R.id.muteBtn);
        mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioManager amanager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
               if(isPressed) {
                   isPressed =false;
                    mute.setBackgroundResource(R.drawable.unmute);
                   amanager.setStreamVolume(AudioManager.STREAM_MUSIC,0, AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND);
               }else{
                   isPressed =true;
                   mute.setBackgroundResource(R.drawable.mute2);
                   amanager.setStreamVolume(AudioManager.STREAM_MUSIC,amanager.getStreamMaxVolume(AudioManager.STREAM_RING), AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND);

               }
            }
        });
        re =(ImageView) findViewById(R.id.imgRed);
        bl =(ImageView) findViewById(R.id.imgBlack);
        radio = new MediaPlayer();
        radio.setAudioStreamType(AudioManager.STREAM_MUSIC);
        play.setOnClickListener(this);
        new PlayerTask().execute(stream);
        notification = new NotificationCompat.Builder(MainActivity.this);
        notification.setAutoCancel(true);
        changeLight(started);






    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_aboutD) {
            this.finish();
            startActivityForResult(new Intent(MainActivity.this,AboutD.class),1);

            return true;
        }else if (id == R.id.action_facebook) {
            this.finish();
            Uri uri = Uri.parse("http://www.facebook.com"); // missing 'http://' will cause crashed
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            return true;
        }else if (id == R.id.action_aboutJ){
            this.finish();
            startActivityForResult(new Intent(MainActivity.this,About.class),1);
            this.finish();
            return true;
        }else if (id == R.id.action_user){
           // this.finish();
            //startActivityForResult(new Intent(MainActivity.this,MainActivity.class),1);
            return true;
        }
        else if (id == R.id.go_to_web){
            Uri uri = Uri.parse("http://www.jlm.fm"); // missing 'http://' will cause crashed
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void changeLight(boolean flag){

        if(flag) {
            re.setVisibility(View.VISIBLE);
            bl.setVisibility(View.INVISIBLE);
        }
        else{
            re.setVisibility(View.INVISIBLE);
            bl.setVisibility(View.VISIBLE);
        }
    }
    protected void showNotification()
    {
        notification.setSmallIcon(R.drawable.icon);
        notification.setTicker("This is a ticker");
        notification.setContentTitle("JLM.FM");
        notification.setContentText("Hope to see you soon.");
        notification.setSound(soundURI);
        notification.setWhen(System.currentTimeMillis());

        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        PendingIntent p = PendingIntent.getActivity(MainActivity.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(p);
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(noficationID,notification.build());

    }
    @Override
    public void onClick(View v)
    {

        if(started) {
            started = false;
            radio.pause();
           // play.setText("PLAY");

        }
        else
        {
            started = true;
            radio.start();
           // play.setText("PAUSE");

        }
        changeLight(started);


    }

    protected void onDestroy()
    {
        showNotification();
        super.onDestroy();
        if(prepared){
            radio.release();
        }
    }

    class PlayerTask extends AsyncTask<String, Void, Boolean>
    {
        @Override
        protected Boolean doInBackground(String... params) {
            try {
                radio.setDataSource(params[0]);
                radio.prepare();

                prepared = true;
            }catch(IOException e){
                e.printStackTrace();
            }
            return prepared;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            radio.start();
            play.setEnabled(true);

           // changeLight(started);
           // play.setText("PAUSE");

        }
    }


}
