package com.antoninclouet.hanzilock.data

import android.content.Context
import org.json.JSONObject
import java.time.LocalDate
import java.util.concurrent.TimeUnit

data class CardProgress(
    val box: Int = 1,
    val dueDate: Long = System.currentTimeMillis(),
    val lastReviewed: Long? = null,
    val timesReviewed: Int = 0,
)

/**
 * Répétition espacée type Leitner : 6 boîtes, intervalles croissants.
 * Une carte ratée retombe en boîte 1. Portage de Shared/ReviewStore.swift.
 * Stockage local SharedPreferences — lu par l'app ET par le widget (même process).
 */
class ReviewStore private constructor(context: Context) {

    private val prefs = context.getSharedPreferences("hanzi_store", Context.MODE_PRIVATE)

    private val boxIntervalsDays = intArrayOf(1, 2, 4, 7, 14, 30)

    /** Valeur « illimité » pour la limite (affichée « ∞ » dans les réglages, Pro uniquement). */
    val unlimited = 9999

    // --- Réglages ---------------------------------------------------------

    fun freeLimit(): Int = prefs.getInt(KEY_FREE_LIMIT, FREE_DAILY_REVIEWS)
    fun setFreeLimit(value: Int) = prefs.edit().putInt(KEY_FREE_LIMIT, value).apply()

    /** Sélection brute de l'utilisateur (peut contenir des niveaux Pro non déverrouillés). */
    fun enabledLevels(): Set<Int> {
        val stored = prefs.getStringSet(KEY_LEVELS, null)
            ?.mapNotNull { it.toIntOrNull() }?.toSet()
        return (stored ?: setOf(1)).intersect(HskData.availableLevels).ifEmpty { setOf(1) }
    }

    fun setEnabledLevels(levels: Set<Int>) {
        val safe = levels.intersect(HskData.availableLevels).ifEmpty { setOf(1) }
        prefs.edit().putStringSet(KEY_LEVELS, safe.map { it.toString() }.toSet()).apply()
    }

    /** Niveaux réellement révisés : bridés à FREE_LEVELS sans abonnement. */
    fun effectiveLevels(isPro: Boolean): Set<Int> =
        if (isPro) enabledLevels()
        else enabledLevels().intersect(FREE_LEVELS).ifEmpty { setOf(1) }

    fun activeDeck(isPro: Boolean): List<HanziCard> =
        HskData.deckForLevels(effectiveLevels(isPro))

    fun resetProgress() {
        prefs.edit()
            .remove(KEY_PROGRESS)
            .remove(KEY_COUNT)
            .remove(KEY_COUNT_DAY)
            .apply()
    }

    // --- Limite quotidienne ---------------------------------------------

    fun reviewsRemainingToday(isPro: Boolean): Int {
        if (!isPro) return (FREE_DAILY_REVIEWS - todaysReviewCount()).coerceAtLeast(0)
        val limit = freeLimit()
        if (limit >= unlimited) return Int.MAX_VALUE
        return (limit - todaysReviewCount()).coerceAtLeast(0)
    }

    private fun recordReview() {
        resetCounterIfNewDay()
        prefs.edit().putInt(KEY_COUNT, prefs.getInt(KEY_COUNT, 0) + 1).apply()
    }

    private fun todaysReviewCount(): Int {
        resetCounterIfNewDay()
        return prefs.getInt(KEY_COUNT, 0)
    }

    private fun resetCounterIfNewDay() {
        val today = LocalDate.now().toEpochDay()
        if (prefs.getLong(KEY_COUNT_DAY, Long.MIN_VALUE) != today) {
            prefs.edit().putLong(KEY_COUNT_DAY, today).putInt(KEY_COUNT, 0).apply()
        }
    }

    // --- Progression ------------------------------------------------------

    fun allProgress(): Map<String, CardProgress> {
        val raw = prefs.getString(KEY_PROGRESS, null) ?: return emptyMap()
        return runCatching {
            val root = JSONObject(raw)
            buildMap {
                for (key in root.keys()) {
                    val o = root.getJSONObject(key)
                    put(
                        key,
                        CardProgress(
                            box = o.getInt("box"),
                            dueDate = o.getLong("dueDate"),
                            lastReviewed = if (o.isNull("lastReviewed")) null else o.getLong("lastReviewed"),
                            timesReviewed = o.getInt("timesReviewed"),
                        ),
                    )
                }
            }
        }.getOrDefault(emptyMap())
    }

    private fun save(progress: Map<String, CardProgress>) {
        val root = JSONObject()
        progress.forEach { (id, p) ->
            root.put(
                id,
                JSONObject().apply {
                    put("box", p.box)
                    put("dueDate", p.dueDate)
                    put("lastReviewed", p.lastReviewed ?: JSONObject.NULL)
                    put("timesReviewed", p.timesReviewed)
                },
            )
        }
        prefs.edit().putString(KEY_PROGRESS, root.toString()).apply()
    }

    fun markKnown(cardId: String) {
        recordReview()
        val all = allProgress().toMutableMap()
        val p = all[cardId] ?: CardProgress()
        val newBox = (p.box + 1).coerceAtMost(boxIntervalsDays.size)
        all[cardId] = p.copy(
            box = newBox,
            timesReviewed = p.timesReviewed + 1,
            lastReviewed = System.currentTimeMillis(),
            dueDate = System.currentTimeMillis() +
                TimeUnit.DAYS.toMillis(boxIntervalsDays[newBox - 1].toLong()),
        )
        save(all)
    }

    fun markUnknown(cardId: String) {
        recordReview()
        val all = allProgress().toMutableMap()
        val p = all[cardId] ?: CardProgress()
        all[cardId] = p.copy(
            box = 1,
            timesReviewed = p.timesReviewed + 1,
            lastReviewed = System.currentTimeMillis(),
            dueDate = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(boxIntervalsDays[0].toLong()),
        )
        save(all)
    }

    /** Cartes jamais vues, ou dont la date de révision est passée. */
    fun dueCards(deck: List<HanziCard>): List<HanziCard> {
        val all = allProgress()
        val now = System.currentTimeMillis()
        return deck.filter { card -> (all[card.id]?.dueDate ?: 0L) <= now }
    }

    fun masteredCount(deck: List<HanziCard>, boxThreshold: Int = 4): Int {
        val all = allProgress()
        return deck.count { (all[it.id]?.box ?: 0) >= boxThreshold }
    }

    /** Caractère du jour, déterministe : app et widget affichent la même carte. */
    fun cardOfTheDay(deck: List<HanziCard>): HanziCard {
        if (deck.isEmpty()) return HanziCard("?", "?", "deck vide", 1, "—")
        val dayIndex = LocalDate.now().dayOfYear
        return deck[dayIndex % deck.size]
    }

    companion object {
        /** Modèle gratuit / Pro — voir MONETIZATION.md. */
        val FREE_LEVELS = setOf(1, 2)
        const val FREE_DAILY_REVIEWS = 20

        private const val KEY_PROGRESS = "hanzi.progress.v1"
        private const val KEY_COUNT = "hanzi.reviewCountToday.v1"
        private const val KEY_COUNT_DAY = "hanzi.reviewCountDay.v1"
        private const val KEY_FREE_LIMIT = "hanzi.freeLimit.v1"
        private const val KEY_LEVELS = "hanzi.levels.v1"

        @Volatile
        private var instance: ReviewStore? = null

        fun get(context: Context): ReviewStore =
            instance ?: synchronized(this) {
                instance ?: ReviewStore(context.applicationContext).also { instance = it }
            }
    }
}
