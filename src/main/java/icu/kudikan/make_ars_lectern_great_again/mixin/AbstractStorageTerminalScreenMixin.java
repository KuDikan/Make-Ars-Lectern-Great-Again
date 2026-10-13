package icu.kudikan.make_ars_lectern_great_again.mixin;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.container.AbstractStorageTerminalScreen;
import com.hollingsworth.arsnouveau.client.container.StorageTerminalMenu;
import com.hollingsworth.arsnouveau.client.container.StoredItemStack;
import com.hollingsworth.arsnouveau.client.gui.NoShadowTextField;
import com.hollingsworth.arsnouveau.client.gui.buttons.StateButton;
import com.hollingsworth.arsnouveau.client.gui.buttons.StorageTabButton;
import icu.kudikan.make_ars_lectern_great_again.Config;
import icu.kudikan.make_ars_lectern_great_again.utils.ClientLayout;
import icu.kudikan.make_ars_lectern_great_again.utils.Constants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(AbstractStorageTerminalScreen.class)
public abstract class AbstractStorageTerminalScreenMixin<T extends StorageTerminalMenu> extends AbstractContainerScreen<T> {
    @Shadow(remap = false)
    protected NoShadowTextField searchField;
    @Shadow
    protected boolean expanded;
    @Shadow(remap = false)
    protected int rowCount;
    @Shadow(remap = false)
    List<StoredItemStack> itemsSorted;
    @Shadow(remap = false)
    public List<StorageTabButton> tabButtons;
    @Shadow(remap = false)
    protected StateButton buttonSortingType;
    @Shadow(remap = false)
    protected StateButton buttonDirection;
    @Shadow(remap = false)
    protected StateButton buttonSearchType;

    // Always the collapsed texture: the expanded one has no plain page below the storage rows
    @Unique
    private static final ResourceLocation STORAGE_BOOK = ArsNouveau.prefix("textures/gui/crafting_terminal.png");

//    @Unique
//    int maga$rowCount;

//    @Unique
//    private StateButton maga$buttonRowCount;

    public AbstractStorageTerminalScreenMixin(T menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    // Search Focused Section START
    @Redirect(method = "init()V",
            at = @At(value = "INVOKE",
                    target = "Lcom/hollingsworth/arsnouveau/client/container/AbstractStorageTerminalScreen;setFocused(Lnet/minecraft/client/gui/components/events/GuiEventListener;)V"))
    public void redirectSetFocused(AbstractStorageTerminalScreen instance, GuiEventListener guiEventListener) {
        if (Config.INSTANCE.disableSearchAutoFocus.isFalse()) {
            instance.setFocused(guiEventListener);
        }
//        maga$buttonRowCount = addRenderableWidget(new StorageSettingsButton(leftPos - 17, topPos - 14, 22, 12, 44, 13, 0, ArsNouveau.prefix("textures/gui/search_sync.png"), b -> {
//            maga$rowCount = (maga$rowCount + 1) % Util.ROW_COUNT;
//            maga$buttonRowCount.state = maga$rowCount;
//        }));
    }

    @Inject(method = "charTyped(CI)Z",
            at = @At(value = "INVOKE",
                    target = "Lcom/hollingsworth/arsnouveau/client/container/AbstractStorageTerminalScreen;clearFocus()V"),
            cancellable = true)
    public void injectChatTyped(char codePoint, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (Config.INSTANCE.disableSearchAutoFocus.isTrue()) {
            if (!searchField.isFocused()) cir.setReturnValue(false);
        }
    }

    @Inject(method = "keyPressed(III)Z",
            at = @At(value = "INVOKE",
                    target = "Lcom/hollingsworth/arsnouveau/client/container/AbstractStorageTerminalScreen;clearFocus()V",
                    ordinal = 0),
            cancellable = true)
    public void injectKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (Config.INSTANCE.disableSearchAutoFocus.isTrue()) {
            if (!searchField.isFocused()) cir.setReturnValue(false);
        }
    }
    // Search Focused Section END

    // Replaces the vanilla scrolling: the column count is not 9 anymore.
    @Inject(method = "scrollTo(F)V", at = @At("HEAD"), cancellable = true)
    public void scrollTo(float scroll, CallbackInfo ci) {
        int columns = Constants.columns();
        int lines = expanded ? Constants.expandedRows() : Constants.rows();
        int i = (itemsSorted.size() + columns - 1) / columns - lines;
        int j = Math.max(0, (int) (scroll * i + 0.5));
        for (int k = 0; k < lines; k++) {
            for (int l = 0; l < columns; l++) {
                int index = l + (k + j) * columns;
                this.menu.setSlotContents(l + k * columns, index < itemsSorted.size() ? itemsSorted.get(index) : null);
            }
        }
        ci.cancel();
    }

