package net.awakaxis.discordchatbridge;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class NeoForgeConfig {

    public final ModConfigSpec.ConfigValue<String> TOKEN;
    public final ModConfigSpec.ConfigValue<List<? extends String>> CHAT_HOOKS;
    public final ModConfigSpec.ConfigValue<List<? extends String>> DEATH_HOOKS;
    public final ModConfigSpec.ConfigValue<List<? extends String>> COMMAND_HOOKS;
    public final ModConfigSpec.ConfigValue<List<? extends String>> ADVANCEMENT_HOOKS;
    public final ModConfigSpec.ConfigValue<List<? extends String>> CONNECTION_HOOKS;

    public NeoForgeConfig(final ModConfigSpec.Builder BUILDER) {
        this.TOKEN = BUILDER
                .worldRestart()
                .comment("Bot token to bind to for listening to discord messages.")
                .define("token", () -> "TOKEN", (obj) -> true);
        this.CHAT_HOOKS = BUILDER
                .comment("Webhooks the bot will send chat messages through.")
                .defineListAllowEmpty("chatHooks", new ArrayList<>(), () -> "", (obj) -> true);
        this.DEATH_HOOKS = BUILDER
                .comment("Webhooks the bot will send death messages through.")
                .defineListAllowEmpty("deathHooks", new ArrayList<>(), () -> "", (obj) -> true);
        this.COMMAND_HOOKS = BUILDER
                .comment("Webhooks the bot will send command execution messages through.")
                .defineListAllowEmpty("commandHooks", new ArrayList<>(), () -> "", (obj) -> true);
        this.ADVANCEMENT_HOOKS = BUILDER
                .comment("Webhooks the bot will send advancement earn messages through.")
                .defineListAllowEmpty("advancementHooks", new ArrayList<>(), () -> "", (obj) -> true);
        this.CONNECTION_HOOKS = BUILDER
                .comment("Webhooks the bot will send server join / leave messages through.")
                .defineListAllowEmpty("logInOutHooks", new ArrayList<>(), () -> "", (obj) -> true);
    }
}
