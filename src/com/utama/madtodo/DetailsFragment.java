package com.utama.madtodo;

import java.text.DateFormat;
import java.util.Date;

import com.utama.madtodo.model.DbConsts;
import com.utama.madtodo.model.DbHelper;
import com.utama.madtodo.model.LocalTodo;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class DetailsFragment extends Fragment {

  private static final String TAG = "DetailsFragment";
  
  private TextView nameTextView;
  private TextView descriptionTextView;
  private TextView expiryTextView;
  private TextView isImportantTextView;
  private TextView isDoneTextView;


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
    DbHelper.setupPersistance(getActivity());
  }


  @Override
  public void onResume() {
    super.onResume();
    long id = getActivity().getIntent().getLongExtra(DbConsts.Column.ID, -1);
    updateView(id);
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

}
