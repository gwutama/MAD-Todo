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


/**
 * The Class SingleTodoActivity is an abstract class that represents activities that
 * display a single todo/task data. Such data are to be displayed, edited or deleted
 * depending on user actions and whether the subclass of this class implements
 * one or many of these interfaces: {@linkplain com.utama.madtodo.ActionSaveable},
 * {@linkplain com.utama.madtodo.ActionDeleteable}, {@linkplain com.utama.madtodo.ActionEditable}.
 */
abstract public class SingleTodoActivity extends Activity implements OnClickListener {

  /** The delete dialog. */
  protected AlertDialog.Builder deleteDialog;
  
  /** The current active todo id. */
  protected long activeTodoId = -1;


  /**
   * Gets the active todo id.
   *
   * @return The active todo id
   */
  public long getActiveTodoId() {
    return activeTodoId;
  }


  /**
   * Sets up persistence for managing both local and remote data. Finally sets up
   * the delete dialog if the subclass of this class implements 
   * {@linkplain com.utama.madtodo.ActionDeleteable}.
   * 
   * @see android.app.Activity#onCreate(android.os.Bundle)
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    LocalRemoteTodo.setupPersistence(this);

    if (this instanceof ActionDeleteable)
      setupDeleteDialog();
  }


  /**
   * Setup delete dialog. The delete dialog is necessary for confirming user if
   * he wants to delete the current active data.
   */
  private void setupDeleteDialog() {
    if (this instanceof ActionDeleteable) {
      deleteDialog = new AlertDialog.Builder(this);
      deleteDialog.setMessage(R.string.delete_confirm).setCancelable(false)
          .setPositiveButton(R.string.yes, this).setNegativeButton(R.string.no, this);
    }
  }


  /**
   * Instances of this classes' subclasses will retrieve activeTodoId from extra parameters
   * passed to the intent using getExtras(DbConsts.Column.ID, ...). This method will
   * retrieve the passed value and sets in to activeTodoId member variable.
   * 
   * @see android.app.Activity#onResume()
   */
  @Override
  protected void onResume() {
    super.onResume();
    activeTodoId = getIntent().getLongExtra(DbConsts.Column.ID, -1);
  }


  /**
   * Inflates a complete menu containing save, edit and delete actions. Depending on 
   * this classes' subclasses implementation, such actions are to be hidden from the menu.
   * 
   * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
   */
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


  /**
   * On menu click, this method will trigger different actions: executing {@link SettingsActivity}, 
   * {@link EditActivity} or {@link DeleteAsync}.
   * 
   * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
   */
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


  /**
   * This method will get executed when user wants to delete the current task data.
   * A confirmation dialog will be shown to user and depending on his input,
   * {@link DeleteAsync} will be executed or the dialog will be simply closed.
   * 
   * @see android.content.DialogInterface.OnClickListener#onClick(android.content.DialogInterface, int)
   */
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
