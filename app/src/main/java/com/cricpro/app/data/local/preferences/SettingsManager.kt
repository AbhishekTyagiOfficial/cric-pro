package com.cricpro.app.data.local.preferences

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("cricpro_app_settings", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_NO_BALL_EXTRA_RUN_ENABLED = "no_ball_extra_run_enabled"
    }

    private val _isNoBallExtraRunEnabled = MutableStateFlow(
        prefs.getBoolean(KEY_NO_BALL_EXTRA_RUN_ENABLED, true)
    )
    val isNoBallExtraRunEnabled: StateFlow<Boolean> = _isNoBallExtraRunEnabled.asStateFlow()

    fun setNoBallExtraRunEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NO_BALL_EXTRA_RUN_ENABLED, enabled).apply()
        _isNoBallExtraRunEnabled.value = enabled
    }

    fun isNoBallExtraRunEnabledSync(): Boolean {
        return prefs.getBoolean(KEY_NO_BALL_EXTRA_RUN_ENABLED, true)
    }
}
