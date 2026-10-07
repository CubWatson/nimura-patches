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

Requires only a JDK 21:

```sh
./build.sh          # → build/nimurapatches-<version>.jar
```

There's no Gradle setup yet. The mixins name the classes they patch as strings, so the code only needs a handful of
Minecraft / NeoForge / Mixin signatures to compile. Those are in `stubs/` as compile-only copies; they are never
packaged, and the real classes are used at runtime. Each stub signature was checked against the real game and mod
bytecode before use.

## Layout

```
src/main/java/dev/nimura/patches/          mod entry point
src/main/java/dev/nimura/patches/mixin/    one mixin per fix (each file explains the bug)
src/main/resources/                        neoforge.mods.toml, mixin config
stubs/                                     compile-only API signatures (not shipped)
```

## License

MIT
