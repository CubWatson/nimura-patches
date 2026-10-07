package dev.nimura.patches.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.nimura.patches.RoadWeaverOverlay;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.progress.StoringChunkProgressListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * RoadWeaver 2.3.1: its "Initial Generation" panel is drawn on top of the vanilla world-loading screen
 * (an @Inject at the TAIL of LevelLoadingScreen.render). The panel is see-through, so vanilla's chunk map and
 * "N%" text show through the middle of it and the screen looks cluttered.
 *
 * Fix: while RoadWeaver's panel is showing, skip vanilla's chunk map and percentage. Once RoadWeaver
 * finishes (or isn't installed) the vanilla screen is drawn exactly as before.
 * Client-only; targets vanilla, so it does nothing unless RoadWeaver reports its panel is active.
 */
@Mixin(LevelLoadingScreen.class)
public abstract class RoadWeaverLoadingScreenMixin {
    @WrapOperation(
            method = "render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/LevelLoadingScreen;renderChunks(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/server/level/progress/StoringChunkProgressListener;IIII)V"),
            require = 1)
    private void nimurapatches$hideChunkMap(GuiGraphics graphics, StoringChunkProgressListener listener, int x, int y, int size, int spacing, Operation<Void> original) {
        if (!RoadWeaverOverlay.isShowing()) original.call(graphics, listener, x, y, size, spacing);
    }

    @WrapOperation(
            method = "render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawCenteredString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)V"),
            require = 1)
    private void nimurapatches$hidePercent(GuiGraphics graphics, Font font, Component text, int x, int y, int color, Operation<Void> original) {
        if (!RoadWeaverOverlay.isShowing()) original.call(graphics, font, text, x, y, color);
    }
}
