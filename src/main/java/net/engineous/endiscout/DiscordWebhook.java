package net.engineous.endiscout;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DiscordWebhook {
    private String discordId, discordWebhook;

    public DiscordWebhook(String discordId, String discordWebhook) {
        this.discordId = discordId;
        this.discordWebhook = discordWebhook;
    }

    public void sendMessage(String webhookURL, String message) {
        if (!webhookURL.isEmpty()) {
            try {
                final HttpsURLConnection connection = (HttpsURLConnection) new URL(webhookURL).openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; U; Linux i686) Gecko/20071127 Firefox/2.0.0.11");
                connection.setDoOutput(true);
                try (final OutputStream outputStream = connection.getOutputStream()) {
                    // Handle backslashes.
                    String preparedCommand = message.replaceAll("\\\\", "");
                    if (preparedCommand.endsWith(" *"))
                        preparedCommand = preparedCommand.substring(0, preparedCommand.length() - 2) + "*";

                    outputStream.write(("{\"content\":\"" + preparedCommand + "\"}").getBytes(StandardCharsets.UTF_8));
                }
                connection.getInputStream();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendAnthonyPingingMessage(String message) {
        sendMessage(discordWebhook, "<@" + discordId + "> " + message);
    }

    public void sendKrumbitPingingMessage(String message) {
        sendMessage(discordWebhook, "<@" + discordId + "> " + message);
    }

    public void sendKrumbitMessage(String message) {
        sendMessage(discordWebhook, message);
    }
}
