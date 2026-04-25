package net.awakaxis.discordchatbridge;

import net.awakaxis.discordchatbridge.discord.listeners.MessageForwarderListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.EnumSet;
import java.util.List;

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
        try {
            BOT = JDABuilder.create(token, EnumSet.of(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS))
                    .addEventListeners(new MessageForwarderListener())
                    .build();
        } catch (final InvalidTokenException invalidTokenException) {
            Constants.LOGGER.warn("!!!!! TOKEN IS INVALID !!!!!");
        }
    }
}