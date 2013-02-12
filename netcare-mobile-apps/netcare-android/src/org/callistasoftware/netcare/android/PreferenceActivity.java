package org.callistasoftware.netcare.android;

import android.graphics.Color;
import android.os.Bundle;

public class PreferenceActivity extends android.preference.PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getListView().setBackgroundColor(Color.BLACK);
		addPreferencesFromResource(R.xml.preferences);
	}
	
	
}
