package com.utama.madtodo;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;

import com.utama.madtodo.model.DbHelper;
import com.utama.madtodo.model.LocalTodo;
import com.utama.madtodo.model.RemoteTodo;
import com.utama.madtodo.model.LocalRemoteTodo;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;


public class EditFragment extends Fragment {

  private static final String TAG = "EditFragment";

  private EditText nameEditText;
  private EditText descriptionEditText;
  private EditText expiryDateEditText;
  private EditText expiryTimeEditText;
  private DatePickerDialog expiryDatePickerDialog;
  private TimePickerDialog expiryTimePickerDialog;
  private CheckBox isImportantCheckBox;
  private CheckBox isDoneCheckBox;

  private Calendar expiry = Calendar.getInstance();


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d(TAG, "onCreate");

    setHasOptionsMenu(true);
    setupDateTimeDialogs();
    
    LocalTodo.setDbHelper(new DbHelper(getActivity()));
  }


  @Override
  public void onResume() {
    super.onResume();
    expiry = Calendar.getInstance();
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_edit, container);

    nameEditText = (EditText) view.findViewById(R.id.nameEditText);
    descriptionEditText = (EditText) view.findViewById(R.id.descriptionEditText);
    expiryDateEditText = (EditText) view.findViewById(R.id.expiryDateEditText);
    expiryTimeEditText = (EditText) view.findViewById(R.id.expiryTimeEditText);
    isImportantCheckBox = (CheckBox) view.findViewById(R.id.isImportantCheckBox);
    isDoneCheckBox = (CheckBox) view.findViewById(R.id.isDoneCheckBox);

    expiryDateEditText.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Log.d(TAG, "onClick expiryDateEditText");
        expiryDatePickerDialog.show();
      }
    });

    expiryTimeEditText.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Log.d(TAG, "onClick expiryTimeEditText");
        expiryTimePickerDialog.show();
      }
    });

    return view;
  }


  private void setupDateTimeDialogs() {
    Log.d(TAG, "setDateTimeDialogs");
    Calendar now = Calendar.getInstance();

    expiryDatePickerDialog = new DatePickerDialog(getActivity(), new OnDateSetListener() {
      @Override
      public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        expiry.set(year, monthOfYear, dayOfMonth);
        String fmt = DateFormat.getDateInstance(DateFormat.SHORT).format(expiry.getTime());
        expiryDateEditText.setText(fmt);
      }
    }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));

    expiryTimePickerDialog = new TimePickerDialog(getActivity(), new OnTimeSetListener() {
      @Override
      public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        int year = expiry.get(Calendar.YEAR);
        int monthOfYear = expiry.get(Calendar.MONTH);
        int dayOfMonth = expiry.get(Calendar.DAY_OF_MONTH);
        expiry.set(year, monthOfYear, dayOfMonth, hourOfDay, minute);
        String fmt = DateFormat.getTimeInstance(DateFormat.SHORT).format(expiry.getTime());
        expiryTimeEditText.setText(fmt);
      }
    }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true);
  }


  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.edit, menu);
  }


  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    if (id == R.id.action_settings) {
      Log.d(TAG, "onOptionsItemSelected: action_settings");
      startActivity(new Intent(getActivity(), SettingsActivity.class));
      return true;
    } else if (id == R.id.action_save) {
      Log.d(TAG, "onOptionsItemSelected: action_save");
      new SaveAsync().execute();
      return true;
    } else if (id == R.id.action_delete) {
      Log.d(TAG, "onOptionsItemSelected: action_delete");
      new DeleteTask().execute();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }


  private final class SaveAsync extends AsyncTask<Void, Void, Integer> {

    @Override
    protected Integer doInBackground(Void... params) {
      Log.d(TAG, "SaveAsync.doInBackground");
      
      try {
        LocalRemoteTodo todo = new LocalRemoteTodo();
        todo.setName(nameEditText.getText().toString());
        todo.setDescription(descriptionEditText.getText().toString());
        todo.setExpiry(buildExpiry(expiryDateEditText.getText().toString()));
        todo.setImportant(isImportantCheckBox.isChecked());
        todo.setMarkedDone(isDoneCheckBox.isChecked());

        if (todo.save() > 0)
          return R.string.edit_success;          
        else
          return R.string.edit_general_error;
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
        return R.string.edit_name_empty_error;
      } catch (IOException e) {
        e.printStackTrace();
        return R.string.network_error;
      } catch (JSONException e) {
        e.printStackTrace();
        return R.string.response_error;
      }        
    }


    private Date buildExpiry(String expiryDateString) {
      if (!TextUtils.isEmpty(expiryDateString))
        return expiry.getTime();
      else
        return new Date(0);
    }


    @Override
    protected void onPostExecute(Integer result) {
      super.onPostExecute(result);
      Toast.makeText(getActivity(), getString(result), Toast.LENGTH_SHORT).show();

      if (result == R.string.edit_success)
        startActivity(new Intent(getActivity(), ListActivity.class));
    }

  }


  private final class DeleteTask extends AsyncTask<Void, Void, String> {

    @Override
    protected String doInBackground(Void... params) {
      Log.d(TAG, "DeleteTask.doInBackground");

      // TODO
      return "Task has been successfully deleted";
    }


    @Override
    protected void onPostExecute(String result) {
      super.onPostExecute(result);
      Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();

      if (result.equals("Task has been successfully saved")) { // TODO
      }
    }

  }


}
