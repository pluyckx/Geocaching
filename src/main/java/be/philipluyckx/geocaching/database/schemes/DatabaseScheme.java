package be.philipluyckx.geocaching.database.schemes;

/**
 * Created by Philip on 22/06/13.
 */
public interface DatabaseScheme {
  public static final String TABLE_GEO_POINTS = "geo_points";

  public int getVersion();

  public String toCreateStatement(String table);
}
