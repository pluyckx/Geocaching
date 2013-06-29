package be.philipluyckx.geocaching.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.Menu;

import be.philipluyckx.geocaching.GeocachingApplication;
import be.philipluyckx.geocaching.R;
import be.philipluyckx.geocaching.fragments.CompassFragment;
import be.philipluyckx.geocaching.fragments.DatabaseFragment;
import be.philipluyckx.geocaching.fragments.SettingsFragment;
import be.philipluyckx.geocaching.database.GeoDatabaseBuffer;
import be.philipluyckx.geocaching.datacomponents.GeoPoint;

public class MainActivity extends FragmentActivity {
  private static final String TAG = "MainActivity";
  private Handler testHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      buffer.readFromDatabase();
      for (GeoPoint p : buffer) {
        Log.d(TAG, p.toString());
      }
    }
  };
  private GeoDatabaseBuffer buffer;
  private FragmentTabHost mTabHost;

  @Override
  protected void onResume() {
    super.onResume();

    ((GeocachingApplication)getApplication()).getLocationManager().startListening();
  }

  @Override
  protected void onPause() {
    ((GeocachingApplication)getApplication()).getLocationManager().stopListening();

    super.onPause();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    buffer = ((GeocachingApplication) getApplication()).getDatabaseBuffer();
    testHandler.sendEmptyMessage(0);

    mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
    mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

    mTabHost.addTab(mTabHost.newTabSpec("Compass").setIndicator("Compass"), CompassFragment.class, null);
    mTabHost.addTab(mTabHost.newTabSpec("Database").setIndicator("Database"), DatabaseFragment.class, null);
    mTabHost.addTab(mTabHost.newTabSpec("Settings").setIndicator("Settings"), SettingsFragment.class, null);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }
}
