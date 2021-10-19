package liu.code.myExample.example6.server;

public class CommonServiceImpl implements CommonService {
    @Override
    public Integer calSum(int num1, int num2) {
        return num1 + num2;
    }

    @Override
    public String upperString(String data) {
        return data.toUpperCase();
    }
}
