package be.philipluyckx.geocaching.database.components;

/**
 * Created by Philip on 22/06/13.
 */
public class DatabaseColumn {
  private String name;
  private String type;
  private boolean primary_key;
  private boolean auto_increment;
  private boolean unique;

  public DatabaseColumn(String name, String type, boolean primary_key, boolean auto_increment, boolean unique) {
    this.name = name;
    this.type = type;
    this.primary_key = primary_key;
    this.auto_increment = auto_increment;
    this.unique = unique;
  }

  public String toCreateSql() {
    StringBuilder sb = new StringBuilder();
    sb.append(name);
    sb.append(' ');
    sb.append(type);

    if(primary_key) {
      sb.append(" PRIMARY KEY");
    }

    if(auto_increment) {
      sb.append(" AUTOINCREMENT");
    }

    if(unique) {
      sb.append(" UNIQUE");
    }

    return sb.toString();
  }
}
