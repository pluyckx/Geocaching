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
import be.philipluyckx.geocaching.database.GeoDatabaseProxy;
import be.philipluyckx.geocaching.datacomponents.GeoPoint;
import be.philipluyckx.geocaching.utils.DegreeConverter;

/**
 * Created by pluyckx on 6/25/13.
 */
public class AddPointDialog extends DialogFragment {
  public static final String TAG = "AddPointDialog";
  private static final String ARG_TITLE = "title";
  private Button mAdd;
  private Button mCancel;
  private EditText mName;
  private EditText mLatDegree;
  private EditText mLatMinutes;
  private EditText mLatSeconds;
  private EditText mLonDegree;
  private EditText mLonMinutes;
  private EditText mLonSeconds;
  private TextView mStatus;
  private Spinner mLatitudeDirection;
  private Spinner mLongitudeDirection;
  private GeoDatabaseProxy mDbBuffer;
  private LatLng mInitLocation;

  public AddPointDialog(GeoDatabaseProxy buffer, int title, LatLng loc) {
    Bundle args = new Bundle();
    args.putInt(ARG_TITLE, title);
    setArguments(args);

    mDbBuffer = buffer;
    mInitLocation = loc;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    int title = getArguments().getInt(ARG_TITLE);

    View v = inflater.inflate(R.layout.dialog_point, container, false);
    TextWatcher watcher = new OnTextChanged();

    mAdd = (Button) v.findViewById(R.id.b_add_point_ok);
    mCancel = (Button) v.findViewById(R.id.b_add_point_cancel);

    mName = (EditText) v.findViewById(R.id.et_name);

    mLatitudeDirection = (Spinner) v.findViewById(R.id.s_latitude);
    mLongitudeDirection = (Spinner) v.findViewById(R.id.s_longitude);

    mLatDegree = (EditText) v.findViewById(R.id.et_latitude_degree);
    mLatMinutes = (EditText) v.findViewById(R.id.et_latitude_minutes);
    mLatSeconds = (EditText) v.findViewById(R.id.et_latitude_seconds);
    mLonDegree = (EditText) v.findViewById(R.id.et_longitude_degree);
    mLonMinutes = (EditText) v.findViewById(R.id.et_longitude_minutes);
    mLonSeconds = (EditText) v.findViewById(R.id.et_longitude_seconds);

    String parts[] = new String[3];

    if (mInitLocation != null) {
      DegreeConverter.toStringParts(mInitLocation.latitude, parts);
    } else {
      parts[0] = "0";
      parts[1] = "0";
      parts[2] = "0";
    }
    mLatDegree.setText(parts[0]);
    mLatMinutes.setText(parts[1]);
    mLatSeconds.setText(parts[2]);

    if (mInitLocation != null) {
      DegreeConverter.toStringParts(mInitLocation.longitude, parts);
    }
    mLonDegree.setText(parts[0]);
    mLonMinutes.setText(parts[1]);
    mLonSeconds.setText(parts[2]);

    mStatus = (TextView) v.findViewById(R.id.tv_status);

    mName.addTextChangedListener(watcher);
    mLatDegree.addTextChangedListener(watcher);
    mLatMinutes.addTextChangedListener(watcher);
    mLatSeconds.addTextChangedListener(watcher);
    mLonDegree.addTextChangedListener(watcher);
    mLonMinutes.addTextChangedListener(watcher);
    mLonSeconds.addTextChangedListener(watcher);

    final View.OnFocusChangeListener focusListener = new View.OnFocusChangeListener() {
      @Override
      public void onFocusChange(View view, boolean b) {
        if (view instanceof EditText) {
          ((EditText) view).setSelection(mLatDegree.getText().length() - 1);
        }
      }
    };

    mLatDegree.setOnFocusChangeListener(focusListener);
    mLatMinutes.setOnFocusChangeListener(focusListener);
    mLatSeconds.setOnFocusChangeListener(focusListener);
    mLonDegree.setOnFocusChangeListener(focusListener);
    mLonMinutes.setOnFocusChangeListener(focusListener);
    mLonSeconds.setOnFocusChangeListener(focusListener);

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
    mAdd.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onAdd();
      }
    });

    getDialog().setTitle(title);
    getDialog().setCanceledOnTouchOutside(false);

    watcher.afterTextChanged(null);

    return v;
  }

  private void onAdd() {
    double latitude = DegreeConverter.toDouble(mLatDegree.getText().toString(),
            mLatMinutes.getText().toString(),
            mLatSeconds.getText().toString());
    double longitude = DegreeConverter.toDouble(mLonDegree.getText().toString(),
            mLonMinutes.getText().toString(),
            mLonSeconds.getText().toString());
    String name = mName.getText().toString();

    if (mLatitudeDirection.getSelectedItemPosition() == 1) {
      latitude = -latitude;
    }

    if (mLongitudeDirection.getSelectedItemPosition() == 1) {
      longitude = -longitude;
    }

    GeoPoint point = new GeoPoint(name, new LatLng(latitude, longitude), true);
    if (mDbBuffer.addPoint(point)) {
      getDialog().dismiss();
    } else {
      mStatus.setText(R.string.msg_unknown_add_point_error);
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

      if (s == mName.getText()) {
        mNameExists = mDbBuffer.nameExists(mName.getText().toString());

        if (mNameExists) {
          mStatus.setText(R.string.msg_name_exists);
        } else {
          mStatus.setText("");
        }
      }

      enabled = enabled && !mNameExists;

      mAdd.setEnabled(enabled);
    }
  }
}
