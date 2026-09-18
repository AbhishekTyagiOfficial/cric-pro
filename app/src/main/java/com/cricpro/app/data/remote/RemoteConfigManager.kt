package com.cricpro.app.data.remote

import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteConfigManager @Inject constructor(
    private val remoteConfig: FirebaseRemoteConfig
) {
    companion object {
        const val KEY_TOURNAMENT_TAB_ENABLE = "Tournament_tab_enable"
    }

    private val _isTournamentTabEnabled = MutableStateFlow(true)
    val isTournamentTabEnabled: StateFlow<Boolean> = _isTournamentTabEnabled.asStateFlow()

    init {
        initRemoteConfig()
    }

    private fun initRemoteConfig() {
        try {
            val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build()
            remoteConfig.setConfigSettingsAsync(configSettings)
            remoteConfig.setDefaultsAsync(mapOf(KEY_TOURNAMENT_TAB_ENABLE to true))

            remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
                _isTournamentTabEnabled.value = remoteConfig.getBoolean(KEY_TOURNAMENT_TAB_ENABLE)
            }

            remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
                override fun onUpdate(configUpdate: ConfigUpdate) {
                    if (configUpdate.updatedKeys.contains(KEY_TOURNAMENT_TAB_ENABLE)) {
                        remoteConfig.activate().addOnCompleteListener {
                            _isTournamentTabEnabled.value = remoteConfig.getBoolean(KEY_TOURNAMENT_TAB_ENABLE)
                        }
                    }
                }

                override fun onError(error: FirebaseRemoteConfigException) {
                    // Fallback to default/cached value
                }
            })
        } catch (e: Exception) {
            _isTournamentTabEnabled.value = true
        }
    }

    fun getTournamentTabEnabledDirect(): Boolean {
        return try {
            remoteConfig.getBoolean(KEY_TOURNAMENT_TAB_ENABLE)
        } catch (e: Exception) {
            true
        }
    }
}
