# Nimura Patches

A small NeoForge mod with targeted bug fixes for mods in the **Nimura** modpack (Minecraft 1.21.1, NeoForge 21.1).

Each fix is a [Mixin](https://github.com/SpongePowered/Mixin) that touches as little of the original code as possible.
The mixin config is `required: false`. If a patched mod updates and its code changes, that patch switches itself off
and the game logs a warning. It never crashes the game. Remove a patch once the mod's author fixes the bug upstream.

## What it fixes

| Mod (version) | Bug | Fix |
|---|---|---|
| Oreganized 5.3.0 | `LeadDoorBlock.getInducedGoopyness` reads a door property from a block that may not be a door → server crash | Falls back to Oreganized's normal melting logic |
| Deep Aether 1.1.5.1 | The player-logout handler crashes the whole server if a login failed part-way | Exception is caught and logged as a warning |
| Ribbits 4.1.6 | A ribbit with no home position crashes the server on its first AI tick | A ribbit with no home uses its current position |
| Create 6.0.10 | A potato cannon projectile with no projectile type crashes the server every tick | The projectile removes itself |
| Croaks 2.0.0 | A raid wave keeps spawning until enough croaks exist, with no attempt limit. If the spawn spot never works, the server hangs forever | Wave spawning stops after 64 attempts and the raid carries on with whatever spawned |

See [CHANGELOG.md](CHANGELOG.md) for details per version.

## Install

Drop `nimurapatches-<version>.jar` into the instance's `mods/` folder. It must be installed on the **server**; clients
can have it too. It depends only on NeoForge, so you don't need any of the patched mods. A patch for a mod that isn't
installed just does nothing.

## Build

[![Build](https://github.com/CubWatson/nimura-patches/actions/workflows/build.yml/badge.svg)](https://github.com/CubWatson/nimura-patches/actions/workflows/build.yml)

GitHub Actions builds every push. The jar is attached to each run under **Artifacts**.

Locally (JDK 21; the first run downloads Minecraft/NeoForge):

```sh
./gradlew build     # → build/libs/nimurapatches-<version>.jar
```

The version comes from `src/main/resources/META-INF/neoforge.mods.toml`.

## Layout

```
src/main/java/dev/nimura/patches/          mod entry point
src/main/java/dev/nimura/patches/mixin/    one mixin per fix (each file explains the bug)
src/main/resources/                        neoforge.mods.toml, mixin config
src/stubs/java/                             compile-only placeholders for patched mods' classes (not shipped)
```

## License

MIT
