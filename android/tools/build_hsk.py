"""Génère app/src/main/assets/hsk_full.json à partir du jeu de données HSK complet
(hanzi + pinyin + niveau) traduit de l'anglais vers le français hors-ligne (argostranslate).

    python android/tools/build_hsk.py

- Source : drkameleon/complete-hsk-vocabulary (CC-CEDICT glosses).
- Niveaux : HSK 2.0 « old » 1 à 6 (~5000 mots).
- Les mots déjà traduits à la main dans HskData.kt gardent leur traduction FR.
"""
import json
import os
import re
import sys
import urllib.request

# ctranslate2 échoue avec mkl_malloc sur certaines machines Windows.
os.environ.setdefault("CT2_USE_MKL", "0")
os.environ.setdefault("CT2_FORCE_CPU_ISA", "GENERIC")
os.environ.setdefault("OMP_NUM_THREADS", "2")

HERE = os.path.dirname(__file__)
ASSETS = os.path.normpath(os.path.join(HERE, "..", "app", "src", "main", "assets"))
CACHE = os.path.join(HERE, "_hsk_source.json")
CURATED = os.path.normpath(os.path.join(
    HERE, "..", "app", "src", "main", "java", "com", "antoninclouet", "hanzilock", "data", "HskData.kt"))
SRC_URL = "https://raw.githubusercontent.com/drkameleon/complete-hsk-vocabulary/main/complete.json"


def load_source():
    if not os.path.exists(CACHE):
        print("Téléchargement du jeu de données…")
        urllib.request.urlretrieve(SRC_URL, CACHE)
    return json.load(open(CACHE, encoding="utf-8"))


def old_level(entry):
    lv = [int(m.group(1)) for tag in entry.get("level", [])
          for m in [re.match(r"old-(\d)$", tag)] if m]
    return min(lv) if lv else None


def short_gloss(meanings):
    """Garde 1-2 sens courts, nettoie les notes CC-CEDICT entre parenthèses trop longues."""
    if not meanings:
        return ""
    picked = []
    for m in meanings:
        m = m.strip()
        m = re.sub(r"\s*\([^)]{20,}\)", "", m)       # enlève les longues parenthèses
        m = re.sub(r"\bCL:.*$", "", m).strip(" ;,")  # enlève les classificateurs
        m = re.sub(r"\bvariant of .*$", "variante", m)
        m = re.sub(r"^(to|a|an|the) ", "", m)         # évite l'artefact « pour … » d'argos
        if not m:
            continue
        picked.append(m)
        if len(picked) == 2 or len("; ".join(picked)) > 38:
            break
    return "; ".join(picked)[:70]


def detok(tokens):
    return "".join(tokens).replace("▁", " ").strip()


def clean_fr(s):
    s = s.strip().rstrip(".")
    s = s.replace(" bb ", " qqn ").replace("(bb", "(qqn").replace("bb)", "qqn)")
    s = re.sub(r"^(pour|de|le|la|les|l'|un|une|des) ", "", s, flags=re.I)
    # garde au plus 2 sens, déduplique, limite la longueur
    parts = [p.strip(" .") for p in re.split(r"[;,]", s) if p.strip(" .")]
    seen, out = set(), []
    for p in parts:
        k = p.lower()
        if k in seen:
            continue
        seen.add(k)
        out.append(p)
        if len(out) == 2 or len(" ; ".join(out)) > 42:
            break
    s = " ; ".join(out)[:48].rstrip(" ;")
    return (s[:1].lower() + s[1:]) if s else s


def clean_pinyin(p):
    p = p.replace(" ", "")
    return (p[:1].lower() + p[1:]) if p else p


def curated_fr():
    """Récupère les traductions FR déjà écrites à la main dans HskData.kt."""
    txt = open(CURATED, encoding="utf-8").read()
    out = {}
    for hanzi, _pin, fr in re.findall(r'Triple\("([^"]+)",\s*"([^"]+)",\s*"([^"]+)"\)', txt):
        out[hanzi] = fr
    return out


