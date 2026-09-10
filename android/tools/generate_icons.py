"""Génère les icônes Android (adaptive foreground + icône Play Store 512).
    python android/tools/generate_icons.py
Nécessite Pillow (pip install Pillow) et une police CJK (SimSun sur Windows).
"""
import os
from PIL import Image, ImageDraw, ImageFont

HERE = os.path.dirname(__file__)
RES = os.path.normpath(os.path.join(HERE, "..", "app", "src", "main", "res"))
PLAY = os.path.normpath(os.path.join(HERE, "..", "play"))

SEAL = (0xB7, 0x30, 0x1F)
PAPER = (0xED, 0xE7, 0xDC)
GLYPH = "\u5370"  # 印

def font(size):
    for p in (r"C:\Windows\Fonts\simsun.ttc", r"C:\Windows\Fonts\msjh.ttc", r"C:\Windows\Fonts\msyh.ttc"):
        if os.path.exists(p):
            return ImageFont.truetype(p, size)
    raise SystemExit("Police CJK introuvable.")

def draw_glyph(img, frac, fill, dy_frac=-0.01):
    d = ImageDraw.Draw(img)
    f = font(int(img.width * frac))
    b = d.textbbox((0, 0), GLYPH, font=f)
    w, h = b[2] - b[0], b[3] - b[1]
    d.text(((img.width - w) / 2 - b[0], (img.height - h) / 2 - b[1] + img.height * dy_frac),
           GLYPH, font=f, fill=fill)

# 1. Adaptive foreground : glyphe papier sur fond transparent (432 px, zone sûre ~66%)
fg = Image.new("RGBA", (432, 432), (0, 0, 0, 0))
draw_glyph(fg, 0.42, PAPER + (255,))
d = os.path.join(RES, "drawable-nodpi")
os.makedirs(d, exist_ok=True)
fg.save(os.path.join(d, "ic_launcher_foreground.png"))

# 2. Icône Play Store 512 : plein cadre vermillon + cadre gravé + glyphe
store = Image.new("RGB", (512, 512), SEAL)
ds = ImageDraw.Draw(store)
inset, stroke = int(512 * 0.10), int(512 * 0.028)
ds.rounded_rectangle([inset, inset, 512 - inset, 512 - inset],
                     radius=int(512 * 0.06), outline=PAPER, width=stroke)
draw_glyph(store, 0.62, PAPER)
os.makedirs(PLAY, exist_ok=True)
store.save(os.path.join(PLAY, "play_store_512.png"))

print("OK :")
print(" ", os.path.join(d, "ic_launcher_foreground.png"))
print(" ", os.path.join(PLAY, "play_store_512.png"))
