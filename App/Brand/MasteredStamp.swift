import SwiftUI

/// Estampille apposée sur une carte qui vient d'être marquée « connue » — écho du
/// tampon d'approbation posé traditionnellement sur un document ou une œuvre.
/// 已 = "fait / déjà accompli".
struct MasteredStamp: View {
    var body: some View {
        Text("已")
            .font(Brand.hanziFont(size: 26))
            .foregroundStyle(Brand.seal)
            .frame(width: 56, height: 56)
            .overlay(
                RoundedRectangle(cornerRadius: 10)
                    .stroke(Brand.seal, lineWidth: 3)
            )
            .rotationEffect(.degrees(-14))
    }
}

#Preview {
    MasteredStamp().padding().background(Brand.paperSoft)
}
