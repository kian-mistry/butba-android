package com.kian.butba.profile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kian.butba.R;

/**
 * Created by Kian Mistry on 17/10/16.
 */

public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        //Required: Empty public constructor.
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
}
