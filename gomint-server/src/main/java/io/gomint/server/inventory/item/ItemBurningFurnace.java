package io.gomint.server.inventory.item;
import io.gomint.inventory.item.ItemType;
import io.gomint.server.registry.RegisterInfo;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 62 )
public class ItemBurningFurnace extends ItemStack implements io.gomint.inventory.item.ItemBurningFurnace {



    @Override
    public ItemType getType() {
        return ItemType.BURNING_FURNACE;
    }

}
