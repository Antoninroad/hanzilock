import Foundation

/// ⚠️ Doit être identique à l'App Group créé dans Signing & Capabilities,
/// sur la target de l'app ET sur la target du widget.
let appGroupID = "group.com.antoninclouet.hanzilock"

struct CardProgress: Codable {
    var box: Int = 1
    var dueDate: Date = Date()
    var lastReviewed: Date?
    var timesReviewed: Int = 0
}

/// Système de répétition espacée type Leitner : 6 boîtes, intervalles croissants.
/// Une carte inconnue au premier essai (ou ratée) retombe en boîte 1.
final class ReviewStore {
    static let shared = ReviewStore()

    private let defaults = UserDefaults(suiteName: appGroupID)
    private let progressKey = "hanzi.progress.v1"
    private let boxIntervals = [1, 2, 4, 7, 14, 30] // jours, index 0 = boîte 1

    private let freeReviewLimit = 5
    private let reviewCountKey = "hanzi.reviewCountToday.v1"
    private let reviewCountDateKey = "hanzi.reviewCountDate.v1"

    private init() {}

    /// Nombre de révisions encore autorisées aujourd'hui pour un utilisateur gratuit (illimité si Pro).
    func reviewsRemainingToday(isPro: Bool) -> Int {
        isPro ? Int.max : max(0, freeReviewLimit - todaysReviewCount())
    }

    private func recordReview() {
        resetCounterIfNewDay()
        let count = defaults?.integer(forKey: reviewCountKey) ?? 0
        defaults?.set(count + 1, forKey: reviewCountKey)
    }

    private func todaysReviewCount() -> Int {
        resetCounterIfNewDay()
        return defaults?.integer(forKey: reviewCountKey) ?? 0
    }

    private func resetCounterIfNewDay() {
        let today = Calendar.current.startOfDay(for: Date())
        let lastDate = defaults?.object(forKey: reviewCountDateKey) as? Date
        if lastDate == nil || !Calendar.current.isDate(lastDate!, inSameDayAs: today) {
            defaults?.set(today, forKey: reviewCountDateKey)
            defaults?.set(0, forKey: reviewCountKey)
        }
    }

    func allProgress() -> [String: CardProgress] {
        guard let data = defaults?.data(forKey: progressKey),
              let decoded = try? JSONDecoder().decode([String: CardProgress].self, from: data) else {
            return [:]
        }
        return decoded
    }

    private func save(_ progress: [String: CardProgress]) {
        guard let data = try? JSONEncoder().encode(progress) else { return }
        defaults?.set(data, forKey: progressKey)
    }

    func markKnown(_ cardID: String) {
        recordReview()
        var all = allProgress()
        var p = all[cardID] ?? CardProgress()
        p.box = min(p.box + 1, boxIntervals.count)
        p.timesReviewed += 1
        p.lastReviewed = Date()
        p.dueDate = Calendar.current.date(byAdding: .day, value: boxIntervals[p.box - 1], to: Date()) ?? Date()
        all[cardID] = p
        save(all)
    }

    func markUnknown(_ cardID: String) {
        recordReview()
        var all = allProgress()
        var p = all[cardID] ?? CardProgress()
        p.box = 1
        p.timesReviewed += 1
        p.lastReviewed = Date()
        p.dueDate = Calendar.current.date(byAdding: .day, value: boxIntervals[0], to: Date()) ?? Date()
        all[cardID] = p
        save(all)
    }

    /// Cartes jamais vues, ou dont la date de révision est passée.
    func dueCards(from deck: [HanziCard]) -> [HanziCard] {
        let all = allProgress()
        let now = Date()
        return deck.filter { card in
            guard let p = all[card.id] else { return true }
            return p.dueDate <= now
        }
    }

    func masteredCount(in deck: [HanziCard], boxThreshold: Int = 4) -> Int {
        let all = allProgress()
        return deck.filter { (all[$0.id]?.box ?? 0) >= boxThreshold }.count
    }

    /// Caractère du jour, déterministe (basé sur le jour de l'année) : app et widget
    /// affichent toujours la même carte sans dépendre d'un état déjà ouvert dans l'app.
    func cardOfTheDay(from deck: [HanziCard]) -> HanziCard {
        guard !deck.isEmpty else {
            return HanziCard(hanzi: "?", pinyin: "?", meaningFR: "deck vide", hskLevel: 1, category: "—")
        }
        let dayIndex = Calendar.current.ordinality(of: .day, in: .year, for: Date()) ?? 1
        return deck[dayIndex % deck.count]
    }
}
