package dev.nimura.patches;

import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Nimura Patches - small, targeted bug fixes for mods in the Nimura modpack.
 * Every fix is a mixin that only applies when its target mod is installed.
 */
@Mod("nimurapatches")
public final class NimuraPatches {
    public static final Logger LOGGER = LoggerFactory.getLogger("Nimura Patches");

    public NimuraPatches() {
        LOGGER.info("Nimura Patches loaded");
    }
}
