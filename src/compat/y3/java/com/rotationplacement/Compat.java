package com.rotationplacement;

import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/** Minecraft 1.21.9 - 1.21.11: key categories are objects, blocks are submitted to a render command queue. */
public final class Compat {
    private Compat() {}

    private static final KeyBinding.Category CATEGORY = KeyBinding.Category.create(Identifier.of("rotationplacement", "main"));

    public static KeyBinding createKeyBinding(String translationKey, int keyCode) {
        return new KeyBinding(translationKey, InputUtil.Type.KEYSYM, keyCode, CATEGORY);
    }

    public static void registerRenderer() {
        WorldRenderEvents.AFTER_ENTITIES.register(Compat::renderGhost);
    }

    private static void renderGhost(WorldRenderContext context) {
        BlockState state = GhostPreview.state;
        BlockPos pos = GhostPreview.pos;
        if (state == null || pos == null) return;

        Vec3d camera = MinecraftClient.getInstance().gameRenderer.getCamera().getCameraPos();

        MatrixStack matrices = context.matrices();
        matrices.push();
        matrices.translate(pos.getX() - camera.x, pos.getY() - camera.y, pos.getZ() - camera.z);
        context.commandQueue().submitBlock(matrices, state, 15728880, OverlayTexture.DEFAULT_UV, 0);
        matrices.pop();
    }
}
