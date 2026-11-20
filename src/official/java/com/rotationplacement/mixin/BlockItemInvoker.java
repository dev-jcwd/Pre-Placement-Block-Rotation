package com.rotationplacement.mixin;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Lets the preview ask the game for the exact state a placement would produce (rotation included). */
@Mixin(BlockItem.class)
public interface BlockItemInvoker {
    @Invoker("getPlacementState")
    BlockState rotationplacement$getPlacementState(BlockPlaceContext context);
}
