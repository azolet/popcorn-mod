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
