import SwiftUI

struct FlashcardView: View {
    @State private var queue: [HanziCard] = ReviewStore.shared.dueCards(from: HSKData.deck).shuffled()
    @State private var revealed = false
    @State private var showPaywall = false
    @State private var justStamped = false
    @ObservedObject private var store = PurchaseManager.shared

    private var remainingToday: Int {
        ReviewStore.shared.reviewsRemainingToday(isPro: store.isPro)
    }

    var body: some View {
        NavigationStack {
            VStack(spacing: 24) {
                if remainingToday <= 0 {
                    ContentUnavailableView(
                        "Limite quotidienne atteinte",
                        systemImage: "lock.fill",
                        description: Text("Passe à Pro pour des révisions illimitées, ou reviens demain.")
                    )
                    Button("Débloquer Pro") { showPaywall = true }
                        .buttonStyle(.borderedProminent)
                } else if let card = queue.first {
                    ZStack(alignment: .topTrailing) {
                        VStack(spacing: 12) {
                            Text(card.hanzi)
                                .font(Brand.hanziFont(size: 72))
                                .foregroundStyle(Brand.ink)
                            if revealed {
                                Text(card.pinyin).font(.title2).foregroundStyle(Brand.inkSoft)
                                Text(card.meaningFR).font(.title3).foregroundStyle(Brand.ink)
                            } else {
                                Text("Touche la carte pour révéler")
                                    .foregroundStyle(Brand.inkSoft)
                            }
                        }
                        .frame(maxWidth: .infinity, minHeight: 220)
                        .background(Brand.paperSoft, in: RoundedRectangle(cornerRadius: 20))
                        .contentShape(Rectangle())
                        .onTapGesture { revealed.toggle() }

                        if justStamped {
                            MasteredStamp()
                                .offset(x: 8, y: -8)
                                .transition(.scale(scale: 1.4).combined(with: .opacity))
                        }
                    }

                    if revealed {
                        HStack(spacing: 16) {
                            Button("À revoir") { answer(known: false) }
                                .buttonStyle(.bordered)
                            Button("Je connais") { answer(known: true) }
                                .buttonStyle(.borderedProminent)
                                .tint(Brand.seal)
                        }
                    }

                    if !store.isPro {
                        Text("\(remainingToday) révision(s) gratuite(s) restante(s) aujourd'hui")
                            .font(.footnote)
                            .foregroundStyle(.secondary)
                    } else {
                        Text("\(queue.count) carte(s) restante(s) aujourd'hui")
                            .font(.footnote)
                            .foregroundStyle(.secondary)
                    }
                } else {
                    ContentUnavailableView(
                        "Tout est révisé pour aujourd'hui !",
                        systemImage: "checkmark.circle",
                        description: Text("Reviens demain pour de nouvelles cartes dues.")
                    )
                }
            }
            .padding()
            .navigationTitle("Révision")
            .sheet(isPresented: $showPaywall) { PaywallView() }
        }
    }

    private func answer(known: Bool) {
        guard let card = queue.first else { return }
        if known {
            ReviewStore.shared.markKnown(card.id)
            withAnimation(.spring(response: 0.3, dampingFraction: 0.6)) { justStamped = true }
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.45) {
                justStamped = false
                revealed = false
                queue.removeFirst()
            }
        } else {
            ReviewStore.shared.markUnknown(card.id)
            revealed = false
            queue.removeFirst()
        }
    }
}

#Preview {
    FlashcardView()
}
