/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world.leveldb;

import io.gomint.jraknet.PacketBuffer;
import io.gomint.leveldb.DB;
import io.gomint.leveldb.WriteBatch;
import io.gomint.math.MathUtils;
import io.gomint.server.entity.Entity;
import io.gomint.server.entity.tileentity.SerializationReason;
import io.gomint.server.entity.tileentity.TileEntities;
import io.gomint.server.entity.tileentity.TileEntity;
import io.gomint.server.util.Allocator;
import io.gomint.server.util.BlockIdentifier;
import io.gomint.server.util.Palette;
import io.gomint.server.world.BlockRuntimeIDs;
import io.gomint.server.world.ChunkAdapter;
import io.gomint.server.world.ChunkSlice;
import io.gomint.server.world.WorldAdapter;
import io.gomint.taglib.NBTReader;
import io.gomint.taglib.NBTReaderNoBuffer;
import io.gomint.taglib.NBTTagCompound;
import io.gomint.taglib.NBTWriter;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;

/**
 * @author geNAZt
 * @version 1.0
 */
public class LevelDBChunkAdapter extends ChunkAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger( LevelDBChunkAdapter.class );
    private int chunkVersion;

    /**
     * Create a new level db backed chunk
     *
     * @param worldAdapter which loaded this chunk
     * @param x            position of chunk
     * @param z            position of chunk
     * @param chunkVersion version of this chunk
     * @param populated    true when chunk is already populated, false when not
     */
    public LevelDBChunkAdapter( WorldAdapter worldAdapter, int x, int z, byte chunkVersion, boolean populated ) {
        super( worldAdapter, x, z );
        this.chunkVersion = chunkVersion;
        this.setPopulated( populated );
        this.loadedTime = worldAdapter.getServer().getCurrentTickTime();
    }

    public LevelDBChunkAdapter( WorldAdapter worldAdapter, int x, int z ) {
        super( worldAdapter, x, z );
        this.chunkVersion = 7;

        this.loadedTime = worldAdapter.getServer().getCurrentTickTime();
    }

    void save( DB db ) {
        WriteBatch writeBatch = new WriteBatch();

        // We do blocks first
        for ( int i = 0; i < this.chunkSlices.length; i++ ) {
            if ( this.chunkSlices[i] == null ) {
                continue;
            }

            saveChunkSlice( i, writeBatch );
        }

        // Safe tiles
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        NBTWriter nbtWriter = new NBTWriter( baos, ByteOrder.LITTLE_ENDIAN );
        for ( TileEntity tileEntity : this.getTileEntities() ) {
            NBTTagCompound compound = new NBTTagCompound( "" );
            tileEntity.toCompound( compound, SerializationReason.PERSIST );

            try {
                nbtWriter.write( compound );
            } catch ( IOException e ) {
                LOGGER.warn( "Could not write tile to leveldb", e );
            }
        }

        if ( baos.size() > 0 ) {
            ByteBuf key = ( (LevelDBWorldAdapter) this.world ).getKey( this.x, this.z, (byte) 0x31 );
            ByteBuf val = Allocator.allocate( baos.toByteArray() );

            writeBatch.put( key, val );
        }

        db.write( writeBatch );
        writeBatch.clear();
        writeBatch.close();
    }

    private void saveChunkSlice( int i, WriteBatch writeBatch ) {
        ChunkSlice slice = this.chunkSlices[i];
        PacketBuffer buffer = new PacketBuffer( 16 );

        buffer.writeByte( (byte) 8 );
        buffer.writeByte( (byte) slice.getAmountOfLayers() );

        for ( int layer = 0; layer < slice.getAmountOfLayers(); layer++ ) {
            List<BlockIdentifier> blocks = slice.getBlocks( layer );

            // Count how many unique blocks we have in this chunk
            int[] indexIDs = new int[4096];

            LongList indexList = new LongArrayList();
            IntList runtimeIndex = new IntArrayList();
            Int2ObjectMap<BlockIdentifier> block = new Int2ObjectOpenHashMap<>();

            int foundIndex = 0;

            String lastBlockId = "";
            short lastDataId = -1;

            int runtimeIdCounter = 0;

            for ( short blockIndex = 0; blockIndex < indexIDs.length; blockIndex++ ) {
                String blockId = blocks.get( blockIndex ).getBlockId();
                short blockData = blocks.get( blockIndex ).getData();

                if ( !blockId.equals( lastBlockId ) || blockData != lastDataId ) {
                    long hashId = ( (long) blockId.hashCode() ) << 32 | ( blockData & 0xFF );

                    foundIndex = indexList.indexOf( hashId );
                    if ( foundIndex == -1 ) {
                        int runtimeId = runtimeIdCounter++;
                        block.put( runtimeId, blocks.get( blockIndex ) );
                        runtimeIndex.add( runtimeId );
                        indexList.add( hashId );
                        foundIndex = indexList.size() - 1;
                    }

                    lastBlockId = blockId;
                    lastDataId = blockData;
                }

                indexIDs[blockIndex] = foundIndex;
            }

            // Get correct wordsize
            int value = indexList.size();
            int numberOfBits = MathUtils.fastFloor( MathUtils.log2( value ) ) + 1;

            // Prepare palette
            int amountOfBlocks = MathUtils.fastFloor( 32f / (float) numberOfBits );

            buffer.writeByte( (byte) 8 );
            buffer.writeByte( (byte) 1 );

            Palette palette = new Palette( buffer, amountOfBlocks, false );

            byte paletteWord = (byte) ( (byte) ( palette.getPaletteVersion().getVersionId() << 1 ) | 1 );
            buffer.writeByte( paletteWord );
            palette.addIndexIDs( indexIDs );
            palette.finish();

            // Write persistant ids
            buffer.writeLInt( indexList.size() );
            for ( int value1 : runtimeIndex.toArray( new int[0] ) ) {
                BlockIdentifier blockIdentifier = block.get( value1 );

                NBTTagCompound compound = new NBTTagCompound( "" );
                compound.addValue( "name", blockIdentifier.getBlockId() );
                compound.addValue( "val", blockIdentifier.getData() );

                try {
                    compound.writeTo( new OutputStream() {
                        @Override
                        public void write( byte[] b, int off, int len ) throws IOException {
                            byte[] finalBytes = new byte[len];
                            System.arraycopy( b, off, finalBytes, 0, len );
                            buffer.writeBytes( finalBytes );
                        }

                        @Override
                        public void write( int b ) throws IOException {
                            buffer.writeByte( (byte) b );
                        }
                    }, false, ByteOrder.LITTLE_ENDIAN );
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
            }
        }

        ByteBuf key = ( (LevelDBWorldAdapter) this.world ).getKeySubChunk( this.x, this.z, (byte) 0x27, (byte) i );
        ByteBuf val = Allocator.allocate( Arrays.copyOf( buffer.getBuffer(), buffer.getPosition() ) );

        writeBatch.put( key, val );
    }

    void loadSection( int sectionY, byte[] chunkData ) {
        PacketBuffer buffer = new PacketBuffer( chunkData, 0 );

        // First byte is chunk section version
        byte subchunkVersion = buffer.readByte();
        int storages = 1;
        switch ( subchunkVersion ) {
            case 8:
                storages = buffer.readByte();
            case 1:
                for ( int sI = 0; sI < storages; sI++ ) {
                    byte data = buffer.readByte();
                    boolean isPersistent = ( ( data >> 8 ) & 1 ) != 1; // last bit is the isPresent state (shift and mask it to 1)
                    byte wordTemplate = (byte) ( data >>> 1 ); // Get rid of the last bit (which seems to be the isPresent state)

                    Palette palette = new Palette( buffer, wordTemplate, true );
                    short[] indexes = palette.getIndexes();

                    // Read NBT data
                    int needed = buffer.readLInt();
                    Int2IntMap chunkPalette = new Int2IntOpenHashMap( needed ); // Varint my ass

                    int index = 0;
                    NBTReaderNoBuffer reader = new NBTReaderNoBuffer( new InputStream() {
                        @Override
                        public int read() throws IOException {
                            return buffer.readByte();
                        }

                        @Override
                        public int available() throws IOException {
                            return buffer.getRemaining();
                        }
                    }, ByteOrder.LITTLE_ENDIAN );
                    while ( index < needed ) {
                        try {
                            NBTTagCompound compound = reader.parse();
                            String blockId = compound.getString( "name", "minecraft:air" );
                            short blockData = compound.getShort( "val", (short) 0 );

                            chunkPalette.put( index++, BlockRuntimeIDs.from( blockId, blockData ) );
                        } catch ( IOException e ) {
                            LOGGER.error( "Error in loading tile entities", e );
                            break;
                        }
                    }

                    ChunkSlice slice = this.ensureSlice( sectionY );
                    for ( short i = 0; i < indexes.length; i++ ) {
                        int runtimeID = chunkPalette.get( indexes[i] );
                        slice.setRuntimeIdInternal( i, sI, runtimeID );
                    }
                }

                break;
        }
    }

    void loadTileEntities( byte[] tileEntityData ) {
        ByteArrayInputStream bais = new ByteArrayInputStream( tileEntityData );
        NBTReader nbtReader = new NBTReader( bais, ByteOrder.LITTLE_ENDIAN );
        while ( nbtReader.hasMoreToRead() ) {
            TileEntity tileEntity = null;

            try {
                NBTTagCompound compound = nbtReader.parse();

                tileEntity = TileEntities.construct( compound, this.world );
                if ( tileEntity != null ) {
                    this.addTileEntity( tileEntity );
                }
            } catch ( Exception e ) {
                LOGGER.error( "Error in loading tile entities", e );
                break;
            }
        }
    }

    void loadEntities( byte[] entityData ) {
        ByteArrayInputStream bais = new ByteArrayInputStream( entityData );
        NBTReader nbtReader = new NBTReader( bais, ByteOrder.LITTLE_ENDIAN );
        while ( nbtReader.hasMoreToRead() ) {
            try {
                NBTTagCompound compound = nbtReader.parse();
                Integer id = compound.getInteger( "id", 0 );
                Entity entity = this.world.getServer().getEntities().create( id & 0xFF );
                if ( entity != null ) {
                    entity.initFromNBT( compound );
                }
            } catch ( IOException e ) {
                LOGGER.error( "Error in loading entities", e );
                break;
            }
        }
    }

}
