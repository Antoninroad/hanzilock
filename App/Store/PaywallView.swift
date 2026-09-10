import SwiftUI
import StoreKit

/// Respecte la Guideline App Store 3.1.2 : titre, durée, prix et fonctionnalités
/// débloquées doivent être visibles AVANT l'achat, avec liens CGU/politique de confidentialité.
struct PaywallView: View {
    @ObservedObject private var store = PurchaseManager.shared
    @Environment(\.dismiss) private var dismiss

    // URLs publiques — pages hébergées via GitHub Pages (dépôt Antoninroad/hanzilock, dossier /docs).
    // Active Pages : Settings → Pages → Deploy from branch → main /docs.
    private let termsURL = URL(string: "https://www.apple.com/legal/internet-services/itunes/dev/stdeula/")!
    private let privacyURL = URL(string: "https://antoninroad.github.io/hanzilock/privacy.html")!

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: 20) {
                    SealMark(size: 64)
                        .padding(.top, 12)

                    Text("Débloque HanziLock Pro")
                        .font(Brand.displayFont)
                        .foregroundStyle(Brand.ink)

                    VStack(alignment: .leading, spacing: 10) {
                        Label("Révisions illimitées chaque jour", systemImage: "infinity")
                        Label("Widget écran verrouillé sur toutes les catégories", systemImage: "lock.rectangle")
                        Label("Suivi de progression détaillé", systemImage: "chart.bar")
                    }
                    .font(.subheadline)
                    .foregroundStyle(Brand.ink)
                    .tint(Brand.seal)
                    .frame(maxWidth: .infinity, alignment: .leading)

                    if store.products.isEmpty {
                        ProgressView("Chargement des offres…")
                            .padding()
                    } else {
                        ForEach(store.products) { product in
                            Button {
                                Task { await store.purchase(product) }
                            } label: {
                                HStack {
                                    VStack(alignment: .leading) {
                                        Text(product.displayName).bold()
                                        Text(subscriptionLengthLabel(product))
                                            .font(.caption)
                                            .foregroundStyle(.secondary)
                                    }
                                    Spacer()
                                    Text(product.displayPrice).bold().foregroundStyle(Brand.seal)
                                }
                                .foregroundStyle(Brand.ink)
                                .padding()
                                .background(Brand.paperSoft, in: RoundedRectangle(cornerRadius: 12))
                                .overlay(RoundedRectangle(cornerRadius: 12).stroke(Brand.seal.opacity(0.25), lineWidth: 1))
                            }
                            .buttonStyle(.plain)
                        }
                    }

                    Button("Restaurer mes achats") {
                        Task { await store.restorePurchases() }
                    }
                    .font(.footnote)
                    .tint(Brand.seal)

                    VStack(spacing: 4) {
                        Text("Abonnement reconductible automatiquement, résiliable à tout moment dans Réglages > [ton identifiant Apple] > Abonnements.")
                        HStack(spacing: 12) {
                            Link("Conditions d'utilisation", destination: termsURL)
                            Link("Politique de confidentialité", destination: privacyURL)
                        }
                    }
                    .font(.caption2)
                    .foregroundStyle(.secondary)
                    .multilineTextAlignment(.center)
                }
                .padding()
            }
            .navigationTitle("HanziLock Pro")
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Fermer") { dismiss() }
                }
            }
            .task { await store.loadProducts() }
        }
    }

    private func subscriptionLengthLabel(_ product: Product) -> String {
        guard let period = product.subscription?.subscriptionPeriod else { return "" }
        switch period.unit {
        case .day: return "\(period.value) jour(s)"
        case .week: return "\(period.value) semaine(s)"
        case .month: return "\(period.value) mois"
        case .year: return "\(period.value) an(s)"
        @unknown default: return ""
        }
    }
}

#Preview {
    PaywallView()
}
