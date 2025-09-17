package auviotre.enigmatic.legacy.proxy;

import auviotre.enigmatic.legacy.client.screen.PermanentDeathScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.GenericMessageScreen;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.network.chat.Component;

public class ClientProxy extends CommonProxy {
    @Override
    public void displayPermanentDeathScreen() {
        if (Minecraft.getInstance().level != null) {
            boolean local = Minecraft.getInstance().isLocalServer();
            Minecraft.getInstance().level.disconnect();

            if (local) {
                Minecraft.getInstance().clearClientLevel(new GenericMessageScreen(Component.translatable("menu.savingLevel")));
            } else {
                Minecraft.getInstance().clearClientLevel(new ProgressScreen(true));
            }
        }

        PermanentDeathScreen screen = new PermanentDeathScreen(new SelectWorldScreen(new TitleScreen()), Component.translatable("gui.enigmaticlegacy.permanent_death_screen_title"),
                Component.translatable("message.enigmaticlegacy.permanent_death_screen"));
        PermanentDeathScreen.active = screen;
        Minecraft.getInstance().setScreen(screen);
    }

    @Override
    public String getClientUsername() {
        return Minecraft.getInstance().getUser().getName();
    }
}
