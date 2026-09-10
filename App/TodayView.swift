import SwiftUI

struct TodayView: View {
    private var card: HanziCard { ReviewStore.shared.cardOfTheDay(from: HSKData.deck) }
    @ObservedObject private var store = PurchaseManager.shared
    @State private var showPaywall = false

    var body: some View {
        NavigationStack {
            VStack(spacing: 16) {
                Spacer()
                Text(card.hanzi)
                    .font(Brand.hanziFont(size: 96))
                    .foregroundStyle(Brand.ink)
                Text(card.pinyin)
                    .font(.title2)
                    .foregroundStyle(Brand.inkSoft)
                Text(card.meaningFR)
                    .font(.title3)
                    .foregroundStyle(Brand.ink)
                Text("HSK \(card.hskLevel) · \(card.category)")
                    .font(.caption)
                    .foregroundStyle(Brand.inkSoft)
                Spacer()
                Text("Ajoute le widget à ton écran verrouillé (appui long → Personnaliser → Écran verrouillé) pour le voir sans ouvrir l'app.")
                    .font(.footnote)
                    .multilineTextAlignment(.center)
                    .foregroundStyle(Brand.inkSoft)
                    .padding(.horizontal)
            }
            .padding()
            .background(Brand.paper.ignoresSafeArea())
            .navigationTitle("Caractère du jour")
            .toolbar {
                if !store.isPro {
                    ToolbarItem(placement: .topBarTrailing) {
                        Button("Pro") { showPaywall = true }
                    }
                }
            }
            .sheet(isPresented: $showPaywall) { PaywallView() }
        }
    }
}

#Preview {
    TodayView()
}
