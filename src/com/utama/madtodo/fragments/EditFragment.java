package com.utama.madtodo.fragments;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import com.utama.madtodo.R;
import com.utama.madtodo.models.DbConsts;
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
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(expDate.getTime());
        expiry = cal;

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
    long activeTodoId = getActivity().getIntent().getLongExtra(DbConsts.Column.ID, -1);
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