def main():
    data = load_source()
    curated = curated_fr()
    print(f"{len(curated)} traductions FR manuelles réutilisées.")

    rows = []
    for e in data:
        lv = old_level(e)
        if lv is None:
            continue
        form = e.get("forms", [{}])[0]
        pinyin = clean_pinyin(form.get("transcriptions", {}).get("pinyin", "").strip())
        rows.append({
            "h": e["simplified"],
            "p": pinyin,
            "l": lv,
            "_en": short_gloss(form.get("meanings", [])),
        })
    rows.sort(key=lambda r: (r["l"], r["h"]))
    print(f"{len(rows)} mots HSK 1-6.")

    to_translate = [r for r in rows if r["h"] not in curated and r["_en"]]
    uniq = sorted({r["_en"] for r in to_translate})
    print(f"{len(to_translate)} mots à traduire ({len(uniq)} glossaires uniques, EN→FR)…")

    # Traduction directe via le modèle OPUS-MT d'argostranslate (ctranslate2 + sentencepiece),
    # sans passer par argostranslate/stanza/torch (qui saturent la mémoire sur cette machine).
    import glob
    try:
        import ctranslate2
        import sentencepiece as spm
    except ImportError:
        sys.exit("pip install ctranslate2 sentencepiece")

    pkg_dir = glob.glob(os.path.expanduser(
        "~/.local/share/argos-translate/packages/translate-en_fr*"))
    if not pkg_dir:
        sys.exit("Modèle EN→FR argos absent : lance une fois "
                 "`python -c \"import argostranslate.package as p; p.update_package_index(); "
                 "pk=next(x for x in p.get_available_packages() if x.from_code=='en' and x.to_code=='fr'); "
                 "p.install_from_path(pk.download())\"`")
    pkg_dir = pkg_dir[0]
    sp = spm.SentencePieceProcessor(model_file=os.path.join(pkg_dir, "sentencepiece.model"))
    model_path = os.path.join(pkg_dir, "model")

    # Cache disque : le script est relançable si ctranslate2 sature la mémoire en cours de route.
    cache_file = os.path.join(HERE, "_fr_cache.json")
    cache = {}
    if os.path.exists(cache_file):
        cache = json.load(open(cache_file, encoding="utf-8"))
        print(f"{len(cache)} traductions déjà en cache.")

    todo = [s for s in uniq if s not in cache]
    B = 48
    import gc
    translator = None
    done = 0
    try:
        for i in range(0, len(todo), B):
            if translator is None or done % 480 == 0:
                del translator
                gc.collect()
                translator = ctranslate2.Translator(model_path, device="cpu",
                                                    intra_threads=2, inter_threads=1)
            chunk = todo[i:i + B]
            batch = [sp.encode(s, out_type=str) for s in chunk]
            res = translator.translate_batch(batch, max_batch_size=B, beam_size=1)
            for s, r in zip(chunk, res):
                cache[s] = clean_fr(detok(r.hypotheses[0]))
            done += len(chunk)
            if i % (B * 10) == 0:
                json.dump(cache, open(cache_file, "w", encoding="utf-8"), ensure_ascii=False)
                print(f"  {len(cache)}/{len(uniq)}")
    finally:
        json.dump(cache, open(cache_file, "w", encoding="utf-8"), ensure_ascii=False)

    missing = [s for s in uniq if s not in cache]
    if missing:
        print(f"⚠️ {len(missing)} glossaires non traduits (relance le script) — repli sur l'anglais.")

    for r in to_translate:
        r["_fr"] = clean_fr(cache.get(r["_en"], r["_en"]))

    # Quelques mots sans glossaire anglais dans le jeu de données.
    patch = {
        "分之": "particule de fraction (三分之一 = 1/3)",
        "呀": "particule (variante de 啊)",
        "底": "particule possessive (littéraire, = 的)",
        "弄": "faire / bricoler",
        "举": "lever / soulever",
    }
    for r in rows:
        r["f"] = patch.get(r["h"]) or curated.get(r["h"]) or r.get("_fr") or r["_en"] or "—"
        r.pop("_en", None)
        r.pop("_fr", None)

    os.makedirs(ASSETS, exist_ok=True)
    out = os.path.join(ASSETS, "hsk_full.json")
    json.dump(rows, open(out, "w", encoding="utf-8"), ensure_ascii=False, separators=(",", ":"))
    kb = os.path.getsize(out) // 1024
    print(f"écrit : {out}  ({len(rows)} mots, {kb} Ko)")


if __name__ == "__main__":
    main()
