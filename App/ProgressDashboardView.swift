import SwiftUI
import Charts

/// Nommé "Dashboard" pour éviter le conflit avec le type SwiftUI natif ProgressView.
struct ProgressDashboardView: View {
    private var deck: [HanziCard] { HSKData.deck }
    private var mastered: Int { ReviewStore.shared.masteredCount(in: deck) }

    var body: some View {
        NavigationStack {
            VStack(spacing: 20) {
                Text("\(mastered) / \(deck.count)")
                    .font(.system(size: 48, weight: .bold, design: .monospaced))
                    .foregroundStyle(Brand.ink)
                Text("caractères maîtrisés (HSK1)")
                    .foregroundStyle(Brand.inkSoft)

                Chart {
                    BarMark(x: .value("État", "Maîtrisés"), y: .value("Nombre", mastered))
                        .foregroundStyle(Brand.jade)
                    BarMark(x: .value("État", "Restants"), y: .value("Nombre", deck.count - mastered))
                        .foregroundStyle(Brand.inkSoft.opacity(0.25))
                }
                .frame(height: 220)
                .padding()

                Spacer()
            }
            .padding()
            .background(Brand.paper.ignoresSafeArea())
            .navigationTitle("Progrès")
        }
    }
}

#Preview {
    ProgressDashboardView()
}
