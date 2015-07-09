package com.utama.madtodo.fragments;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import com.utama.madtodo.R;
import com.utama.madtodo.models.LocalRemoteTodo;
import com.utama.madtodo.models.LocalTodo;
import com.utama.madtodo.tasks.UpdateImportanceAsync;
import com.utama.madtodo.tasks.UpdateMarkedDoneAsync;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;


/**
 * The Class TodoListAdapter represents an adapter that takes care the logic of displaying todo
 * lists in {@link TodoListFragment}.
 * 
 * Resources :
 * 
 * - https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
 * - http://www.vogella.com/tutorials/AndroidListView/article.html
 */
public class TodoListAdapter extends ArrayAdapter<LocalTodo> {

  /** The Constant BACKGROUND_RED used for background color for due tasks. */
  private static final int BACKGROUND_RED = 0xFFFFE7E6;

  /** The Constant BACKGROUND_GREEN used for background color for finished tasks. */
  private static final int BACKGROUND_GREEN = 0xFFF2FFE6;

  /** The Constant BACKGROUND_DEFAULT used for background color for normal tasks.. */
  private static final int BACKGROUND_DEFAULT = android.R.drawable.btn_default;


  /**
   * Instantiates a new todo list adapter.
   *
   * @param context This is usually an activity.
   * @param todos The todo list to display in the list.
   */
  public TodoListAdapter(Context context, List<LocalTodo> todos) {
    super(context, 0, todos);
  }


  /**
   * Inflates todo_list_item.xml and sets up the text views inside it, background color, binding
   * toggle buttons of toggling task's importance and status.
   * 
   * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
   */
  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    final LocalTodo todo = getItem(position);

    if (convertView == null) {
      LayoutInflater inflater = LayoutInflater.from(getContext());
      convertView = inflater.inflate(R.layout.todo_list_item, parent, false);
    }

    // Red background color for tasks due but not for tasks without expiration dates.
    // Green background color for tasks that are done.
    Date now = new Date();
    boolean isPastDue = todo.getExpiry().compareTo(now) < 0;
    boolean isExpireNotSet = todo.getExpiry().getTime() == 0;

    if (isPastDue && !isExpireNotSet && !todo.isMarkedDone()) {
      convertView.setBackgroundColor(BACKGROUND_RED);
    } else if (todo.isMarkedDone()) {
      convertView.setBackgroundColor(BACKGROUND_GREEN);
    } else {
      convertView.setBackgroundColor(BACKGROUND_DEFAULT);
    }


    // Task name
    TextView nameTextView = (TextView) convertView.findViewById(R.id.listItemNameTextView);
    nameTextView.setText(todo.getName());


    // Task due date
    TextView expiryTextView = (TextView) convertView.findViewById(R.id.listItemExpiryTextView);
    Date expiry = todo.getExpiry();

    if (expiry.getTime() > 0) {
      String expDate = DateFormat.getDateInstance().format(expiry);
      String expTime = DateFormat.getTimeInstance(DateFormat.SHORT).format(expiry);
      String expText =
          String.format(getContext().getString(R.string.task_due_text), expDate, expTime);
      expiryTextView.setText(expText);
    } else {
      expiryTextView.setText("");
    }


    // Toggle button (check/done task)
    final ToggleButton markedDoneToggle =
        (ToggleButton) convertView.findViewById(R.id.listItemMarkedDoneToggle);

    if (todo.isMarkedDone())
      markedDoneToggle.setChecked(true);
    else
      markedDoneToggle.setChecked(false);

    markedDoneToggle.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        final boolean isChecked = markedDoneToggle.isChecked();
        Log.d("TodoListAdapter", "markedDoneToggle.onCheckedChanged " + isChecked);
        todo.setMarkedDone(isChecked);
        markedDoneToggle.setChecked(isChecked);

        LocalRemoteTodo lrTodo = new LocalRemoteTodo(todo);
        new UpdateMarkedDoneAsync(getContext()).execute(lrTodo);

      }
    });


    // Toggle button (important task)
    final ToggleButton importantToggle =
        (ToggleButton) convertView.findViewById(R.id.listItemImportantToggle);

    if (todo.isImportant())
      importantToggle.setChecked(true);
    else
      importantToggle.setChecked(false);

    importantToggle.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        final boolean isChecked = importantToggle.isChecked();
        Log.d("TodoListAdapter", "importantToggle.onCheckedChanged " + isChecked);
        todo.setImportant(isChecked);

        LocalRemoteTodo lrTodo = new LocalRemoteTodo(todo);
        new UpdateImportanceAsync(getContext()).execute(lrTodo);

      }
    });

    return convertView;
  }


}
