package com.utama.madtodo.tasks;

import com.utama.madtodo.R;
import com.utama.madtodo.TodoListActivity;
import com.utama.madtodo.fragments.TodoListFragment;

import android.app.FragmentManager;
import android.content.Context;
import android.widget.Toast;


/**
 * The Class UpdateImportanceAsync represents an asynchronous task to update a task's
 * importance/priority. This is quite the same as {@link SaveAsync}, but only with
 * different onPostExecute implementation.
 */
public class UpdateImportanceAsync extends SaveAsync {

  /**
   * Instantiates a new update importance async.
   *
   * @param context The context. This can be an activity or fragment.
   */
  public UpdateImportanceAsync(Context context) {
    super(context);
  }


  /**
   * After a successful execution, the todo list will be refreshed. Otherwise an error
   * message will be shown in a toast.
   * 
   * @see com.utama.madtodo.tasks.SaveAsync#onPostExecute(java.lang.Integer)
   */
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
