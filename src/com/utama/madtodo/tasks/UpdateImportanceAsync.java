package com.utama.madtodo.tasks;

import com.utama.madtodo.R;
import com.utama.madtodo.TodoListActivity;
import com.utama.madtodo.fragments.TodoListFragment;

import android.app.FragmentManager;
import android.content.Context;
import android.widget.Toast;

public class UpdateImportanceAsync extends SaveAsync {

  public UpdateImportanceAsync(Context context) {
    super(context);
  }


  @Override
  protected void onPostExecute(Integer result) {
    if ((result == R.string.edit_success || result == R.string.network_error)
        && context instanceof TodoListActivity) {
      TodoListActivity activity = (TodoListActivity) context;
      FragmentManager fragmentManager = activity.getFragmentManager();
      TodoListFragment fragment =
          (TodoListFragment) fragmentManager.findFragmentById(R.id.todoListFragment);
      fragment.forceRefreshList();
    } else {
      Toast.makeText(context, context.getString(R.string.update_importance_error),
          Toast.LENGTH_SHORT).show();
    }
  }

}
