package be.philipluyckx.geocaching.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import be.philipluyckx.geocaching.GeocachingApplication;
import be.philipluyckx.geocaching.R;

/**
 * Created by pluyckx on 6/24/13.
 */
public class SettingsFragment extends Fragment {
  public static final String PREF_ROTATE_COMPASS = "rotate_compass";
  public static final String PREF_DRAW_HEADING_LINE = "draw_heading_line";

  private CheckBox mRotateCompass;
  private CheckBox mDrawHeadingLine;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.settings_layout, container, false);

    mRotateCompass = (CheckBox)v.findViewById(R.id.cb_compass_rotate);
    mDrawHeadingLine = (CheckBox)v.findViewById(R.id.cb_compass_heading_line);

    mRotateCompass.setChecked(GeocachingApplication.getApplication().getPreferences().getBoolean(PREF_ROTATE_COMPASS, true));
    mDrawHeadingLine.setChecked(GeocachingApplication.getApplication().getPreferences().getBoolean(PREF_DRAW_HEADING_LINE, true));

    mRotateCompass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        SharedPreferences.Editor editor = GeocachingApplication.getApplication().getPreferences().edit();
        editor.putBoolean(PREF_ROTATE_COMPASS, b);
        editor.commit();
      }
    });
    mDrawHeadingLine.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        SharedPreferences.Editor editor = GeocachingApplication.getApplication().getPreferences().edit();
        editor.putBoolean(PREF_DRAW_HEADING_LINE, b);
        editor.commit();
      }
    });

    return v;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }
}
