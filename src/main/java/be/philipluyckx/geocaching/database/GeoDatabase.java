package be.philipluyckx.geocaching.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import be.philipluyckx.geocaching.database.schemes.DatabaseScheme;
import be.philipluyckx.geocaching.database.schemes.DatabaseSchemeV1;
import be.philipluyckx.geocaching.datacomponents.GeoPoint;

/**
 * Created by Philip on 22/06/13.
 */
public class GeoDatabase extends SQLiteOpenHelper {
  private static final String TAG = "GeoDatabase";
  private static final DatabaseScheme scheme = DatabaseSchemeV1.getScheme();

  public GeoDatabase(Context context) {
    super(context, "GeoCaching", null, 1);

    Log.d(TAG, "Created GeoDatabase object");
  }

  @Override
  public void onCreate(SQLiteDatabase sqLiteDatabase) {
    Log.d(TAG, scheme.toCreateStatement(DatabaseSchemeV1.TABLE_GEO_POINTS));
    Log.d(TAG, scheme.toCreateStatement(DatabaseSchemeV1.TABLE_VERSION));

    sqLiteDatabase.execSQL(scheme.toCreateStatement(DatabaseSchemeV1.TABLE_VERSION));
    sqLiteDatabase.execSQL(scheme.toCreateStatement(DatabaseSchemeV1.TABLE_GEO_POINTS));
    sqLiteDatabase.execSQL("INSERT INTO version VALUES(1);");
  }

  @Override
  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

  }
  
  public boolean readPoints(List<GeoPoint> points) {
    SQLiteDatabase db = getReadableDatabase();
    Cursor c = db.query(DatabaseScheme.TABLE_GEO_POINTS, null, null, null, null, null, "name ASC");
    c.moveToFirst();
    while(!c.isAfterLast()) {
      long id = c.getLong(c.getColumnIndex("id"));
      String name = c.getString(c.getColumnIndex("name"));
      double lat = c.getDouble(c.getColumnIndex("latitude"));
      double lon = c.getDouble(c.getColumnIndex("longitude"));
      int visible = c.getInt(c.getColumnIndex("visible"));

      points.add(new GeoPoint(id, name, new LatLng(lat, lon), (visible == 0 ? false : true)));
      c.moveToNext();
    }

    return true;
  }
}
