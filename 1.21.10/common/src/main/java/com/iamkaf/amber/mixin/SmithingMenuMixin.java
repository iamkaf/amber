package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.api.event.v1.events.common.ItemEvents;
import com.iamkaf.amber.event.internal.CommonEventHooks;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SmithingMenu.class)
public abstract class SmithingMenuMixin {
    @Shadow
    @Final
    private Level level;

    @Unique
    private boolean amber$hasValidRecipe;

    @Unique
    private int amber$lastSmithingSignature;

    @Inject(method = "createResult", at = @At("TAIL"))
    private void amber$onCreateResult(CallbackInfo ci) {
        ItemCombinerMenuAccessor accessor = (ItemCombinerMenuAccessor) this;
        if (!(accessor.amber$getPlayer() instanceof ServerPlayer serverPlayer)) {
            return;
        }

        ItemStack result = accessor.amber$getResultSlots().getItem(0);
        RecipeHolder<?> recipeHolder = accessor.amber$getResultSlots().getRecipeUsed();
        if (recipeHolder == null || result.isEmpty()) {
            this.amber$hasValidRecipe = false;
            this.amber$lastSmithingSignature = 0;
            return;
        }

        ItemStack template = accessor.amber$getInputSlots().getItem(0).copy();
        ItemStack base = accessor.amber$getInputSlots().getItem(1).copy();
        ItemStack addition = accessor.amber$getInputSlots().getItem(2).copy();
        int signature = CommonEventHooks.getSmithingSignature(recipeHolder, template, base, addition, result);
        if (!this.amber$hasValidRecipe || this.amber$lastSmithingSignature != signature) {
            ItemEvents.SMITHING_START.invoker().start(
                    new ItemEvents.SimpleSmithingContext(
                            serverPlayer,
                            (SmithingMenu) (Object) this,
                            template,
                            base,
                            addition,
                            this.level
                    )
            );
            this.amber$hasValidRecipe = true;
            this.amber$lastSmithingSignature = signature;
        }
    }

    @Inject(method = "onTake", at = @At("HEAD"))
    private void amber$onSmithingTake(Player player, ItemStack result, CallbackInfo ci) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        ItemCombinerMenuAccessor accessor = (ItemCombinerMenuAccessor) this;
        RecipeHolder<?> recipeHolder = accessor.amber$getResultSlots().getRecipeUsed();
        Recipe<?> recipe = recipeHolder != null ? recipeHolder.value() : null;
        ItemStack template = accessor.amber$getInputSlots().getItem(0).copy();
        ItemStack base = accessor.amber$getInputSlots().getItem(1).copy();
        ItemStack addition = accessor.amber$getInputSlots().getItem(2).copy();

        ItemEvents.SMITHING_COMPLETE.invoker().complete(
                new ItemEvents.SimpleSmithingCompleteContext(
                        serverPlayer,
                        (SmithingMenu) (Object) this,
                        template,
                        base,
                        addition,
                        this.level,
                        result.copy(),
                        CommonEventHooks.getConsumedSmithingItems(template, base, addition),
                        !result.isEmpty(),
                        CommonEventHooks.classifySmithingType(recipe, template),
                        recipe
                )
        );

        this.amber$hasValidRecipe = false;
        this.amber$lastSmithingSignature = 0;
    }

    static {
        AmberMod.AMBER_MIXINS.add("SmithingMenuMixin");
    }
}
