package Lang;

import Managers.DataManager;
import arc.util.Log;
import com.google.gson.Gson;
import mindustry.gen.Player;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class LanguageManager {

    public static String server_info_prefix = "[accent]\uE837[standard]";
    public static String server_warn_prefix = "[orange]\uE810";
    public static String server_err_prefix = "[red]⚠";
    private static Gson gson = new Gson();
    public static Map<String, Map<String, String>> languages;
    public static void init()
    {
        try {
            languages = gson.fromJson(Files.readString(Path.of("translation.json")), HashMap.class); // languageMap - карта языка
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getTranslation(String language, String key) {
        Map<String, String> languageMap = languages.get(key);
        if (languageMap == null) {
            languageMap = languages.get("en"); // не нашлась карта языка => en
        }
        if (!languageMap.containsKey(key))
        {
            languageMap = languages.get("en"); // не нашелся ключ в карте языка => язык меняется на en
        }
        return languageMap.getOrDefault(key, key).replace("[i]", server_info_prefix).replace("[!]", server_err_prefix); // если нет key в карте языка, то будет отображен сам key
    }

    public static String getLocalisedMessage(Player player, String key)
    {
        return getTranslation(player.locale, key).replace("[w]", server_warn_prefix).replace("[standard]", (String)DataManager.get_data(player.uuid(), "standard_color")).replace("[title]", (String)DataManager.get_data(player.uuid(), "title_color")).replace("[accent]", (String)DataManager.get_data(player.uuid(), "accent_color"));
    }
}
