package be.philipluyckx.geocaching.location;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import be.philipluyckx.geocaching.GeocachingApplication;
import be.philipluyckx.geocaching.fragments.SettingsFragment;

/**
 * Created by pluyckx on 6/26/13.
 */
public class LocationManager implements LocationListener, SensorEventListener {

  private android.location.LocationManager mManager;
  private SensorManager mSensorManager;
  private Sensor mAccelerometer;
  private Sensor mMagneticField;

  private float mMagneticFieldData[] = new float[3];
  private float mAccelerometerData[] = new float[3];

  private double latitude;
  private double longitude;
  private List<ILocationListener> mListeners;

  public LocationManager() {
    mManager = (android.location.LocationManager) GeocachingApplication.getApplication().getSystemService(Context.LOCATION_SERVICE);
    mSensorManager = (SensorManager)GeocachingApplication.getApplication().getSystemService(Context.SENSOR_SERVICE);

    mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    mMagneticField = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

    latitude = 0.0;
    longitude = 0.0;

    mListeners = new ArrayList<ILocationListener>();
  }

  public void startListening() {
    long gpsUpdateSpeed;

    try {
      gpsUpdateSpeed = Long.parseLong(GeocachingApplication.getApplication().getPreferences().getString(SettingsFragment.PREF_LOC_UPDATE_SPEED, "2000"));
    } catch(RuntimeException ex) {
      gpsUpdateSpeed = 2000;
      Toast.makeText(GeocachingApplication.getApplication().getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG);
    }

    mManager.requestLocationUpdates(android.location.LocationManager.GPS_PROVIDER, gpsUpdateSpeed, 0, this);
    mSensorManager.registerListener(this, mAccelerometer, 5000000);
    mSensorManager.registerListener(this, mMagneticField, 5000000);
  }

  public void stopListening() {
    mManager.removeUpdates(this);
  }

  public void addListener(ILocationListener l) {
    mListeners.add(l);

    l.locationChanged(latitude, longitude);
  }

  public void removeListener(ILocationListener l) {
    mListeners.remove(l);
  }

  private void fireOnLocationChanged() {
    for(ILocationListener l : mListeners) {
      l.locationChanged(latitude, longitude);
    }
  }

  private void fireOnOrientationChanged() {
    float R[] = new float[9];
    float I[] = new float[9];
    boolean success = SensorManager.getRotationMatrix(R, I, mAccelerometerData.clone(), mMagneticFieldData.clone());
    if(success) {
      float orientation[] = new float[3];
      SensorManager.getOrientation(R, orientation);


      for(ILocationListener l : mListeners) {
        l.orientationChanged(orientation[0]);
      }
    }
  }

  public double getLatitude() {
    return latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public LatLng getLocation() {
    return new LatLng(latitude, longitude);
  }

  @Override
  public void onLocationChanged(Location location) {
    latitude =  location.getLatitude();
    longitude = location.getLongitude();

    fireOnLocationChanged();
  }

  @Override
  public void onStatusChanged(String provider, int status, Bundle extras) {

  }

  @Override
  public void onProviderEnabled(String provider) {

  }

  @Override
  public void onProviderDisabled(String provider) {

  }

  @Override
  public void onSensorChanged(SensorEvent event) {
    if(event.sensor.equals(mAccelerometer)) {
      mAccelerometerData[0] = event.values[0];
      mAccelerometerData[1] = event.values[1];
      mAccelerometerData[2] = event.values[2];

      fireOnOrientationChanged();
    }

    if(event.sensor.equals(mMagneticField)) {
      mMagneticFieldData[0] = event.values[0];
      mMagneticFieldData[1] = event.values[1];
      mMagneticFieldData[2] = event.values[2];

      fireOnOrientationChanged();
    }
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {

  }
}
