package com.utama.madtodo;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

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


// https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
// http://www.vogella.com/tutorials/AndroidListView/article.html
public class TodoListAdapter extends ArrayAdapter<LocalTodo> {

  public TodoListAdapter(Context context, List<LocalTodo> todos) {
    super(context, 0, todos);
  }


  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    final LocalTodo todo = getItem(position);

    if (convertView == null) {
      LayoutInflater inflater = LayoutInflater.from(getContext());
      convertView = inflater.inflate(R.layout.todo_list_item, parent, false);
    }
    

    // Task name
    TextView nameTextView = (TextView) convertView.findViewById(R.id.listItemNameTextView);
    nameTextView.setText(todo.getName());

    
    // Task due date
    TextView expiryTextView = (TextView) convertView.findViewById(R.id.listItemExpiryTextView);
    Date expiry = todo.getExpiry();

    if (expiry.getTime() > 0) {
      String expDate = DateFormat.getDateInstance().format(expiry);
      String expTime = DateFormat.getTimeInstance().format(expiry);
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
