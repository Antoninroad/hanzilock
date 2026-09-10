import SwiftUI

/// Le repère de marque : un sceau chinois (印章) gravé en style báiwén — le caractère
/// 印 ("sceau / empreinte") en réserve sur un aplat vermillon, avec un cadre intérieur
/// qui imite la bordure gravée (边栏) des sceaux traditionnels. Voir le canvas
/// "HanziLock — Identité visuelle", planche 01, pour le détail de la démarche.
struct SealMark: View {
    var size: CGFloat = 44
    var tint: Color = Brand.seal
    var glyphColor: Color = Brand.paper

    var body: some View {
        ZStack {
            RoundedRectangle(cornerRadius: size * 0.13, style: .continuous)
                .fill(tint)
            RoundedRectangle(cornerRadius: size * 0.08, style: .continuous)
                .stroke(glyphColor, lineWidth: max(1, size * 0.02))
                .padding(size * 0.07)
            Text("印")
                .font(Brand.hanziFont(size: size * 0.6))
                .foregroundStyle(glyphColor)
        }
        .frame(width: size, height: size)
    }
}

#Preview {
    HStack(spacing: 20) {
        SealMark(size: 88)
        SealMark(size: 44, tint: Brand.ink, glyphColor: Brand.paper)
    }
    .padding()
    .background(Brand.paperSoft)
}
