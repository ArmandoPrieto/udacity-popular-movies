package com.udacity.popularMovies.view;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.udacity.popularMovies.R;


public class PreferenceFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}
