package com.utama.madtodo;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import com.utama.madtodo.models.LocalRemoteTodo;
import com.utama.madtodo.tasks.SaveAsync;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;


// https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
// http://www.vogella.com/tutorials/AndroidListView/article.html
public class TodoListAdapter extends ArrayAdapter<LocalRemoteTodo> {

  Context ctx;
  
  
  public TodoListAdapter(Context context, List<LocalRemoteTodo> todos) {
    super(context, 0, todos);
    ctx = context;
  }


  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    final LocalRemoteTodo todo = getItem(position);
//    final Context ctx = parent.getContext();

    if (convertView == null)
      convertView =
          LayoutInflater.from(getContext()).inflate(R.layout.todo_list_item, parent, false);

    // Task name
    TextView nameTextView = (TextView) convertView.findViewById(R.id.listItemNameTextView);
    nameTextView.setText(todo.getName());

    // Task due date
    TextView expiryTextView = (TextView) convertView.findViewById(R.id.listItemExpiryTextView);
    Date expiry = todo.getExpiry();

    if (expiry.getTime() > 0) {
      String expDate = DateFormat.getDateInstance().format(expiry);
      String expTime = DateFormat.getTimeInstance().format(expiry);
      String expText = String.format(ctx.getString(R.string.task_due_text), expDate, expTime);
      expiryTextView.setText(expText);
    } else {
      expiryTextView.setText("");
    }

    // Toggle button (check/done task)
    ToggleButton checkToggle = (ToggleButton) convertView.findViewById(R.id.listItemCheckToggle);

    if (todo.isMarkedDone())
      checkToggle.setChecked(true);
    
    checkToggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {      
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        todo.setMarkedDone(isChecked);
        new SaveAsync(ctx).execute(todo);
      }
    });
    
    // Toggle button (important task)
    ToggleButton importantToggle =
        (ToggleButton) convertView.findViewById(R.id.listItemImportantToggle);

    if (todo.isImportant())
      importantToggle.setChecked(true);
    
    importantToggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {      
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        todo.setImportant(isChecked);
        new SaveAsync(ctx).execute(todo);
      }
    });    
    
    return convertView;
  }


}
