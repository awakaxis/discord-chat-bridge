package net.awakaxis.discordchatbridge.discord.listeners;

import net.awakaxis.discordchatbridge.Constants;
import net.awakaxis.discordchatbridge.platform.Services;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MessageForwarderListener extends ListenerAdapter {

    private static final int MAX_REPLY_LENGTH = 25;

    @Nullable
    private static MinecraftServer server;

    public static void setServer(@Nullable MinecraftServer minecraftServer) {
        server = minecraftServer;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot() || event.getAuthor().isSystem()) {
            return;
        }
        if (Services.PLATFORM.isDevelopmentEnvironment()) {
            Constants.LOGGER.info("!{}! [{}] {}: {}\n", server == null ? "NO SERVER" : "SERVER", event.getChannel(), event.getAuthor(), event.getMessage().getContentDisplay());
        }
        if (server != null) {
            if (event.getChannel() instanceof GuildChannel guildChannel) {
                if (!Services.PLATFORM.getListenChannels().contains(guildChannel.getIdLong())) return;
                Member member = guildChannel.getGuild().getMember(event.getAuthor());
                assert member != null;
                int color = member.getColors().getPrimaryRaw();

                MutableComponent component = Component.empty()
                        .append(Component.literal("[DC] ").withColor(0x5865F2).withStyle(ChatFormatting.BOLD))
                        .append(Component.literal(String.format("%s", event.getAuthor().getName())).withColor(color));

                if (event.getMessage().getReferencedMessage() != null) {
                    String referenceContent = event.getMessage().getReferencedMessage().getContentDisplay();
                    if (referenceContent.length() > MAX_REPLY_LENGTH - 3) {
                        referenceContent = referenceContent.substring(0, MAX_REPLY_LENGTH - 3).trim().concat("...");
                    }
                    component.append(Component.literal(String.format(" ⤷「%s」", referenceContent)).withStyle(ChatFormatting.GRAY));
                }

                component.append(Component.nullToEmpty(String.format(": %s", event.getMessage().getContentDisplay())));

                server.getPlayerList().broadcastSystemMessage(component, false);
            }
        }
    }
}
