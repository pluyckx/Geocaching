package be.philipluyckx.geocaching.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import be.philipluyckx.geocaching.GeocachingApplication;
import be.philipluyckx.geocaching.R;
import be.philipluyckx.geocaching.datacomponents.GeoPoint;
import be.philipluyckx.geocaching.utils.DegreeConverter;

/**
 * Created by Philip on 24/06/13.
 */
public class ListItemView extends LinearLayout {
  private TextView mName;
  private TextView mLatitude;
  private TextView mLongitude;
  private CheckBox mVisible;

  private GeoPoint point;

  public ListItemView(Context context) {
    super(context);
  }

  public ListItemView(Context context, AttributeSet attributeSet) {
    super(context, attributeSet);
  }

  public ListItemView(Context context,AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();

    mName = (TextView)findViewById(R.id.tv_name);
    mLatitude = (TextView)findViewById(R.id.tv_latitude);
    mLongitude = (TextView)findViewById(R.id.tv_longitude);
    mVisible = (CheckBox)findViewById(R.id.cb_visible);

    mVisible.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if(point != null) {
          GeoPoint newPoint = new GeoPoint(point);
          newPoint.edit().isVisible(!point.isVisible());
          if(!GeocachingApplication.getApplication().getDatabaseBuffer().editPoint(point, newPoint)) {
            // mVisible.setChecked(!mVisible.isChecked());
            Toast.makeText(getContext(), R.string.msg_edit_point_visibility_error, Toast.LENGTH_LONG).show();
          }
        }
      }
    });
  }

  public void setPoint(GeoPoint point) {
    this.point = point;

    mName.setText(point.getName());

    String tmp = (point.getLocation().latitude < 0.0 ? "S " : "N ") + DegreeConverter.toString(Math.abs(point.getLocation().latitude));
    mLatitude.setText(tmp);

    tmp = (point.getLocation().longitude < 0.0 ? "W " : "E ") + DegreeConverter.toString(Math.abs(point.getLocation().longitude));
    mLongitude.setText(tmp);
    mVisible.setChecked(point.isVisible());
  }

  public GeoPoint getPoint() {
    return point;
  }
}
