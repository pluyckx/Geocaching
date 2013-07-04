package be.philipluyckx.geocaching.components.compass;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import be.philipluyckx.geocaching.GeocachingApplication;
import be.philipluyckx.geocaching.datacomponents.GeoPoint;

/**
 * Created by Philip on 4/07/13.
 */
public class CompassPoint {
  private static Paint mCircleUnselected;
  private static Paint mCircleUnselectedFar;
  private static Paint mCircleSelected;
  private static Paint mCircleText;
  private GeoPoint mPoint;
  private boolean mSelected;
  private float mTextWidth;
  private float mTextHeight;
  private float mX;
  private float mY;

  public CompassPoint(GeoPoint point, boolean selected) {
    mPoint = point;
    mX = 0.0f;
    mY = 0.0f;
    mSelected = selected;

    initialize();

    Rect bounds = new Rect();
    mCircleText.getTextBounds(point.getName(), 0, point.getName().length(), bounds);
    mTextWidth = bounds.width();
    mTextHeight = bounds.height();
  }

  private void initialize() {
    if (mCircleUnselected == null || mCircleText == null) {
      mCircleUnselected = new Paint();
      mCircleUnselected.setColor(Color.WHITE);
      mCircleUnselected.setStyle(Paint.Style.FILL);
      mCircleUnselected.setStrokeWidth(1.0f);

      mCircleUnselectedFar = new Paint(mCircleUnselected);
      mCircleUnselectedFar.setColor(Color.RED);

      mCircleSelected = new Paint(mCircleUnselected);
      mCircleSelected.setColor(Color.GREEN);

      mCircleText = new Paint();
      mCircleText.setColor(Color.DKGRAY);
      mCircleText.setTextAlign(Paint.Align.CENTER);
      mCircleText.setTextSize(13 * GeocachingApplication.getApplication().getDisplayMetrics().scaledDensity);
    }
  }

  public void isSelected(boolean value) {
    mSelected = value;
  }

  public boolean isSelected() {
    return mSelected;
  }

  public void draw(Canvas canvas, float radius, float maxDistance, LatLng position) {
    if (mPoint.isVisible()) {
      float[] results = new float[1];
      Paint pointPaint = (mSelected ? mCircleSelected : mCircleUnselected);

      Location.distanceBetween(position.latitude, position.longitude,
              mPoint.getLocation().latitude, mPoint.getLocation().longitude,
              results);

      double angle = Math.atan2(mPoint.getLocation().latitude - position.latitude,
              mPoint.getLocation().longitude - position.longitude);

      results[0] = results[0] / maxDistance * radius;

      if (results[0] > radius) {
        results[0] = radius;
        if (!mSelected) {
          pointPaint = mCircleUnselectedFar;
        }
      } else if (results[0] < -radius) {
        results[0] = -radius;
        if (!mSelected) {
          pointPaint = mCircleUnselectedFar;
        }
      }

      mX = (float) (Math.cos(angle) * results[0]);
      mY = (float) (Math.sin(angle) * results[0]);

      mY = -mY;

      canvas.drawCircle(mX, mY, 10.0f, pointPaint);


      mY += Compass.distanceBetweenTextCircle + mTextHeight;
      if (mX + mTextWidth / 2.0f > radius) {
        mX = radius - mTextWidth / 2.0f - 10.0f - Compass.distanceBetweenTextCircle;
      } else if (mX - mTextWidth / 2.0f < -radius) {
        mX = -radius + mTextWidth / 2.0f + 10.0f + Compass.distanceBetweenTextCircle;
      }

      if (mY > radius) {
        mY = radius - mTextHeight - Compass.distanceBetweenTextCircle;
      }

      canvas.drawText(mPoint.getName(), mX, mY, mCircleText);
    }
  }

  public GeoPoint getPoint() {
    return mPoint;
  }

  public float getX() {
    return mX;
  }

  public void setX(float value) {
    mX = value;
  }

  public float getY() {
    return mY;
  }

  public void setY(float value) {
    mY = value;
  }

  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o instanceof CompassPoint) {
      CompassPoint cp = (CompassPoint) o;
      return (cp.mPoint.equals(mPoint));
    } else {
      return false;
    }
  }
}
