package com.utama.madtodo;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import com.utama.madtodo.model.DbConsts;
import com.utama.madtodo.model.DbHelper;
import com.utama.madtodo.model.LocalRemoteTodo;
import com.utama.madtodo.model.LocalTodo;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


public class DetailsFragment extends Fragment {

  private static final String TAG = "DetailsFragment";

  private TextView nameTextView;
  private TextView descriptionTextView;
  private TextView expiryTextView;
  private TextView isImportantTextView;
  private TextView isDoneTextView;
  private AlertDialog.Builder deleteDialog;

  private long activeTodoId = -1;


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
    DbHelper.setupPersistance(getActivity());
    setupDeleteDialog();
  }


  private void setupDeleteDialog() {
    deleteDialog = new AlertDialog.Builder(getActivity());
    deleteDialog.setMessage("Delete this task?")
      .setCancelable(false)
        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
        
        @Override
        public void onClick(DialogInterface dialog, int which) {
          new DeleteTask().execute(activeTodoId);
        }
      })
      .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
        
        @Override
        public void onClick(DialogInterface dialog, int which) {
          dialog.cancel();
        }
      });
  }


  @Override
  public void onResume() {
    super.onResume();
    activeTodoId = getActivity().getIntent().getLongExtra(DbConsts.Column.ID, -1);
    updateView(activeTodoId);
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_details, container);

    nameTextView = (TextView) view.findViewById(R.id.nameTextView);
    descriptionTextView = (TextView) view.findViewById(R.id.descriptionTextView);
    expiryTextView = (TextView) view.findViewById(R.id.expiryTextView);
    isImportantTextView = (TextView) view.findViewById(R.id.isImportantTextView);
    isDoneTextView = (TextView) view.findViewById(R.id.isDoneTextView);

    return view;
  }


  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.details, menu);
  }


  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    if (id == R.id.action_settings) {
      Log.d(TAG, "onOptionsItemSelected: action_settings");
      startActivity(new Intent(getActivity(), SettingsActivity.class));
      return true;
    } else if (id == R.id.action_delete) {
      Log.d(TAG, "onOptionsItemSelected: action_delete");
      deleteDialog.show();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }


  public void updateView(long id) {
    if (id != -1) {
      LocalTodo todo = LocalTodo.findOne(id);

      if (todo != null) {
        nameTextView.setText(todo.getName());
        descriptionTextView.setText(todo.getDescription());

        Date expiry = todo.getExpiry();

        if (expiry.getTime() > 0) {
          String expDate = DateFormat.getDateInstance().format(expiry);
          String expTime = DateFormat.getTimeInstance().format(expiry);
          String expText = String.format(getString(R.string.task_due_text), expDate, expTime);
          expiryTextView.setText(expText);
        } else
          expiryTextView.setText(getString(R.string.task_no_due_text));

        if (todo.isImportant())
          isImportantTextView.setText(getString(R.string.task_important_text));
        else
          isImportantTextView.setText(getString(R.string.task_not_important_text));

        if (todo.isMarkedDone())
          isDoneTextView.setText(getString(R.string.task_done_text));
        else
          isDoneTextView.setText(getString(R.string.task_not_done_text));
      }
    } else {
      nameTextView.setText("");
      descriptionTextView.setText("");
      expiryTextView.setText("");
      isImportantTextView.setText("");
      isDoneTextView.setText("");
    }
  }


  private final class DeleteTask extends AsyncTask<Long, Void, Integer> {

    @Override
    protected Integer doInBackground(Long... params) {
      Log.d(TAG, "DeleteTask.doInBackground");
      long id = params[0];
      LocalRemoteTodo todo = LocalRemoteTodo.findOne(id);

      try {
        long count = todo.delete();
        if (todo != null && count > 0)
          return R.string.delete_success;
        else
          return R.string.delete_general_error;
      } catch (IOException e) {
        e.printStackTrace();
        return R.string.network_error;
      }
    }


    @Override
    protected void onPostExecute(Integer result) {
      super.onPostExecute(result);
      Toast.makeText(getActivity(), getString(result), Toast.LENGTH_SHORT).show();

      if (result == R.string.delete_success)
        startActivity(new Intent(getActivity(), TodoListActivity.class));
    }

  }

}
