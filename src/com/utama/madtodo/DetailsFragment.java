package com.utama.madtodo;

import java.text.DateFormat;
import java.util.Date;

import com.utama.madtodo.models.LocalTodo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class DetailsFragment extends SingleTodoFragment
    implements ViewUpdateable, ActionEditable, ActionDeleteable {

  private TextView nameTextView;
  private TextView descriptionTextView;
  private TextView expiryTextView;
  private TextView isImportantTextView;
  private TextView isDoneTextView;

  
  @Override
  protected View inflateFragment(LayoutInflater inflater, ViewGroup container) {
    View view = inflater.inflate(R.layout.fragment_details, container);
    return view;    
  }
  
  
  @Override
  protected void setupView(View view) {
    nameTextView = (TextView) view.findViewById(R.id.nameTextView);
    descriptionTextView = (TextView) view.findViewById(R.id.descriptionTextView);
    expiryTextView = (TextView) view.findViewById(R.id.expiryTextView);
    isImportantTextView = (TextView) view.findViewById(R.id.isImportantTextView);
    isDoneTextView = (TextView) view.findViewById(R.id.isDoneTextView);
  }


  @Override
  public void updateView(long id) {
    super.updateView(id);

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
