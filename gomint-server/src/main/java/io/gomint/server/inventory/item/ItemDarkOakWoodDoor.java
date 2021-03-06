package io.gomint.server.inventory.item;
import io.gomint.inventory.item.ItemType;

import io.gomint.server.registry.RegisterInfo;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 431 )
 public class ItemDarkOakWoodDoor extends ItemStack implements io.gomint.inventory.item.ItemDarkOakWoodDoor {



    @Override
    public int getBlockId() {
        return 197;
    }

    @Override
    public ItemType getType() {
        return ItemType.DARK_OAK_DOOR;
    }

}
