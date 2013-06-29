package be.philipluyckx.geocaching.database.schemes;

import be.philipluyckx.geocaching.database.components.DatabaseColumn;

/**
 * Created by Philip on 22/06/13.
 */
public class DatabaseSchemeV1 extends AbstractDatabaseScheme {
  private static final DatabaseColumn[] columns = new DatabaseColumn[]{
          new DatabaseColumn("id", "INTEGER", true, true, true),
          new DatabaseColumn("name", "TEXT", false, false, true),
          new DatabaseColumn("latitude", "DOUBLE", false, false, false),
          new DatabaseColumn("longitude", "DOUBLE", false, false, false),
          new DatabaseColumn("visible", "INTEGER", false, false, false)
  };
  private static final DatabaseScheme scheme = new DatabaseSchemeV1();

  private DatabaseSchemeV1() {
    super(1);
  }

  public static DatabaseScheme getScheme() {
    return scheme;
  }

  public String toCreateStatement(String table) {
    if(table.equals(DatabaseSchemeV1.TABLE_VERSION)) {
      return this.versionCreateStatement();
    } else if(table.equals(DatabaseSchemeV1.TABLE_GEO_POINTS)) {
      return this.geopointsCreateStatement();
    } else {
      return null;
    }
  }

  private String geopointsCreateStatement() {
    StringBuilder sb = new StringBuilder();

    sb.append("CREATE TABLE ");
    sb.append(DatabaseSchemeV1.TABLE_GEO_POINTS);
    sb.append(" (");

    if(columns.length > 0) {
      sb.append(columns[0].toCreateSql());
    }
    for (int i=1; i<columns.length; i++) {
      sb.append(',');
      sb.append(columns[i].toCreateSql());
    }

    sb.append(");");

    return sb.toString();
  }
}
