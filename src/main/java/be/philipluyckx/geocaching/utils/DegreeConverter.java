package be.philipluyckx.geocaching.utils;

import java.text.DecimalFormat;

/**
 * Created by pluyckx on 6/25/13.
 */
public class DegreeConverter {
  private static final DecimalFormat mSecondsFormat = new DecimalFormat("00.0000");
  private static final DecimalFormat mDegreeFormat = new DecimalFormat("000");
  private static final DecimalFormat mMinutesFormat = new DecimalFormat("000");
  private static final DecimalFormat mStringDegreeFormat = new DecimalFormat("000.000000");
  private static final DecimalFormat mStringMinutesFormat = new DecimalFormat("00.00000");

  private DegreeConverter() {
  }

  public static String toString(double deg) {
    long degree = (long) deg;
    deg = deg - degree;

    long minutes = (long) (deg * 60.0);
    deg = deg - (minutes / 60.0);

    double seconds = deg * 60.0 * 60.0;

    StringBuilder sb = new StringBuilder();
    sb.append(mDegreeFormat.format(degree));
    sb.append('\u00B0');
    sb.append(mMinutesFormat.format(minutes));
    sb.append('\'');
    sb.append(mSecondsFormat.format(seconds));
    sb.append('"');

    return sb.toString();
  }

  public static String toCompassDirection(double angleRad) {
    while (angleRad < 0.0) {
      angleRad += Math.PI * 2;
    }

    String possibleValues[] = new String[]{"N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW"};
    double piece = Math.PI / 2.0 / 2.0 / 2.0;
    double testAngle = piece / 2.0;
    int index = 0;

    while(angleRad > testAngle) {
      index++;
      testAngle += piece;
    }

    return possibleValues[index % 16];
  }

  public static double toSmallestPositiveAngle(double angleRad) {
    double fullCircle = 2.0 * Math.PI;

    while(angleRad < 0.0) {
      angleRad += fullCircle;
    }

    while(angleRad > fullCircle) {
      angleRad -= fullCircle;
    }

    return angleRad;
  }

  public static void toStringParts(double degree, String[] dest) {
    if (dest != null) {
      switch (dest.length) {
        case 0:
          break;
        case 1:
          toStringDegree(degree, dest);
          break;
        case 2:
          toStringMinutes(degree, dest);
          break;
        default:
          toStringSeconds(degree, dest);
      }
    }
  }

  private static void toStringDegree(double deg, String[] dest) {
    dest[0] = mStringDegreeFormat.format(deg);
  }

  private static void toStringMinutes(double deg, String[] dest) {
    long degree = (long) deg;
    deg = deg - degree;

    double minutes = deg * 60.0;

    dest[0] = mDegreeFormat.format(degree);
    dest[1] = mStringDegreeFormat.format(minutes);
  }

  private static void toStringSeconds(double deg, String[] dest) {
    long degree = (long) deg;
    deg = deg - degree;

    long minutes = (long) (deg * 60.0);
    deg = deg - (minutes / 60.0);

    double seconds = deg * 60.0 * 60.0;

    dest[0] = mDegreeFormat.format(degree);
    dest[1] = mMinutesFormat.format(minutes);
    dest[2] = mSecondsFormat.format(seconds);
  }

  public static double toDouble(String degree, String minutes, String seconds) {
    double location = 0.0;
    double tmp = Double.parseDouble(degree);
    location += tmp;

    tmp = Double.parseDouble(minutes);
    location += tmp / 60;

    tmp = Double.parseDouble(seconds);
    location += tmp / 60 / 60;

    return location;
  }
}
