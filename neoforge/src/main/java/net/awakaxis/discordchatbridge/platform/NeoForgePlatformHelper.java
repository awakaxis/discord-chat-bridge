package net.awakaxis.discordchatbridge.platform;

import net.awakaxis.discordchatbridge.DCBNeoForge;
import net.awakaxis.discordchatbridge.platform.services.IPlatformHelper;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;

import java.util.List;

public class NeoForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {

        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return !FMLLoader.isProduction();
    }

    @Override
    public List<? extends Long> getListenChannels() {
        return DCBNeoForge.CONFIG.LISTEN_CHANNELS.get();
    }
}