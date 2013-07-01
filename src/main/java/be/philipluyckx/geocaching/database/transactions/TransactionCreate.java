package be.philipluyckx.geocaching.database.transactions;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import be.philipluyckx.geocaching.database.schemes.DatabaseScheme;
import be.philipluyckx.geocaching.datacomponents.GeoPoint;

/**
 * Created by Philip on 22/06/13.
 */
public class TransactionCreate implements Transaction {
  public static final String TYPE = "Create";
  private GeoPoint point;

  public TransactionCreate(GeoPoint point) {
    if (point == null) {
      throw new IllegalArgumentException("point may not be null!");
    }
    this.point = point;
  }

  public String getType() {
    return TransactionCreate.TYPE;
  }

  public boolean execute(SQLiteDatabase database) {
    if(database == null) {
      return false;
    }

    ContentValues values = new ContentValues();
    values.put("name", point.getName());
    values.put("latitude", point.getLocation().latitude);
    values.put("longitude", point.getLocation().longitude);
    values.put("visible", (point.isVisible() ? 1 : 0));

    long ret = database.insert(DatabaseScheme.TABLE_GEO_POINTS, null, values);

    Cursor c = database.query(DatabaseScheme.TABLE_GEO_POINTS,
            new String[]{"id"},
            "rowid == ?", new String[]{Long.toString(ret)},
            null, null, null, null);

    if(c.getCount() == 1) {
      c.moveToFirst();
      point.edit().setId(c.getLong(0));
    } else if(c.getCount() > 1) {
      throw new RuntimeException("Multiple rows with the same id are found! This is impossible!");
    } else {
      return false;
    }

    c.close();

    return true;
  }
}
