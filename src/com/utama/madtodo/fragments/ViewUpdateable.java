package com.utama.madtodo.fragments;

/**
 * The Interface ViewUpdateable represents a fragment that should be updated with a new data
 * on fragment resume.
 */
interface ViewUpdateable {

  /**
   * This method will set up the text views with the data of a task.
   * 
   * @param id The local task id to retrieve.
   */
  void updateView(long id);
  
}
