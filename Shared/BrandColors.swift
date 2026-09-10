import SwiftUI

/// Tokens de la charte HanziLock (voir le canvas d'identité visuelle : sceau / palette / typographie).
enum Brand {
    static let paper = Color(red: 0xED / 255, green: 0xE7 / 255, blue: 0xDC / 255)
    static let paperSoft = Color(red: 0xF6 / 255, green: 0xF2 / 255, blue: 0xE9 / 255)
    static let ink = Color(red: 0x1C / 255, green: 0x1A / 255, blue: 0x17 / 255)
    static let inkSoft = Color(red: 0x5B / 255, green: 0x56 / 255, blue: 0x4C / 255)
    static let seal = Color(red: 0xB7 / 255, green: 0x30 / 255, blue: 0x1F / 255)
    static let sealPressed = Color(red: 0x8C / 255, green: 0x22 / 255, blue: 0x16 / 255)
    static let jade = Color(red: 0x2F / 255, green: 0x6F / 255, blue: 0x5E / 255)

    /// Approximation système de Noto Serif SC (design serif + graisse black) : évite d'avoir
    /// à embarquer le fichier de police pour un MVP. Pour la fidélité exacte, ajoute
    /// NotoSerifSC-Black.ttf au projet + Info.plist "Fonts provided by application",
    /// puis remplace ceci par Font.custom("NotoSerifSC-Black", size: size).
    static func hanziFont(size: CGFloat) -> Font {
        .system(size: size, weight: .black, design: .serif)
    }

    static let displayFont = Font.system(.title, design: .serif).weight(.semibold)
}
