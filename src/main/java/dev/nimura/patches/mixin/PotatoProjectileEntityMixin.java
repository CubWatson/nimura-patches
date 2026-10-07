package dev.nimura.patches.mixin;

import com.simibubi.create.api.equipment.potatoCannon.PotatoCannonProjectileType;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Create 6.0.10: a potato cannon projectile whose projectile type is missing (summoned without one, or
 * saved in a world and loaded after the type's datapack/addon was removed) crashes the server every tick.
 *
 * Fix: such a projectile removes itself instead.
 */
@Mixin(targets = "com.simibubi.create.content.equipment.potatoCannon.PotatoProjectileEntity")
public abstract class PotatoProjectileEntityMixin {
    @Shadow protected PotatoCannonProjectileType type;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true, require = 1)
    private void nimurapatches$discardTypeless(CallbackInfo ci) {
        if (this.type == null) {
            ((Entity) (Object) this).discard();
            ci.cancel();
        }
    }
}
