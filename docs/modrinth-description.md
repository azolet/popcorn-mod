<!--
  The body of the Modrinth project page. Keep it in sync with README.md when the
  content changes, but not identical: this one is a shop window, the README is
  documentation.

  The SCREENSHOT markers are placeholders. Upload the image to the Modrinth
  gallery first, then replace the marker with the ![]() line underneath it.
-->

# Popcorn

You have eleven stacks of wheat seeds and nothing to do with them. Put them in a furnace.

<!-- SCREENSHOT: a lit furnace mid-pop, wheat seeds in the input slot, popcorn in the output -->

Popcorn turns leftover seeds into a snack worth eating, and the furnace pops out loud
while it works — you can hear a batch cooking from across the room.

## Popping

| Cooked in | Input | Time |
| --- | --- | --- |
| Furnace | Wheat Seeds | 10 seconds |
| Smoker | Wheat Seeds | 5 seconds |

The recipes unlock in the recipe book the moment you pick up your first seeds. While a lit
furnace or smoker has seeds in it, it pops roughly every two seconds, at a random pitch.

## Caramel

<!-- SCREENSHOT: plain and caramel buckets side by side, so the colour difference reads -->

Sweeten it and it goes from 2 hunger to 3:

| Recipe | Result |
| --- | --- |
| Popcorn + Sugar | 1 × Caramel Popcorn |
| 3 × Popcorn + Honey Bottle | 3 × Caramel Popcorn |

Both are shapeless and fit the 2×2 inventory grid. The honey recipe hands the empty bottle back.

## Buckets

<!-- SCREENSHOT: four buckets of different colours standing on a table, cinema-style -->

Fold a bucket out of paper and a dye, in red, creeper green or enderman black, then fill it
with eight popcorn — plain or caramel.

```
Empty bucket            Filled bucket

  s d s                   p p p
  s s s                   p b p
                          p p p

s = paper               p = popcorn (plain or caramel)
d = red / green /       b = empty popcorn bucket
    black dye
```

A full bucket restores 8 hunger (10 for caramel) and can be eaten on a full hunger bar —
it is a cinema snack, not a meal. Right-click the air to eat it, bucket and all, or
right-click a block to stand it on top. Up to four fit on one block, facing you as you place
them, and right-clicking a pile with an empty hand takes one back the way a cake loses a slice.

<!-- SCREENSHOT: the crafting grid for a filled bucket -->

## Requirements

- Minecraft 1.21.11
- Fabric Loader 0.19.5 or newer
- [Fabric API](https://modrinth.com/mod/fabric-api) 0.141.6 or newer
- Needed on both the client and the server

## Notes

The mod id is `argo_popcorn` — plain `popcorn` was already taken several times over, and two
mods sharing an id cannot be loaded together.

Source and issues on [GitHub](https://github.com/azolet/popcorn-mod). MIT licensed.
