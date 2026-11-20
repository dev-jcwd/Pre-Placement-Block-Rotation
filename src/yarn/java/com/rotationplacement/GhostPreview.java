package com.rotationplacement;

import com.rotationplacement.mixin.BlockItemInvoker;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

/** Works out, once per tick, which block would be placed where. The renderer only draws the result. */
public final class GhostPreview {
    private GhostPreview() {}

    public static BlockState state = null;
    public static BlockPos pos = null;

    public static void update(MinecraftClient client) {
        state = null;
        pos = null;
        if (!RotationState.isEnabled() || client.player == null || client.world == null) return;

        ItemStack stack = client.player.getMainHandStack();
        if (!(stack.getItem() instanceof BlockItem blockItem)) return;
        if (!(client.crosshairTarget instanceof BlockHitResult hit) || hit.getType() != HitResult.Type.BLOCK) return;

        ItemPlacementContext context = new ItemPlacementContext(client.player, Hand.MAIN_HAND, stack, hit);
        // Same call the real placement makes, so the preview matches it exactly. Null means "cannot be placed here".
        BlockState placed = ((BlockItemInvoker) blockItem).rotationplacement$getPlacementState(context);
        if (placed == null) return;

        state = placed;
        pos = context.getBlockPos();
    }
}
