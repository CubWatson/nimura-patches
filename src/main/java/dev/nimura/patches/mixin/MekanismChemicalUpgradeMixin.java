package dev.nimura.patches.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mekanism.api.Upgrade;
import mekanism.common.tile.interfaces.IUpgradeTile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Mekanism 10.7.19 + Mekanism Unleashed 0.3.2: chemical machines (Purification Chamber, Chemical Injection Chamber,
 * Osmium Compressor, Chemical Dissolution Chamber and their factories) use far too little chemical.
 *
 * Mekanism scales chemical use with {@code MekanismUtils.fractionUpgrades}, which is {@code installed / Upgrade.getMax()}.
 * Unleashed raises {@code getMax()} for speed and energy upgrades from 8 to 32 and rewrites Mekanism's speed, energy
 * and capacity formulas to keep using {@code installed / 8}, but it doesn't rewrite the two chemical formulas
 * ({@code getGasPerTickMeanMultiplier} and {@code getBaseUsage}). Those now see a quarter of the real upgrade count:
 * with 8 speed upgrades a Purification Chamber used ~57 oxygen per operation instead of stock Mekanism's ~2000 (measured in game).
 *
 * Fix: inside those two methods only, the fraction is measured against Mekanism's normal maximum of 8 again, capped at 8.
 * 0-8 upgrades behave exactly like stock Mekanism. Above 8 the per-tick chemical rate stays at the stock maximum, so
 * very fast machines don't outrun their small chemical tanks. Without Unleashed (getMax() is 8) this changes nothing.
 */
@Mixin(targets = "mekanism.common.util.MekanismUtils")
public abstract class MekanismChemicalUpgradeMixin {
    @Unique
    private static final double nimurapatches$STOCK_MAX = 8.0;

    @WrapOperation(
            method = {"getGasPerTickMeanMultiplier", "getBaseUsage"},
            at = @At(value = "INVOKE",
                    target = "Lmekanism/common/util/MekanismUtils;fractionUpgrades(Lmekanism/common/tile/interfaces/IUpgradeTile;Lmekanism/api/Upgrade;)D"),
            require = 1)
    private static double nimurapatches$stockChemicalScale(IUpgradeTile tile, Upgrade upgrade, Operation<Double> original) {
        double fraction = original.call(tile, upgrade);
        int max = upgrade.getMax();
        if (max <= nimurapatches$STOCK_MAX) {
            return fraction;
        }
        return Math.min(fraction * max, nimurapatches$STOCK_MAX) / nimurapatches$STOCK_MAX;
    }
}
