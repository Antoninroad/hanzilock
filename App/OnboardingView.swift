import SwiftUI

/// Parcours d'accueil affiché une seule fois au premier lancement (drapeau `hasOnboarded`),
/// terminé par une présentation douce du paywall — conforme à l'esprit Guideline 3.1.2
/// (l'utilisateur voit la valeur avant l'offre, et peut fermer sans payer).
struct OnboardingView: View {
    @Binding var hasOnboarded: Bool
    @State private var page = 0
    @State private var showPaywall = false

    private struct Slide: Identifiable {
        let id = UUID()
        let glyph: String
        let title: String
        let body: String
    }

    private let slides: [Slide] = [
        .init(glyph: "日", title: "Un caractère par jour",
              body: "Chaque jour, un nouveau hanzi HSK1 avec son pinyin et son sens. Toujours le même pour l'app et le widget."),
        .init(glyph: "锁", title: "Sur ton écran verrouillé",
              body: "Ajoute le widget à ton écran verrouillé : tu révises sans même ouvrir l'app, juste en regardant l'heure."),
        .init(glyph: "已", title: "Mémorisation espacée",
              body: "Le système Leitner te fait revoir chaque carte au bon moment. Marque « Je connais » et regarde ta progression monter."),
    ]

    var body: some View {
        VStack {
            TabView(selection: $page) {
                ForEach(Array(slides.enumerated()), id: \.element.id) { index, slide in
                    VStack(spacing: 24) {
                        Spacer()
                        Text(slide.glyph)
                            .font(Brand.hanziFont(size: 120))
                            .foregroundStyle(Brand.seal)
                        Text(slide.title)
                            .font(Brand.displayFont)
                            .foregroundStyle(Brand.ink)
                            .multilineTextAlignment(.center)
                        Text(slide.body)
                            .font(.body)
                            .foregroundStyle(Brand.inkSoft)
                            .multilineTextAlignment(.center)
                            .padding(.horizontal, 32)
                        Spacer()
                    }
                    .tag(index)
                }
            }
            .tabViewStyle(.page(indexDisplayMode: .always))

            Button(page == slides.count - 1 ? "Commencer" : "Suivant") {
                if page == slides.count - 1 {
                    showPaywall = true
                } else {
                    withAnimation { page += 1 }
                }
            }
            .buttonStyle(.borderedProminent)
            .tint(Brand.seal)
            .controlSize(.large)
            .padding(.horizontal, 32)

            Button("Ignorer") { hasOnboarded = true }
                .font(.footnote)
                .tint(Brand.inkSoft)
                .padding(.top, 4)
                .padding(.bottom, 12)
        }
        .background(Brand.paper.ignoresSafeArea())
        .sheet(isPresented: $showPaywall, onDismiss: { hasOnboarded = true }) {
            PaywallView()
        }
    }
}

#Preview {
    OnboardingView(hasOnboarded: .constant(false))
}
