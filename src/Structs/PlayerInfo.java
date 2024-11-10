package Structs;

import Constants.GameModeRules;
import Managers.RulesManager;
import mindustry.game.Team;

public class PlayerInfo {
    public Team team;
    public long last_respawned;

    public PlayerInfo()
    {
        this.team = Team.all[0];
        this.last_respawned = System.currentTimeMillis() - GameModeRules.respawnCooldown;
    }
}
