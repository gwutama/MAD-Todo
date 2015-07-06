package com.utama.madtodo;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import com.utama.madtodo.models.LocalRemoteTodo;
import com.utama.madtodo.models.LocalTodo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;


public class EditFragment extends CreateFragment
    implements ViewUpdateable, ActionSaveable, ActionDeleteable {

  private CheckBox isDoneCheckBox;


  @Override
  protected View inflateFragment(LayoutInflater inflater, ViewGroup container) {
    View view = inflater.inflate(R.layout.fragment_edit, container);
    return view;
  }


  @Override
  protected void setupView(View view) {
    super.setupView(view);
    isDoneCheckBox = (CheckBox) view.findViewById(R.id.isDoneCheckBox);
  }


  @Override
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


  @Override
  protected LocalRemoteTodo buildTodo() {
    LocalRemoteTodo todo = LocalRemoteTodo.findOne(activeTodoId);
    todo.setName(nameEditText.getText().toString());
    todo.setDescription(descriptionEditText.getText().toString());
    todo.setExpiry(buildExpiry(expiryDateEditText.getText().toString()));
    todo.setImportant(isImportantCheckBox.isChecked());
    todo.setMarkedDone(isDoneCheckBox.isChecked());
    return todo;
  }

}
