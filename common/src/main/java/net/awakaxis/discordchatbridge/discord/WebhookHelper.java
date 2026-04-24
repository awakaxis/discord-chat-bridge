package net.awakaxis.discordchatbridge.discord;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookMessage;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;

public final class WebhookHelper {
    public static void sendPlayerMessage(String username, String content, String url) {
        try (WebhookClient client = WebhookClient.withUrl(url)) {
            client.send(buildForPlayer(username, content));
        }
    }

    public static void sendServerMessage(String content, String url) {
        try (WebhookClient client = WebhookClient.withUrl(url)) {
            client.send(buildForServer(content));
        }
    }

    private static WebhookMessage buildForPlayer(String username, String content) {
        return new WebhookMessageBuilder().setUsername(username).setAvatarUrl("https://minotar.net/avatar/" + username).setContent(content).build();
    }

    private static WebhookMessage buildForServer(String content) {
        return new WebhookMessageBuilder().setUsername("Server").setAvatarUrl("https://cdn.discordapp.com/attachments/1127684842176380938/1496999492296380499/server-icon.png?ex=69ebeceb&is=69ea9b6b&hm=ba4bd7f9eb2bb1b324ef68799de2148c8b76724765809dd45beace01d59bad1a&").setContent(content).build();
    }
}
