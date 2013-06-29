package be.philipluyckx.geocaching.datacomponents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.Observable;
import java.util.Observer;

import be.philipluyckx.geocaching.R;
import be.philipluyckx.geocaching.components.ListItemView;
import be.philipluyckx.geocaching.database.GeoDatabaseBuffer;

/**
 * Created by Philip on 24/06/13.
 */
public class GeopointAdapter extends BaseAdapter implements Observer {

  private GeoDatabaseBuffer mDatabaseBuffer;
  private Context context;
  private View.OnLongClickListener mLongClickListener;
  private View.OnClickListener mClickListener;

  public GeopointAdapter(Context context, GeoDatabaseBuffer databaseBuffer, View.OnLongClickListener longClickListener, View.OnClickListener clickListener) {
    super();

    this.context = context;
    this.mDatabaseBuffer = databaseBuffer;
    databaseBuffer.addObserver(this);

    mClickListener = clickListener;
    mLongClickListener = longClickListener;
  }

  @Override
  public void update(Observable observable, Object o) {
    this.notifyDataSetChanged();
  }
  @Override
  public int getCount() {
    return mDatabaseBuffer.size();
  }

  @Override
  public Object getItem(int i) {
    return mDatabaseBuffer.getPoint(i);
  }

  @Override
  public long getItemId(int i) {
    return i;
  }

  @Override
  public View getView(int i, View view, ViewGroup viewGroup) {

    if(view == null || !(view instanceof ListItemView)) {
      LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

      view = inflater.inflate(R.layout.list_item_layout, viewGroup, false);
      view.setOnLongClickListener(mLongClickListener);
      view.setOnClickListener(mClickListener);
    }

    ListItemView liv = (ListItemView)view;
    liv.setPoint(mDatabaseBuffer.getPoint(i));

    return liv;
  }
}
