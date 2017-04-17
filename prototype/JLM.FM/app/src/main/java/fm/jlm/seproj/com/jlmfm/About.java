package fm.jlm.seproj.com.jlmfm;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by Omri on 09/04/2017.
 */

public class About extends  AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(myToolbar);


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
            startActivityForResult(new Intent(About.this,AboutD.class),1);
            this.finish();
            return true;
        } else if (id == R.id.action_facebook) {
           // this.finish();
            Uri uri = Uri.parse("http://www.facebook.com"); // missing 'http://' will cause crashed
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_aboutJ) {
            //startActivityForResult(new Intent(About.this,About.class),1);
            // this.finish();
            return true;
        } else if (id == R.id.action_user) {
            this.finish();
            startActivityForResult(new Intent(About.this, MainActivity.class), 1);
            return true;
        } else if (id == R.id.go_to_web) {
            Uri uri = Uri.parse("http://www.jlm.fm"); // missing 'http://' will cause crashed
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
           // this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}