    @ModifyConstant(method = "needsScrollBars()Z", constant = @Constant(intValue = 9))
    private int needsScrollBarsColumns(int original) {
        return Constants.columns();
    }

    @ModifyConstant(method = "mouseScrolled(DDDD)Z", constant = @Constant(intValue = 9))
    private int mouseScrolledColumns(int original) {
        return Constants.columns();
    }

    @ModifyConstant(method = "mouseScrolled(DDDD)Z", constant = @Constant(intValue = 5))
    private int mouseScrolledLines(int original) {
        return rowCount; // Scroll step: one line per wheel notch
    }

    // init() also runs on window resize: recompute the layout from the screen size and re-layout the slots.
    @Inject(method = "init()V", at = @At("HEAD"))
    public void injectInitHead(CallbackInfo ci) {
        Constants.setRows(ClientLayout.desiredRows(this.height, this.imageHeight));
        Constants.setColumns(ClientLayout.desiredColumns(this, this.imageWidth));
        this.menu.addStorageSlots(expanded);
        tabButtons.clear(); // init() re-creates them; stale ones would keep receiving the tab names
    }

    @Inject(method = "init()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;init()V",
                    shift = At.Shift.AFTER))
    public void injectAfterInit(CallbackInfo ci) {
        topPos -= Constants.extraRows() * 9;  // Setup topPos to change button's y Pos
    }

    @Inject(method = "init()V",
            at = @At("TAIL"))
    public void injectInitTail(CallbackInfo ci) {
        topPos += Constants.extraRows() * 9;  // Reverse topPos
        // Follow the widened storage book: search on its right part, buttons on its left side
        searchField.setX(searchField.getX() + Constants.rightShift());
        for (var button : new AbstractWidget[]{buttonSortingType, buttonDirection, buttonSearchType}) {
            button.setX(button.getX() - Constants.leftShift());
        }
        for (var button : tabButtons) {
            button.setX(button.getX() - Constants.leftShift());
        }
    }

    @ModifyVariable(method = "render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
            at = @At(value = "STORE", ordinal = 0),
            name = "k")
    private int modifyScrollRenderLeftPos(int value) {
        return value + Constants.rightShift();
    }

    @ModifyVariable(method = "render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
            at = @At(value = "STORE", ordinal = 0),
            name = "l")
    private int modifyScrollRenderTopPos(int value) {
        return value - Constants.extraRows() * 18;
    }

    // Two stacked books: the storage one (widened, with its own bottom edge) is drawn over Ars' texture,
    // whose crafting / inventory part sticks out below it.
    @Inject(method = "renderBg(Lnet/minecraft/client/gui/GuiGraphics;FII)V",
            at = @At(value = "TAIL"))
    public void injectRenderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY, CallbackInfo ci) {
        int left = this.leftPos - Constants.leftShift();
        int top = this.topPos - Constants.extraRows() * 18;
        int lines = expanded ? Constants.expandedRows() : Constants.rows();
        // Header (above the first row)
        malga$blitWide(graphics, left, top, 0, 20, 58, 40);
        // Rows (the first and last ones hold the top / bottom of the scrollbar tab)
        for (int i = 0; i < lines; i++) {
            int v = i == 0 ? 20 : i == lines - 1 ? 56 : 38;
            malga$blitWide(graphics, left, top + 20 + i * 18, v, 18, 121, 103);
        }
        int y = top + 20 + lines * 18;
        // End of the scrollbar tab
        graphics.blit(STORAGE_BOOK, left + 183 + Constants.extraColumns() * 18, y, 183, 74, 19, 2);
        if (expanded) {
            // Push the book edge down to cover the inventory header, which starts right below the last row
            malga$blitWide(graphics, left, y, 76, 3, 121, 103);
            y += 3;
        }
        // Bottom edge of the book
        malga$blitWide(graphics, left, y, 243, 6, 121, 103);
        // Search Bar
        graphics.blit(ArsNouveau.prefix("textures/gui/search_paper.png"), left + 102 + Constants.extraColumns() * 18, top + 3, 0, 0, 72, 15, 72, 15);
    }

    // Draws a horizontal strip of the storage book texture, repeating an 18px column once per extra storage column.
    @Unique
    private void malga$blitWide(GuiGraphics graphics, int x, int y, int v, int height, int split, int tileU) {
        int extra = Constants.extraColumns();
        graphics.blit(STORAGE_BOOK, x, y, 0, v, split, height);
        for (int i = 0; i < extra; i++) {
            graphics.blit(STORAGE_BOOK, x + split + i * 18, y, tileU, v, 18, height);
        }
        graphics.blit(STORAGE_BOOK, x + split + extra * 18, y, split, v, this.imageWidth - split, height);
    }
}
