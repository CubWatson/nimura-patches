# Changelog

## 1.2.0
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
