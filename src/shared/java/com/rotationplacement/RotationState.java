package com.rotationplacement;

import java.util.UUID;

/**
 * The rotation the player has dialled in. Contains no Minecraft types, so it is shared by every Minecraft version.
 *
 * Written only on the client main thread (keybinds, tick). Read on the client main thread (preview) and on the
 * integrated-server thread (the real block placement in singleplayer), hence the volatile fields.
 */
public final class RotationState {
    private RotationState() {}

    private static volatile boolean enabled = false;
    private static volatile int rotationX = 0; // 0, 90, 180, 270: flip up/down/opposite
    private static volatile int rotationY = 0; // 0, 90, 180, 270: spin around the vertical axis
    private static volatile UUID placer = null; // the local player: only their placements are rotated

    public static boolean isEnabled() { return enabled; }

    public static int rotationX() { return rotationX; }

    public static int rotationY() { return rotationY; }

    public static void setEnabled(boolean value) { enabled = value; }

    public static void rotateX() { rotationX = (rotationX + 90) % 360; }

    public static void rotateY() { rotationY = (rotationY + 90) % 360; }

    public static void setPlacer(UUID id) { placer = id; }

    /**
     * Placements by anything other than the local player (dispensers, other players on a LAN world, command
     * blocks...) must stay vanilla, so the rotation is tied to the local player's id.
     */
    public static boolean appliesTo(UUID playerId) {
        return enabled && playerId != null && playerId.equals(placer);
    }
}
