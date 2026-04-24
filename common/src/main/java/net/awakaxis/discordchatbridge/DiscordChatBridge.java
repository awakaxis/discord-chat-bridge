package net.awakaxis.discordchatbridge;

import net.awakaxis.discordchatbridge.discord.listeners.MessageForwarderListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.EnumSet;

public class DiscordChatBridge {

    @Nullable
    public static JDA BOT;


    public static void init() {
        Constants.LOGGER.info("Hello discord haters");
    }

    public static void initBot(String token) {
        if (BOT != null) {
            BOT.shutdown();
            try {
                if (!BOT.awaitShutdown(Duration.ofSeconds(10))) {
                    BOT.shutdownNow();
                    BOT.awaitShutdown();
                }
            } catch (final InterruptedException e) {
                throw new RuntimeException("Exception in JDA shutdown");
            }
        }
        BOT = JDABuilder.create(token, EnumSet.allOf(GatewayIntent.class))
                .addEventListeners(new MessageForwarderListener()).build();
    }
}