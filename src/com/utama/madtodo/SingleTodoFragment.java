package com.utama.madtodo;

import com.utama.madtodo.models.DbConsts;
import com.utama.madtodo.models.DbHelper;
import com.utama.madtodo.models.LocalRemoteTodo;
import com.utama.madtodo.tasks.DeleteAsync;
import com.utama.madtodo.tasks.SaveAsync;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


public abstract class SingleTodoFragment extends Fragment implements OnClickListener {

  protected AlertDialog.Builder deleteDialog;
  protected long activeTodoId = -1;


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
    DbHelper.setupPersistance(getActivity());

    if (this instanceof ActionDeleteable)
      setupDeleteDialog();
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflateFragment(inflater, container);
    setupView(view);
    return view;
  }


  protected View inflateFragment(LayoutInflater inflater, ViewGroup container) {
    return null;
  }


  protected void setupView(View view) {}


  private void setupDeleteDialog() {
    if (this instanceof ActionDeleteable) {
      deleteDialog = new AlertDialog.Builder(getActivity());
      deleteDialog.setMessage(R.string.delete_confirm).setCancelable(false)
          .setPositiveButton(R.string.yes, this).setNegativeButton(R.string.no, this);
    }
  }


  @Override
  public void onResume() {
    super.onResume();
    activeTodoId = getActivity().getIntent().getLongExtra(DbConsts.Column.ID, -1);

    if (this instanceof ViewUpdateable)
      updateView(activeTodoId);
  }


  public void updateView(long id) {
  }


  @Override
  public void onClick(DialogInterface dialog, int which) {
    if (dialog instanceof AlertDialog) {
      switch (which) {
        case DialogInterface.BUTTON_POSITIVE:
          new DeleteAsync(this.getActivity()).execute(activeTodoId);
          break;
        case DialogInterface.BUTTON_NEGATIVE:
          dialog.cancel();
          break;
        default:
          break;
      }
    }
  }


  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.singletodo, menu);

    if (this instanceof ActionSaveable == false)
      menu.removeItem(R.id.action_save);
    if (this instanceof ActionEditable == false)
      menu.removeItem(R.id.action_edit);
    if (this instanceof ActionDeleteable == false)
      menu.removeItem(R.id.action_delete);

  }


  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_settings:
        startActivity(new Intent(getActivity(), SettingsActivity.class));
        return true;
      case R.id.action_save:
        LocalRemoteTodo todo = buildTodo();
        if (todo != null) {
          new SaveAsync(this.getActivity()).execute(todo);
          return true;
        }
        return false;
      case R.id.action_edit:
        startActivity(new Intent(getActivity(), EditActivity.class).putExtra(DbConsts.Column.ID,
            activeTodoId));
        return true;
      case R.id.action_delete:
        if (deleteDialog != null)
          deleteDialog.show();
        return true;
    }

    return super.onOptionsItemSelected(item);
  }


  protected LocalRemoteTodo buildTodo() {
    return null;
  }

}
