package Managers;

import arc.Events;
import arc.util.Log;
import com.google.gson.Gson;
import mindustry.game.EventType;
import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.lang.reflect.Type;
import java.util.Map;

import com.google.gson.reflect.TypeToken;

public class DataManager
{
    public static class PlayerData
    {
        public long info_x, info_y, voting_x, voting_y, ban_time_end, mute_time_end, last_time_welcome_showed;
        public String info_align, voting_align, title_color, standard_color, accent_color;
        public PlayerData()
        {
            info_x = 0;
            info_y = 140;
            voting_x = 0;
            voting_y = 200;
            ban_time_end = 0;
            mute_time_end = 0;
            info_align = "top_left";
            voting_align = "top_left";
            title_color = "[white]";
            standard_color = "[#bfbfbf]";
            accent_color = "[#ffd37f]";
            last_time_welcome_showed = 0;
        }
    }

    public static PlayerData default_values = new PlayerData();
    private static final Gson gson = new Gson();
    public static Map<String, settingTypes> available_params;
    public static Map<String, PlayerData> players_settings;

    public enum settingTypes {
        text,
        digit,
        float_digit
    }

    static
    {
        available_params = new HashMap<>();
        available_params.put("info_x", settingTypes.digit);
        available_params.put("info_y", settingTypes.digit);
        available_params.put("info_align", settingTypes.text);
        available_params.put("voting_x", settingTypes.digit);
        available_params.put("voting_y", settingTypes.digit);
        available_params.put("voting_align", settingTypes.text);
        available_params.put("accent_color", settingTypes.text);
        available_params.put("standard_color", settingTypes.text);
        available_params.put("title_color", settingTypes.text);
        try
        {
            Type type = new TypeToken<Map<String, PlayerData>>(){}.getType();
            players_settings = gson.fromJson(Files.readString(Path.of("players_saves.json")), type);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static Object get_data(String player_uuid, String setting)
    {
        PlayerData settingMap = players_settings.getOrDefault(player_uuid, default_values);
        Field field;
        try {
            field = PlayerData.class.getField(setting);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        try {
            return field.get(settingMap);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void edit_data(String player_uuid, String setting, Object value)
    {
        PlayerData settingMap = players_settings.get(player_uuid);
        Field field;
        try {
            field = PlayerData.class.getField(setting);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        try {
            field.set(settingMap, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void save_data()
    {
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream("players_saves.json"), StandardCharsets.UTF_8))
        {
            writer.write(gson.toJson(players_settings));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void init()
    {
        Events.on(EventType.PlayerJoin.class, e -> {
            if (!players_settings.containsKey(e.player.uuid()))
            {
                players_settings.put(e.player.uuid(), new PlayerData());
            }
        });
    }

    public static Object get_default_value(String setting)
    {
        try {
            return PlayerData.class.getField(setting).get(default_values);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
