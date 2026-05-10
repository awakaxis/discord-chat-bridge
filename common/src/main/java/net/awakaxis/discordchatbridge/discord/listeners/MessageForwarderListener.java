package net.awakaxis.discordchatbridge.discord.listeners;

import net.awakaxis.discordchatbridge.Constants;
import net.awakaxis.discordchatbridge.discord.WebhookHelper;
import net.awakaxis.discordchatbridge.platform.Services;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.PlayerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class MessageForwarderListener extends ListenerAdapter {

    private static final int MAX_REPLY_LENGTH = 25;

    @Nullable
    private static MinecraftServer server;

    public static void setServer(@Nullable MinecraftServer minecraftServer) {
        server = minecraftServer;
    }

    private boolean handleCommandUsage(final String command, final Message message, final User user, final MessageChannel messageChannel) {
        if (command.equals("!playerlist") || command.equals("!pl")) {
            if (server == null) {
                message.reply("Server has not started!").mentionRepliedUser(false).queue();
                return false;
            }
            PlayerList playerList = server.getPlayerList();
            String serverName = Services.PLATFORM.getServerName();
            String authorString = String.format("(%s/%s) Players Online", playerList.getPlayerCount(), playerList.getMaxPlayers());

            StringBuilder players = new StringBuilder();
            playerList.getPlayers().forEach(serverPlayer -> players.append(String.format("%s. %s\n", players.length() + 1, serverPlayer.getName().getString())));

            MessageEmbed embed = new EmbedBuilder()
                    .setAuthor(!serverName.isEmpty() ? String.format("%s (%s):", authorString, serverName) : authorString + ":")
                    .setThumbnail(WebhookHelper.SERVER_AVATAR)
                    .setDescription(playerList.getPlayerCount() == 0 ? "No players are currently online." : players)
                    .build();
            messageChannel.sendMessageEmbeds(embed).queue();
            return true;
        }

        Constants.LOGGER.warn("Unable to handle unknown command: {}. You can probably ignore this, or disable these messages in config.", command);
        return false;
    }

    @Override
    public void onMessageReceived(final @NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot() || event.getAuthor().isSystem()) {
            return;
        }
        final Message message = event.getMessage();
        if (Services.PLATFORM.isDevelopmentEnvironment()) {
            Constants.LOGGER.debug("!{}! [{}] {}: {}\n", server == null ? "NO SERVER" : "SERVER", event.getChannel(), event.getAuthor(), message.getContentDisplay());
        }
        //TODO: make configurable prefix
        if (message.getContentRaw().startsWith("!")) {
            if (handleCommandUsage(message.getContentRaw().split(" ", 1)[0], message, message.getAuthor(), message.getChannel())) {
                return;
            }
        }
        if (server != null) {
            if (event.getChannel() instanceof GuildChannel guildChannel) {
                if (!Services.PLATFORM.getListenChannels().contains(guildChannel.getIdLong())) return;
                final Member member = guildChannel.getGuild().getMember(event.getAuthor());
                assert member != null;
                int color = member.getColors().getPrimaryRaw();

                final MutableComponent component = Component.empty()
                        .append(Component.literal("[DC] ").withColor(0x5865F2).withStyle(ChatFormatting.BOLD))
                        .append(Component.literal(String.format("%s", event.getAuthor().getName())).withColor(color));

                if (message.getReferencedMessage() != null) {
                    String referenceContent = message.getReferencedMessage().getContentDisplay();
                    if (referenceContent.length() > MAX_REPLY_LENGTH - 3) {
                        referenceContent = referenceContent.substring(0, MAX_REPLY_LENGTH - 3).trim().concat("...");
                    }
                    component.append(Component.literal(String.format(" ⤷「%s」", referenceContent)).withStyle(ChatFormatting.GRAY));
                }

                component.append(Component.nullToEmpty(String.format(": %s", message.getContentDisplay())));

                server.getPlayerList().broadcastSystemMessage(component, false);
            }
        }
    }
}
