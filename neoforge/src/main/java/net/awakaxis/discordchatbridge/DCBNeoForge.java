package net.awakaxis.discordchatbridge;


import net.awakaxis.discordchatbridge.discord.WebhookHelper;
import net.awakaxis.discordchatbridge.discord.listeners.MessageForwarderListener;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.CommandEvent;
import net.neoforged.neoforge.event.ServerChatEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Optional;

@Mod(Constants.MOD_ID)
public class DCBNeoForge {

    public static final NeoForgeConfig CONFIG;
    public static final ModConfigSpec CONFIG_SPEC;

    static {
        Pair<NeoForgeConfig, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(NeoForgeConfig::new);
        CONFIG = pair.getLeft();
        CONFIG_SPEC = pair.getRight();
    }

    public DCBNeoForge(IEventBus eventBus, ModContainer container) {

        container.registerConfig(ModConfig.Type.COMMON, CONFIG_SPEC);
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        DiscordChatBridge.init();

        NeoForge.EVENT_BUS.addListener(ServerStartedEvent.class, (serverStartedEvent -> {
            MessageForwarderListener.setServer(serverStartedEvent.getServer());
            DiscordChatBridge.initBot(CONFIG.TOKEN.get());
        }));

        NeoForge.EVENT_BUS.addListener(ServerChatEvent.class, (serverChatEvent -> {
            CONFIG.CHAT_HOOKS.get().forEach((url) ->
                    WebhookHelper.sendPlayerMessage(serverChatEvent.getUsername(), serverChatEvent.getRawText(), url));
        }));

        NeoForge.EVENT_BUS.addListener(LivingDeathEvent.class, (livingDeathEvent -> {
            if (livingDeathEvent.getEntity() instanceof ServerPlayer player) {
                CONFIG.DEATH_HOOKS.get().forEach((url) ->
                        WebhookHelper.sendServerMessage(livingDeathEvent.getSource().getLocalizedDeathMessage(player).getString(), url));
            }
        }));

        NeoForge.EVENT_BUS.addListener(CommandEvent.class, (commandEvent -> {
            if ((commandEvent.getParseResults().getContext().getSource().getEntity() instanceof ServerPlayer serverPlayer)) {
                CONFIG.COMMAND_HOOKS.get().forEach((url) ->
                        WebhookHelper.sendServerMessage(String.format("%s executed `%s`", serverPlayer.getName().getString(), commandEvent.getParseResults().getReader().getString()), url));
            }
        }));

        NeoForge.EVENT_BUS.addListener(PlayerEvent.PlayerLoggedInEvent.class, (playerLoggedInEvent -> {
            if (playerLoggedInEvent.getEntity() instanceof ServerPlayer serverPlayer) {
                CONFIG.CONNECTION_HOOKS.get().forEach((url) ->
                        WebhookHelper.sendServerMessage(String.format("%s joined the game", serverPlayer.getName().getString()), url));
            }
        }));

        NeoForge.EVENT_BUS.addListener(PlayerEvent.PlayerLoggedOutEvent.class, (playerLoggedOutEvent -> {
            if (playerLoggedOutEvent.getEntity() instanceof ServerPlayer serverPlayer) {
                CONFIG.CONNECTION_HOOKS.get().forEach((url) ->
                        WebhookHelper.sendServerMessage(String.format("%s left the game", serverPlayer.getName().getString()), url));
            }
        }));

        NeoForge.EVENT_BUS.addListener(AdvancementEvent.AdvancementEarnEvent.class, (advancementEarnEvent -> {
            if (advancementEarnEvent.getEntity() instanceof ServerPlayer serverPlayer) {
                Optional<DisplayInfo> display = advancementEarnEvent.getAdvancement().value().display();
                if (display.isEmpty() || !display.get().shouldAnnounceChat()) return;

                CONFIG.CONNECTION_HOOKS.get().forEach((url) ->
                        WebhookHelper.sendServerMessage(String.format("%s has made the advancement **%S**", serverPlayer.getName().getString(), Advancement.name(advancementEarnEvent.getAdvancement()).getString()), url));
            }
        }));
    }
}