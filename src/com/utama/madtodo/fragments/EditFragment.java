package com.utama.madtodo.fragments;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import com.utama.madtodo.R;
import com.utama.madtodo.models.DbConsts.Table.Tasks;
import com.utama.madtodo.models.LocalRemoteTodo;
import com.utama.madtodo.models.LocalTodo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;


/**
 * The Class EditFragment represents a fragment that contains a form for editing an existing task
 */
public class EditFragment extends CreateFragment {

  /** The "is done" check box. */
  private CheckBox isDoneCheckBox;


  /**
   * Inflate fragment_edit.xml, setup the member variables and bind listener classes to them.
   * 
   * @see com.utama.madtodo.fragments.CreateFragment#onCreateView(android.view.LayoutInflater,
   *      android.view.ViewGroup, android.os.Bundle)
   */
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
        setupDateTimeDialogs();
        expiryDatePickerDialog.show();
      }
    });

    expiryTimeEditText.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        setupDateTimeDialogs();
        expiryTimePickerDialog.show();
      }
    });

    setupDateTimeDialogs();

    return view;
  }


  /**
   * We don't want the expiry to be reset to current date time on resume.
   * 
   * @see com.utama.madtodo.fragments.CreateFragment#onResume()
   */
  @Override
  public void onResume() {
    Calendar oldExpiry = expiry;
    super.onResume();
    expiry = oldExpiry;
  }


  /**
   * Setup the date time dialogs. We want to update date and time pickers to the date and time of
   * the task's. If the date time of the task was not set, set the dialogs to current date time.
   * 
   * @see com.utama.madtodo.fragments.CreateFragment#setupDateTimeDialogs()
   */
  @Override
  protected void setupDateTimeDialogs() {
    super.setupDateTimeDialogs();

    if (expiry.getTime().getTime() > 0) {
      int year = expiry.get(Calendar.YEAR);
      int monthOfYear = expiry.get(Calendar.MONTH);
      int dayOfMonth = expiry.get(Calendar.DAY_OF_MONTH);
      expiryDatePickerDialog.updateDate(year, monthOfYear, dayOfMonth);

      int hourOfDay = expiry.get(Calendar.HOUR);
      int minutOfHour = expiry.get(Calendar.MINUTE);
      expiryTimePickerDialog.updateTime(hourOfDay, minutOfHour);
    } else {
      Calendar now = Calendar.getInstance();
      int year = now.get(Calendar.YEAR);
      int monthOfYear = now.get(Calendar.MONTH);
      int dayOfMonth = now.get(Calendar.DAY_OF_MONTH);
      expiryDatePickerDialog.updateDate(year, monthOfYear, dayOfMonth);

      int hourOfDay = now.get(Calendar.HOUR);
      int minutOfHour = now.get(Calendar.MINUTE);
      expiryTimePickerDialog.updateTime(hourOfDay, minutOfHour);
    }
  }


  /**
   * This method will set up the text edits and check boxes with the data of a task.
   * 
   * @param id The local task id to retrieve.
   * @see com.utama.madtodo.fragments.ViewUpdateable#updateView(long)
   */
  public void updateView(long id) {
    if (id != -1) {
      LocalTodo todo = LocalTodo.findOne(id);

      if (todo != null) {
        nameEditText.setText(todo.getName());
        descriptionEditText.setText(todo.getDescription());

        Date expDate = todo.getExpiry();
        expiry.setTimeInMillis(expDate.getTime());

        if (expDate.getTime() > 0) {
          expiryDateEditText.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(expDate));
          expiryTimeEditText.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(expDate));
        } else {
          expiryDateEditText.setText("");
          expiryTimeEditText.setText("");
        }

        if (todo.isImportant())
          isImportantCheckBox.setChecked(true);
        else
          isImportantCheckBox.setChecked(false);

        if (todo.isMarkedDone())
          isDoneCheckBox.setChecked(true);
        else
          isDoneCheckBox.setChecked(false);
      }
    } else {
      nameEditText.setText("");
      descriptionEditText.setText("");
      expiryDateEditText.setText("");
      expiryTimeEditText.setText("");
      isImportantCheckBox.setChecked(false);
      isDoneCheckBox.setChecked(false);
    }
  }


  /**
   * Builds the todo instance for local and remote operations.
   *
   * @return The local remote todo instance.
   * @see com.utama.madtodo.fragments.CreateFragment#buildTodo()
   */
  @Override
  public LocalRemoteTodo buildTodo() {
    long activeTodoId = getActivity().getIntent().getLongExtra(Tasks.Column.ID, -1);
    LocalRemoteTodo todo = LocalRemoteTodo.findOne(activeTodoId);

    if (todo != null) {
      todo.setName(nameEditText.getText().toString());
      todo.setDescription(descriptionEditText.getText().toString());
      todo.setExpiry(buildExpiry(expiryDateEditText.getText().toString()));
      todo.setImportant(isImportantCheckBox.isChecked());
      todo.setMarkedDone(isDoneCheckBox.isChecked());
    }

    return todo;
  }

}
