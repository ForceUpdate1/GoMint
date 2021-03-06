package io.gomint.inventory.item;

import io.gomint.GoMint;

/**
 * @author geNAZt
 * @version 1.0
 */
public interface ItemGoldenPickaxe extends ItemReduceBreaktime {

    /**
     * Create a new item stack with given class and amount
     *
     * @param amount which is used for the creation
     */
    static ItemGoldenPickaxe create( int amount ) {
        return GoMint.instance().createItemStack( ItemGoldenPickaxe.class, amount );
    }

}
