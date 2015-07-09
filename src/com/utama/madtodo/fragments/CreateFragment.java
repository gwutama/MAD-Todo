package com.utama.madtodo.fragments;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import com.utama.madtodo.R;
import com.utama.madtodo.models.LocalRemoteTodo;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Fragment;
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


/**
 * The Class CreateFragment represents a fragment that contains a form for creating a new task
 * record.
 */
public class CreateFragment extends Fragment {

  /** The name edit text. */
  protected EditText nameEditText;

  /** The description edit text. */
  protected EditText descriptionEditText;

  /** The expiry date edit text. */
  protected EditText expiryDateEditText;

  /** The expiry time edit text. */
  protected EditText expiryTimeEditText;

  /** The expiry date picker dialog. */
  protected DatePickerDialog expiryDatePickerDialog;

  /** The expiry time picker dialog. */
  protected TimePickerDialog expiryTimePickerDialog;

  /** The "is important" check box. */
  protected CheckBox isImportantCheckBox;

  /** The expiry date. */
  protected Calendar expiry = Calendar.getInstance();


  /**
   * Inflate fragment_create.xml, setup the member variables and bind listener classes to them.
   * 
   * @see android.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup,
   *      android.os.Bundle)
   */
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


  /**
   * We want to update the {@link CreateFragment#expiry} value to the current date every time this
   * fragment gets resumed.
   * 
   * @see android.app.Fragment#onResume()
   */
  @Override
  public void onResume() {
    super.onResume();
    expiry = Calendar.getInstance();
  }


  /**
   * Setup the date time dialogs.
   */
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


  /**
   * Builds the todo instance for local and remote operations.
   *
   * @return The local remote todo instance.
   */
  public LocalRemoteTodo buildTodo() {
    LocalRemoteTodo todo = new LocalRemoteTodo();
    todo.setName(nameEditText.getText().toString());
    todo.setDescription(descriptionEditText.getText().toString());
    todo.setExpiry(buildExpiry(expiryDateEditText.getText().toString()));
    todo.setImportant(isImportantCheckBox.isChecked());
    return todo;
  }


  /**
   * Builds the expiry date based on the expiry string.
   *
   * @param expiryDateString The expiry date string.
   * @return The new date instance. Returns date of unix epoch 0 if passed string is invalid or if
   *         it is empty.
   */
  protected Date buildExpiry(String expiryDateString) {
    if (!TextUtils.isEmpty(expiryDateString))
      return expiry.getTime();
    else
      return new Date(0);
  }

}
