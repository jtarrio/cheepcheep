package org.tarrio.cheepcheep.task;

/**
 * A callback interface for code to run after running an asynchronous task.
 */
public interface TaskCallback {
	void onSuccess(AsyncTwitterTask task);
	void onFailure(int statusCode, AsyncTwitterTask task);
}