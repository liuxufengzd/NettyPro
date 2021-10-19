package liu.code.netty.tcp2;
// 利用它明确界定发送/接收数据的边界
public class MessagePackage {
    private int length;
    private byte[] content;

    public MessagePackage() {}

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
}
