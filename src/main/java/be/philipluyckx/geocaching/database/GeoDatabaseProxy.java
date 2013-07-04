package be.philipluyckx.geocaching.database;

import android.util.Log;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

import be.philipluyckx.geocaching.database.transactions.Transaction;
import be.philipluyckx.geocaching.database.transactions.TransactionCreate;
import be.philipluyckx.geocaching.database.transactions.TransactionEdit;
import be.philipluyckx.geocaching.database.transactions.TransactionRemove;
import be.philipluyckx.geocaching.datacomponents.GeoPoint;

/**
 * Created by Philip on 22/06/13.
 */
public class GeoDatabaseProxy extends Observable implements Iterable<GeoPoint> {
  private static final String TAG = "GeoDatabaseProxy";
  private GeoDatabase mDatabase;
  private List<Transaction> mTransactions;
  private SortedKeyedList<String, GeoPoint> mPoints;

  public GeoDatabaseProxy(GeoDatabase database) {
    this.mDatabase = database;
    database.getWritableDatabase().close();

    Log.v(TAG, "[constructor] Creating transaction list");

    this.mTransactions = new LinkedList<Transaction>();
    this.mPoints = new SortedKeyedList<String, GeoPoint>();
  }

  public int size() {
    return mPoints.size();
  }

  public GeoPoint getPoint(String name) {
    return mPoints.get(name);
  }

  public GeoPoint getPoint(int index) {
    return mPoints.get(index);
  }

  public String getKey(int i) {
    return mPoints.getKey(i);
  }

  public Iterator<GeoPoint> iterator() {
    return mPoints.iterator();
  }

  public boolean addPoint(GeoPoint point) {
    Log.i(TAG, "[addPoint] Adding new point: " + point.toString());

    if (!nameExists(point.getName())) {
      mPoints.add(point.getName(), point);
      this.setChanged();
      this.notifyObservers(new GeoDatabaseChange(new GeoPoint[] { point }, GeoDatabaseChange.TYPE_ADD));
      if (mTransactions.add(new TransactionCreate(point))) {
        return mTransactions.get(mTransactions.size() - 1).execute(mDatabase.getWritableDatabase());
      } else {
        return false;
      }
    } else {
      return false;
    }
  }

  public boolean nameExists(String name) {
    return mPoints.get(name) != null;
  }

  public boolean removePoint(GeoPoint point) {
    Log.i(TAG, "[removePoint] Removing point: " + point.toString());

    if (nameExists(point.getName())) {
      mPoints.remove(point.getName());
      this.setChanged();
      this.notifyObservers(new GeoDatabaseChange(new GeoPoint[] { point }, GeoDatabaseChange.TYPE_REMOVE));
      if (mTransactions.add(new TransactionRemove(point))) {
        return mTransactions.get(mTransactions.size() - 1).execute(mDatabase.getWritableDatabase());
      } else {
        return false;
      }
    } else {
      return false;
    }
  }

  public boolean removeAll() {
    Log.i(TAG, "[removeAll]");

    for (GeoPoint gp : this) {
      Log.i(TAG, "[removeAll] Removing " + gp.toString());

      if (mTransactions.add(new TransactionRemove(gp))) {
        mTransactions.get(mTransactions.size() - 1).execute(mDatabase.getWritableDatabase());
      }
    }

    mPoints.removeAll();

    this.setChanged();
    this.notifyObservers(new GeoDatabaseChange(null, GeoDatabaseChange.TYPE_REMOVE_ALL));

    return true;
  }

  public boolean editPoint(GeoPoint oldPoint, GeoPoint newPoint) {
    Log.i(TAG, "[editPoint] changing " + oldPoint + " to " + newPoint);

    if (nameExists(oldPoint.getName())) {
      if (oldPoint.getName().equals(newPoint.getName()) || !nameExists(newPoint.getName())) {
        mPoints.remove(oldPoint.getName());
        mPoints.add(newPoint.getName(), newPoint);

        this.setChanged();
        this.notifyObservers(new GeoDatabaseChange(new GeoPoint[] { newPoint, oldPoint}, GeoDatabaseChange.TYPE_EDIT));
        if (mTransactions.add(new TransactionEdit(oldPoint, newPoint))) {
          return mTransactions.get(mTransactions.size() - 1).execute(mDatabase.getWritableDatabase());
        } else {
          return false;
        }
      } else {
        return false;
      }
    } else {
      return false;
    }
  }

  public int getTransactionCount() {
    return mTransactions.size();
  }

  public boolean readFromDatabase() {
    mPoints.removeAll();
    List<GeoPoint> points = new LinkedList<GeoPoint>();

    boolean success = mDatabase.readPoints(points);
    if (success) {
      for (GeoPoint p : points) {
        this.mPoints.add(p.getName(), p);
      }
    }

    return success;
  }

  public static class GeoDatabaseChange {
    public static final int TYPE_ADD = 0;
    public static  final int TYPE_EDIT = 1;
    public static final int TYPE_REMOVE = 2;
    public static final int TYPE_REMOVE_ALL = 3;

    public static final int POINT_CURRENT  = 0;
    public static final int POINT_OLD = 1;

    private GeoPoint mPoint[];
    private int mType;

    public GeoDatabaseChange(GeoPoint point[], int type) {
      mPoint = point;
      mType = type;
    }

    public int getType() {
      return mType;
    }

    public GeoPoint[] getPoint() {
      return mPoint;
    }
  }
}
