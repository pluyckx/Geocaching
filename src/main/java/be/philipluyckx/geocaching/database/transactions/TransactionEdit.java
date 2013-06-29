package be.philipluyckx.geocaching.database.transactions;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import be.philipluyckx.geocaching.database.schemes.DatabaseScheme;
import be.philipluyckx.geocaching.datacomponents.GeoPoint;

/**
 * Created by Philip on 22/06/13.
 */
public class TransactionEdit implements Transaction {
  public static final String TYPE = "Edit";
  private GeoPoint oldPoint;
  private GeoPoint newPoint;

  public TransactionEdit(GeoPoint oldPoint, GeoPoint newPoint) {
    if(oldPoint == null || newPoint == null) {
      throw new IllegalArgumentException("oldPoint and newPoint may not be null!");
    }
    this.oldPoint = oldPoint;
    this.newPoint = newPoint;
  }

  public String getType() {
    return TransactionEdit.TYPE;
  }

  public boolean execute(SQLiteDatabase database) {
    ContentValues values = new ContentValues();
    values.put("name", newPoint.getName());
    values.put("latitude", newPoint.getLocation().latitude);
    values.put("longitude", newPoint.getLocation().longitude);
    values.put("visible", newPoint.isVisible());

    int ret = database.update(DatabaseScheme.TABLE_GEO_POINTS, values, "id == ?", new String[] { Long.toString(oldPoint.getId()) });

    newPoint.edit().setId(oldPoint.getId());

    return (ret == 1);
  }
}
