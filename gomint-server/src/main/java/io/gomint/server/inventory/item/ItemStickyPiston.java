package io.gomint.server.inventory.item;
import io.gomint.inventory.item.ItemType;

import io.gomint.server.registry.RegisterInfo;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 29 )
 public class ItemStickyPiston extends ItemStack implements io.gomint.inventory.item.ItemStickyPiston {



    @Override
    public ItemType getType() {
        return ItemType.STICKY_PISTON;
    }

}
