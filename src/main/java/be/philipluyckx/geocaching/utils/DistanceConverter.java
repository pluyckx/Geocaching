package be.philipluyckx.geocaching.utils;

import java.text.DecimalFormat;

/**
 * Created by Philip on 29/06/13.
 */
public class DistanceConverter {
  public static int DISTANCE_AUTO = 0;
  public static int DISTANCE_METER = 1;
  public static int DISTANCE_KILOMETER = 2;

  private static final DecimalFormat mMeterFormatter = new DecimalFormat("0.0m");
  private static final DecimalFormat mKilometerFormatter = new DecimalFormat("0.00km");

  private DistanceConverter() {
  }

  public static String toString(double distance, int sourceUnit, int destUnit) {
    if(sourceUnit == DISTANCE_KILOMETER) {
      if(destUnit == DISTANCE_KILOMETER) {
        return mKilometerFormatter.format(distance);
      } else if(destUnit == DISTANCE_METER) {
        return mMeterFormatter.format(distance * 1000.0);
      } else {
        if(distance < 1.5) {
          return mMeterFormatter.format(distance * 1000);
        } else {
          return mKilometerFormatter.format(distance);
        }
      }
    } else {
      if(destUnit == DISTANCE_KILOMETER) {
        return mKilometerFormatter.format(distance / 1000.0);
      } else if(destUnit == DISTANCE_METER) {
        return mMeterFormatter.format(distance);
      } else {
        if(distance < 1500.0) {
          return mMeterFormatter.format(distance);
        } else {
          return mKilometerFormatter.format(distance / 1000.0);
        }
      }
    }
  }

  public static String toMeterString(double distanceInMeter) {
    return mMeterFormatter.format(distanceInMeter);
  }

  public  static String toKilometerString(double distanceInMeter) {
    return mMeterFormatter.format(distanceInMeter / 1000.0);
  }
}
