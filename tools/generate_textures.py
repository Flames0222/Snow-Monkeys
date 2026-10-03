"""Generates the mod's pixel-art textures. Run from the repo root: python3 tools/generate_textures.py

The snow monkey UV layout matches SnowMonkeyModel.createBodyLayer() (standard Minecraft box UVs).
"""
import random
from pathlib import Path

from PIL import Image

ASSETS = Path("src/main/resources/assets/snowmonkeys/textures")
rng = random.Random(1234)


def jitter(color, amount):
    return tuple(max(0, min(255, c + rng.randint(-amount, amount))) for c in color) + (255,)


def fill(img, x, y, w, h, color, noise=0):
    for px in range(x, x + w):
        for py in range(y, y + h):
            img.putpixel((px, py), jitter(color, noise))


def box_faces(u, v, w, h, d):
    """Face rectangles (x, y, w, h) for a cube using Minecraft's box UV layout."""
    return {
        "top": (u + d, v, w, d),
        "bottom": (u + d + w, v, w, d),
        "right": (u, v + d, d, h),
        "front": (u + d, v + d, w, h),
        "left": (u + d + w, v + d, d, h),
        "back": (u + 2 * d + w, v + d, w, h),
    }


FUR = (146, 133, 118)
FUR_DARK = (122, 110, 97)
FUR_LIGHT = (178, 168, 152)
FACE = (222, 108, 106)
FACE_DARK = (190, 82, 82)
EYE = (48, 32, 28)
NOSTRIL = (110, 46, 46)


def paint_box(img, u, v, w, h, d, top=FUR_DARK, sides=FUR, bottom=FUR_LIGHT):
    faces = box_faces(u, v, w, h, d)
    for name, (x, y, fw, fh) in faces.items():
        color = top if name == "top" else bottom if name == "bottom" else sides
        fill(img, x, y, fw, fh, color, 10)
    return faces


def snow_monkey(relaxed):
    img = Image.new("RGBA", (64, 64), (0, 0, 0, 0))

    body = paint_box(img, 0, 0, 7, 6, 10)
    # Red rump on the back face, the macaque's best-known feature after its face.
    bx, by, _, _ = body["back"]
    fill(img, bx + 2, by + 2, 3, 3, FACE, 6)

    head = paint_box(img, 0, 16, 6, 6, 5)
    fx, fy, _, _ = head["front"]
    # 6x6 face: grey fur ring around a red, hairless face.
    layout = [
        "FFFFFF",
        "FPPPPF",
        "PEPPEP",
        "PPPPPP",
        "PPDDPP",
        "FPPPPF",
    ]
    for row, line in enumerate(layout):
        for col, ch in enumerate(line):
            if ch == "F":
                color = FUR_LIGHT
            elif ch == "E":
                color = FACE_DARK if relaxed else EYE
            elif ch == "D":
                color = FACE_DARK
            else:
                color = FACE
            img.putpixel((fx + col, fy + row), jitter(color, 4))
    if relaxed:
        # Closed eyes: a small dark crease line under each eyelid.
        img.putpixel((fx + 1, fy + 2), (120, 50, 50, 255))
        img.putpixel((fx + 4, fy + 2), (120, 50, 50, 255))

    muzzle = box_faces(22, 16, 3, 2, 1)
    for x, y, w, h in muzzle.values():
        fill(img, x, y, w, h, FACE, 4)
    mx, my, _, _ = muzzle["front"]
    img.putpixel((mx, my), NOSTRIL + (255,))
    img.putpixel((mx + 2, my), NOSTRIL + (255,))

    for u in (22, 26):
        ear = paint_box(img, u, 19, 1, 2, 1)
        ex, ey, _, _ = ear["front"]
        fill(img, ex, ey, 1, 2, FACE, 4)

    paint_box(img, 32, 16, 1, 1, 3, top=FUR, sides=FUR_DARK)

    for u in (0, 8, 16, 24):
        limb = paint_box(img, u, 27, 2, 6, 2)
        # Darker hands and feet.
        for name in ("front", "back", "left", "right"):
            x, y, w, h = limb[name]
            fill(img, x, y + h - 1, w, 1, (96, 84, 74), 4)
        x, y, w, h = limb["bottom"]
        fill(img, x, y, w, h, (150, 92, 86), 4)
    return img


