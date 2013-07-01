package be.philipluyckx.geocaching.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.location.Location;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;

import be.philipluyckx.geocaching.GeocachingApplication;
import be.philipluyckx.geocaching.database.GeoDatabaseProxy;
import be.philipluyckx.geocaching.datacomponents.GeoPoint;
import be.philipluyckx.geocaching.fragments.SettingsFragment;
import be.philipluyckx.geocaching.location.ILocationListener;

/**
 * TODO:
 * Create Compass Point which holds a GeoPoint + xy information
 * Redraw: first remove previous points, then draw new ones
 *         also for the distance circles
 *         hopefully this speeds up the process and makes it less intensive
 */

/**
 * Created by pluyckx on 6/26/13.
 */
public class Compass extends View implements ILocationListener {
  private static final DecimalFormat mDistanceFormatter = new DecimalFormat("#.0m");
  private static final Paint pPosition = new Paint();
  private static final Paint pCircle = new Paint();
  private static final Paint pPoint = new Paint();
  private static final Paint pDirection = new Paint();
  private static final Paint pDistanceText = new Paint();
  private static final Paint pPointText = new Paint();
  private static float distanceBetweenTextCircle;
  private ScaleGestureDetector mScaleDetector;
  private GeoDatabaseProxy buffer;
  private float mMaxDistance = 0;
  private String sMaxDistance;
  private String sHalfMaxDistance;
  private LatLng mPosition;
  private Smoother orientation;
  private double mRotate = 0.0;
  private boolean mTouching = false;
  private Context context;
  private boolean mRotateCompass;
  private boolean mDrawHeadingLine;
  private LocationListener locationListener = null;
  private HeadingListener headingListener = null;

  static {
    pPosition.setColor(Color.WHITE);
    pPosition.setStyle(Paint.Style.STROKE);
    pPosition.setStrokeWidth(2.0f);

    pPoint.setColor(Color.WHITE);
    pPoint.setStyle(Paint.Style.FILL);
    pPoint.setStrokeWidth(1.0f);

    pCircle.setColor(Color.WHITE);
    pCircle.setStyle(Paint.Style.STROKE);
    pCircle.setStrokeWidth(1.0f);

    pDirection.setColor(Color.RED);
    pDirection.setStyle(Paint.Style.STROKE);
    pDirection.setStrokeWidth(3.0f);

    pDistanceText.setColor(Color.DKGRAY);
    pDistanceText.setTextAlign(Paint.Align.CENTER);

    pPointText.setColor(Color.DKGRAY);
    pPointText.setTextAlign(Paint.Align.CENTER);
  }

  public Compass(Context context) {
    super(context);
    this.context = context;
  }

  public Compass(Context context, AttributeSet attrs) {
    super(context, attrs);
    this.context = context;
  }

  public Compass(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    this.context = context;
  }

  public void initialize() {
    mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    orientation = new Smoother(0.0, 0.5);

    pDistanceText.setTextSize(13 * GeocachingApplication.getApplication().getDisplayMetrics().scaledDensity);
    pPointText.setTextSize(13 * GeocachingApplication.getApplication().getDisplayMetrics().scaledDensity);

    distanceBetweenTextCircle = 5.0f * GeocachingApplication.getApplication().getDisplayMetrics().density;

    setMaxDistance(1000);

    GeocachingApplication.getApplication().getLocationManager().addListener(this);
  }

  public LocationListener setLocationListener(LocationListener listener) {
    LocationListener prev = locationListener;
    locationListener = listener;

    locationListener.onLocationChanged(mPosition.latitude, mPosition.longitude);

    return prev;
  }

  public HeadingListener setHeadingListener(HeadingListener listener) {
    HeadingListener prev = headingListener;
    headingListener = listener;

    headingListener.onHeadingChanged(orientation.getValue());

    return prev;
  }

