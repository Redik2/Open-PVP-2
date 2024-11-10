package Managers;

import Constants.GameModeRules;
import Lang.LanguageManager;
import mindustry.gen.Call;
import mindustry.gen.Player;
import mindustry.ui.Menus;

public class MenuManager {
    public static int welcomeMenu;
    public static int rulesMenu;

    public static void setupMenus()
    {
        welcomeMenu = Menus.registerMenu(((player, option) -> {
            switch (option)
            {
                case 0 -> {
                    Call.openURI(player.con(), GameModeRules.discord_link);
                }
                case 2 -> {
                    callWelcomeRulesMenu(player);
                }
            }
        }));

        rulesMenu = Menus.registerMenu(((player, option) -> {
            callWelcomeMenu(player);
        }));
    }

    public static void callWelcomeMenu(Player player)
    {
        Call.menu(player.con(), welcomeMenu, LanguageManager.getLocalisedMessage(player, "menu.welcome.title"), LanguageManager.getLocalisedMessage(player, "menu.welcome.main"), new String[][] {{LanguageManager.getLocalisedMessage(player, "menu.welcome.discord"), LanguageManager.getLocalisedMessage(player, "menu.close"), LanguageManager.getLocalisedMessage(player, "menu.welcome.rules")}});
    }

    public static void callWelcomeRulesMenu(Player player)
    {
        Call.menu(player.con(), rulesMenu, LanguageManager.getLocalisedMessage(player, "menu.rules.title"), LanguageManager.getLocalisedMessage(player, "menu.rules.main"), new String[][] {{LanguageManager.getLocalisedMessage(player, "menu.back")}});
    }
    public static void callRulesMenu(Player player)
    {
        Call.menu(player.con(), -1, LanguageManager.getLocalisedMessage(player, "menu.rules.title"), LanguageManager.getLocalisedMessage(player, "menu.rules.main"), new String[][] {{LanguageManager.getLocalisedMessage(player, "menu.close")}});
    }
}
