package com.iamkaf.amber.api.common;

//? if >1.19.4 {
import net.minecraft.world.entity.ai.attributes.Attributes;
//?}
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/**
 * @deprecated Use {@link com.iamkaf.amber.api.functions.v1.WorldFunctions} for world utilities or other appropriate functions class.
 * This class will be removed in Amber 10.0
 */
@Deprecated
public class CommonUtils {
    /**
     * Performs a raytrace to determine the block the player is looking at.
     *
     * @param level  The level in which the player is located.
     * @param player The player performing the raytrace.
     * @return The result of the raytrace.
     */
    public static @NotNull BlockHitResult raytrace(Level level, Player player) {
        //? if <=1.16.5 {
        /*Vec3 eyePosition = player.getEyePosition(1.0f);
        Vec3 rotation = player.getViewVector(1.0f);*/
        //?} else {
        Vec3 eyePosition = player.getEyePosition();
        Vec3 rotation = player.getViewVector(1);
        //?}
        //? if <=1.19.4 {
        /*double reach = player.isCreative() ? 5.0D : 4.5D;*/
        //?} else {
        double reach = player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE);
        //?}
        Vec3 combined = eyePosition.add(rotation.x * reach, rotation.y * reach, rotation.z * reach);
        return level.clip(new ClipContext(eyePosition, combined, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
    }
}
