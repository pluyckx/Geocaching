package be.philipluyckx.geocaching.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import be.philipluyckx.geocaching.GeocachingApplication;
import be.philipluyckx.geocaching.R;
import be.philipluyckx.geocaching.components.ListItemView;
import be.philipluyckx.geocaching.database.GeoDatabaseBuffer;
import be.philipluyckx.geocaching.datacomponents.GeoPoint;
import be.philipluyckx.geocaching.datacomponents.GeopointAdapter;
import be.philipluyckx.geocaching.dialogs.AddPointDialog;
import be.philipluyckx.geocaching.dialogs.EditPointDialog;

/**
 * Created by pluyckx on 6/24/13.
 */
public class DatabaseFragment extends Fragment {

  private ListView mListView;
  private Button mAddPoint;
  private Button mSave;
  private OnLongClickListener mLongClickListener;
  private OnClickListener mClickListener;

  public DatabaseFragment() {
    mLongClickListener = new OnLongClickListener();
    mClickListener = new OnClickListener();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.database_layout, container, false);
    GeopointAdapter adapter = new GeopointAdapter(getActivity().getApplicationContext(),
            ((GeocachingApplication) getActivity().getApplication()).getDatabaseBuffer(),
            mLongClickListener,
            mClickListener);

    mListView = (ListView) view.findViewById(R.id.lv_points);
    mListView.setAdapter(adapter);

    mAddPoint = (Button) view.findViewById(R.id.b_db_add_point);
    mAddPoint.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onShowAddPointDialog(((GeocachingApplication) getActivity().getApplication()).getDatabaseBuffer());
      }
    });

    mSave = (Button) view.findViewById(R.id.b_db_save);
    mSave.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ((GeocachingApplication) getActivity().getApplication()).getDatabaseBuffer().save();
      }
    });

    return view;
  }

  private void onShowAddPointDialog(GeoDatabaseBuffer buffer) {
    DialogFragment dialog = new AddPointDialog(buffer, R.string.add_point_title);
    dialog.show(getFragmentManager(), "dialog");
  }

  private class OnLongClickListener implements View.OnLongClickListener {
    @Override
    public boolean onLongClick(View v) {
      if(v instanceof ListItemView) {
        final GeoPoint point = ((ListItemView)v).getPoint();

        String msg = getString(R.string.msg_remove_point).replace("{item}", point.getName());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.remove_dialog_title);
        builder.setMessage(msg);
        builder.setPositiveButton(R.string.btn_remove, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            if(((GeocachingApplication)getActivity().getApplication()).getDatabaseBuffer().removePoint(point)) {
              String msg = getString(R.string.msg_remove_success).replace("{item}", point.getName());
              Toast.makeText(getActivity().getApplicationContext(), msg, Toast.LENGTH_LONG);
            } else {
              String msg = getString(R.string.msg_remove_failed).replace("{item}", point.getName());
              Toast.makeText(getActivity().getApplicationContext(), msg, Toast.LENGTH_LONG);
            }
          }
        });

        builder.setNegativeButton(R.string.btn_cancel, null);

        builder.create().show();

        return true;
      } else {
        return false;
      }
    }
  }

  private class OnClickListener implements View.OnClickListener {
    @Override
    public void onClick(View v) {
      if(v instanceof ListItemView) {
        ListItemView liv = (ListItemView) v;

        DialogFragment dialog = new EditPointDialog(((GeocachingApplication) getActivity().getApplication()).getDatabaseBuffer(),
                R.string.edit_point_dialog_title,
                liv.getPoint());
        dialog.show(getFragmentManager(), "dialog");
      }
    }
  }
}
