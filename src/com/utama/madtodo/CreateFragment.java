package com.utama.madtodo;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import com.utama.madtodo.models.LocalRemoteTodo;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;


public class CreateFragment extends Fragment {

  protected EditText nameEditText;
  protected EditText descriptionEditText;
  protected EditText expiryDateEditText;
  protected EditText expiryTimeEditText;
  protected DatePickerDialog expiryDatePickerDialog;
  protected TimePickerDialog expiryTimePickerDialog;
  protected CheckBox isImportantCheckBox;

  protected Calendar expiry = Calendar.getInstance();


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_create, container);
    
    nameEditText = (EditText) view.findViewById(R.id.nameEditText);
    descriptionEditText = (EditText) view.findViewById(R.id.descriptionEditText);
    expiryDateEditText = (EditText) view.findViewById(R.id.expiryDateEditText);
    expiryTimeEditText = (EditText) view.findViewById(R.id.expiryTimeEditText);
    isImportantCheckBox = (CheckBox) view.findViewById(R.id.isImportantCheckBox);

    expiryDateEditText.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        expiryDatePickerDialog.show();
      }
    });

    expiryTimeEditText.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        expiryTimePickerDialog.show();
      }
    });    

    setupDateTimeDialogs();    
    
    return view;
  }  


  @Override
  public void onResume() {
    super.onResume();
    expiry = Calendar.getInstance();
  }

  
  protected void setupDateTimeDialogs() {
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
  
  
  public LocalRemoteTodo buildTodo() {
    LocalRemoteTodo todo = new LocalRemoteTodo();
    todo.setName(nameEditText.getText().toString());
    todo.setDescription(descriptionEditText.getText().toString());
    todo.setExpiry(buildExpiry(expiryDateEditText.getText().toString()));
    todo.setImportant(isImportantCheckBox.isChecked());      
    return todo;
  }
  

  protected Date buildExpiry(String expiryDateString) {
    if (!TextUtils.isEmpty(expiryDateString))
      return expiry.getTime();
    else
      return new Date(0);
  }
  
}
