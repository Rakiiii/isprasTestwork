import cases.CaseClass;
import factory.PoolFactoryImpl;
import interfaces.IPoolFactory;

public class Main {
    public static void main(String[] args) {
        IPoolFactory poolFactory = new PoolFactoryImpl();

        CaseClass caseImpl = new CaseClass();

        caseImpl.harcodeDebug(poolFactory);
        caseImpl.caseImpl(poolFactory,4,5,2,2,2);

        System.exit(0);
    }
}
