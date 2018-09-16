/*
 * Copyright (c) 2018, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world.converter.anvil.tileentity.v1_8;

import io.gomint.math.Location;
import io.gomint.server.entity.tileentity.BedTileEntity;
import io.gomint.server.entity.tileentity.CommandBlockTileEntity;
import io.gomint.server.inventory.item.Items;
import io.gomint.taglib.NBTTagCompound;
import io.gomint.world.block.data.BlockColor;
import it.unimi.dsi.fastutil.objects.Object2IntMap;

/**
 * @author geNAZt
 * @version 1.0
 */
public class ControlConverter extends BasisConverter<CommandBlockTileEntity> {

    public ControlConverter( Items items, Object2IntMap<String> itemConverter ) {
        super( items, itemConverter );
    }

    @Override
    public CommandBlockTileEntity readFrom( NBTTagCompound compound ) {
        return new CommandBlockTileEntity( compound, null, this.items );
    }

}