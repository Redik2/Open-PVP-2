package Constants;

import mindustry.game.Team;
import mindustry.gen.Player;
import mindustry.world.Tile;

public class OpenEvents {
    public static class NewTeamSpawn
    {
        public final Player player;
        public final Tile tile;
        public final Team team;

        public NewTeamSpawn(Player player, Tile tile, Team team) {
            this.player = player;
            this.tile = tile;
            this.team = team;
        }
    }

    public static class Notification
    {
        public final Tile tile;
        public final String text;
        public final float duration;
        public final Player player;

        public Notification(Player player, Tile tile, String text, float duration) {
            this.player = player;
            this.tile = tile;
            this.text = text;
            this.duration = duration;
        }
    }

    public static class ReloadGame
    {
        public ReloadGame()
        {

        }
    }

    public static class ResetVars
    {
        public ResetVars()
        {

        }
    }

    public static class PlayerVoted
    {
        public final Player player;
        public final boolean vote;

        public PlayerVoted(Player player, boolean vote) {
            this.player = player;
            this.vote = vote;
        }
    }
}
