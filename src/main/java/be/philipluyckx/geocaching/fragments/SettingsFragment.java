package be.philipluyckx.geocaching.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import be.philipluyckx.geocaching.GeocachingApplication;
import be.philipluyckx.geocaching.R;

/**
 * Created by pluyckx on 6/24/13.
 */
public class SettingsFragment extends Fragment {
  public static final String PREF_ROTATE_COMPASS = "rotate_compass";
  public static final String PREF_DRAW_HEADING_LINE = "draw_heading_line";
  public static final String PREF_OUTER_DISTANCE = "outer_distance";
  public static final String PREF_LOC_UPDATE_SPEED = "loc_update_speed";

  private CheckBox mRotateCompass;
  private CheckBox mDrawHeadingLine;
  private EditText mMaxDistance;
  private EditText mLocSpeedUpdate;

  @Override
  public void onPause() {
    SharedPreferences.Editor editor = GeocachingApplication.getApplication().getPreferences().edit();
    editor.putBoolean(PREF_ROTATE_COMPASS, mRotateCompass.isChecked());
    editor.putBoolean(PREF_DRAW_HEADING_LINE, mDrawHeadingLine.isChecked());
    editor.putString(PREF_OUTER_DISTANCE, mMaxDistance.getText().toString());
    editor.putString(PREF_LOC_UPDATE_SPEED, mLocSpeedUpdate.getText().toString());
    editor.commit();

    GeocachingApplication.getApplication().getLocationManager().stopListening();
    GeocachingApplication.getApplication().getLocationManager().startListening();

    super.onPause();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.settings_layout, container, false);

    mRotateCompass = (CheckBox)v.findViewById(R.id.cb_compass_rotate);
    mDrawHeadingLine = (CheckBox)v.findViewById(R.id.cb_compass_heading_line);
    mMaxDistance = (EditText)v.findViewById(R.id.et_outer_distance);
    mLocSpeedUpdate = (EditText)v.findViewById(R.id.et_loc_update_speed);

    mRotateCompass.setChecked(GeocachingApplication.getApplication().getPreferences().getBoolean(PREF_ROTATE_COMPASS, true));
    mDrawHeadingLine.setChecked(GeocachingApplication.getApplication().getPreferences().getBoolean(PREF_DRAW_HEADING_LINE, true));
    mMaxDistance.setText(GeocachingApplication.getApplication().getPreferences().getString(PREF_OUTER_DISTANCE, "1000"));
    mLocSpeedUpdate.setText(GeocachingApplication.getApplication().getPreferences().getString(PREF_LOC_UPDATE_SPEED, "2000"));

    return v;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }
}
