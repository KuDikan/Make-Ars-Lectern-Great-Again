package icu.kudikan.make_ars_lectern_great_again.mixin;

import com.hollingsworth.arsnouveau.client.container.SlotStorage;
import com.hollingsworth.arsnouveau.client.container.StorageTerminalMenu;
import com.hollingsworth.arsnouveau.common.block.tile.StorageLecternTile;
import icu.kudikan.make_ars_lectern_great_again.utils.Constants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(StorageTerminalMenu.class)
public class StorageTerminalMenuMixin {
    @Shadow(remap = false)
    protected StorageLecternTile te;
    @Shadow(remap = false)
    protected List<SlotStorage> storageSlotList;

    // Replaces the vanilla layout: the row / column count may change at runtime (window resize),
    // so slots are created on demand and repositioned on every call.
    @Inject(method = "addStorageSlots(Z)V", at = @At("HEAD"), cancellable = true, remap = false)
    public void addStorageSlots(boolean expanded, CallbackInfo ci) {
        int rows = Constants.rows();
        int columns = Constants.columns();
        int lines = Constants.expandedRows();
        int left = 13 - Constants.leftShift(); // Slot x Pos
        int top = 21 - Constants.extraRows() * Constants.ROW_HEIGHT; // Slot y Pos
        for (int i = 0; i < lines; i++) {
            for (int j = 0; j < columns; j++) {
                int index = i * columns + j;
                int x = left + j * Constants.SLOT_SIZE;
                int y = top + i * Constants.ROW_HEIGHT;
                boolean show = expanded || i < rows;
                if (index < storageSlotList.size()) {
                    SlotStorage slot = storageSlotList.get(index);
                    slot.xDisplayPosition = x;
                    slot.yDisplayPosition = y;
                    slot.show = show;
                } else {
                    storageSlotList.add(new SlotStorage(te, index, x, y, show));
                }
            }
        }
        // Slots left over from a previous, bigger layout
        for (int index = lines * columns; index < storageSlotList.size(); index++) {
            SlotStorage slot = storageSlotList.get(index);
            slot.show = false;
            slot.setStack(null);
        }
        ci.cancel();
    }
}
