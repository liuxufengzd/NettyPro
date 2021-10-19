package liu.code.delegate.dynamicDelegate;

public class ComputeServiceImpl implements ComputeService{
    @Override
    public int compute(int num) {
        return num*2;
    }
}
