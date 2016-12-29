package com.kian.butba.permissions;

import android.content.pm.PackageManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

/**
 * Created by Kian Mistry on 28/12/16.
 */

public abstract class RequestPermissionsFragment extends Fragment {

	public static final int REQUEST_CODE_RESULT_EXTERNAL_STORAGE = 1;

	protected abstract void executeActions();
	protected abstract void executeActionsIfNotGranted();

	public void requestPermission(String permission, int requestCodeResult) {
		if(ContextCompat.checkSelfPermission(getContext(), permission) == PackageManager.PERMISSION_GRANTED) {
			executeActions();
		}
		else {
			requestPermissions(new String[] {permission}, requestCodeResult);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch(requestCode) {
			case REQUEST_CODE_RESULT_EXTERNAL_STORAGE:
				if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					executeActions();
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
