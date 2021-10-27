import cases.CaseClass;
import factory.PoolFactoryImpl;
import interfaces.IPoolFactory;

public class Main {
    public static void main(String[] args) {
        IPoolFactory poolFactory = new PoolFactoryImpl();

        CaseClass caseImpl = new CaseClass();

        caseImpl.harcodeDebug(poolFactory);
        caseImpl.caseImpl(poolFactory,10_000_000,5_000_000,50_000_000,2,3);

        System.exit(0);
    }
}
