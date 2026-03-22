package com.rotationplacement;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents; // <-- The 1.21.1 Fix (Removed .world.)
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class RotationPlacementClient implements ClientModInitializer {

// Notice the ROTATION_CATEGORY variable is completely deleted!

    public static KeyBinding toggleKey, rotateXKey, rotateYKey;
    public static boolean isModEnabled = false;
    public static int customRotationX = 0, customRotationY = 0;

    @Override
    public void onInitializeClient() {
        // 1. Keybinds (In 1.21.1, we just type the plain string "Rotation Placement" at the end!)
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.rotationplacement.toggle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, "Rotation Placement"));
        rotateXKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.rotationplacement.rotate_x", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UP, "Rotation Placement"));
        rotateYKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.rotationplacement.rotate_y", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT, "Rotation Placement"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Keep the ON/OFF toggle notification
            while (toggleKey.wasPressed()) {
                isModEnabled = !isModEnabled;
                if (client.player != null) {
                    client.player.sendMessage(Text.literal(isModEnabled ? "§aPlacement Rotation Active!" : "§cPlacement Rotation Disabled!"), true);
                }
            }

            // Rotate silently
            if (isModEnabled) {
                while (rotateXKey.wasPressed()) {
                    customRotationX = (customRotationX + 90) % 360;
                }
                while (rotateYKey.wasPressed()) {
                    customRotationY = (customRotationY + 90) % 360;
                }
            }
        });

        // 2. The Fabric Renderer (Perfect Sync Method)
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            if (!isModEnabled) return;

            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null) return;

            ItemStack stack = client.player.getMainHandStack();
            if (!(stack.getItem() instanceof BlockItem blockItem)) return;

            HitResult hit = client.crosshairTarget;
            if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
                BlockHitResult blockHit = (BlockHitResult) hit;
                BlockPos targetPos = blockHit.getBlockPos().offset(blockHit.getSide());

                ItemPlacementContext placementContext = new ItemPlacementContext(client.player, Hand.MAIN_HAND, stack, blockHit);

                // --- We pass the vanilla state through the brain so the preview perfectly matches! ---
                BlockState vanillaState = blockItem.getBlock().getPlacementState(placementContext);
                BlockState ghostState = applyCustomRotations(vanillaState);

                if (ghostState == null) return;

                Vec3d cameraPos = client.player.getEyePos();

                MatrixStack matrices = new MatrixStack();
                matrices.push();

                // Align with the world relative to the camera
                matrices.translate(targetPos.getX() - cameraPos.x, targetPos.getY() - cameraPos.y, targetPos.getZ() - cameraPos.z);

                // Draw it!
                VertexConsumerProvider.Immediate consumers = client.getBufferBuilders().getEntityVertexConsumers();
                client.getBlockRenderManager().renderBlockAsEntity(ghostState, matrices, consumers, 15728880, 0);

                matrices.pop();
            }
        });
    }

    // --- THE BRAIN: This must be inside the class so the Mixin can find it! ---
    public static BlockState applyCustomRotations(BlockState state) {
        if (state == null) return null;

        // 1. ALL-DIRECTIONAL BLOCKS (Observers, Pistons, Dispensers)
        if (state.contains(Properties.FACING)) {
            Direction dir = state.get(Properties.FACING);

            // Failsafe: Break out of the Up/Down lock if the user wants to spin sideways
            if (dir.getAxis() == Direction.Axis.Y && customRotationY != 0) {
                dir = Direction.NORTH;
            }

            for (int i = 0; i < customRotationY / 90; i++) dir = dir.rotateYClockwise();

            if (customRotationX == 90) dir = Direction.UP;
            else if (customRotationX == 270) dir = Direction.DOWN;
            else if (customRotationX == 180) dir = dir.getOpposite();

            state = state.with(Properties.FACING, dir);
        }
        // 2. HORIZONTAL BLOCKS (Furnaces, Chests)
        else if (state.contains(Properties.HORIZONTAL_FACING)) {
            Direction dir = state.get(Properties.HORIZONTAL_FACING);
            for (int i = 0; i < customRotationY / 90; i++) dir = dir.rotateYClockwise();
            if (customRotationX == 180) dir = dir.getOpposite();

            state = state.with(Properties.HORIZONTAL_FACING, dir);
        }
        // 3. LOGS & PILLARS
        else if (state.contains(Properties.AXIS)) {
            Direction.Axis axis = state.get(Properties.AXIS);
            if (customRotationX == 90 || customRotationX == 270) axis = (axis == Direction.Axis.Y) ? Direction.Axis.X : Direction.Axis.Y;
            if (customRotationY == 90 || customRotationY == 270) axis = (axis == Direction.Axis.X) ? Direction.Axis.Z : (axis == Direction.Axis.Z ? Direction.Axis.X : axis);

            state = state.with(Properties.AXIS, axis);
        }

        // 4. STAIRS & SLABS (Upside down flipping)
        if (state.contains(Properties.BLOCK_HALF)) {
            if (customRotationX == 90 || customRotationX == 270) {
                BlockHalf currentHalf = state.get(Properties.BLOCK_HALF);
                state = state.with(Properties.BLOCK_HALF, currentHalf == BlockHalf.TOP ? BlockHalf.BOTTOM : BlockHalf.TOP);
            }
        }

        return state;
    }
}