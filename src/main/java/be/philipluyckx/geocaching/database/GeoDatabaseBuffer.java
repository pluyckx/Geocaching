package be.philipluyckx.geocaching.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
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
public class GeoDatabaseBuffer extends Observable implements Iterable<GeoPoint> {
  private static final String TAG = "GeoDatabaseBuffer";
  private GeoDatabase database;
  private List<Transaction> transactions;
  private List<GeoPoint> points;

  public GeoDatabaseBuffer(GeoDatabase database) {
    this.database = database;
    database.getWritableDatabase().close();

    Log.v(TAG, "[constructor] Creating transaction list");

    this.transactions = new LinkedList<Transaction>();
    this.points = new ArrayList<GeoPoint>();
  }

  public int size() {
    return points.size();
  }

  public GeoPoint getPoint(int index) {
    return points.get(index);
  }

  public GeoPoint getPoint(String name) {
    int index = 0;
    while(index < points.size() && !points.get(index).getName().equals(name)) {
      index++;
    }

    return (index < points.size() ? points.get(index) : null);
  }

  public Iterator<GeoPoint> iterator() {
    return points.iterator();
  }

  public boolean addPoint(GeoPoint point) {
    Log.i(TAG, "[addPoint] Adding new point: " + point.toString());

    if(!nameExists(point.getName())) {
      points.add(point);
      this.setChanged();
      this.notifyObservers();
      return transactions.add(new TransactionCreate(point));
    } else {
      return false;
    }
  }

  public boolean nameExists(String name) {
    int index = 0;
    while(index < points.size() && !points.get(index).getName().equals(name)) {
      index++;
    }

    return (index < points.size());
  }

  public boolean removePoint(GeoPoint point) {
    Log.i(TAG, "[removePoint] Removing point: " + point.toString());

    if(points.contains(point)) {
      points.remove(point);
      this.setChanged();
      this.notifyObservers();
      return transactions.add(new TransactionRemove(point));
    } else {
      return false;
    }
  }

  public boolean removeAll() {
    Log.i(TAG, "[removeAll]");

    for(GeoPoint gp : points) {
      Log.i(TAG, "[removeAll] Removing " + gp.toString());

      transactions.add(new TransactionRemove(gp));
    }

    points.clear();

    this.setChanged();
    this.notifyObservers();

    return true;
  }

  public boolean editPoint(GeoPoint oldPoint, GeoPoint newPoint) {
    Log.i(TAG, "[editPoint] changing " + oldPoint + " to " + newPoint);

    if(points.contains(oldPoint)) {
      if(oldPoint.getName().equals(newPoint.getName()) || !nameExists(newPoint.getName())) {
        points.set(points.indexOf(oldPoint), newPoint);
        this.setChanged();
        this.notifyObservers();
        return transactions.add(new TransactionEdit(oldPoint, newPoint));
      } else {
        return false;
      }
    } else {
      return false;
    }
  }

  public Transaction getFirstTransaction() {
    return transactions.get(0);
  }

  public int getTransactionCount() {
    return transactions.size();
  }

  public boolean save() {
    if(transactions.size() == 0) {
      return true;
    }

    SQLiteDatabase database = this.database.getWritableDatabase();
    boolean success = false;
    do {
      success = transactions.get(0).execute(database);
      transactions.remove(0);
    } while(transactions.size() > 0 && success);

    return success;
  }

  public boolean readFromDatabase() {
    points.clear();

    return database.readPoints(points);
  }
}
