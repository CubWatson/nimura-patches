package dev.nimura.patches.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Ribbits 4.1.6: RibbitGoHomeGoal / RibbitStrollGoal call getHomePosition().getX() without a null check.
 * The home is set in finalizeSpawn and when loading, but a ribbit created any other way (another mod
 * converting/spawning it without finalizeSpawn) has no home and crashes the server on its first AI tick.
 *
 * Fix: when no home is set, report the ribbit's current position as its home.
 */
@Mixin(targets = "com.yungnickyoung.minecraft.ribbits.entity.RibbitEntity")
public abstract class RibbitEntityMixin {
    @ModifyReturnValue(method = "getHomePosition", at = @At("RETURN"), require = 1)
    private BlockPos nimurapatches$homeFallback(BlockPos original) {
        return original != null ? original : ((Entity) (Object) this).blockPosition();
    }
}
