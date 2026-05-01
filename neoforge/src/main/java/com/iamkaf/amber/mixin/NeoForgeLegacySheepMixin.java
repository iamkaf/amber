package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.api.event.v1.events.common.EntityEvent;
//? if <1.21.2 {
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//?}
import org.spongepowered.asm.mixin.Mixin;

@Mixin(targets = "net.minecraft.world.entity.animal.Sheep")
public abstract class NeoForgeLegacySheepMixin {
    //? if <1.21.2 {
    @Inject(
            method = "mobInteract",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/animal/Sheep;shear(Lnet/minecraft/sounds/SoundSource;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void amber$fireLegacyShear(Player player, InteractionHand hand,
            CallbackInfoReturnable<InteractionResult> cir) {
        net.minecraft.world.entity.animal.Sheep sheep = (net.minecraft.world.entity.animal.Sheep) (Object) this;
        ItemStack shears = player.getItemInHand(hand);
        if (!(sheep.level() instanceof ServerLevel level)) {
            return;
        }

        EntityEvent.SHEAR.invoker().shear(
                new EntityEvent.SimpleShearingContext(
                        player instanceof ServerPlayer serverPlayer ? serverPlayer : null,
                        shears,
                        sheep,
                        level,
                        EntityEvent.ShearTarget.SHEEP,
                        List.of(new ItemStack(amber$woolItem(sheep.getColor()))),
                        true,
                        EntityEvent.ShearSource.PLAYER
                )
        );
    }

    private static net.minecraft.world.item.Item amber$woolItem(DyeColor color) {
        return switch (color) {
            case WHITE -> Items.WHITE_WOOL;
            case ORANGE -> Items.ORANGE_WOOL;
            case MAGENTA -> Items.MAGENTA_WOOL;
            case LIGHT_BLUE -> Items.LIGHT_BLUE_WOOL;
            case YELLOW -> Items.YELLOW_WOOL;
            case LIME -> Items.LIME_WOOL;
            case PINK -> Items.PINK_WOOL;
            case GRAY -> Items.GRAY_WOOL;
            case LIGHT_GRAY -> Items.LIGHT_GRAY_WOOL;
            case CYAN -> Items.CYAN_WOOL;
            case PURPLE -> Items.PURPLE_WOOL;
            case BLUE -> Items.BLUE_WOOL;
            case BROWN -> Items.BROWN_WOOL;
            case GREEN -> Items.GREEN_WOOL;
            case RED -> Items.RED_WOOL;
            case BLACK -> Items.BLACK_WOOL;
        };
    }
    //?}

    static {
        AmberMod.AMBER_MIXINS.add("NeoForgeLegacySheepMixin");
    }
}
