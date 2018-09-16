/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.entity.tileentity;

import io.gomint.math.Location;
import io.gomint.server.inventory.item.Items;
import io.gomint.server.world.WorldAdapter;
import io.gomint.taglib.NBTTagCompound;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author geNAZt
 * @version 1.0
 */
public class BannerTileEntity extends TileEntity {

    private int baseColor;
    private Map<String, Integer> patterns = new LinkedHashMap<>();

    public BannerTileEntity( Location location ) {
        super( location );
    }

    public BannerTileEntity( NBTTagCompound compound, WorldAdapter worldAdapter, Items items ) {
        super( compound, worldAdapter, items );

        this.baseColor = compound.getInteger( "Base", 0 );

        List<Object> patterns = compound.getList( "Patterns", false );
        if ( patterns != null ) {
            for ( Object pattern : patterns ) {
                NBTTagCompound patternCompound = (NBTTagCompound) pattern;
                this.patterns.put( patternCompound.getString( "Pattern", "ss" ), patternCompound.getInteger( "Color", 0 ) );
            }
        }
    }

    @Override
    public void update( long currentMillis ) {

    }

    @Override
    public void toCompound( NBTTagCompound compound, SerializationReason reason ) {
        super.toCompound( compound, reason );

        compound.addValue( "id", "Banner" );
        compound.addValue( "Base", this.baseColor );

        if ( this.patterns.size() > 0 ) {
            List<Object> patterns = compound.getList( "Patterns", true );
            for ( Map.Entry<String, Integer> entry : this.patterns.entrySet() ) {
                NBTTagCompound patternCompound = new NBTTagCompound( "" );
                patternCompound.addValue( "Pattern", entry.getKey() );
                patternCompound.addValue( "Color", entry.getValue() );

                patterns.add( patternCompound );
            }
        }
    }

}
