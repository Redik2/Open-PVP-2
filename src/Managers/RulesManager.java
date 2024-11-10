package Managers;

import arc.func.Cons;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.game.Rules;
import mindustry.game.Team;
import mindustry.net.Administration;

public class RulesManager {
    public static void init()
    {
        Blocks.coreShard.unitCapModifier = 64;
        Blocks.coreFoundation.unitCapModifier = 128;
        Blocks.coreNucleus.unitCapModifier = 256;
        Vars.netServer.admins.setPlayerLimit(-1);
    }
    public static final Cons<Rules> ruleSetter = rules -> {
        rules.pvp = true;
        rules.cleanupDeadTeams = false;
        rules.unitCap = 9999999;
        rules.waves = false;
        rules.coreCapture = false;
        rules.pvpAutoPause = false;
        rules.canGameOver = false;
        rules.buildCostMultiplier = 1f;
        rules.buildSpeedMultiplier = 1f;
        rules.blockHealthMultiplier = 1f;
        rules.unitBuildSpeedMultiplier = 0.5f;
        rules.unitCostMultiplier = 2f;
        rules.unitDamageMultiplier = 1.41f;
        rules.unitHealthMultiplier = 1.41f;
        rules.modeName = "OPvP";
        rules.unitPayloadUpdate = true;
        rules.attackMode = false;
        rules.defaultTeam = Team.derelict;
    };
    public static Rules fakeRules = new Rules();
    static {
        fakeRules.attackMode = false;
        fakeRules.pvp = true;
    }

}
