package be.philipluyckx.geocaching.fragments;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import be.philipluyckx.geocaching.GeocachingApplication;
import be.philipluyckx.geocaching.R;
import be.philipluyckx.geocaching.components.Compass;
import be.philipluyckx.geocaching.datacomponents.GeoPoint;
import be.philipluyckx.geocaching.dialogs.SelectInformationPointDialog;
import be.philipluyckx.geocaching.utils.DegreeConverter;
import be.philipluyckx.geocaching.utils.DistanceConverter;

/**
 * TODO:
 *   make a view that displays information for a selected point like distance and heading
 *   The point is selectable through a List in a dialog
 */

/**
 * Created by pluyckx on 6/24/13.
 */
public class CompassFragment extends Fragment {
  private static final int UPDATE_LOCATION = 1;
  private static final int UPDATE_HEADING = 2;
  private static final String KEY_LATITUDE = "latitude";
  private static final String KEY_LONGITUDE = "longitude";
  private static final String KEY_HEADING = "heading";
  private static Handler mUpdater = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      CompassFragment cf;

      switch (msg.what) {
        case CompassFragment.UPDATE_LOCATION:
          cf = (CompassFragment) msg.obj;
          cf.onSetLocation(msg.getData());
          break;
        case CompassFragment.UPDATE_HEADING:
          cf = (CompassFragment) msg.obj;
          cf.onSetHeading(msg.getData());
          break;
        default:
          super.handleMessage(msg);
      }
    }
  };

  private TableLayout mPointInformation;
  private Compass mCompass;
  private TextView mLatitude;
  private TextView mLongitude;
  private TextView mHeading;
  private TextView mPoint;
  private TextView mDistance;
  private TextView mDirection;

  @Override
  public void onResume() {
    super.onResume();

    mCompass.reloadSettings();
  }

  @Override
  public void onDestroy() {
    mCompass.cleanup();

    super.onDestroy();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.compass_layout, container, false);

    mPointInformation = (TableLayout)view.findViewById(R.id.tl_point);
    mPointInformation.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        onSelectPoint();
      }
    });

    mCompass = (Compass) view.findViewById(R.id.compass);
    mCompass.initialize();

    mLatitude = (TextView) view.findViewById(R.id.tv_latitude);
    mLongitude = (TextView) view.findViewById(R.id.tv_longitude);
    mHeading = (TextView) view.findViewById(R.id.tv_heading);

    mPoint = (TextView) view.findViewById(R.id.tv_point);
    mDistance = (TextView) view.findViewById(R.id.tv_distance);
    mDirection = (TextView) view.findViewById(R.id.tv_direction);

    Listener l = new Listener();
    mCompass.setLocationListener(l);
    mCompass.setHeadingListener(l);

    GeoPoint p = GeocachingApplication.getApplication().getDatabaseBuffer().getPoint(0);
    if (p != null) {
      mPoint.setText(p.getName());
    }

    return view;
  }

  public void setSelectedPoint(String point) {
    mPoint.setText(point);
    updatePointInformation();
  }

  public String getSelectetPoint() {
    return mPoint.getText().toString();
  }

  private void onSelectPoint() {
    SelectInformationPointDialog dialog = new SelectInformationPointDialog(this);
    dialog.show(getFragmentManager(), "select_point_dialog");
  }

  private void onSetLocation(Bundle data) {
    double latitude = data.getDouble(KEY_LATITUDE);
    double longitude = data.getDouble(KEY_LONGITUDE);

    String tmp = DegreeConverter.toString(Math.abs(latitude));
    mLatitude.setText((latitude < 0.0 ? "S " : "N ") + tmp);
    tmp = DegreeConverter.toString(Math.abs(longitude));
    mLongitude.setText((longitude < 0.0 ? "W " : "E ") + tmp);

    updatePointInformation();
  }

  private void updatePointInformation() {
    GeoPoint point = GeocachingApplication.getApplication().getDatabaseBuffer().getPoint(mPoint.getText().toString());
    if (point != null) {
      LatLng position = mCompass.getPosition();
      float results[] = new float[1];
      Location.distanceBetween(point.getLocation().latitude, point.getLocation().longitude, position.latitude, position.longitude, results);

      mDistance.setText(DistanceConverter.toString(results[0], DistanceConverter.DISTANCE_METER, DistanceConverter.DISTANCE_AUTO));

      double dLongitude = point.getLocation().longitude - position.longitude;
      double dLatitude = point.getLocation().latitude - position.latitude;
      double direction = Math.PI / 2.0 - Math.atan2(dLatitude, dLongitude);
      mDirection.setText(DegreeConverter.toCompassDirection(direction));
    } else {
      mPoint.setText("");
      mDirection.setText("");
      mDistance.setText("");
    }
  }

  private void onSetHeading(Bundle data) {
    double angle = DegreeConverter.toSmallestPositiveAngle(data.getDouble(KEY_HEADING));
    String direction = DegreeConverter.toCompassDirection(angle);
    String sAngle = DegreeConverter.toString(angle * 180 / Math.PI);

    mHeading.setText(direction + " " + sAngle);

    updatePointInformation();
  }

  private void setLocation(double latitude, double longitude) {
    Message msg = Message.obtain();
    msg.getData().putDouble(CompassFragment.KEY_LATITUDE, latitude);
    msg.getData().putDouble(CompassFragment.KEY_LONGITUDE, longitude);
    msg.what = CompassFragment.UPDATE_LOCATION;
    msg.obj = this;

    mUpdater.sendMessage(msg);
  }

  private void setHeading(double heading) {
    Message msg = Message.obtain();
    msg.getData().putDouble(CompassFragment.KEY_HEADING, heading);
    msg.what = CompassFragment.UPDATE_HEADING;
    msg.obj = this;

    mUpdater.sendMessage(msg);
  }

  private class Listener implements Compass.HeadingListener, Compass.LocationListener {
    @Override
    public void onHeadingChanged(double heading) {
      setHeading(heading);
    }

    @Override
    public void onLocationChanged(double latitude, double longitude) {
      setLocation(latitude, longitude);
    }
  }
}
