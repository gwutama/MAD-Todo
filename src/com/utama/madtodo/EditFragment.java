package com.utama.madtodo;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import com.utama.madtodo.model.DbConsts;
import com.utama.madtodo.model.LocalRemoteTodo;
import com.utama.madtodo.model.LocalTodo;

import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;


public class EditFragment extends CreateFragment {

  private static final String TAG = "EditFragment";
  private CheckBox isDoneCheckBox;
  private long activeTodoId = -1;


  @Override
  protected View inflateFragment(LayoutInflater inflater, ViewGroup container) {
    View view = inflater.inflate(R.layout.fragment_edit, container);
    return view;
  }


  @Override
  protected void setupViews(View view) {
    super.setupViews(view);
    isDoneCheckBox = (CheckBox) view.findViewById(R.id.isDoneCheckBox);
  }


  @Override
  public void onResume() {
    super.onResume();
    activeTodoId = getActivity().getIntent().getLongExtra(DbConsts.Column.ID, -1);
    updateView(activeTodoId);
  }


  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.edit, menu);
  }


  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    if (id == R.id.action_save) {
      Log.d(TAG, "onOptionsItemSelected: action_save");
      new EditAsync().execute();
      return true;
    } else if (id == R.id.action_delete) {
      Log.d(TAG, "onOptionsItemSelected: action_delete");
      new DeleteTask().execute();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }


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


  protected class EditAsync extends CreateAsync {

    protected LocalRemoteTodo buildTodo() {
      LocalRemoteTodo todo = LocalRemoteTodo.findOne(activeTodoId);
      todo.setName(nameEditText.getText().toString());
      todo.setDescription(descriptionEditText.getText().toString());
      todo.setExpiry(buildExpiry(expiryDateEditText.getText().toString()));
      todo.setImportant(isImportantCheckBox.isChecked());
      todo.setMarkedDone(isDoneCheckBox.isChecked());
      Log.d(TAG, todo.toString());
      return todo;
    }

  }


  protected class DeleteTask extends AsyncTask<Void, Void, String> {

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
