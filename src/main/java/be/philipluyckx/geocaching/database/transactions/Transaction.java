package be.philipluyckx.geocaching.database.transactions;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Philip on 22/06/13.
 */
public interface Transaction {
  public boolean execute(SQLiteDatabase database);
}
