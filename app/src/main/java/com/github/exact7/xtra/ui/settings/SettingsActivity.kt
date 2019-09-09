package com.github.exact7.xtra.ui.settings

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import androidx.preference.SwitchPreferenceCompat
import com.github.exact7.xtra.R
import com.github.exact7.xtra.util.C
import com.github.exact7.xtra.util.DisplayUtils
import com.github.exact7.xtra.util.applyTheme
import com.github.exact7.xtra.util.isInLandscapeOrientation
import com.github.exact7.xtra.util.isInPortraitOrientation
import com.github.exact7.xtra.util.prefs
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyTheme()
        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }
        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.settings, SettingsFragment())
                    .commit()
        }
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            val activity = requireActivity()
            findPreference<ListPreference>(C.THEME).setOnPreferenceChangeListener { _, _ ->
                activity.apply {
                    applyTheme()
                    recreate()
                }
                true
            }
            findPreference<SeekBarPreference>("chatWidth").setOnPreferenceChangeListener { _, newValue ->
                val chatWidth = DisplayUtils.calculateLandscapeWidthByPercent(activity, newValue as Int)
                activity.prefs().edit { putInt(C.LANDSCAPE_CHAT_WIDTH, chatWidth) }
                activity.setResult(Activity.RESULT_OK, Intent().putExtra(C.LANDSCAPE_CHAT_WIDTH, chatWidth))
                true
            }
            findPreference<ListPreference>(C.PORTRAIT_COLUMN_COUNT).setOnPreferenceChangeListener { _, _ ->
                activity.setResult(Activity.RESULT_OK, Intent().putExtra("shouldRecreate", activity.isInPortraitOrientation))
                true
            }
            findPreference<ListPreference>(C.LANDSCAPE_COLUMN_COUNT).setOnPreferenceChangeListener { _, _ ->
                activity.setResult(Activity.RESULT_OK, Intent().putExtra("shouldRecreate", activity.isInLandscapeOrientation))
                true
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && activity.packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)) {
                findPreference<SwitchPreferenceCompat>(C.PICTURE_IN_PICTURE).isVisible = true
            }
        }
    }
}