package net.engineous.endiscout;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.io.File;

@Mod(modid = EnDiscOut.MODID, version = EnDiscOut.VERSION)
public class EnDiscOut {
    public static final String MODID = "endiscout";
    public static final String VERSION = "1.0";
    private Helper helper;
    private Minecraft mc;
    private DiscordWebhook webhook;
    private Configuration config;
    private String discordId = "", discordWebhook = "";

    @EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        File configFile = new File(Loader.instance().getConfigDir(), "endiscout.cfg");
        config = new Configuration(configFile);
        config.load();
        Property discordIdTemp = config.get("discord", "id", "634958475000152087");
        discordId = discordIdTemp.getString();
        Property discordWebhookTemp = config.get("discord", "webhook", "https://discord.com/api/webhooks/1456896392302821511/uNGsSwU4tUh9Kodk9W2BSxWkesGgmW_Blz2FLa2Yh2LpAlzUQPw1mhLUzTXysc_MDlxW");
        discordWebhook = discordWebhookTemp.getString();
        if (config.hasChanged()) {
            config.save();
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        helper = new Helper(MODID, VERSION, "EnDiscOut");
        MinecraftForge.EVENT_BUS.register(this);
        mc = Minecraft.getMinecraft();
        webhook = new DiscordWebhook(discordId, discordWebhook);
    }

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        if (!helper.isHypixel()) return;

        if (event.message.getFormattedText().contains("§r§c[Important] §r§eThis server will restart soon:")) {
            helper.sendMessage("Detected server restart, sending webhook message...");
            webhook.sendPingingMessage("server restarted");
        }

        if (event.message.getFormattedText().contains("§cYou are AFK. Move around to return from AFK.")) {
            helper.sendMessage("Detected AFK kick, sending webhook message...");
            webhook.sendPingingMessage("kicked for AFK");
        }

        if (event.message.getFormattedText().contains("You were spawned in Limbo.")) {
            helper.sendMessage("Detected Limbo spawn, sending webhook message...");
            webhook.sendPingingMessage("spawned in Limbo");
        }
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        try {
            java.lang.reflect.Field field = event.manager.getClass().getDeclaredField("terminationReason");
            field.setAccessible(true);
            net.minecraft.util.IChatComponent reason = (net.minecraft.util.IChatComponent) field.get(event.manager);

            if (reason instanceof ChatComponentTranslation) {
                ChatComponentTranslation chatComponentTranslation = (ChatComponentTranslation) reason;
                String key = chatComponentTranslation.getKey();
                helper.sendMessage("Disconnected: " + key);
                webhook.sendPingingMessage("Disconnected: " + key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
