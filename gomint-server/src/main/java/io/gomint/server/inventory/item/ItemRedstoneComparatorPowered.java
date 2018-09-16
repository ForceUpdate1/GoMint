package io.gomint.server.inventory.item;
import io.gomint.inventory.item.ItemType;
import io.gomint.server.registry.RegisterInfo;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 150 )
public class ItemRedstoneComparatorPowered extends ItemStack {



    @Override
    public String getBlockId() {
        return "minecraft:powered_comparator";
    }

    @Override
    public ItemType getType() {
        return ItemType.REDSTONE_COMPARATOR_POWERED;
    }

}
