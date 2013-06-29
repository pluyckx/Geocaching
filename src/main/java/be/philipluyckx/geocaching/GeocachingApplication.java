package be.philipluyckx.geocaching;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;

import be.philipluyckx.geocaching.database.GeoDatabase;
import be.philipluyckx.geocaching.database.GeoDatabaseBuffer;
import be.philipluyckx.geocaching.location.LocationManager;

/**
 * Created by Philip on 24/06/13.
 */
public class GeocachingApplication extends Application {
  private static GeocachingApplication app;

  private GeoDatabaseBuffer buffer = null;
  private DisplayMetrics mDisplayMetrics;
  private LocationManager mLocationManager;
  private SharedPreferences mPreferences;

  public GeocachingApplication() {
  }

  @Override
  public void onCreate() {
    super.onCreate();

    app = this;
    mDisplayMetrics = getApplicationContext().getResources().getDisplayMetrics();
    mLocationManager = new LocationManager();
  }

  public LocationManager getLocationManager() {
    return mLocationManager;
  }

  public SharedPreferences getPreferences() {
    if(mPreferences == null) {
      mPreferences = getSharedPreferences(GeocachingApplication.class.getName(), Context.MODE_PRIVATE);
    }

    return mPreferences;
  }

  public GeoDatabaseBuffer getDatabaseBuffer() {
    if(buffer == null) {
      buffer = new GeoDatabaseBuffer(new GeoDatabase(getApplicationContext()));
    }

    return buffer;
  }

  public DisplayMetrics getDisplayMetrics() {
    return mDisplayMetrics;
  }

  public static GeocachingApplication getApplication() {
    return app;
  }
}
