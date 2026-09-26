package com.cricpro.app.presentation.settings

import androidx.lifecycle.ViewModel
import com.cricpro.app.data.local.preferences.SettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsManager: SettingsManager
) : ViewModel() {

    val isNoBallExtraRunEnabled: StateFlow<Boolean> = settingsManager.isNoBallExtraRunEnabled
    val isWideExtraRunEnabled: StateFlow<Boolean> = settingsManager.isWideExtraRunEnabled

    fun setNoBallExtraRunEnabled(enabled: Boolean) {
        settingsManager.setNoBallExtraRunEnabled(enabled)
    }

    fun setWideExtraRunEnabled(enabled: Boolean) {
        settingsManager.setWideExtraRunEnabled(enabled)
    }
}
