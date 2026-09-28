# Popcorn

A Minecraft **1.21.11** mod for the **Fabric** loader that lets you reuse leftover seeds to... pop popcorn!

> Status: early days. The popcorn exists and can be cooked; more to come.

## Content

**Popcorn** (`popcorn:popcorn`) — an edible snack worth 2 hunger points and 0.3 saturation,
listed in the Food & Drinks creative tab.

| Cooked in | Input | Output | Time | XP |
| --- | --- | --- | --- | --- |
| Furnace | 1 × Wheat Seeds | 1 × Popcorn | 10s (200 ticks) | 0.1 |
| Smoker | 1 × Wheat Seeds | 1 × Popcorn | 5s (100 ticks) | 0.1 |

Both recipes unlock in the recipe book as soon as you pick up wheat seeds.

While a lit furnace or smoker has wheat seeds in its input slot it pops, roughly once
every two seconds, at a random pitch.

### Popcorn buckets

Fold a bucket from paper and a dye, fill it with popcorn, then eat it or set it down.

### Caramel popcorn

Popcorn turned in something sweet, worth 3 hunger and 0.5 saturation:

| Recipe | Ingredients | Result |
| --- | --- | --- |
| With sugar | 1 × Popcorn + 1 × Sugar | 1 × Caramel Popcorn |
| With honey | 3 × Popcorn + 1 × Honey Bottle | 3 × Caramel Popcorn |

Both are shapeless and fit the 2×2 inventory grid; the honey recipe hands the empty bottle back.
Fill a bucket with caramel popcorn instead of plain and you get a caramel popcorn bucket, worth
10 hunger and 0.9 saturation.

### Crafting the buckets

Both recipes are shaped and need a crafting table.

```
Empty bucket            Filled bucket

  s d s                   p p p
  s s s                   p b p
                          p p p

s = paper               p = popcorn
d = red/green/black     b = empty popcorn
    dye                     bucket
```

So five paper and one dye make an empty bucket, and eight popcorn fill one.

A filled bucket restores 8 hunger with 0.8 saturation and can be eaten on a full hunger bar. Right-click the air to eat it (the bucket
goes with it), or right-click a block to stand it on top. Up to four buckets fit on one block;
right-click a pile with an empty hand to take one back, the way a cake loses a slice.

Each bucket is a carton the size of a player head — red-and-white stripes, a creeper face, or
enderman eyes on the walls — with the popcorn heaped inside, two pixels below the rim. The walls
have thickness, so you can see into an empty one. Four fit on a block in a 2×2.

## Requirements

- **JDK 21** — required to develop for Minecraft 1.21.11
- IntelliJ IDEA (recommended) or Visual Studio Code with the Gradle for Java extension
- Fabric API, pulled automatically by Gradle

## Getting started

```bash
git clone https://github.com/azolet/popcorn-mod.git
cd popcorn-mod
./gradlew runClient
```

Or open the folder in IntelliJ IDEA and let it import the Gradle project — the
**Minecraft Client**, **Minecraft Server** and **Data Generation** run configurations
appear once the import finishes.

## Commands

```bash
./gradlew build        # build the mod jar into build/libs
./gradlew runClient    # launch a dev client with the mod loaded
./gradlew runServer    # launch a dev dedicated server
./gradlew runDatagen   # run data generation into src/main/generated
./gradlew genSources   # decompile Minecraft sources for browsing in the IDE
```

## Project layout

| Path | Purpose |
| --- | --- |
| `src/main/java` | Common code, runs on both client and dedicated server |
| `src/client/java` | Client-only code (rendering, UI, key binds) |
| `src/main/resources/fabric.mod.json` | Mod metadata, entrypoints, dependencies |
| `src/main/resources/popcorn.mixins.json` | Common mixin config |
| `src/client/resources/popcorn.client.mixins.json` | Client-only mixin config |
| `src/main/resources/assets/popcorn/` | Textures, models, lang files (namespace `popcorn`) |
| `src/main/generated/` | Output of data generation (created by `runDatagen`) |

Entrypoints:

- `main` → `it.argo.mc.mods.popcorn.PopcornMod`
- `client` → `it.argo.mc.mods.popcorn.client.PopcornModClient`
- `fabric-datagen` → `it.argo.mc.mods.popcorn.datagen.PopcornDataGenerator`

## Versions

Pinned in `gradle.properties`; check <https://fabricmc.net/develop> for updates.

| | |
| --- | --- |
| Minecraft | 1.21.11 |
| Fabric Loader | 0.19.5 |
| Fabric API | 0.141.6+1.21.11 |
| Loom | 1.17-SNAPSHOT |
| Mappings | Official Mojang mappings |

## Documentation

- [`docs/SETUP.md`](docs/SETUP.md) — how the project was scaffolded, the decisions behind it,
  and how to add mixins or bump versions.

## License

CC0-1.0, inherited from the Fabric example mod template — see [`LICENSE`](LICENSE).
