package com.antoninclouet.hanzilock

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import com.antoninclouet.hanzilock.billing.BillingManager
import com.antoninclouet.hanzilock.data.AppSettings
import com.antoninclouet.hanzilock.data.ThemeMode
import com.antoninclouet.hanzilock.notif.PersistentCard
import com.antoninclouet.hanzilock.notif.ReminderScheduler
import com.antoninclouet.hanzilock.ui.MainScreen
import com.antoninclouet.hanzilock.ui.OnboardingScreen
import com.antoninclouet.hanzilock.ui.theme.HanziLockTheme
import com.antoninclouet.hanzilock.widget.HanziWidget
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val prefs = getSharedPreferences("hanzi_app", Context.MODE_PRIVATE)
        val settings = AppSettings.get(this)
        ReminderScheduler.ensureChannel(this)
        ReminderScheduler.apply(this)

        setContent {
            var themeMode by remember { mutableStateOf(settings.themeMode) }
            val dark = when (themeMode) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }

            HanziLockTheme(darkTheme = dark) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    var onboarded by remember { mutableStateOf(prefs.getBoolean(KEY_ONBOARDED, false)) }

                    if (onboarded) {
                        MainScreen(onThemeChange = { themeMode = it })
                    } else {
                        val finish: () -> Unit = {
                            prefs.edit { putBoolean(KEY_ONBOARDED, true) }
                            onboarded = true
                        }
                        OnboardingScreen(onFinish = finish, onOpenPaywall = finish)
                    }
                }
            }
        }

        BillingManager.get(this).start()
    }

    override fun onResume() {
        super.onResume()
        PersistentCard.apply(this)
        lifecycleScope.launch { HanziWidget().updateAll(this@MainActivity) }
    }

    private companion object {
        const val KEY_ONBOARDED = "hasOnboarded"
    }
}
