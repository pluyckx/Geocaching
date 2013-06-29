package be.philipluyckx.geocaching.database.schemes;

import be.philipluyckx.geocaching.database.components.DatabaseColumn;

/**
 * Created by Philip on 22/06/13.
 */
public abstract class AbstractDatabaseScheme implements DatabaseScheme {
  public static final String TABLE_VERSION = "version";

  private static final DatabaseColumn columns[] = new DatabaseColumn[] {
    new DatabaseColumn("version", "INTEGER", false, false, false)
  };

  private int version;

  public AbstractDatabaseScheme(int version) {
    this.version = version;
  }

  public int getVersion() {
    return version;
  }

  protected String versionCreateStatement() {
    StringBuilder sb = new StringBuilder();

    sb.append("CREATE TABLE version (");

    for (DatabaseColumn c : AbstractDatabaseScheme.columns) {
      sb.append(c.toCreateSql());
    }

    sb.append(");");

    return sb.toString();
  }
}
