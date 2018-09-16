package io.gomint.server.inventory.item;
import io.gomint.inventory.item.ItemType;

import io.gomint.server.registry.RegisterInfo;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 140 )
 public class ItemFlowerPotBlock extends ItemStack implements io.gomint.inventory.item.ItemFlowerPotBlock {



    @Override
    public String getBlockId() {
        return "minecraft:flower_pot";
    }

    @Override
    public ItemType getType() {
        return ItemType.FLOWER_POT_BLOCK;
    }

}
