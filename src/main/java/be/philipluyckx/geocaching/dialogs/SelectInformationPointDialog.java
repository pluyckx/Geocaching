package be.philipluyckx.geocaching.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import be.philipluyckx.geocaching.GeocachingApplication;
import be.philipluyckx.geocaching.R;
import be.philipluyckx.geocaching.datacomponents.GeoPoint;
import be.philipluyckx.geocaching.fragments.CompassFragment;

/**
 * Created by Philip on 30/06/13.
 */
public class SelectInformationPointDialog extends DialogFragment {
  private CompassFragment mParent;
  private ListView mPoints;
  private List<GeoPoint> mPointsList;

  public SelectInformationPointDialog(CompassFragment parent, List<GeoPoint> points) {
    this.mParent = parent;
    this.mPointsList = points;
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    Dialog dlg = super.onCreateDialog(savedInstanceState);

    dlg.setTitle("Select Point");
    return dlg;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.dialog_select_information_point, container, false);

    List<String> list = new ArrayList<String>();
    if (mPointsList == null) {
      for (GeoPoint p : GeocachingApplication.getApplication().getDatabaseBuffer()) {
        list.add(p.getName());
      }
    } else {
      for (GeoPoint p : mPointsList) {
        list.add(p.getName());
      }
    }

    Collections.sort(list);

    int selected = 0;
    while (selected < list.size() && !list.get(selected).equals(mParent.getSelectetPoint())) {
      selected++;
    }

    mPoints = (ListView) v.findViewById(R.id.lv_points);
    mPoints.setAdapter(new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.select_point_item, R.id.tv_name, list));
    mPoints.setItemChecked(selected, true);

    mPoints.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        mParent.setSelectedPoint(adapterView.getItemAtPosition(i).toString());
        dismiss();
      }
    });

    return v;
  }
}
