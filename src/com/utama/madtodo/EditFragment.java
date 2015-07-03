package com.utama.madtodo;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.AsyncTask;
import android.os.Bundle;
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

  private EditText summaryEditText;
  private EditText descriptionEditText;
  private EditText dueDateEditText;
  private EditText dueTimeEditText;
  private DatePickerDialog dueDatePickerDialog;
  private TimePickerDialog dueTimePickerDialog;
  private CheckBox isImportantCheckBox;
  private CheckBox isDoneCheckBox;

  private Calendar dueDate = Calendar.getInstance();


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d(TAG, "onCreate");
    
    setHasOptionsMenu(true);    
    setupDateTimeDialogs();
  }


  @Override
  public void onResume() {
    super.onResume();
    dueDate = Calendar.getInstance();
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_edit, container);

    summaryEditText = (EditText) view.findViewById(R.id.summaryEditText);
    descriptionEditText = (EditText) view.findViewById(R.id.descriptionEditText);
    dueDateEditText = (EditText) view.findViewById(R.id.dueDateEditText);
    dueTimeEditText = (EditText) view.findViewById(R.id.dueTimeEditText);
    isImportantCheckBox = (CheckBox) view.findViewById(R.id.isImportantCheckBox);
    isDoneCheckBox = (CheckBox) view.findViewById(R.id.isDoneCheckBox);

    dueDateEditText.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Log.d(TAG, "onClick dueDateEditText");
        dueDatePickerDialog.show();
      }
    });

    dueTimeEditText.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Log.d(TAG, "onClick dueTimeEditText");
        dueTimePickerDialog.show();
      }
    });

    return view;
  }


  private void setupDateTimeDialogs() {
    Log.d(TAG, "setDateTimeDialogs");

    Calendar nowCalendar = Calendar.getInstance();
    int year = nowCalendar.get(Calendar.YEAR);
    int monthOfYear = nowCalendar.get(Calendar.MONTH);
    int dayOfMonth = nowCalendar.get(Calendar.DAY_OF_MONTH);
    int hourOfDay = nowCalendar.get(Calendar.HOUR_OF_DAY);
    int minute = nowCalendar.get(Calendar.MINUTE);

    dueDatePickerDialog = new DatePickerDialog(getActivity(), new OnDateSetListener() {
      @Override
      public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        dueDate.set(year, monthOfYear, dayOfMonth);
        String fmt = DateFormat.getDateInstance().format(dueDate.getTime());
        dueDateEditText.setText(fmt);
      }
    }, year, monthOfYear, dayOfMonth);

    dueTimePickerDialog = new TimePickerDialog(getActivity(), new OnTimeSetListener() {
      @Override
      public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        int year = dueDate.get(Calendar.YEAR);
        int monthOfYear = dueDate.get(Calendar.MONTH);
        int dayOfMonth = dueDate.get(Calendar.DAY_OF_MONTH);
        dueDate.set(year, monthOfYear, dayOfMonth, hourOfDay, minute);
        String fmt = DateFormat.getDateInstance().format(dueDate.getTime());
        dueTimeEditText.setText(fmt);
      }
    }, hourOfDay, minute, true);
  }


  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.edit, menu);
  }


  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    if (id == R.id.action_settings) {
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


  private final class SaveAsync extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {
      Log.d(TAG, "SaveTask.doInBackground");
      TodolistWebappClient client;

      try {
        client = new TodolistWebappClient("http://192.168.1.191:8080/TodolistWebapp/todos");
        
        String summary = summaryEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        Date due = dueDate.getTime();        
        
        client.createTodo(summary, description, due);
        return "Task has been successfully saved";
      } catch (MalformedURLException e) {
        e.printStackTrace();
        return "Incorrect API root";        
      } catch (IOException e) {
        e.printStackTrace();
        return "Error creating task";
      }
    }

    @Override
    protected void onPostExecute(String result) {
      super.onPostExecute(result);
      Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
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
    }

  }


}
