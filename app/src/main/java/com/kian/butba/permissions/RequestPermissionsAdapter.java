package com.kian.butba.permissions;

import android.content.pm.PackageManager;
import android.support.v13.app.FragmentCompat.OnRequestPermissionsResultCallback;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;

/**
 * Created by Kian Mistry on 29/12/16.
 */

public abstract class RequestPermissionsAdapter<VH extends ViewHolder> extends Adapter<VH> implements OnRequestPermissionsResultCallback {

	protected static final int REQUEST_CODE_RESULT_EXTERNAL_STORAGE = 1;

	private Fragment fragment;

	private View view;
	private VH holder;
	private int position;

	protected abstract void executeActions(View view, VH holder, int position);
	protected abstract void executeActionsIfNotGranted();

	public RequestPermissionsAdapter(Fragment fragment) {
		this.fragment = fragment;
	}

	public void requestPermission(View view, VH holder, int position, String permission, int requestCodeResult) {
		this.view = view;
		this.holder = holder;
		this.position = position;

		if(ContextCompat.checkSelfPermission(fragment.getContext(), permission) == PackageManager.PERMISSION_GRANTED) {
			executeActions(view, holder, position);
		}
		else {
			fragment.requestPermissions(new String[]{permission}, requestCodeResult);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch(requestCode) {
			case REQUEST_CODE_RESULT_EXTERNAL_STORAGE:
				if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					executeActions(view, holder, position);
				}
				else {
					executeActionsIfNotGranted();
				}
				break;
			default:
				fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
				break;
		}

	}
}
