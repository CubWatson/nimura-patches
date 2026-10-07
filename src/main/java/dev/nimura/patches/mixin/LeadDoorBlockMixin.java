package dev.nimura.patches.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Oreganized 5.3.0: LeadDoorBlock#getInducedGoopyness(level, otherState, otherPos, state, pos) reads the
 * door HALF property from {@code state} without checking that it is a door. Oreganized also calls it with
 * other meltable blocks (e.g. a lead block next to a lead door), and then the server crashes with
 * "Cannot get property ... as it does not exist".
 *
 * Fix: when the state has no HALF property, return null. The original code then compares null against
 * UPPER and LOWER, neither matches, and it falls through to Oreganized's normal (IMeltableBlock) behaviour.
 */
@Mixin(targets = "galena.oreganized.plumbum.world.block.LeadDoorBlock")
public abstract class LeadDoorBlockMixin {
    @WrapOperation(
        method = "getInducedGoopyness",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;"),
        require = 1
    )
    private Comparable<?> nimurapatches$safeHalf(BlockState state, Property<?> property, Operation<Comparable<?>> original) {
        if (!state.hasProperty(property)) {
            return null;
        }
        return original.call(state, property);
    }
}
