package icu.kudikan.make_ars_lectern_great_again.compat.jei;

import com.hollingsworth.arsnouveau.client.container.CraftingTerminalScreen;
import icu.kudikan.make_ars_lectern_great_again.MakeArsLecternGreatAgain;
import icu.kudikan.make_ars_lectern_great_again.utils.Constants;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiProperties;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;

// Only loaded by JEI, when it is installed.
@JeiPlugin
public class MalgaJeiPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(MakeArsLecternGreatAgain.MODID, "jei_plugin");
    }

    // Report the real (taller, wider) bounds of the lectern so JEI lays its panels out around it.
    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiScreenHandler(CraftingTerminalScreen.class, screen -> {
            int left = screen.getGuiLeft() - Constants.leftShift() - Constants.LEFT_BUTTONS_WIDTH;
            int top = screen.getGuiTop() - Constants.extraRows() * Constants.ROW_HEIGHT;
            int right = screen.getGuiLeft() + screen.getXSize() + Constants.rightShift();
            int bottom = screen.getGuiTop() + screen.getYSize();
            return new GuiProperties(screen.getClass(), left, top, right - left, bottom - top, screen.width, screen.height);
        });
    }

    private record GuiProperties(Class<? extends Screen> screenClass, int guiLeft, int guiTop, int guiXSize,
                                 int guiYSize, int screenWidth, int screenHeight) implements IGuiProperties {
    }
}
