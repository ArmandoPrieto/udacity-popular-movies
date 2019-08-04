package com.udacity.popularMovies;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;


public class PreferenceFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
    }
}
