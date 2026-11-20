package com.rotationplacement;

import com.rotationplacement.mixin.BlockItemInvoker;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

/** Works out, once per tick, which block would be placed where. The renderer only draws the result. */
public final class GhostPreview {
    private GhostPreview() {}

    public static BlockState state = null;
    public static BlockPos pos = null;

    public static void update(Minecraft client) {
        state = null;
        pos = null;
        if (!RotationState.isEnabled() || client.player == null || client.level == null) return;

        ItemStack stack = client.player.getMainHandItem();
        if (!(stack.getItem() instanceof BlockItem blockItem)) return;
        if (!(client.hitResult instanceof BlockHitResult hit) || hit.getType() != HitResult.Type.BLOCK) return;

        BlockPlaceContext context = new BlockPlaceContext(client.player, InteractionHand.MAIN_HAND, stack, hit);
        // Same call the real placement makes, so the preview matches it exactly. Null means "cannot be placed here".
        BlockState placed = ((BlockItemInvoker) blockItem).rotationplacement$getPlacementState(context);
        if (placed == null) return;

        state = placed;
        pos = context.getClickedPos();
    }
}
