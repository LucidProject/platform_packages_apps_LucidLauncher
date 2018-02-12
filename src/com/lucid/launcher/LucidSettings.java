package com.lucid.launcher;

import android.content.Context;
import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.Preference.OnPreferenceChangeListener;

import com.android.launcher3.settings.SettingsActivity;
import com.android.launcher3.Utilities;

import com.lucid.launcher.customization.IconDatabase;
import com.lucid.launcher.settings.IconPackPrefSetter;
import com.lucid.launcher.settings.ReloadingListPreference;
import com.lucid.launcher.util.AppReloader;

public class LucidSettings extends SettingsActivity {
    public interface OnResumePreferenceCallback {
        void onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public static class LucidSettingsFragment extends LauncherSettingsFragment {
        private static final String KEY_ICON_PACK = "pref_icon_pack";

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            super.onCreatePreferences(savedInstanceState, rootKey);

            final Context context = getActivity();
            ReloadingListPreference icons = (ReloadingListPreference) findPreference(KEY_ICON_PACK);
            icons.setOnReloadListener(new IconPackPrefSetter(context));
            icons.setOnPreferenceChangeListener((pref, val) -> {
                IconDatabase.clearAll(context);
                IconDatabase.setGlobal(context, (String) val);
                AppReloader.get(context).reload();
                return true;
            });

            final ListPreference iconSizes = (ListPreference) findPreference(Utilities.ICON_SIZE);
            iconSizes.setSummary(iconSizes.getEntry());
            iconSizes.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    int index = iconSizes.findIndexOfValue((String) newValue);
                    iconSizes.setSummary(iconSizes.getEntries()[index]);
                    Utilities.restart(getActivity());
                    return true;
                }
            });
        }
    }
}
