import Foundation

struct HanziCard: Identifiable, Codable, Hashable {
    /// Le hanzi sert d'identifiant unique dans ce MVP (pas de doublons dans le deck).
    var id: String { hanzi }

    let hanzi: String
    let pinyin: String
    let meaningFR: String
    let hskLevel: Int
    let category: String
}
