package com.kian.butba.permissions;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;

/**
 * Created by Kian Mistry on 28/12/16.
 */

/**
 * Class used when a permission needs to be requested from a fragment which is populated using a recycler view.
 */
public abstract class RequestPermissionsAdapterFragment<M, VH extends ViewHolder> extends Fragment {

	private View view;
	private M model;
	private VH viewHolder;

	/**
	 * Called when the permission requested has been granted.
	 *
	 * @param view The view which has been granted the permission.
	 * @param model The object containing the data used to populate the view.
	 * @param viewHolder The container of the view which has been granted permission.
	 */
	protected abstract void executeActions(View view, M model, VH viewHolder);

	/**
	 * Called when the permission requested has been denied.
	 */
	protected abstract void executeActionsIfNotGranted();

	/**
	 * Check whether the permission requested has been granted.
	 *
	 * @param view The view which the request needs to be called from.
	 * @param model The object containing the data used to populate the view.
	 * @param viewHolder The container of the view which the request needs to be called from.
	 * @param permission The permission that needs to be requested.
	 * @param requestCode The unique identifier given to the permission.
	 */
	public void requestPermission(View view, M model, VH viewHolder, String permission, int requestCode) {
		this.view = view;
		this.model = model;
		this.viewHolder = viewHolder;

		if(ContextCompat.checkSelfPermission(getContext(), permission) == PackageManager.PERMISSION_GRANTED) {
			executeActions(view, model, viewHolder);
		}
		else {
			requestPermissions(new String[] {permission}, requestCode);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch(requestCode) {
			case PermissionConstants.REQUEST_CODE_RESULT_EXTERNAL_STORAGE:
				if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					executeActions(view, model, viewHolder);
				}
				else {
					executeActionsIfNotGranted();
				}
				break;
			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
				break;
		}
	}
}
