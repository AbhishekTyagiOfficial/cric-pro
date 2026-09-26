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

    fun setNoBallExtraRunEnabled(enabled: Boolean) {
        settingsManager.setNoBallExtraRunEnabled(enabled)
    }
}
