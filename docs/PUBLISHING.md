# Publishing

Everything the mod needs in order to be published, and what has to be done by hand.

## What ships

`./gradlew build` produces `build/libs/argo_popcorn-<version>.jar`. Upload **only** that one —
not `-sources.jar`, not `-dev.jar`. Its contents have been checked: the mod's own classes, assets
and data, the two mixin configs and the licence, and nothing from the development client (Iris,
Sodium and the shader pack are copied into `run/`, never onto the classpath).

The mixins are remapped to intermediary inside the jar (`class_2609`, `method_…`), so they apply
outside the development environment too. Worth re-checking after any Loom upgrade:

```bash
unzip -p build/libs/argo_popcorn-*.jar \
  it/argo/mc/mods/popcorn/mixin/AbstractFurnaceBlockEntityMixin.class | strings | grep method_
```

## The mod id is permanent

`argo_popcorn`. Two mods that declare the same id cannot be loaded together — Fabric refuses to
start — and "popcorn" alone was too likely to collide, with eight popcorn mods already on Modrinth.
Changing it after release breaks existing worlds: every block and item in the namespace becomes
unknown and is silently dropped from saves. Treat it as fixed.

The Modrinth slug is separate and can stay `popcorn` if it is free.

## Modrinth project settings

| Field | Value |
| --- | --- |
| Name | Popcorn |
| Summary | Cook leftover wheat seeds into popcorn, caramelise it, and serve it in paper buckets. |
| Categories | Food, Decoration, Equipment |
| Environment | Client **and** server, both required (`"environment": "*"`, and the block and recipes are server-side) |
| Loader | Fabric |
| Game version | 1.21.11 |
| Licence | MIT |
| Icon | `docs/modrinth-icon.png` (512×512) |
| Source / Issues | https://github.com/azolet/popcorn-mod |

Fabric API is a required dependency — declare it on the version, or players get a confusing crash
instead of a clear message.

## Release checklist

1. `version=` in `gradle.properties` matches what you are about to publish.
2. `CHANGELOG.md` has an entry for it.
3. `./gradlew clean build`, then launch `runClient` once and actually craft the thing.
4. Tag it: `git tag -a v<version> -m "<version>" && git push --tags`.
5. Upload the jar, paste the changelog entry as the version's changelog.

## Gallery

Screenshots have to be taken in game — the isometric renders in this repo are for checking models,
not for a shop window. Worth having, with BSL on:

- a furnace mid-pop, seeds in the slot
- four buckets of different colours on a table
- the caramel and plain buckets side by side, so the difference in colour reads
- the crafting grids for the bucket and for filling it
