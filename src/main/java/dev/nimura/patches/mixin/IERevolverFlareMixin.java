package dev.nimura.patches.mixin;

import blusunrize.immersiveengineering.api.tool.BulletHandler;
import blusunrize.immersiveengineering.api.utils.Color4;
import blusunrize.immersiveengineering.common.entities.RevolvershotEntity;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Immersive Engineering 12.4.2: a revolver flare entity without flare bullet data crashes the game of every player who
 * can see it. Only code that creates the entity directly can make one (another mod, a script; found by the Nimura test
 * harness): /summon, command blocks and spawners go through IE's NBT loader, which rejects a flare without data. Its client tick calls {@code getColour()}, which reads the
 * colour with {@code BulletData.getFor(FLARE_TYPE)}; that throws a NullPointerException when the stored bullet isn't
 * a flare. Flares fired from a revolver always carry their colour (it's saved with the entity too), so they never hit this.
 *
 * Fix: {@code getColour()} uses IE's own null-returning {@code getForOptional} and falls back to white.
 */
@Mixin(targets = "blusunrize.immersiveengineering.common.entities.RevolvershotFlareEntity")
public abstract class IERevolverFlareMixin {
    @WrapOperation(
            method = "getColour",
            at = @At(value = "INVOKE",
                    target = "Lblusunrize/immersiveengineering/common/entities/RevolvershotEntity$BulletData;getFor(Lblusunrize/immersiveengineering/api/tool/BulletHandler$IBullet;)Ljava/lang/Object;"),
            require = 1)
    private Object nimurapatches$fallbackColour(RevolvershotEntity.BulletData<?> data, BulletHandler.IBullet<?> type, Operation<Object> original) {
        Object colour = data.getForOptional(type);
        return colour != null ? colour : Color4.WHITE;
    }
}
