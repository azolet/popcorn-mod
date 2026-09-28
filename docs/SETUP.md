# Project setup

How this project was scaffolded, what was decided, and how to work with it.

Scaffolded from the official [FabricMC example mod](https://github.com/FabricMC/fabric-example-mod)
(branch `1.21.11`), following the Fabric documentation for
[creating a project](https://docs.fabricmc.net/1.21.11/develop/getting-started/creating-a-project),
[project structure](https://docs.fabricmc.net/1.21.11/develop/getting-started/project-structure) and
[setting up](https://docs.fabricmc.net/1.21.11/develop/getting-started/setting-up).

## Identity

| | |
| --- | --- |
| Mod ID | `popcorn` |
| Root package | `it.argo.mc.mods.popcorn` |
| Gradle group | `it.argo.mc.mods` |
| `rootProject.name` | `popcorn` |
| Mod version | `0.1.0` (`gradle.properties`) |
| Asset namespace | `popcorn` |

Whenever the Fabric docs use `example-mod`, `modid` or `com.example`, substitute the values above.

## Toolchain

| | |
| --- | --- |
| Minecraft | 1.21.11 |
| Fabric Loader | 0.19.5 |
| Fabric API | 0.141.6+1.21.11 |
| Fabric Loom | 1.17-SNAPSHOT |
| Gradle | 9.5.1 (via the wrapper) |
| JDK | 21 — required for 1.21.11 |
| Mappings | Official Mojang mappings |

All of the Fabric versions are pinned in `gradle.properties`; check
<https://fabricmc.net/develop> before bumping them.

## Decisions

- **Mojang mappings, not Yarn.** This is the default for the 1.21.11 template and for the docs.
  Class names are the Mojang ones — `Minecraft` (not `MinecraftClient`),
  `net.minecraft.resources.Identifier`. Tutorials written against Yarn will need translating.
- **Split environment source sets** (`splitEnvironmentSourceSets()` in `build.gradle`):
  `src/main` is common code, `src/client` is client-only. Code that touches rendering, screens or
  key binds belongs in `src/client`; anything the dedicated server must also run belongs in `src/main`.
- **Data generation enabled.** `build.gradle` declares
  `fabricApi { configureDataGeneration() { client = true } }`, which adds the "Data Generation" run
  configuration. Because `client = true`, the generator entrypoint lives in the client source set.
- **No example mixins.** The template's sample mixins were removed. Both mixin configs are kept and
  registered in `fabric.mod.json` with empty lists, and each mixin package is held by a
  `package-info.java` so the package exists before the first mixin is written.

## Layout

```
src/main/java/it/argo/mc/mods/popcorn/          common code
                                      /mixin/    common mixins
src/main/resources/fabric.mod.json               mod metadata
                  /popcorn.mixins.json          common mixin config
                  /assets/argo_popcorn/              textures, models, lang
src/main/generated/                              runDatagen output (a resource root)
src/client/java/it/argo/mc/mods/popcorn/client/ client-only code
                                         /mixin/ client-only mixins
                                  .../datagen/   data generation entrypoint
src/client/resources/popcorn.client.mixins.json client mixin config
```

## Entrypoints

Declared in `src/main/resources/fabric.mod.json`:

| Entrypoint | Class |
| --- | --- |
| `main` | `it.argo.mc.mods.popcorn.PopcornMod` |
| `client` | `it.argo.mc.mods.popcorn.client.PopcornModClient` |
| `fabric-datagen` | `it.argo.mc.mods.popcorn.datagen.PopcornDataGenerator` |

`PopcornMod` exposes `MOD_ID`, an SLF4J `LOGGER` named after the mod id, and
`id(String path)` for building identifiers in the `popcorn` namespace.

## Commands

```bash
./gradlew build        # build the mod jar into build/libs
./gradlew runClient    # launch a dev client with the mod loaded
./gradlew runServer    # launch a dev dedicated server
./gradlew runDatagen   # run data generation into src/main/generated
./gradlew genSources   # decompile Minecraft for browsing in the IDE
```

## Running the dev client with shaders

`build.gradle` can pull Iris, Sodium and a shader pack into the development client. It is off until
you fill in three properties in `gradle.properties`, taken from the Modrinth pages for the
Minecraft version in use:

```properties
iris_version=1.10.7+1.21.11-fabric
sodium_version=mc1.21.11-0.8.14-fabric
shaderpack=bsl-shaders:10.1.8
```

**Do not pick the Sodium version by "newest".** Sodium declares which Iris versions it breaks, so
a newer Sodium than the one Iris was built against stops the client at load with "Some of your mods
are incompatible". Take the version Iris itself requires:

```
GET https://api.modrinth.com/v2/project/iris/version?loaders=["fabric"]&game_versions=["<mc>"]
      -> the wanted release's dependencies[].version_id
GET https://api.modrinth.com/v2/version/<version_id>
      -> version_number, which is the Sodium to pin
```

Iris 1.10.7 pins Sodium `mc1.21.11-0.8.7-fabric` this way. Re-run those two calls whenever Iris is
bumped, and move both properties together.

With them set, `./gradlew runClient` copies Iris and Sodium into `run/mods/`, copies the pack into
`run/shaderpacks/`, and writes `shaderPack` and `enableShaders` into `run/config/iris.properties`.
With them unset, nothing changes. The Modrinth repository is scoped with `exclusiveContent` to the
`maven.modrinth` group, so no other dependency can ever be resolved from it.

**Install them into `run/mods/`, not as `modLocalRuntime` dependencies.** Iris nests `jcpp`,
`glsl-transformer` and `antlr4-runtime` inside its own jar. Loom's remapping of a classpath mod
dependency drops the `jars` entry from `fabric.mod.json`, so those libraries are never loaded and
the client dies on `NoClassDefFoundError: org/anarres/cpp/LexerException` the moment Iris reads a
shader pack. Dropped into the mods folder, the jar stays intact and Fabric Loader unpacks the
nested libraries — and remaps the mod for the dev environment — by itself.

The shader pack is a zip rather than a jar, which is why it uses its own `shaderpacks`
configuration and the `@zip` artifact notation instead of sitting on the classpath. Note that
this project is on Gradle 9, where `configurations { name }` no longer creates a configuration
from a bare name — it has to be `configurations.create('name')`, and dependencies are added
with `add('name', ...)` rather than a dynamic method.

Shaders are worth having on for any work on block models: they change how ambient occlusion and
the lit rim of the popcorn bucket read, which flat vanilla lighting hides.

## Adding an item

Items are declared as constants in `PopcornItems`, which registers each one through its
`register(name, factory, properties)` helper (`ResourceKey` → `Item.Properties.setId` →
`Registry.register`). The class is loaded — and its creative-tab hooks installed — by
`PopcornItems.initialize()`, called from `PopcornMod.onInitialize()`.

Each item also needs, all under `src/main/resources`:

| File | Purpose |
| --- | --- |
| `assets/argo_popcorn/items/<name>.json` | Client item definition: which model to use |
| `assets/argo_popcorn/models/item/<name>.json` | The model itself (`minecraft:item/generated` + a texture layer) |
| `assets/argo_popcorn/textures/item/<name>.png` | 16×16 texture |
| `assets/argo_popcorn/lang/en_us.json` | `item.argo_popcorn.<name>` display name |

Recipes go in `data/popcorn/recipe/` and their recipe-book unlocks in
`data/popcorn/advancement/recipes/`. Note both directory names are singular — that changed in 1.21.

Before adding a shaped recipe, check it against vanilla's: two shaped recipes collide when their
trimmed patterns have the same dimensions and each cell accepts the same item (mirrored patterns
count too). Vanilla's own recipe JSON is readable straight out of the Minecraft jar in
`.gradle/loom-cache/`, so the check can be scripted over all ~1470 of them rather than guessed at.
The bucket patterns were checked that way; the near neighbours are `map` (3×3 paper around a
compass) and the stained-glass family (3×3 of something around a dye).

## Adding a sound

Sound events are registered in `PopcornSounds` (`SoundEvent.createVariableRangeEvent` into
`BuiltInRegistries.SOUND_EVENT`), and `PopcornSounds.initialize()` is called from
`PopcornMod.onInitialize()` so registration happens while the registries are still open — a sound
first touched at runtime would come too late.

Each event also needs, under `src/main/resources`:

| File | Purpose |
| --- | --- |
| `assets/argo_popcorn/sounds.json` | Maps the event name to its ogg files, category and subtitle key |
| `assets/argo_popcorn/sounds/<name>.ogg` | Mono Ogg Vorbis. Mono matters: stereo files are not positional |
| `assets/argo_popcorn/lang/en_us.json` | `subtitles.argo_popcorn.<name>` caption |

Play one server-side with `level.playSound(null, pos, event, SoundSource.BLOCKS, volume, pitch)` —
a null player means everyone nearby hears it.

## Adding a block

Blocks are registered in `PopcornBlocks`: build `BlockBehaviour.Properties`, call `.setId(blockKey)`,
construct the block, register a `BlockItem` under the *same* name, then register the block itself.
`useBlockDescriptionPrefix()` on the item makes both share the `block.argo_popcorn.<name>` translation key.

`PopcornBucketBlock` is the worked example — a `buckets` `IntegerProperty` (1–4), `canBeReplaced` +
`getStateForPlacement` to stack another bucket on the pile, `canSurvive` + `updateShape` to require
a solid block underneath, and `useWithoutItem` to hand one back.

Each block needs, under `src/main/resources`:

| File | Purpose |
| --- | --- |
| `assets/argo_popcorn/blockstates/<name>.json` | Maps each state to a model |
| `assets/argo_popcorn/models/block/<name>_<n>.json` | The models themselves |
| `assets/argo_popcorn/models/item/<name>.json` + `assets/argo_popcorn/items/<name>.json` | The inventory icon |
| `assets/argo_popcorn/textures/block/*.png` | Block textures |
| `data/popcorn/loot_table/blocks/<name>.json` | What it drops — **`loot_table`, singular** |
| `data/popcorn/recipe/<name>.json` | How it is crafted |

The bucket models share four `template_popcorn_bucket_<n>.json` parents that place 1–4 boxes and
reference `#side`, `#top` and `#bottom`; each colour only overrides those three textures. The box
positions in those templates must stay in step with `PopcornBucketBlock.LAYOUTS`, which builds the
collision shapes from the same numbers.

Regenerating all of that by hand is tedious — the JSON was written by a throwaway Python script,
which is the sane way to add a fourth colour.

## Adding a mixin

1. Create the class in `…/popcorn/mixin/` (common) or `…/popcorn/client/mixin/` (client only).
2. Add its simple name to the `mixins` array of `src/main/resources/popcorn.mixins.json`,
   or to the `client` array of `src/client/resources/popcorn.client.mixins.json`.

## Bumping versions

Everything lives in `gradle.properties`. After changing `minecraft_version`, also update the
`minecraft` range in the `depends` block of `fabric.mod.json`, then re-run `./gradlew genSources`.
