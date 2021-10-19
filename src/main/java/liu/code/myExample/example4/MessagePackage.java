package liu.code.myExample.example4;

import java.util.Arrays;

public class MessagePackage {
    private int length;
    private byte[] content;

    public MessagePackage() {
    }

    public MessagePackage(int length, byte[] content) {
        this.length = length;
        this.content = content;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "MessagePackage{" +
                "length=" + length +
                ", content=" + Arrays.toString(content) +
                '}';
    }
}
