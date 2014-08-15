package com.aam.latecoffee;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.aam.latecoffee.LocationHandler;

import java.lang.Override;


public class MainActivity extends Activity {
                                                   //input   ????  output
    private class SendLocationTask extends AsyncTask<String, Void, HttpResponse> {

        private Exception exception;

        @Override
        protected HttpResponse doInBackground(String... in) {
            try {
                HttpPost httpPost = new HttpPost(in[0]);
                httpPost.setEntity(new StringEntity(in[1]));
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");
                return new DefaultHttpClient().execute(httpPost);

            } catch (Exception e) {
                this.exception = e;
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(HttpResponse response) {
            if (response != null) {
                textView.setText(response.getStatusLine().getReasonPhrase());
            } else {
                textView.setText(exception.getMessage());
            }
        }
    }

    private TextView textView = null;
    private LocationHandler locHandlr = null;
    private long lastTime = 0;

    public String getLocation() {
        return locHandlr.getLastLoc();
    }

    public void sendLocation() {
        String json = "{\"name\":\"Leo\", \"geolocation\":\"" + getLocation() + "\", \"timestamp\":"+ (System.currentTimeMillis()/1000) + "}";
        String url = "http://178.62.12.246/coffee_preference";
        new SendLocationTask().execute(new String[]{url, json});
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_screen);

        // save some defaults for testing
        SharedPreferences.Editor edit = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                .edit();
        edit.putString(getString(R.string.saved_ip), getString(R.string.default_ip));
        edit.putInt(getString(R.string.saved_port), 80);
        edit.commit();

        textView = (TextView) findViewById(R.id.detailsText);
        locHandlr = new LocationHandler(this);

        processLocation();

        timerHandler.postDelayed(timerRunnable, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerHandler.removeCallbacks(timerRunnable);
    }

    //runs without a timer by reposting this handler at the end of the runnable
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            processLocation();
            timerHandler.postDelayed(this, 500);
        }
    };

    public processLocation() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            if (lastTime < locHandlr.getLastTimestamp()) {
                lastTime = locHandlr.getLastTimestamp();
                sendLocation();
                Time now = new Time();
                now.setToNow();
                textView.setText("Sent data to server at " + now.format("%r"));
            }

        } else {
            // display error
            textView.setText("" + networkInfo.getState() + ": "+ networkInfo.getReason());
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}