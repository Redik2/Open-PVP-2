package Constants;

import Lang.LanguageManager;
import Managers.DataManager;
import arc.util.Log;
import mindustry.gen.Groups;
import mindustry.gen.Player;

import java.util.concurrent.TimeUnit;

public class Utilities {
    static int founded;
    public static Player find_by_name(String name)
    {
        var need_to_find = Player.create();
        need_to_find.name(name);

        founded = 0;
        Groups.player.each(player -> {
            if (player.plainName().contains(need_to_find.plainName().replace(" ", "_")))
            {
                founded++;
            }
        });


        if (founded != 1) return null;

        return Groups.player.find(find_player -> find_player.plainName().contains(need_to_find.plainName()));
    }

    public static String timestamp(long millis)
    {
        millis += TimeUnit.MINUTES.toMillis(1) - 1;
        long days = TimeUnit.MILLISECONDS.toDays(millis);
        long hours = TimeUnit.MILLISECONDS.toHours(millis) - TimeUnit.DAYS.toHours(days);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(hours);

        StringBuilder result = new StringBuilder();

        if (days > 0) result.append(days).append("d ");
        if (hours > 0 || days > 0) result.append(hours).append("h ");
        if (minutes >= 0 || days > 0 || hours > 0) result.append(minutes).append("m");

        return result.toString();
    }
}
