package me.javlin.glowsquid.network.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class UtilMojangAPI
{
    private static final String authenticate = "https://sessionserver.mojang.com/session/minecraft/join";
    private static final String finishAuth = "https://sessionserver.mojang.com/session/minecraft/hasJoined?username=%s&serverId=%s";

    public static boolean startAuth(String accessToken, String uuid, String hash) throws IOException {
        HttpURLConnection connection = (HttpURLConnection)new URL(authenticate).openConnection();
        String json = "{\"accessToken\": \"" + accessToken + "\", \"selectedProfile\": \"" + uuid + "\", \"serverId\": \"" + hash + "\"}";

        try {
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            connection.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
        } finally {
            connection.disconnect();
        }

        return connection.getResponseCode() == 204;
    }

    public static String finishAuth(String name, String hash) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(String.format(finishAuth, name, hash)).openConnection();

        connection.setRequestMethod("GET");

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = bufferedReader.readLine()) != null)
                response.append(inputLine);

            connection.disconnect();

            if (response.toString().contains("\"name\""))
                return response.toString();
        } finally {
            connection.disconnect();
        }

        return null;
    }
}
