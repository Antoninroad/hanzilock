import WidgetKit
import SwiftUI

struct HanziEntry: TimelineEntry {
    let date: Date
    let card: HanziCard
}

struct HanziProvider: TimelineProvider {
    func placeholder(in context: Context) -> HanziEntry {
        HanziEntry(date: Date(), card: HSKData.deck.first!)
    }

    func getSnapshot(in context: Context, completion: @escaping (HanziEntry) -> Void) {
        completion(HanziEntry(date: Date(), card: ReviewStore.shared.cardOfTheDay(from: HSKData.deck)))
    }

    func getTimeline(in context: Context, completion: @escaping (Timeline<HanziEntry>) -> Void) {
        let card = ReviewStore.shared.cardOfTheDay(from: HSKData.deck)
        let entry = HanziEntry(date: Date(), card: card)
        // Rafraîchit à minuit pour faire tourner le "caractère du jour" automatiquement.
        let midnight = Calendar.current.nextDate(
            after: Date(),
            matching: DateComponents(hour: 0, minute: 0),
            matchingPolicy: .nextTime
        ) ?? Date().addingTimeInterval(86400)
        completion(Timeline(entries: [entry], policy: .after(midnight)))
    }
}

struct HanziWidgetEntryView: View {
    @Environment(\.widgetFamily) var family
    var entry: HanziProvider.Entry

    var body: some View {
        switch family {
        // Familles "accessory" (écran verrouillé) : iOS impose son propre rendu monochrome/tinté,
        // toute couleur personnalisée y est ignorée — seule la police de marque s'applique.
        case .accessoryRectangular:
            VStack(alignment: .leading, spacing: 2) {
                Text(entry.card.hanzi).font(Brand.hanziFont(size: 20))
                Text(entry.card.pinyin).font(.caption2)
                Text(entry.card.meaningFR).font(.caption2).lineLimit(1)
            }

        case .accessoryInline:
            Text("\(entry.card.hanzi) · \(entry.card.pinyin) · \(entry.card.meaningFR)")

        case .accessoryCircular:
            VStack(spacing: 0) {
                Text(entry.card.hanzi).font(Brand.hanziFont(size: 22))
                Text(entry.card.pinyin).font(.system(size: 9))
            }

        default: // .systemSmall (écran d'accueil) : là, les couleurs de marque s'appliquent vraiment.
            VStack(spacing: 6) {
                Text(entry.card.hanzi).font(Brand.hanziFont(size: 36)).foregroundStyle(Brand.ink)
                Text(entry.card.pinyin).font(.subheadline).foregroundStyle(Brand.inkSoft)
                Text(entry.card.meaningFR).font(.caption).foregroundStyle(Brand.inkSoft)
            }
            .padding()
        }
    }
}

struct HanziWidget: Widget {
    let kind: String = "HanziWidget"

    var body: some WidgetConfiguration {
        StaticConfiguration(kind: kind, provider: HanziProvider()) { entry in
            HanziWidgetEntryView(entry: entry)
                .containerBackground(Brand.paper, for: .widget) // iOS 17+ ; ignoré sur écran verrouillé (accessory), appliqué sur systemSmall
        }
        .configurationDisplayName("Caractère du jour")
        .description("Affiche un caractère chinois, son pinyin et son sens.")
        .supportedFamilies([.accessoryRectangular, .accessoryInline, .accessoryCircular, .systemSmall])
    }
}
