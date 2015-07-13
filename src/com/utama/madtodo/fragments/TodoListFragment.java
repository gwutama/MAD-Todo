package com.utama.madtodo.fragments;

import java.util.List;

import com.utama.madtodo.DetailsActivity;
import com.utama.madtodo.R;
import com.utama.madtodo.models.DbConsts;
import com.utama.madtodo.models.DbConsts.Table.Tasks;
import com.utama.madtodo.models.LocalRemoteTodo;
import com.utama.madtodo.models.LocalTodo;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;


/**
 * The Class TodoListFragment represents a fragment that displays a list of tasks.
 */
public class TodoListFragment extends ListFragment {

  /** For debugging purposes */
  private static final String TAG = "TodoListFragment";

  /** The adapter that takes care of the logic of displaying the data. */
  TodoListAdapter adapter;

  /** The todos. */
  List<LocalTodo> todos;

  /** The sort order. */
  private String sortOrder = Tasks.DEFAULT_SORT;

  /** The offline mode text view. */
  private TextView offlineModeTextView;


  /**
   * Sets the sort order.
   *
   * @param sortOrder The sort order. By default this is {@link DbConsts#DEFAULT_SORT} but you can
   *        pass any "WHERE" clause here.
   */
  public void setSortOrder(String sortOrder) {
    this.sortOrder = sortOrder;
  }


  /**
   * Sets the todo list adapter for displaying data, checks whether offline mode is active and
   * displays a warning in {@link TodoListFragment#offlineModeTextView} if it is.
   * 
   * @see android.app.Fragment#onActivityCreated(android.os.Bundle)
   */
  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    Log.d(TAG, "onActivityCreated");
    super.onActivityCreated(savedInstanceState);
    LocalRemoteTodo.setupPersistence(getActivity());
    todos = LocalTodo.findAll(sortOrder);
    adapter = new TodoListAdapter(getActivity(), todos);
    setListAdapter(adapter);

    offlineModeTextView = (TextView) getActivity().findViewById(R.id.offlineModeTextView);
    offlineModeTextView.setBackgroundColor(0xFFDD792E);
    offlineModeTextView.setTextColor(0xFFFFFFFF);

    if (LocalRemoteTodo.isOfflineMode())
      offlineModeTextView.setVisibility(TextView.VISIBLE);
    else
      offlineModeTextView.setVisibility(TextView.GONE);
  }


  /**
   * Force refresh the todo list.
   */
  public void forceRefreshList() {
    Log.d(TAG, "refreshList");
    todos = LocalTodo.findAll(sortOrder);
    adapter.clear();
    adapter.addAll(todos);
    adapter.notifyDataSetChanged();
  }


  /**
   * Starts {@link DetailsActivity} on item click.
   * 
   * @see android.app.ListFragment#onListItemClick(android.widget.ListView, android.view.View, int,
   *      long)
   */
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
      startActivity(
          new Intent(getActivity(), DetailsActivity.class).putExtra(Tasks.Column.ID, todo.getId()));
    }
  }

}
