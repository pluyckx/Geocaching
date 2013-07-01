package be.philipluyckx.geocaching.database.transactions;

import android.database.sqlite.SQLiteDatabase;

import be.philipluyckx.geocaching.database.schemes.DatabaseScheme;
import be.philipluyckx.geocaching.datacomponents.GeoPoint;

/**
 * Created by Philip on 22/06/13.
 */
public class TransactionRemove implements Transaction {
  public static final String TYPE = "Remove";
  private GeoPoint point;

  public TransactionRemove(GeoPoint point) {
    if (point == null) {
      throw new IllegalArgumentException("point may not be null!");
    }
    this.point = point;
  }

  public String getType() {
    return TransactionRemove.TYPE;
  }

  public boolean execute(SQLiteDatabase database) {
    if(database == null) {
      return false;
    }

    int ret = database.delete(DatabaseScheme.TABLE_GEO_POINTS, "id == ?", new String[] { Long.toString(point.getId() )});
    return (ret == 1);
  }
}