def onsen_stone():
    img = Image.new("RGBA", (16, 16))
    for y in range(16):
        band = (206, 196, 176) if (y // 3) % 2 == 0 else (192, 178, 152)
        for x in range(16):
            img.putpixel((x, y), jitter(band, 8))
    for _ in range(9):
        x, y = rng.randrange(16), rng.randrange(16)
        img.putpixel((x, y), jitter((214, 152, 92), 10))
    for _ in range(6):
        x, y = rng.randrange(16), rng.randrange(16)
        img.putpixel((x, y), jitter((150, 142, 128), 6))
    return img


def vent_side():
    img = Image.new("RGBA", (16, 16))
    for y in range(16):
        for x in range(16):
            img.putpixel((x, y), jitter((62, 60, 66), 8))
    # Glowing cracks.
    x = 5
    for y in range(16):
        x = max(1, min(14, x + rng.choice((-1, 0, 0, 1))))
        img.putpixel((x, y), jitter((232, 122, 44), 12))
    x = 11
    for y in range(4, 13):
        x = max(1, min(14, x + rng.choice((-1, 0, 1))))
        img.putpixel((x, y), jitter((240, 160, 60), 12))
    return img


def vent_top():
    img = Image.new("RGBA", (16, 16))
    for y in range(16):
        for x in range(16):
            d = ((x - 7.5) ** 2 + (y - 7.5) ** 2) ** 0.5
            if d < 2.5:
                color = (255, 196, 90)
            elif d < 4:
                color = (232, 110, 40)
            elif d < 5.5:
                color = (40, 38, 44)
            else:
                color = (66, 64, 70)
            img.putpixel((x, y), jitter(color, 8))
    return img


def vent_bottom():
    img = Image.new("RGBA", (16, 16))
    for y in range(16):
        for x in range(16):
            img.putpixel((x, y), jitter((58, 56, 62), 8))
    return img


def from_ascii(rows, palette):
    img = Image.new("RGBA", (len(rows[0]), len(rows)), (0, 0, 0, 0))
    for y, row in enumerate(rows):
        for x, ch in enumerate(row):
            if ch != ".":
                img.putpixel((x, y), palette[ch] + (255,))
    return img


BUCKET = [
    "................",
    "................",
    "................",
    "....kkkkkkkk....",
    "...kwWwwWwwWk...",
    "..kwwWwwwWwwwk..",
    "..klllllllllhk..",
    "..kllllllllhhk..",
    "...kllllllhhk...",
    "...klllllllhk...",
    "...kllllllhhk...",
    "....klllllhk....",
    "....kllllhhk....",
    "....kkkkkkkk....",
    "................",
    "................",
]
BUCKET_PALETTE = {
    "k": (52, 52, 56),
    "w": (110, 214, 200),
    "W": (196, 244, 236),
    "l": (198, 198, 204),
    "h": (150, 150, 158),
}

EGG = [
    "................",
    ".....s...s......",
    "......s...s.....",
    ".....s...s......",
    "................",
    "......oooo......",
    ".....occcco.....",
    "....occcccco....",
    "....occcccdo....",
    "...occcccccdo...",
    "...occcyyccdo...",
    "...occyYYycdo...",
    "...occcyyccdo...",
    "....occcccdo....",
    ".....oddddo.....",
    "......oooo......",
]
EGG_PALETTE = {
    "o": (122, 98, 72),
    "c": (238, 226, 198),
    "d": (206, 188, 152),
    "y": (240, 170, 60),
    "Y": (255, 214, 110),
    "s": (230, 236, 240),
}

# Onsen symbol: three wavy steam lines rising from a bowl.
WARMTH = [
    "..................",
    "..................",
    "....s....s....s...",
    "...s....s....s....",
    "....s....s....s...",
    ".....s....s....s..",
    "....s....s....s...",
    "...s....s....s....",
    "....s....s....s...",
    "..................",
    "..o............o..",
    "..oo..........oo..",
    "...ooo......ooo...",
    "....oooooooooo....",
    "......oooooo......",
    "..................",
    "..................",
    "..................",
]
WARMTH_PALETTE = {"s": (240, 120, 70), "o": (226, 82, 60)}


def main():
    (ASSETS / "entity").mkdir(parents=True, exist_ok=True)
    (ASSETS / "block").mkdir(parents=True, exist_ok=True)
    (ASSETS / "item").mkdir(parents=True, exist_ok=True)
    (ASSETS / "mob_effect").mkdir(parents=True, exist_ok=True)
    snow_monkey(False).save(ASSETS / "entity/snow_monkey.png")
    snow_monkey(True).save(ASSETS / "entity/snow_monkey_relaxed.png")
    onsen_stone().save(ASSETS / "block/onsen_stone.png")
    vent_side().save(ASSETS / "block/hot_spring_vent_side.png")
    vent_top().save(ASSETS / "block/hot_spring_vent_top.png")
    vent_bottom().save(ASSETS / "block/hot_spring_vent_bottom.png")
    from_ascii(BUCKET, BUCKET_PALETTE).save(ASSETS / "item/hot_spring_water_bucket.png")
    from_ascii(EGG, EGG_PALETTE).save(ASSETS / "item/onsen_egg.png")
    from_ascii(WARMTH, WARMTH_PALETTE).save(ASSETS / "mob_effect/warmth.png")


if __name__ == "__main__":
    main()
