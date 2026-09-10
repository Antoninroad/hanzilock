package com.antoninclouet.hanzilock.data

/** Le hanzi sert d'identifiant unique dans ce MVP (pas de doublons dans le deck). */
data class HanziCard(
    val hanzi: String,
    val pinyin: String,
    val meaningFr: String,
    val hskLevel: Int,
    val category: String,
) {
    val id: String get() = hanzi
}
