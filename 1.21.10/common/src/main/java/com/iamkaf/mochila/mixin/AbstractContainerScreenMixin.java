package com.iamkaf.mochila.mixin;

import com.iamkaf.mochila.item.backpack.BackpackUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin<T extends AbstractContainerMenu> extends Screen implements MenuAccess<T> {
    protected AbstractContainerScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "slotClicked", at = @At("HEAD"), cancellable = true)
    protected void mochila$slotClicked(Slot slot, int slotId, int mouseButton, ClickType type,
            CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;
        assert player != null;
        // TODO: this won't work until I have a real registered menu
//        if (player.hasContainerOpen() && player.containerMenu instanceof BackpackMenu) {
        if (player.hasContainerOpen()) {
            boolean illegalBackpackMovementDetected = mochila$checkForBlacklist(slot, type);
            if (illegalBackpackMovementDetected) {
                ci.cancel();
            }
        }
    }

    @Unique
    private boolean mochila$checkForBlacklist(Slot slot, ClickType type) {
        if (type.equals(ClickType.SWAP)) {
            assert Minecraft.getInstance().player != null;
            if (BackpackUtils.isBlacklistedItem(Minecraft.getInstance().player.getOffhandItem().getItem())) {
                return true;
            }
        }
//        return BackpackUtils.isBlacklistedItem(slot.getItem().getItem());
        return false;
    }
}
