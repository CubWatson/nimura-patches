package dev.nimura.patches.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.nimura.patches.CroaksRaidAbort;
import dev.nimura.patches.NimuraPatches;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Croaks 2.0.0 (Ribbits add-on): RaidOnEntityTickUpdateProcedure starts each raid wave with
 *
 * <pre>
 * while (total &lt; target) {                       // target = random "health budget" for the wave
 *     total = 0;
 *     RaidFindSmallSpotProcedure / RaidFindBigSpotProcedure.execute(...)   // tries to spawn a croak group
 *     total = max health of every croaks:croak within 200 blocks
 * }
 * </pre>
 *
 * There is no attempt limit. When the spawn-spot search keeps failing (bad or unset raid spot, odd terrain,
 * spawns blocked), nothing ever spawns, the loop never ends and the server thread hangs forever.
 *
 * Fix: count spawn attempts per tick; after 64 the loop is left by throwing {@link CroaksRaidAbort}, which the
 * method wrapper catches. The wrapper then stores the wave's "MaxHealth" the same way the original does after
 * the loop (total max health of the croaks that did spawn, at least 1 so the raid bar never divides by zero).
 * Normal raids, which need far fewer attempts, behave exactly as before.
 */
@Mixin(targets = "net.mcreator.croaks.procedures.RaidOnEntityTickUpdateProcedure")
public abstract class CroaksRaidMixin {
    @Unique
    private static int nimurapatches$spawnAttempts;

    @WrapMethod(method = "execute", require = 1)
    private static void nimurapatches$guardWave(LevelAccessor level, Entity raid, Operation<Void> original) {
        nimurapatches$spawnAttempts = 0;
        try {
            original.call(level, raid);
        } catch (CroaksRaidAbort abort) {
            CompoundTag data = raid.getPersistentData();
            Vec3 spot = new Vec3(data.getDouble("SpotX"), data.getDouble("SpotY"), data.getDouble("SpotZ"));
            TagKey<EntityType<?>> croaks = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.parse("croaks:croak"));
            double total = 0;
            for (Entity e : level.getEntitiesOfClass(Entity.class, new AABB(spot, spot).inflate(200), any -> true)) {
                if (e.getType().is(croaks) && e instanceof LivingEntity living) {
                    total += living.getMaxHealth();
                }
            }
            data.putDouble("MaxHealth", Math.max(total, 1.0));
            NimuraPatches.LOGGER.warn("Croaks raid wave stopped after 64 spawn attempts (spawn spot unusable?); continuing with {} croak health instead of hanging the server", total);
        }
    }

    @WrapOperation(
        method = "execute",
        at = @At(value = "INVOKE", target = "Lnet/mcreator/croaks/procedures/RaidFindSmallSpotProcedure;execute(Lnet/minecraft/world/level/LevelAccessor;DDD)V"),
        require = 1
    )
    private static void nimurapatches$countSmall(LevelAccessor level, double x, double y, double z, Operation<Void> original) {
        nimurapatches$countAttempt();
        original.call(level, x, y, z);
    }

    @WrapOperation(
        method = "execute",
        at = @At(value = "INVOKE", target = "Lnet/mcreator/croaks/procedures/RaidFindBigSpotProcedure;execute(Lnet/minecraft/world/level/LevelAccessor;DDD)V"),
        require = 1
    )
    private static void nimurapatches$countBig(LevelAccessor level, double x, double y, double z, Operation<Void> original) {
        nimurapatches$countAttempt();
        original.call(level, x, y, z);
    }

    @Unique
    private static void nimurapatches$countAttempt() {
        if (++nimurapatches$spawnAttempts > 64) {
            throw new CroaksRaidAbort();
        }
    }
}
