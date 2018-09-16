package io.gomint.server.inventory.item;
import io.gomint.inventory.item.ItemType;

import io.gomint.server.registry.RegisterInfo;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 31 )
 public class ItemTallGrass extends ItemStack implements io.gomint.inventory.item.ItemTallGrass {



    @Override
    public String getBlockId() {
        return "minecraft:tallgrass";
    }

    @Override
    public ItemType getType() {
        return ItemType.TALL_GRASS;
    }

}
