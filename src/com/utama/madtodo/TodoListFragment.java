package com.utama.madtodo;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.utama.madtodo.model.DbHelper;
import com.utama.madtodo.model.LocalTodo;
import com.utama.madtodo.model.RemoteTodo;

import android.app.ListFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;


public class TodoListFragment extends ListFragment{

  private static final String TAG = "TodoListFragment";
  List<LocalTodo> todos;
  
  
  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    Log.d(TAG, "onActivityCreated");
    super.onActivityCreated(savedInstanceState);
    
    try {
      SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
      URL apiRoot = new URL(pref.getString("apiRoot", ""));
      RemoteTodo.setApiRoot(apiRoot);
    } catch (MalformedURLException e) {
      e.printStackTrace();
      Toast.makeText(getActivity(), R.string.api_root_error, Toast.LENGTH_SHORT).show();
    }
    
    LocalTodo.setDbHelper(new DbHelper(getActivity()));    
        
    todos = LocalTodo.findAll();
    TodoListAdapter adapter = new TodoListAdapter(getActivity(), todos);
    setListAdapter(adapter);
  }
  
  
  @Override
  public void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
    
    LocalTodo todo = todos.get(position);        
    Log.d(TAG, "onListItemClick: " + todo.getId());
  }
  
}
