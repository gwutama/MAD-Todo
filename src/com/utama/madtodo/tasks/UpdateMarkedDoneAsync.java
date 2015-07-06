package com.utama.madtodo.tasks;

import com.utama.madtodo.R;
import com.utama.madtodo.TodoListActivity;
import com.utama.madtodo.TodoListFragment;

import android.app.FragmentManager;
import android.content.Context;
import android.widget.Toast;

public class UpdateMarkedDoneAsync extends SaveAsync {

  public UpdateMarkedDoneAsync(Context context) {
    super(context);
  }


  @Override
  protected void onPostExecute(Integer result) {
    if (result == R.string.edit_success && context instanceof TodoListActivity) {
      TodoListActivity activity = (TodoListActivity) context;
      FragmentManager fragmentManager = activity.getFragmentManager();
      TodoListFragment fragment =
          (TodoListFragment) fragmentManager.findFragmentById(R.id.todoListFragment);
      fragment.forceRefreshList();
    } else {
      Toast.makeText(context, context.getString(R.string.update_markeddone_error),
          Toast.LENGTH_SHORT).show();
    }
  }

}
