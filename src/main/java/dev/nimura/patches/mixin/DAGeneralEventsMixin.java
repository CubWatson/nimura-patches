package dev.nimura.patches.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.nimura.patches.NimuraPatches;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Deep Aether 1.1.5.1: DAGeneralEvents#playerLoggedOutEvent looks up the Floaty Scarf through
 * Accessories, which may try to send a packet. If a login failed part-way (connection not fully set
 * up), that packet is rejected and the exception takes down the whole server.
 *
 * Fix: run the handler inside a try/catch. On failure we log a warning instead of crashing; the only
 * thing skipped is removing the scarf's Gentle Wind entity for that one failed login.
 */
@Mixin(targets = "io.github.razordevs.deep_aether.event.DAGeneralEvents")
public abstract class DAGeneralEventsMixin {
    @WrapMethod(method = "playerLoggedOutEvent", require = 1)
    private static void nimurapatches$guardLogout(PlayerEvent.PlayerLoggedOutEvent event, Operation<Void> original) {
        try {
            original.call(event);
        } catch (RuntimeException e) {
            NimuraPatches.LOGGER.warn("Deep Aether logout handler failed for a player whose login did not complete; skipped to avoid a server crash: {}", e.toString());
        }
    }
}
