package firedasher.creativeshulkers.mixin;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeModeInventoryScreen.class)
public class CreativeModeInventoryScreenMixin extends AbstractContainerScreen<CreativeModeInventoryScreen.ItemPickerMenu> {
	@Shadow private static CreativeModeTab selectedTab;
	@Shadow private float scrollOffs;

	public CreativeModeInventoryScreenMixin(CreativeModeInventoryScreen.ItemPickerMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
	}

	@Inject(method = "slotClicked", at = @At("HEAD"), cancellable = true)
	private void slotClicked(Slot slot, int slotId, int buttonNum, ContainerInput containerInput, CallbackInfo ci) {
		if (slot == null) return;
		ItemStack item = slot.getItem(); // gets the item that was clicked
		if (selectedTab.getType() == CreativeModeTab.Type.HOTBAR && item.has(DataComponents.CONTAINER) && buttonNum == 1) {
			ci.cancel(); // prevents default action of grabbing a shulker box
			ItemContainerContents component = item.get(DataComponents.CONTAINER); // gets the container component
			if (component == null) return; // unreachable because has check
//			selectedTab = CreativeModeTab.builder(selectedTab.row(), selectedTab.column()).title(item.getDisplayName()).build(); // sets the title (causes crashes when you re-open inventory due to trying to restore the non-existent tab)
			menu.items.clear(); // empties hotbar thing to add new contents
			for (int i = 0; i < 27; ++i) menu.items.add(ItemStack.EMPTY); // fills it with empty items so that copyInto works
			component.copyInto(menu.items); // adds the items
			scrollOffs = 0.0F; // reset scroll
			menu.scrollTo(0.0F); // refresh screen to make sure the new items appear
		}
	}
}