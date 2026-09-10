"""Bannière Play Store 1024x500 (feature graphic).
    python android/tools/generate_feature_graphic.py
"""
import os
from PIL import Image, ImageDraw, ImageFont

HERE = os.path.dirname(__file__)
OUT = os.path.normpath(os.path.join(HERE, "..", "play", "feature_graphic_1024x500.png"))

PAPER = (0xED, 0xE7, 0xDC)
INK = (0x1C, 0x1A, 0x17)
INK_SOFT = (0x5B, 0x56, 0x4C)
SEAL = (0xB7, 0x30, 0x1F)
GLYPH = "\u5370"  # 印

W, H = 1024, 500
img = Image.new("RGB", (W, H), PAPER)
d = ImageDraw.Draw(img)


def font(path_list, size):
    for p in path_list:
        if os.path.exists(p):
            return ImageFont.truetype(p, size)
    return ImageFont.load_default()


cjk = [r"C:\Windows\Fonts\simsun.ttc", r"C:\Windows\Fonts\msjh.ttc"]
serif = [r"C:\Windows\Fonts\georgia.ttf", r"C:\Windows\Fonts\times.ttf"]
sans = [r"C:\Windows\Fonts\segoeui.ttf", r"C:\Windows\Fonts\arial.ttf"]

# Sceau à gauche
seal_size = 300
sx, sy = 90, (H - seal_size) // 2
d.rounded_rectangle([sx, sy, sx + seal_size, sy + seal_size], radius=40, fill=SEAL)
d.rounded_rectangle([sx + 22, sy + 22, sx + seal_size - 22, sy + seal_size - 22],
                    radius=24, outline=PAPER, width=7)
gf = font(cjk, 190)
b = d.textbbox((0, 0), GLYPH, font=gf)
d.text((sx + (seal_size - (b[2] - b[0])) / 2 - b[0],
        sy + (seal_size - (b[3] - b[1])) / 2 - b[1] - 6), GLYPH, font=gf, fill=PAPER)

# Texte à droite
tx = sx + seal_size + 80
d.text((tx, 150), "HanziLock", font=font(serif, 92), fill=INK)
d.text((tx, 260), "Un caractère chinois par jour,", font=font(sans, 40), fill=INK_SOFT)
d.text((tx, 312), "sur ton écran d'accueil.", font=font(sans, 40), fill=INK_SOFT)
d.text((tx, 386), "HSK 1 à 6  ·  widget  ·  révision espacée", font=font(sans, 30), fill=SEAL)

os.makedirs(os.path.dirname(OUT), exist_ok=True)
img.save(OUT)
print("écrit :", OUT)
