package be.philipluyckx.geocaching.datacomponents;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by pluyckx on 6/21/13.
 */
public class GeoPoint {
  private long id;
  private String name;
  private LatLng location;
  private boolean visible;

  public GeoPoint(String name, LatLng location, boolean visible) {
    this(-1, name, location, visible);
  }

  public GeoPoint(long id, String name, LatLng location, boolean visible) {
    this.id = id;
    this.name = name;
    this.location = location;
    this.visible = visible;
  }

  public GeoPoint(GeoPoint point) {
    this.id = point.id;
    this.name = point.name;
    this.location = point.location;
    this.visible = point.visible;
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public LatLng getLocation() {
    return location;
  }

  public boolean isVisible() {
    return visible;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();

    sb.append('(');
    sb.append(this.name);
    sb.append(", ");
    sb.append(location);
    sb.append(", ");
    sb.append(visible);
    sb.append(')');

    return sb.toString();
  }

  public boolean equals(Object o) {
    if (o instanceof GeoPoint) {
      GeoPoint gp = (GeoPoint) o;

      return gp.name.equals(name);
    } else {
      return false;
    }
  }

  public Editor edit() {
    return new Editor(this);
  }

  public static class Editor {
    private GeoPoint point;

    public Editor(GeoPoint point) {
      this.point = point;
    }

    public void setId(long id) {
      point.id = id;
    }

    public void setName(String name) {
      point.name = name;
    }

    public void setLocation(LatLng location) {
      point.location = location;
    }

    public void isVisible(boolean visible) {
      point.visible = visible;
    }
  }
}