  public void reloadSettings() {
    mRotateCompass = GeocachingApplication.getApplication().getPreferences().getBoolean(SettingsFragment.PREF_ROTATE_COMPASS, true);
    mDrawHeadingLine = GeocachingApplication.getApplication().getPreferences().getBoolean(SettingsFragment.PREF_DRAW_HEADING_LINE, true);

    invalidate();
  }

  public LatLng getLocation() {
    return mPosition;
  }

  public void cleanup() {
    GeocachingApplication.getApplication().getLocationManager().removeListener(this);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    mScaleDetector.onTouchEvent(event);
    if (event.getPointerCount() == 1 && event.getAction() == MotionEvent.ACTION_UP) {
      mTouching = false;
    } else if (event.getPointerCount() == 1) {
      float x = event.getX();
      float y = event.getY();

      float half_width = getWidth() / 2.0f;
      float half_height = getHeight() / 2.0f;

      x -= half_width;
      y -= half_height;

      mRotate = Math.PI / 2.0 - Math.atan2(y, x) + Math.PI;
      mTouching = true;
    }

    return true;
  }

  public float getMaxDistance() {
    return mMaxDistance;
  }

  public void setMaxDistance(float maxDistance) {
    mMaxDistance = maxDistance;

    sMaxDistance = mDistanceFormatter.format(mMaxDistance);
    sHalfMaxDistance = mDistanceFormatter.format(mMaxDistance / 2.0f);

    invalidate();
  }

  public LatLng getPosition() {
    return mPosition;
  }

