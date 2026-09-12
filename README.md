# Popcorns

A Minecraft **1.21.11** mod for the **Fabric** loader that lets you reuse leftover seeds to... pop popcorn!

## Requirements

- **JDK 21** (required to develop for Minecraft 1.21.11)
- IntelliJ IDEA (recommended) or Visual Studio Code with the Gradle for Java extension

## Project layout

| Path | Purpose |
| --- | --- |
| `src/main/java` | Common code, runs on both client and dedicated server |
| `src/client/java` | Client-only code (rendering, UI, key binds) |
| `src/main/resources/fabric.mod.json` | Mod metadata, entrypoints, dependencies |
| `src/main/resources/popcorns.mixins.json` | Common mixin config |
| `src/client/resources/popcorns.client.mixins.json` | Client-only mixin config |
| `src/main/resources/assets/popcorns/` | Textures, models, lang files (namespace `popcorns`) |
| `src/main/generated/` | Output of data generation (created by `runDatagen`) |

Entrypoints:

- `main` → `it.argo.mc.mods.popcorns.PopcornsMod`
- `client` → `it.argo.mc.mods.popcorns.client.PopcornsModClient`
- `fabric-datagen` → `it.argo.mc.mods.popcorns.datagen.PopcornsDataGenerator`

## Common commands

```bash
./gradlew build        # build the mod jar into build/libs
./gradlew runClient    # launch a dev client with the mod loaded
./gradlew runServer    # launch a dev dedicated server
./gradlew runDatagen   # run data generation into src/main/generated
./gradlew genSources   # decompile Minecraft sources for browsing in the IDE
```

## Versions

Pinned in `gradle.properties`; check https://fabricmc.net/develop for updates.

| | |
| --- | --- |
| Minecraft | 1.21.11 |
| Fabric Loader | 0.19.5 |
| Fabric API | 0.141.6+1.21.11 |
| Loom | 1.17-SNAPSHOT |
| Mappings | Official Mojang mappings |

## License

CC0-1.0 (inherited from the Fabric example mod template — change `LICENSE` and the `license` field in `fabric.mod.json` if you want something else).
