package be.philipluyckx.geocaching.location;

/**
 * Created by pluyckx on 6/26/13.
 */
public interface ILocationListener {
  public void locationChanged(double latitude, double longitude);
  public void orientationChanged(double angle);
}
