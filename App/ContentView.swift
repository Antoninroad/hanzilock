import SwiftUI

struct ContentView: View {
    var body: some View {
        TabView {
            TodayView()
                .tabItem { Label("Aujourd'hui", systemImage: "sun.max") }
            FlashcardView()
                .tabItem { Label("Cartes", systemImage: "rectangle.stack") }
            ProgressDashboardView()
                .tabItem { Label("Progrès", systemImage: "chart.bar") }
        }
        .tint(Brand.seal)
    }
}

#Preview {
    ContentView()
}
