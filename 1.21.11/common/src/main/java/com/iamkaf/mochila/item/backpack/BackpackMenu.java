package com.iamkaf.mochila.item.backpack;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BackpackMenu extends ChestMenu {
    public BackpackMenu(MenuType<?> type, int containerId, Inventory playerInventory, Container container,
            int rows) {
        super(type, containerId, playerInventory, container, rows);
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (slotId > -1) {
            Slot slot = this.slots.get(slotId);

            if (clickType.equals(ClickType.SWAP) && BackpackUtils.isBlacklistedItem(player.getOffhandItem()
                    .getItem())) {
                return;
            }

            if (slot.hasItem() && slotContainsBlacklistedItem(slotId)) {
                return;
            }
        }

        super.clicked(slotId, button, clickType, player);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (slot.hasItem() && slotContainsBlacklistedItem(index)) {
            return ItemStack.EMPTY;
        }
        return super.quickMoveStack(player, index);
    }

    private boolean slotContainsBlacklistedItem(int slotId) {
        Item theItem = getSlot(slotId).getItem().getItem();
        return BackpackUtils.isBlacklistedItem(theItem);
    }
}
