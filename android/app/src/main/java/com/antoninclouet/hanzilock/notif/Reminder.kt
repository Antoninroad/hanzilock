package com.antoninclouet.hanzilock.notif

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import com.antoninclouet.hanzilock.MainActivity
import com.antoninclouet.hanzilock.R
import com.antoninclouet.hanzilock.billing.BillingManager
import com.antoninclouet.hanzilock.data.AppSettings
import com.antoninclouet.hanzilock.data.HanziCard
import com.antoninclouet.hanzilock.data.HskData
import com.antoninclouet.hanzilock.data.ReviewStore
import java.util.Calendar

private const val CH_REMINDER = "daily_reminder"
private const val CH_PERSISTENT = "persistent_card"
private const val ID_REMINDER = 4201
private const val ID_PERSISTENT = 4203
private const val REQUEST_CODE = 4202

private fun todayCard(context: Context): HanziCard {
    val isPro = BillingManager.get(context).isPro.value
    val store = ReviewStore.get(context)
    return store.cardOfTheDay(store.activeDeck(isPro).ifEmpty { HskData.deck })
}

private fun openAppIntent(context: Context): PendingIntent = PendingIntent.getActivity(
    context, 0,
    Intent(context, MainActivity::class.java),
    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
)

/** Notification permanente affichée sur l'écran verrouillé (équivalent Android du widget lock screen). */
object PersistentCard {

    fun apply(context: Context) {
        ensureChannels(context)
        val manager = context.getSystemService<NotificationManager>() ?: return
        if (!AppSettings.get(context).lockScreenCard) {
            manager.cancel(ID_PERSISTENT)
            return
        }
        val card = todayCard(context)
        val notif = NotificationCompat.Builder(context, CH_PERSISTENT)
            .setSmallIcon(R.drawable.ic_stat_seal)
            .setContentTitle("${card.hanzi}  ${card.pinyin}")
            .setContentText(card.meaningFr)
            .setSubText("Caractère du jour")
            .setOngoing(true)
            .setSilent(true)
            .setShowWhen(false)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(openAppIntent(context))
            .build()
        manager.notify(ID_PERSISTENT, notif)
    }
}

/** Rappel quotidien + rafraîchissement de la notification permanente, via une alarme quotidienne. */
object ReminderScheduler {

    fun apply(context: Context) {
        ensureChannels(context)
        PersistentCard.apply(context)

        val settings = AppSettings.get(context)
        val alarm = context.getSystemService<AlarmManager>() ?: return
        val pending = PendingIntent.getBroadcast(
            context, REQUEST_CODE,
            Intent(context, DailyReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        alarm.cancel(pending)

        // Alarme utile si l'un OU l'autre est actif (le rappel à l'heure choisie, ou la MàJ quotidienne).
        if (!settings.reminderEnabled && !settings.lockScreenCard) return

        val hour = if (settings.reminderEnabled) settings.reminderHour else 0
        val minute = if (settings.reminderEnabled) settings.reminderMinute else 1
        val next = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
        }
        alarm.setInexactRepeating(
            AlarmManager.RTC_WAKEUP, next.timeInMillis, AlarmManager.INTERVAL_DAY, pending,
        )
    }

    fun ensureChannel(context: Context) = ensureChannels(context)
}

private fun ensureChannels(context: Context) {
    val manager = context.getSystemService<NotificationManager>() ?: return
    if (manager.getNotificationChannel(CH_REMINDER) == null) {
        manager.createNotificationChannel(
            NotificationChannel(CH_REMINDER, "Rappel quotidien", NotificationManager.IMPORTANCE_DEFAULT)
                .apply { description = "Un rappel par jour pour réviser le caractère du jour." },
        )
    }
    if (manager.getNotificationChannel(CH_PERSISTENT) == null) {
        manager.createNotificationChannel(
            NotificationChannel(CH_PERSISTENT, "Caractère sur l'écran verrouillé", NotificationManager.IMPORTANCE_LOW)
                .apply {
                    description = "Notification discrète affichant le caractère du jour."
                    setShowBadge(false)
                    lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
                },
        )
    }
}

/** Déclenché chaque jour par l'alarme. */
class DailyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        ensureChannels(context)
        PersistentCard.apply(context)

        if (!AppSettings.get(context).reminderEnabled) return
        val card = todayCard(context)
        val notif = NotificationCompat.Builder(context, CH_REMINDER)
            .setSmallIcon(R.drawable.ic_stat_seal)
            .setContentTitle("Caractère du jour : ${card.hanzi} (${card.pinyin})")
            .setContentText("${card.meaningFr} — touche pour réviser")
            .setAutoCancel(true)
            .setContentIntent(openAppIntent(context))
            .build()
        context.getSystemService<NotificationManager>()?.notify(ID_REMINDER, notif)
    }
}

/** Replanifie l'alarme et remet la notification permanente après un redémarrage. */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            ReminderScheduler.apply(context)
        }
    }
}
