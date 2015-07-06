package com.utama.madtodo;

import java.util.List;

import com.utama.madtodo.models.DbConsts;
import com.utama.madtodo.models.DbHelper;
import com.utama.madtodo.models.LocalTodo;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;


public class TodoListFragment extends ListFragment {

  private static final String TAG = "TodoListFragment";
  TodoListAdapter adapter;
  List<LocalTodo> todos;


  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    Log.d(TAG, "onActivityCreated");
    super.onActivityCreated(savedInstanceState);
    DbHelper.setupPersistance(getActivity());
    todos = LocalTodo.findAll();
    adapter = new TodoListAdapter(getActivity(), todos);
    setListAdapter(adapter);
  }

  
  public void forceRefreshList() {
    Log.d(TAG, "refreshList");
    todos = LocalTodo.findAll();
    adapter.clear();
    adapter.addAll(todos);
    adapter.notifyDataSetChanged();
  }

  
  @Override
  public void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);

    LocalTodo todo = todos.get(position);
    Log.d(TAG, "onListItemClick: " + todo.getId());

    DetailsFragment fragment =
        (DetailsFragment) getFragmentManager().findFragmentById(R.id.detailsFragment);

    if (fragment != null && fragment.isVisible()) {
      fragment.updateView(id);
    } else {
      startActivity(new Intent(getActivity(), DetailsActivity.class).putExtra(DbConsts.Column.ID,
          todo.getId()));
    }
  }

}
