package com.radio.fm.jlm.jlmfm2;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener ,View.OnClickListener{

    MongoCollection<BasicDBObject> collection;
    MongoCursor iterator;
    private final String stream = "http://uk6.internet-radio.com:8418/live";
    private Button play;
    private ImageView re;
    private ImageView bl;
    private Button mute;
    private boolean prepared;
    private boolean isPressed;
    private Button share;
    private Time time;
    private boolean started;
    private Thread t;
    public static boolean notificationB=true;
    private MediaPlayer radio;
    private static final int noficationID = 583321;
    private NotificationCompat.Builder notification;
    private Uri soundURI;
    private boolean doubleTap = false;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;

    //timer
    private Handler customHandler = new Handler();
    private long startTime = 0L;

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            Time t =new Time();
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;
            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            int milliseconds = (int) (updatedTime % 1000);
            customHandler.postDelayed(this, 0);
            int min=t.getMinutes();
            if((min==0)||(mins==59)) {
                nextPic();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //start timer
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 30000);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        //.setAction("Action", null).show();
                sendEmail();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);




        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        soundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        prepared = false;
        started = true;
        isPressed =true;
        play = (Button) findViewById(R.id.playBtn);

        play.setEnabled(false);
        share=(Button) findViewById(R.id.shareBtn);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareIt();
            }
        });

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
                    mute.setBackgroundResource(R.drawable.mute);
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



        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        MongoClientURI uri  = new MongoClientURI("mongodb://doron_97:Doron97@ds155961.mlab.com:55961/pictures_db");
        MongoClient client = new MongoClient(uri);
        MongoDatabase db = client.getDatabase(uri.getDatabase());

        collection = db.getCollection("pictures_db", BasicDBObject.class);

        BasicDBObject document = new BasicDBObject();

        //INSERT QUERIES
        /*
        document.put("id", "1");
        document.put("link", "http://www.hindustantimes.com/rf/image_size_960x540/HT/p2/2016/12/11/Pictures/radio_7417c9bc-bf69-11e6-9409-56819dc9550f.jpg");
        collection.insertOne(document);

        document = new BasicDBObject();
        document.put("id", "2");
        document.put("link", "http://cdn.interestingengineering.com/wp-content/uploads/2016/06/6138240034_a4ea109e99_b.jpg");
        collection.insertOne(document);

        document = new BasicDBObject();
        document.put("id", "3");
        document.put("link", "http://www.wxyz-radio.com/Images/mike-on-off-air-sign.gif");
        collection.insertOne(document);
        */
        iterator = collection.find().iterator();
        nextPic();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } /*else {
            super.onBackPressed();
        }*/
        if(!doubleTap) {
            Toast.makeText(MainActivity.this, "Please press BACK again to exit", Toast.LENGTH_SHORT).show();
            doubleTap = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleTap = false;
                }
            }, 3000);
        }else{
            finish();
            System.exit(0);
        }
    }




    public void nextPic(){
        //SELECT QUERY
        if (iterator.hasNext()) {
            new DownloadImageTask((ImageView) findViewById(R.id.imageView1))
                    .execute(((BasicDBObject) iterator.next()).getString("link"));
        }else{
            iterator = collection.find().iterator();
        }

    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       /* if (id == R.id.action_settings) {
            return true;
        }*/

       /* if (id == R.id.action_aboutD) {   //noinspection SimplifiableIfStatement
            // this.finish();
            startActivityForResult(new Intent(MainActivity.this,AboutUs.class),1);

            return true;
        }else*/ if (id == R.id.action_facebook) {
            this.finish();
            Uri uri = Uri.parse("https://www.facebook.com/jlm.fm/"); // missing 'http://' will cause crashed
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            finish();
            return true;
        }else if (id == R.id.action_aboutJ){
            // this.finish();
            startActivityForResult(new Intent(MainActivity.this,AboutUs.class),1);
            //this.finish();
            return true;
        }else if (id == R.id.action_user){
            // this.finish();
            //startActivityForResult(new Intent(MainActivity.this,MainActivity.class),1);
            //startActivityForResult(new Intent(MainActivity.this,Play.class),1);
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


        notification.setSound(soundURI);
        notification.setSmallIcon(R.drawable.icon);
        notification.setTicker("This is a ticker");
        notification.setContentTitle("JLM.FM");
        notification.setContentText("Hope to see you soon.");

        notification.setWhen(System.currentTimeMillis());

        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        PendingIntent p = PendingIntent.getActivity(MainActivity.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(p);
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(noficationID,notification.build());
        //notification.setSound(soundURI);


    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

       /* if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {
            startActivityForResult(new Intent(MainActivity.this,picSlider.class),1);

        } else */if (id == R.id.nav_manage) {
            startActivityForResult(new Intent(MainActivity.this,SettingsActivity.class),1);
            //finish();

        } else if (id == R.id.nav_share) {
            shareIt();

        } else if (id == R.id.nav_send) {
           // startActivityForResult(new Intent(MainActivity.this,sendingEmail.class),1);
            sendEmail();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void shareIt() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Here is the share content body";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }
    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        //if (R.id.ShowAllNotificationNotification == id)


        if(started) {
            started = false;
            radio.pause();
            // play.setText("PLAY");
            //startActivityForResult(new Intent(MainActivity.this,Main2Activity.class),1);
            play.setBackgroundResource(R.drawable.play_2);

        }
        else
        {
            started = true;
            radio.start();
            // play.setText("PAUSE");
            // startActivityForResult(new Intent(MainActivity.this,Main2Activity.class),1);
            play.setBackgroundResource(R.drawable.play_disabled);


        }
        changeLight(started);


    }

    protected void onDestroy()
    {
        if(notificationB) {
            showNotification();
        }
        //super.onDestroy();
        // if(prepared){
        radio.release();
        //}
        super.onDestroy();
        //finish();
        // System.exit(0);
    }
    private void sendEmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO)
                .setData(new Uri.Builder().scheme("mailto").build())
                .putExtra(Intent.EXTRA_EMAIL, new String[]{ "John Smith <johnsmith@yourdomain.com>" })
                .putExtra(Intent.EXTRA_SUBJECT, "Email subject")
                .putExtra(Intent.EXTRA_TEXT, "Email body")
                ;

        ComponentName emailApp = intent.resolveActivity(getPackageManager());
        ComponentName unsupportedAction = ComponentName.unflattenFromString("com.android.fallback/.Fallback");
        if (emailApp != null && !emailApp.equals(unsupportedAction))
            try {
                // Needed to customise the chooser dialog title since it might default to "Share with"
                // Note that the chooser will still be skipped if only one app is matched
                Intent chooser = Intent.createChooser(intent, "Send email with");
                startActivity(chooser);
                return;
            }
            catch (ActivityNotFoundException ignored) {
            }

        Toast
                .makeText(this, "Couldn't find an email app and account", Toast.LENGTH_LONG)
                .show();
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