  /**
   * Returns the current smoothed heading in radians
   * @return Heading in radians
   */
  public double getHeading() {
    return orientation.getValue();
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    canvas.save();
    int width = getWidth();
    int height = getHeight();

    float half_width = width / 2.0f;
    float half_height = height / 2.0f;
    float smallest = (half_width < half_height ? half_width : half_height);
    float x, y;
    double rotateAngle = 0.0;
    double angle;
    Rect bounds = new Rect();
    canvas.translate(half_width, half_height);

    if (!isInEditMode()) {
      GeoDatabaseProxy db = GeocachingApplication.getApplication().getDatabaseBuffer();

      if (!mTouching) {
        if (mRotateCompass) {
          rotateAngle = -orientation.getValue();
        }
      } else {
        rotateAngle = -mRotate;
      }
      canvas.rotate((float) (rotateAngle / Math.PI * 180.0));

      // draw heading line
      if (mDrawHeadingLine) {
        angle = orientation.getValue(); // this is the angle between y and the long side
        x = (float) (smallest * Math.sin(angle));
        y = (float) (-smallest * Math.cos(angle));
        canvas.drawLine(0.0f, 0.0f, x, y, pDirection);
      }

      // draw distance circles
      x = y = 0.0f;
      canvas.drawCircle(x, y, 10.0f, pPosition);
      canvas.drawCircle(x, y, smallest / 2, pCircle);
      canvas.drawCircle(x, y, smallest, pCircle);

      // draw distance circles text
      if (height > width) {
        y = -smallest / 2.0f - distanceBetweenTextCircle;
      } else {
        pDistanceText.getTextBounds(sHalfMaxDistance, 0, sHalfMaxDistance.length(), bounds);
        y = -smallest / 2.0f + distanceBetweenTextCircle + bounds.height();
      }

      canvas.drawText(sHalfMaxDistance, x, y, pDistanceText);

      if (height > width) {
        y = -smallest - distanceBetweenTextCircle;
      } else {
        pDistanceText.getTextBounds(sHalfMaxDistance, 0, sMaxDistance.length(), bounds);
        y = -smallest + distanceBetweenTextCircle + bounds.height();
      }

      canvas.drawText(sMaxDistance, x, y, pDistanceText);

      // draw points
      float results[] = new float[1];

      for (GeoPoint p : db) {
        if (p.isVisible()) {
          Location.distanceBetween(mPosition.latitude, mPosition.longitude,
                  p.getLocation().latitude, p.getLocation().longitude,
                  results);

          angle = Math.atan2(p.getLocation().latitude - mPosition.latitude,
                  p.getLocation().longitude - mPosition.longitude);

          results[0] = results[0] / mMaxDistance * smallest;

          if(results[0] > smallest) {
            results[0] = smallest;
            pPoint.setColor(Color.RED);
          } else if(results[0] < -smallest) {
            results[0] = -smallest;
            pPoint.setColor(Color.RED);
          } else {
            pPoint.setColor(Color.WHITE);
          }

          x = (float) (Math.cos(angle) * results[0]);
          y = (float) (Math.sin(angle) * results[0]);

          /*if (x + 5.0f >= smallest) {
            x = smallest;
            float dist = (float) (x / Math.cos(angle));
            y = (float) (Math.sin(angle) * dist);
          } else if (x - 5.0f <= -smallest) {
            x = -smallest;
            float dist = (float) (x / Math.cos(angle));
            y = (float) (Math.sin(angle) * dist);
          }

          if (y >= smallest) {
            y = smallest;
            float dist = (float) (y / Math.sin(angle));
            x = (float) (Math.cos(angle) * dist);
          } else if (y <= -smallest) {
            y = -smallest;
            float dist = (float) (y / Math.sin(angle));
            x = (float) (Math.cos(angle) * dist);
          }*/

          y = -y;

          canvas.drawCircle(x, y, 10.0f, pPoint);

          pPointText.getTextBounds(p.getName(), 0, p.getName().length(), bounds);
          y += distanceBetweenTextCircle + bounds.height();
          if (x + bounds.width() / 2.0f > half_width) {
            x = half_width - bounds.width() / 2.0f - 10.0f - distanceBetweenTextCircle;
          } else if (x - bounds.width() / 2.0f < -half_width) {
            x = -half_width + bounds.width() / 2.0f + 10.0f + distanceBetweenTextCircle;
          }

          if (y > half_height) {
            y = half_height - bounds.height() - distanceBetweenTextCircle;
          }

          canvas.drawText(p.getName(), x, y, pPointText);
        }
      }
    } else {
// draw heading line
      angle = 0.0; // this is the angle between y and the long side
      x = (float) (smallest * Math.sin(angle));
      y = (float) (-smallest * Math.cos(angle));
      canvas.drawLine(0.0f, 0.0f, x, y, pDirection);

      // draw distance circles
      x = y = 0.0f;
      canvas.drawCircle(x, y, 10.0f, pPosition);
      canvas.drawCircle(x, y, smallest / 2, pCircle);
      canvas.drawCircle(x, y, smallest, pCircle);
    }

    canvas.restore();
  }

  @Override
  public void locationChanged(double latitude, double longitude) {
    mPosition = new LatLng(latitude, longitude);
    invalidate();

    if (locationListener != null) {
      locationListener.onLocationChanged(latitude, longitude);
    }
  }

  @Override
  public void orientationChanged(double angle) {
    orientation.addValue(angle);
    invalidate();

    if (headingListener != null) {
      headingListener.onHeadingChanged(angle);
    }
  }

  public interface LocationListener {
    public void onLocationChanged(double latitude, double longitude);
  }

  public interface HeadingListener {
    public void onHeadingChanged(double heading);
  }

  private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
      float newDistance = mMaxDistance / detector.getScaleFactor();

      newDistance = Math.max(10.0f, newDistance);
      setMaxDistance(newDistance);

      return true;
    }
  }

  private class Smoother {
    private double value;
    private double alpha;
    private double beta;

    public Smoother(double initialValue, double factor) {
      this.value = initialValue;
      this.alpha = factor;
      this.beta = (1.0 - factor);
    }

    public double getValue() {
      return value;
    }

    public double addValue(double value) {
      this.value = value * alpha + this.value * beta;

      return this.value;
    }
  }
}
