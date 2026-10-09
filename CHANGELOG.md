# Changelog

## 1.8.0
- Moonlight 1.21.1-3.7.1: fixed an intermittent crash at game start ("Supplementaries Squared has failed to load
  correctly", `ConcurrentModificationException`) (`MoonlightProviderRaceMixin`). NeoForge builds mods in parallel, and
  Supplementaries and Supplementaries Squared both register Moonlight dynamic-resource providers at the same moment;
  Moonlight's provider list had no locking, so one mod could change it while the other was reading it. Registration now
  runs under a lock. Found by the Nimura test loop (1 crash in about 9 launches); nothing changes when the race doesn't
  happen.

## 1.7.0
- Removed the Oreganized lead-door patch (`LeadDoorBlockMixin`). Oreganized was taken out of Nimura, so the patch has
  nothing left to fix. All other fixes are unchanged. Recover it from tag `v1.6.1` if Oreganized ever comes back.

## 1.6.1
- Immersive Engineering: the 1.6.0 flare fix only covered the client. On the server a flare created without its data has
  no bullet data at all, so `getColour()` (server tick → `spawnParticles`) still crashed the server. `getColour()` now
  returns white for missing data too. Found by the Nimura entity harness.

## 1.6.0
- Immersive Engineering 12.4.2: a revolver flare entity created without its flare bullet data no longer crashes the
  game of every player who can see it (`IERevolverFlareMixin`). Its client tick read the colour with
  `BulletData.getFor`, which throws when the data isn't a flare's; it now uses IE's own `getForOptional` and falls back
  to white. Only code that creates the entity directly can produce such a flare (another mod, a script, or the Nimura
  test harness, which found it). `/summon`, command blocks and spawners can't: IE refuses to load a flare without its
  data. Flares fired from a revolver always have their colour and look exactly the same as before.

## 1.5.0
- Mekanism 10.7.19 + Mekanism Unleashed 0.3.2: chemical machines no longer use far too little chemical when they have
  speed upgrades (`MekanismChemicalUpgradeMixin`). Unleashed raises the upgrade cap from 8 to 32 and rewrites Mekanism's
  speed, energy and capacity formulas, but not the two chemical ones (`getGasPerTickMeanMultiplier`, `getBaseUsage`),
  which still divided by the new cap of 32. With 8 speed upgrades a Purification Chamber used ~57 oxygen per operation
  instead of stock Mekanism's ~2000. Now 0-8 upgrades cost exactly what stock Mekanism charges; above 8 the per-tick
  chemical rate stays at the stock maximum. Without Mekanism Unleashed the patch changes nothing.

## 1.4.0
- Removed the RoadWeaver loading-screen patch (`RoadWeaverLoadingScreenMixin` and its `RoadWeaverOverlay` helper).
  RoadWeaver was taken out of Nimura, so the patch has nothing left to fix. All other fixes are unchanged.

## 1.3.0
- RoadWeaver 2.3.1: the world-loading screen no longer looks cluttered (`RoadWeaverLoadingScreenMixin`, client only).
  RoadWeaver draws its see-through "Initial Generation" panel on top of the vanilla screen, so the chunk map and
  "N%" text showed through the middle of it. While RoadWeaver's panel is showing, those two vanilla elements are
  skipped; once RoadWeaver finishes, or if it isn't installed, the vanilla screen is unchanged.

## 1.2.0
First release published on GitHub. It also includes every fix from 1.1.0 and 1.0.0 (Oreganized, Deep Aether, Ribbits, Create).

- Croaks 2.0.0: a raid wave whose spawn spot never works no longer hangs the server forever (`CroaksRaidMixin`).
  `RaidOnEntityTickUpdateProcedure` spawned croak groups in a `while` loop with no attempt limit; it now stops after
  64 attempts and stores the wave's MaxHealth the same way the original does after the loop.
  Verified in-game: a raid summoned over the End void stopped after 64 attempts instead of freezing; a normal raid
  (raid core on overworld terrain) still spawned its wave (11 croaks) with the patch never triggering.

## 1.1.0
- Ribbits 4.1.6: a ribbit without a home position no longer crashes the server (`RibbitEntityMixin`).
- Create 6.0.10: a potato cannon projectile with a missing projectile type removes itself instead of crashing the server every tick (`PotatoProjectileEntityMixin`).

## 1.0.0
- Oreganized 5.3.0: lead door no longer crashes the server when the block it checks isn't a door (`LeadDoorBlockMixin`).
- Deep Aether 1.1.5.1: a failed player login no longer crashes the server in the logout handler (`DAGeneralEventsMixin`).
