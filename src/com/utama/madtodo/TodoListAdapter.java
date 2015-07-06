package com.utama.madtodo;

import java.util.List;

import com.utama.madtodo.models.LocalTodo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


// https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
// http://www.vogella.com/tutorials/AndroidListView/article.html
public class TodoListAdapter extends ArrayAdapter<LocalTodo> {

  public TodoListAdapter(Context context, List<LocalTodo> todos) {
    super(context, 0, todos);
  }


  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    LocalTodo todo = getItem(position);

    if (convertView == null)
      convertView =
          LayoutInflater.from(getContext()).inflate(R.layout.todo_list_item, parent, false);

    TextView todoListItemName = (TextView) convertView.findViewById(R.id.todo_list_item_name);
    todoListItemName.setText(todo.getName());

    return convertView;
  }


}
