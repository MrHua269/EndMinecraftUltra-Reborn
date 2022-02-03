package co.akarin.endminecraftultra.ATTACK;

import com.github.steveice10.packetlib.io.NetInput;

import java.io.IOException;
import java.util.UUID;

public class NETabinput implements NetInput {
    private String string = null;
    public NETabinput(String text){
        this.string = text;
    }
    @Override
    public boolean readBoolean() throws IOException {
        return false;
    }

    @Override
    public byte readByte() throws IOException {
        return 0;
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return 0;
    }

    @Override
    public short readShort() throws IOException {
        return 0;
    }

    @Override
    public int readUnsignedShort() throws IOException {
        return 0;
    }

    @Override
    public char readChar() throws IOException {
        return 0;
    }

    @Override
    public int readInt() throws IOException {
        return 0;
    }

    @Override
    public int readVarInt() throws IOException {
        return 0;
    }

    @Override
    public long readLong() throws IOException {
        return 0;
    }

    @Override
    public long readVarLong() throws IOException {
        return 0;
    }

    @Override
    public float readFloat() throws IOException {
        return 0;
    }

    @Override
    public double readDouble() throws IOException {
        return 0;
    }

    @Override
    public byte[] readBytes(int i) throws IOException {
        return new byte[0];
    }

    @Override
    public int readBytes(byte[] bytes) throws IOException {
        return 0;
    }

    @Override
    public int readBytes(byte[] bytes, int i, int i1) throws IOException {
        return 0;
    }

    @Override
    public short[] readShorts(int i) throws IOException {
        return new short[0];
    }

    @Override
    public int readShorts(short[] shorts) throws IOException {
        return 0;
    }

    @Override
    public int readShorts(short[] shorts, int i, int i1) throws IOException {
        return 0;
    }

    @Override
    public int[] readInts(int i) throws IOException {
        return new int[0];
    }

    @Override
    public int readInts(int[] ints) throws IOException {
        return 0;
    }

    @Override
    public int readInts(int[] ints, int i, int i1) throws IOException {
        return 0;
    }

    @Override
    public long[] readLongs(int i) throws IOException {
        return new long[0];
    }

    @Override
    public int readLongs(long[] longs) throws IOException {
        return 0;
    }

    @Override
    public int readLongs(long[] longs, int i, int i1) throws IOException {
        return 0;
    }

    @Override
    public String readString() throws IOException {
        return this.string;
    }

    @Override
    public UUID readUUID() throws IOException {
        return null;
    }

    @Override
    public int available() throws IOException {
        return 0;
    }
}
