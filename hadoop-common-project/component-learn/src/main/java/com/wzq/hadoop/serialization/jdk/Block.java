package com.wzq.hadoop.serialization.jdk;

import java.io.Serializable;

/**
 * 需要序列化的对象
 *
 * @author wzq
 * @create 2023-04-12 14:42
 */
public class Block implements Serializable {

    private long blockId;
    private long numBytes;
    private long generationStamp;

    public Block(long blockId, long numBytes, long generationStamp) {
        this.blockId = blockId;
        this.numBytes = numBytes;
        this.generationStamp = generationStamp;
    }

    public Block() {
    }

    @Override
    public String toString() {
        return "Block{" +
                "blockId=" + blockId +
                ", numBytes=" + numBytes +
                ", generationStamp=" + generationStamp +
                '}';
    }

    public long getBlockId() {
        return blockId;
    }

    public void setBlockId(long blockId) {
        this.blockId = blockId;
    }

    public long getNumBytes() {
        return numBytes;
    }

    public void setNumBytes(long numBytes) {
        this.numBytes = numBytes;
    }

    public long getGenerationStamp() {
        return generationStamp;
    }

    public void setGenerationStamp(long generationStamp) {
        this.generationStamp = generationStamp;
    }
}
