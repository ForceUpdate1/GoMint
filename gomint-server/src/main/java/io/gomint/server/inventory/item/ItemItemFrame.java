package io.gomint.server.inventory.item;
import io.gomint.inventory.item.ItemType;

import io.gomint.server.registry.RegisterInfo;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 389 )
public class ItemItemFrame extends ItemStack implements io.gomint.inventory.item.ItemItemFrame {



    @Override
    public int getBlockId() {
        return 199;
    }

    @Override
    public ItemType getType() {
        return ItemType.ITEM_FRAME;
    }

}
