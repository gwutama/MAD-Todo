package com.utama.madtodo;

import com.utama.madtodo.models.DbConsts;
import com.utama.madtodo.models.LocalRemoteTodo;
import com.utama.madtodo.tasks.DeleteAsync;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


abstract public class SingleTodoActivity extends Activity implements OnClickListener {

  protected AlertDialog.Builder deleteDialog;
  protected long activeTodoId = -1;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    LocalRemoteTodo.setupPersistence(this);

    if (this instanceof ActionDeleteable)
      setupDeleteDialog();
  }


  private void setupDeleteDialog() {
    if (this instanceof ActionDeleteable) {
      deleteDialog = new AlertDialog.Builder(this);
      deleteDialog.setMessage(R.string.delete_confirm).setCancelable(false)
          .setPositiveButton(R.string.yes, this).setNegativeButton(R.string.no, this);
    }
  }


  @Override
  protected void onResume() {
    super.onResume();
    activeTodoId = getIntent().getLongExtra(DbConsts.Column.ID, -1);
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    getMenuInflater().inflate(R.menu.singletodo, menu);

    if (this instanceof ActionSaveable == false)
      menu.removeItem(R.id.action_save);
    if (this instanceof ActionEditable == false)
      menu.removeItem(R.id.action_edit);
    if (this instanceof ActionDeleteable == false)
      menu.removeItem(R.id.action_delete);

    return true;
  }


  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_settings:
        startActivity(new Intent(this, SettingsActivity.class));
        return true;
      case R.id.action_save:
        return false;
      case R.id.action_edit:
        startActivity(
            new Intent(this, EditActivity.class).putExtra(DbConsts.Column.ID, activeTodoId));
        return true;
      case R.id.action_delete:
        if (deleteDialog != null)
          deleteDialog.show();
        return true;
    }

    return super.onOptionsItemSelected(item);
  }


  @Override
  public void onClick(DialogInterface dialog, int which) {
    if (dialog instanceof AlertDialog) {
      switch (which) {
        case DialogInterface.BUTTON_POSITIVE:
          new DeleteAsync(this).execute(activeTodoId);
          break;
        case DialogInterface.BUTTON_NEGATIVE:
          dialog.cancel();
          break;
        default:
          break;
      }
    }
  }

}
