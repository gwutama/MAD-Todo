package com.utama.madtodo.fragments;

import java.text.DateFormat;
import java.util.Date;

import com.utama.madtodo.R;
import com.utama.madtodo.models.LocalTodo;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * The Class DetailsFragment represents a fragment that contains a form for viewing details of an
 * existing task record.
 */
public class DetailsFragment extends Fragment implements ViewUpdateable {

  /** The name text view. */
  protected TextView nameTextView;

  /** The description text view. */
  protected TextView descriptionTextView;

  /** The expiry text view. */
  protected TextView expiryTextView;

  /** The "is important" text view. */
  protected TextView isImportantTextView;

  /** The "is done" text view. */
  protected TextView isDoneTextView;


  /**
   * Inflate fragment_details.xml, setup the member variables and bind listener classes to them.
   * 
   * @see android.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup,
   *      android.os.Bundle)
   */
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_details, container);

    nameTextView = (TextView) view.findViewById(R.id.nameTextView);
    descriptionTextView = (TextView) view.findViewById(R.id.descriptionTextView);
    expiryTextView = (TextView) view.findViewById(R.id.listItemExpiryTextView);
    isImportantTextView = (TextView) view.findViewById(R.id.isImportantTextView);
    isDoneTextView = (TextView) view.findViewById(R.id.isDoneTextView);

    return view;
  }


  /**
   * This method will set up the text views with the data of a task.
   * 
   * @param id The local task id to retrieve.
   * @see com.utama.madtodo.fragments.ViewUpdateable#updateView(long)
   */
  @Override
  public void updateView(long id) {
    if (id != -1) {
      LocalTodo todo = LocalTodo.findOne(id);

      if (todo != null) {
        nameTextView.setText(todo.getName());
        descriptionTextView.setText(todo.getDescription());

        Date expiry = todo.getExpiry();

        // Format text for due date
        if (expiry.getTime() > 0) {
          String expDate = DateFormat.getDateInstance().format(expiry);
          String expTime = DateFormat.getTimeInstance(DateFormat.SHORT).format(expiry);
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
