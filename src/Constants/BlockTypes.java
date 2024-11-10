package Constants;

import mindustry.content.Blocks;
import mindustry.world.Block;

import java.util.ArrayList;
import java.util.List;

public class BlockTypes {
    public static List<Block> nonvalid_floors = new ArrayList<Block>();
    public static List<Block> valid_blocks = new ArrayList<Block>();
    public static List<Block> cores = new ArrayList<Block>();
    public static List<Block> storage_payloads = new ArrayList<Block>();
    public static List<Block> liquid_payloads = new ArrayList<Block>();

    static {
        nonvalid_floors.add(Blocks.space);
        nonvalid_floors.add(Blocks.slag);
        nonvalid_floors.add(Blocks.tar);
        nonvalid_floors.add(Blocks.deepwater);
        nonvalid_floors.add(Blocks.cryofluid);
        nonvalid_floors.add(Blocks.deepTaintedWater);
        nonvalid_floors.add(Blocks.arkyciteFloor);

        valid_blocks.add(Blocks.shaleBoulder);
        valid_blocks.add(Blocks.sandBoulder);
        valid_blocks.add(Blocks.daciteBoulder);
        valid_blocks.add(Blocks.boulder);
        valid_blocks.add(Blocks.snowBoulder);
        valid_blocks.add(Blocks.basaltBoulder);
        valid_blocks.add(Blocks.carbonBoulder);
        valid_blocks.add(Blocks.ferricBoulder);
        valid_blocks.add(Blocks.beryllicBoulder);
        valid_blocks.add(Blocks.yellowStoneBoulder);
        valid_blocks.add(Blocks.arkyicBoulder);
        valid_blocks.add(Blocks.crystalCluster);
        valid_blocks.add(Blocks.crystallineBoulder);
        valid_blocks.add(Blocks.redIceBoulder);
        valid_blocks.add(Blocks.rhyoliteBoulder);
        valid_blocks.add(Blocks.redStoneBoulder);
        valid_blocks.add(Blocks.sporeCluster);
        valid_blocks.add(Blocks.air);

        cores.add(Blocks.coreShard);
        cores.add(Blocks.coreNucleus);
        cores.add(Blocks.coreBastion);
        cores.add(Blocks.coreCitadel);
        cores.add(Blocks.coreFoundation);
        cores.add(Blocks.coreAcropolis);

        storage_payloads.add(Blocks.container);
        storage_payloads.add(Blocks.reinforcedContainer);
        storage_payloads.add(Blocks.reinforcedVault);
        storage_payloads.add(Blocks.vault);
        storage_payloads.add(Blocks.conveyor);
        storage_payloads.add(Blocks.armoredConveyor);
        storage_payloads.add(Blocks.plastaniumConveyor);
        storage_payloads.add(Blocks.titaniumConveyor);
        storage_payloads.add(Blocks.duct);
        storage_payloads.add(Blocks.armoredDuct);
        storage_payloads.add(Blocks.itemBridge);
        storage_payloads.add(Blocks.ductBridge);
        storage_payloads.add(Blocks.phaseConveyor);
        storage_payloads.add(Blocks.surgeRouter);

        liquid_payloads.add(Blocks.liquidContainer);
        liquid_payloads.add(Blocks.liquidRouter);
        liquid_payloads.add(Blocks.liquidTank);
        liquid_payloads.add(Blocks.reinforcedLiquidContainer);
        liquid_payloads.add(Blocks.reinforcedLiquidRouter);
        liquid_payloads.add(Blocks.reinforcedLiquidTank);
    }
}
