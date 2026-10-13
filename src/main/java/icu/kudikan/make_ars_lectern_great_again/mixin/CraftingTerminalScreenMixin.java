package icu.kudikan.make_ars_lectern_great_again.mixin;

import com.hollingsworth.arsnouveau.client.container.AbstractStorageTerminalScreen;
import com.hollingsworth.arsnouveau.client.container.CraftingTerminalMenu;
import com.hollingsworth.arsnouveau.client.container.CraftingTerminalScreen;
import icu.kudikan.make_ars_lectern_great_again.utils.ClientLayout;
import icu.kudikan.make_ars_lectern_great_again.utils.Constants;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CraftingTerminalScreen.class)
public abstract class CraftingTerminalScreenMixin extends AbstractStorageTerminalScreen<CraftingTerminalMenu> {
    public CraftingTerminalScreenMixin(CraftingTerminalMenu screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Inject(method = "init()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/recipebook/RecipeBookComponent;init(IILnet/minecraft/client/Minecraft;ZLnet/minecraft/world/inventory/RecipeBookMenu;)V"))
    public void init(CallbackInfo ci) {
        this.topPos += Constants.extraRows() * 9;
    }

    @ModifyConstant(method = "init()V",
            constant = {
                    @Constant(intValue = 3, ordinal = 0),
                    @Constant(intValue = 34, ordinal = 0),
                    @Constant(intValue = 23, ordinal = 0),
            })
    private int init(int value) {
        return switch (value) {
            case 3 -> Constants.rows(); // rowCount
            case 34 -> value - Constants.extraRows() * 9; // - recipeButtonY
            case 23 -> value + Constants.extraRows() * 9; // collapseButtonY
            default -> value;
        };
    }

    @Inject(method = "onExpandedChanged",
            at = @At("HEAD")
    )
    private void collapseScreen(CallbackInfo ci) {
        rowCount = expanded ? Constants.expandedRows() : Constants.rows();
    }

    @Inject(method = "expandScreen()V",
            at = @At("TAIL")
    )
    private void expandScreen(CallbackInfo ci) {
        this.scrollTo(this.currentScroll); // Update storageSlotList
    }

    @Inject(
            method = "hasClickedOutside(DDIII)Z",
            at = @At("RETURN"),
            cancellable = true)
    private void hasClickedOutside(double mouseX, double mouseY, int guiLeftIn, int guiTopIn, int mouseButton, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            cir.setReturnValue(mouseX < guiLeftIn - Constants.leftShift() || mouseY < guiTopIn - Constants.extraRows() * 18 || mouseX >= guiLeftIn + this.imageWidth + Constants.rightShift() || mouseY >= guiTopIn + this.imageHeight);
        }
    }

    // Opening / closing the recipe book changes the available width: rebuild the layout when needed.
    @Inject(method = "containerTick()V", at = @At("TAIL"))
    private void containerTick(CallbackInfo ci) {
        if (ClientLayout.desiredColumns(this, this.imageWidth) != Constants.columns()) {
            this.rebuildWidgets();
        }
    }
}
