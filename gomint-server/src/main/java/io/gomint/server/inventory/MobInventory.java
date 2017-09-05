package io.gomint.server.inventory;

import io.gomint.inventory.ItemStack;
import io.gomint.inventory.Material;
import io.gomint.server.network.PlayerConnection;
import io.gomint.server.network.packet.PacketMobArmorEquipment;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author geNAZt
 * @version 1.0
 *          <p>
 *          Inventory for any type of mob. This holds additional Armor Contents
 */
public class MobInventory extends Inventory {

    // TODO: Decide if armor moves into its own inventory
    private ItemStack helmet = new ItemStack( Material.AIR );
    private ItemStack chestPlate = new ItemStack( Material.AIR );
    private ItemStack leggings = new ItemStack( Material.AIR );
    private ItemStack boots = new ItemStack( Material.AIR );

    public MobInventory( InventoryHolder inventoryHolder, int size ) {
        super( inventoryHolder, size );
    }

    public void setHelmet( ItemStack itemStack ) {
        this.helmet = itemStack;
    }

    public void setChestplate( ItemStack itemStack ) {
        this.chestPlate = itemStack;
    }

    public void setLeggings( ItemStack itemStack ) {
        this.setItem( this.size - 2, itemStack );
    }

    public void setBoots( ItemStack itemStack ) {
        this.setItem( this.size - 1, itemStack );
    }

    public ItemStack[] getContents() {
        return Arrays.copyOf( this.contents, this.size - 4 );
    }

    @Override
    public int getSize() {
        return this.size - 4;
    }

    public void setItem( int index, ItemStack item ) {
        this.contents[index] = item;

        if ( index < this.size - 4 ) {
            for ( PlayerConnection playerConnection : this.viewer ) {
                this.sendContents( index, playerConnection );
            }
        }
    }

    @Override
    public void sendContents( PlayerConnection playerConnection ) {
        PacketMobArmorEquipment armorEquipment = new PacketMobArmorEquipment();
        armorEquipment.setEntityId( this.owner.getEntityId() );
        armorEquipment.setHelmet( this.contents[this.size - 4] );
        armorEquipment.setChestplate( this.contents[this.size - 3] );
        armorEquipment.setLeggings( this.contents[this.size - 2] );
        armorEquipment.setBoots( this.contents[this.size - 1] );
        playerConnection.addToSendQueue( armorEquipment );
    }

    @Override
    public void sendContents( int slot, PlayerConnection playerConnection ) {
        /*if ( slot > originalSize ) {
            PacketMobArmorEquipment armorEquipment = new PacketMobArmorEquipment();
            armorEquipment.setEntityId( this.owner.getEntityId() );
            armorEquipment.setHelmet( this.contents[originalSize] );
            armorEquipment.setChestplate( this.contents[originalSize + 1] );
            armorEquipment.setLeggings( this.contents[originalSize + 2] );
            armorEquipment.setBoots( this.contents[originalSize + 3] );
            playerConnection.addToSendQueue( armorEquipment );
        }*/
    }

}
