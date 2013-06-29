package be.philipluyckx.geocaching.dialogs;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import be.philipluyckx.geocaching.R;
import be.philipluyckx.geocaching.database.GeoDatabaseBuffer;
import be.philipluyckx.geocaching.datacomponents.GeoPoint;
import be.philipluyckx.geocaching.utils.DegreeConverter;

/**
 * Created by pluyckx on 6/25/13.
 */
public class EditPointDialog extends DialogFragment {
  public static final String TAG = "EditPointDialog";
  private static final String ARG_TITLE = "title";

  private Button mEdit;
  private Button mCancel;

  private Spinner mLatitudeDirection;
  private Spinner mLongitudeDirection;

  private EditText mName;
  private EditText mLatDegree;
  private EditText mLatMinutes;
  private EditText mLatSeconds;
  private EditText mLonDegree;
  private EditText mLonMinutes;
  private EditText mLonSeconds;
  private TextView mStatus;
  private GeoDatabaseBuffer mDbBuffer;
  private GeoPoint mEditPoint;

  public EditPointDialog(GeoDatabaseBuffer buffer, int title, GeoPoint point) {
    Bundle args = new Bundle();
    args.putInt(ARG_TITLE, title);
    setArguments(args);

    mDbBuffer = buffer;
    mEditPoint = point;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    int title = getArguments().getInt(ARG_TITLE);

    View v = inflater.inflate(R.layout.dialog_point, container, false);
    TextWatcher watcher = new OnTextChanged();

    mEdit = (Button) v.findViewById(R.id.b_add_point_ok);
    mEdit.setText(R.string.edit_point_dialog_edit);
    mCancel = (Button) v.findViewById(R.id.b_add_point_cancel);

    mName = (EditText) v.findViewById(R.id.et_name);

    mLatitudeDirection = (Spinner)v.findViewById(R.id.s_latitude);
    mLongitudeDirection = (Spinner)v.findViewById(R.id.s_longitude);

    mLatDegree = (EditText) v.findViewById(R.id.et_latitude_degree);
    mLatMinutes = (EditText) v.findViewById(R.id.et_latitude_minutes);
    mLatSeconds = (EditText) v.findViewById(R.id.et_latitude_seconds);
    mLonDegree = (EditText) v.findViewById(R.id.et_longitude_degree);
    mLonMinutes = (EditText) v.findViewById(R.id.et_longitude_minutes);
    mLonSeconds = (EditText) v.findViewById(R.id.et_longitude_seconds);

    mStatus = (TextView) v.findViewById(R.id.tv_status);

    mName.addTextChangedListener(watcher);
    mLatDegree.addTextChangedListener(watcher);
    mLatMinutes.addTextChangedListener(watcher);
    mLatSeconds.addTextChangedListener(watcher);
    mLonDegree.addTextChangedListener(watcher);
    mLonMinutes.addTextChangedListener(watcher);
    mLonSeconds.addTextChangedListener(watcher);

    mName.setNextFocusDownId(R.id.et_latitude_degree);
    mLatDegree.setNextFocusDownId(R.id.et_latitude_minutes);
    mLatMinutes.setNextFocusDownId(R.id.et_latitude_seconds);
    mLatSeconds.setNextFocusDownId(R.id.et_longitude_degree);
    mLonDegree.setNextFocusDownId(R.id.et_longitude_minutes);
    mLonMinutes.setNextFocusDownId(R.id.et_longitude_seconds);

    mCancel.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onCancel();
      }
    });
    mEdit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onEdit();
      }
    });

    getDialog().setTitle(title);
    getDialog().setCanceledOnTouchOutside(false);

    mName.setText(mEditPoint.getName());

    String parts[] = new String[3];
    DegreeConverter.toStringParts(Math.abs(mEditPoint.getLocation().latitude), parts);
    mLatDegree.setText(parts[0]);
    mLatMinutes.setText(parts[1]);
    mLatSeconds.setText(parts[2]);

    DegreeConverter.toStringParts(Math.abs(mEditPoint.getLocation().longitude), parts);
    mLonDegree.setText(parts[0]);
    mLonMinutes.setText(parts[1]);
    mLonSeconds.setText(parts[2]);

    if(mEditPoint.getLocation().latitude < 0.0) {
      mLatitudeDirection.setSelection(1);
    }

    if(mEditPoint.getLocation().longitude < 0.0) {
      mLongitudeDirection.setSelection(1);
    }

    watcher.afterTextChanged(null);

    return v;
  }

  private void onEdit() {
    double latitude = DegreeConverter.toDouble(mLatDegree.getText().toString(),
            mLatMinutes.getText().toString(),
            mLatSeconds.getText().toString());
    double longitude = DegreeConverter.toDouble(mLonDegree.getText().toString(),
            mLonMinutes.getText().toString(),
            mLonSeconds.getText().toString());
    String name = mName.getText().toString();

    if(mLatitudeDirection.getSelectedItemPosition() == 1) {
      latitude = -latitude;
    }

    if(mLongitudeDirection.getSelectedItemPosition() == 1) {
      longitude = -longitude;
    }

    GeoPoint point = new GeoPoint(name, new LatLng(latitude, longitude), true);
    if(mDbBuffer.editPoint(mEditPoint, point)) {
      getDialog().dismiss();
    } else {
      mStatus.setText(R.string.msg_unknown_edit_point_error);
    }
  }

  private void onCancel() {
    this.dismiss();
  }

  private class OnTextChanged implements TextWatcher {
    private boolean mNameExists = false;

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
      boolean enabled = (!mLatDegree.getText().toString().equals("") &&
              !mLatMinutes.getText().toString().equals("") &&
              !mLatSeconds.getText().toString().equals("") &&
              !mLonDegree.getText().toString().equals("") &&
              !mLonMinutes.getText().toString().equals("") &&
              !mLonSeconds.getText().toString().equals(""));

      if(s == mName.getText()) {
        if(mName.getText().toString().equals(mEditPoint.getName())) {
          mNameExists = false;
        } else {
          mNameExists = mDbBuffer.nameExists(mName.getText().toString());

          if(mNameExists) {
            mStatus.setText(R.string.msg_name_exists);
          } else {
            mStatus.setText("");
          }
        }
      }

      enabled = enabled && !mNameExists;

      mEdit.setEnabled(enabled);
    }
  }
}
