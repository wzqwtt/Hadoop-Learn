package com.wzq.hadoop.io.entity;

import com.wzq.hadoop.io.Writeable;
import lombok.Data;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @author wzq
 * @create 2023-04-12 21:13
 */
@Data
public class Block implements Writeable {

    private long blockId;
    private long numBytes;
    private long generationStamp;

    public Block() {
    }

    public Block(long blockId, long numBytes, long generationStamp) {
        this.blockId = blockId;
        this.numBytes = numBytes;
        this.generationStamp = generationStamp;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeLong(blockId);
        out.writeLong(numBytes);
        out.writeLong(generationStamp);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        blockId = in.readLong();
        numBytes = in.readLong();
        generationStamp = in.readLong();
    }

    public static Block read(DataInput in) throws IOException {
        Block block = new Block();
        block.readFields(in);
        return block;
    }

    @Override
    public String toString() {
        return "Block{" +
                "blockId=" + blockId +
                ", numBytes=" + numBytes +
                ", generationStamp=" + generationStamp +
                '}';
    }
}
