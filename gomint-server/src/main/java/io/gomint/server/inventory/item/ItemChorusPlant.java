package io.gomint.server.inventory.item;
import io.gomint.inventory.item.ItemType;
import io.gomint.server.registry.RegisterInfo;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 240 )
public class ItemChorusPlant extends ItemStack implements io.gomint.inventory.item.ItemChorusPlant {



    @Override
    public String getBlockId() {
        return "minecraft:chorus_plant";
    }

    @Override
    public ItemType getType() {
        return ItemType.CHORUS_PLANT;
    }

}
