import Foundation

/// ⚠️ Ces identifiants doivent être créés à l'identique dans App Store Connect
/// (Fonctionnalités de l'app → Achats intégrés → Abonnements), dans le même groupe d'abonnement.
enum ProductID {
    static let monthly = "com.antoninclouet.hanzilock.pro.monthly"
    static let yearly = "com.antoninclouet.hanzilock.pro.yearly"
    static let all: [String] = [monthly, yearly]
}
