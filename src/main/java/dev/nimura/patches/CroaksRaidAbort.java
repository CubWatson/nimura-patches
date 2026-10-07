package dev.nimura.patches;

/**
 * Thrown inside the Croaks raid wave-spawning loop once it has made too many spawn attempts, so the
 * loop can be left from the outside (see CroaksRaidMixin). Never escapes the patched method.
 * Lives outside the mixin package because mixin-package classes can't be referenced directly.
 */
public final class CroaksRaidAbort extends RuntimeException {
    public CroaksRaidAbort() {
        super("Croaks raid wave spawning aborted", null, false, false);
    }
}
