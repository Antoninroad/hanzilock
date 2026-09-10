"""Génère l'icône App Store 1024x1024 (le sceau 印) sans dépendance externe autre que Pillow.
Reproduit la planche "Icône d'app" du canvas d'identité : aplat vermillon plein cadre,
cadre intérieur papier, glyphe 印 en réserve. Pas de transparence, pas de coins arrondis
(Apple les applique lui-même).

    python Brand/generate_icon.py
"""
from PIL import Image, ImageDraw, ImageFont
import os

SIZE = 1024
SEAL = (0xB7, 0x30, 0x1F)
PAPER = (0xED, 0xE7, 0xDC)

OUT_DIR = os.path.join(os.path.dirname(__file__), "..", "App", "Resources",
                       "Assets.xcassets", "AppIcon.appiconset")
os.makedirs(OUT_DIR, exist_ok=True)

img = Image.new("RGB", (SIZE, SIZE), SEAL)
draw = ImageDraw.Draw(img)

# Cadre intérieur gravé (边栏)
inset = int(SIZE * 0.10)
stroke = int(SIZE * 0.028)
draw.rounded_rectangle(
    [inset, inset, SIZE - inset, SIZE - inset],
    radius=int(SIZE * 0.06),
    outline=PAPER,
    width=stroke,
)

# Glyphe 印 centré, police serif chinoise (SimSun) présente sur Windows
font_path = None
for candidate in (r"C:\Windows\Fonts\simsun.ttc", r"C:\Windows\Fonts\msjh.ttc",
                  r"C:\Windows\Fonts\msyh.ttc"):
    if os.path.exists(candidate):
        font_path = candidate
        break
if font_path is None:
    raise SystemExit("Aucune police CJK trouvée — installe SimSun ou modifie font_path.")

font = ImageFont.truetype(font_path, int(SIZE * 0.62))
glyph = "\u5370"  # 印
bbox = draw.textbbox((0, 0), glyph, font=font)
gw, gh = bbox[2] - bbox[0], bbox[3] - bbox[1]
draw.text(
    ((SIZE - gw) / 2 - bbox[0], (SIZE - gh) / 2 - bbox[1] - int(SIZE * 0.01)),
    glyph,
    font=font,
    fill=PAPER,
)

out = os.path.join(OUT_DIR, "icon-1024.png")
img.save(out, "PNG")
print("écrit :", os.path.normpath(out))
