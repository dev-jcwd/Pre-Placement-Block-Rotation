package com.rotationplacement;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/** Minecraft 1.21 - 1.21.1: String key categories, blocks drawn with renderBlockAsEntity. */
public final class Compat {
    private Compat() {}

    public static KeyBinding createKeyBinding(String translationKey, int keyCode) {
        return new KeyBinding(translationKey, InputUtil.Type.KEYSYM, keyCode, "category.rotationplacement.main");
    }

    public static void registerRenderer() {
        WorldRenderEvents.AFTER_ENTITIES.register(Compat::renderGhost);
    }

    private static void renderGhost(WorldRenderContext context) {
        BlockState state = GhostPreview.state;
        BlockPos pos = GhostPreview.pos;
        if (state == null || pos == null) return;

        MinecraftClient client = MinecraftClient.getInstance();
        Vec3d camera = context.camera().getPos();

        MatrixStack matrices = new MatrixStack();
        matrices.translate(pos.getX() - camera.x, pos.getY() - camera.y, pos.getZ() - camera.z);

        VertexConsumerProvider.Immediate consumers = client.getBufferBuilders().getEntityVertexConsumers();
        client.getBlockRenderManager().renderBlockAsEntity(state, matrices, consumers, 15728880, OverlayTexture.DEFAULT_UV);
    }
}
