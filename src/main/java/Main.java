import interfaces.IPool;
import interfaces.IPoolFactory;
import poolsImpl.GlobalStatePoolImpl;
import poolsImpl.TypeCastWayPoolImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        //PoolFactory factory = PoolSimple::new;
//        IPoolFactory factory = InversedMasterPoolImpl::new;
        IPoolFactory factory = GlobalStatePoolImpl::new;
//        IPoolFactory factory = TypeCastWayPoolImpl::new;
//        IPoolFactory factory = ConnectAndAddHighPerfomancePoolImpl::new;
        //PoolFactory factory = FullGraphPoolImpl::new;

        System.out.println("Heap size: "+Runtime.getRuntime().maxMemory());

        {
            long startTime = System.nanoTime();
            case1(factory);

            long endTime = System.nanoTime();

            System.out.println("Total time: " + (endTime - startTime) / 1000000 + " ms.");
        }

        {
            long startTime = System.nanoTime();
            case2(factory);

            long endTime = System.nanoTime();

            System.out.println("Total time: " + (endTime - startTime) / 1000000 + " ms.");
        }

        {
            long startTime = System.nanoTime();
            case3(factory);

            long endTime = System.nanoTime();

            System.out.println("Total time: " + (endTime - startTime) / 1000000 + " ms.");
        }

        {
            long startTime = System.nanoTime();
            caseB1(factory);

            long endTime = System.nanoTime();

            System.out.println("Total time: " + (endTime - startTime) / 1000000 + " ms.");
        }

        {
            long startTime = System.nanoTime();
            caseB2(factory);

            long endTime = System.nanoTime();

            System.out.println("Total time: " + (endTime - startTime) / 1000000 + " ms.");
        }
    }

    //many connections
    private static void case1(IPoolFactory factory) {
        int N = 10_000_000;
        int K = N / 2;
        int L = 50_000_000;
        int M1 = 2;
        int M2 = 3;

        caseImpl(factory, N, K, L, M1, M2);
    }

    //many adds
    private static void case2(IPoolFactory factory) {
        int N = 10_000_000;
        int K = N / 10;
        int L = 100_000_000;
        int M1 = 2;
        int M2 = 3;

        caseImpl(factory, N, K, L, M1, M2);
    }

    //small connections
    private static void case3(IPoolFactory factory) {
        int N = 10_000_000;
        int K = N / 1000;
        int L = 200_000_000;
        int M1 = 2;
        int M2 = 3;

        caseImpl(factory, N, K, L, M1, M2);
    }

    private static void caseB1(IPoolFactory factory) {
        int N = 1_000_000;
        int K = N / 1000; //channels
        int KL = 5000; //chanels length
        int L = 5_000_000;
        int M1 = 2;
        int M2 = 3;

        caseBImpl(factory, N, K, KL, L, M1, M2);
    }

    private static void caseB2(IPoolFactory factory) {
        int N = 10_000_000;
        int K = N / 1000; //channels
        int KL = 5000; //chanels length
        int L = 50_000_000;
        int M1 = 2;
        int M2 = 3;

        caseBImpl(factory, N, K, KL, L, M1, M2);
    }

    private static Random rnd = new Random();

    private static int getRnd(int from, int to) {
        int bound = to - from;
        return from + (rnd.nextInt(bound));
    }

    private static void caseImpl(IPoolFactory factory,
                                 int N, int K, int L, int M1, int M2) {
        int a = 10;
        int b = 1000;

        System.out.println("Start");

        List<IPool> pools = new ArrayList<>();

        //operation 1: create pools
        for(int i=0;i<N;i++) {
            pools.add(factory.create());
        }

        System.out.println("Pools are created");

        Random rnd = new Random();

        //operation 2: add water
        for (IPool pool : pools) {
            int water = getRnd(a, b);
            pool.add(water);
        }

        System.out.println("Water is added");

        //operation 3: connect pools
        for (int i=0;i<K;++i) {
            int pool1 = getRnd(0, N - 1);
            int pool2 = getRnd(0, N - 1);
            pools.get(pool1).connect(pools.get(pool2));
        }

        System.out.println("Pools are connected");

        for(int i=0;i<M1;++i) {
            for (IPool cur : pools) {
                cur.measure();
            }
        }

        System.out.println("Pools are measured");

        //operation 4: add water
        for (int i=0;i<L;++i) {
            int pool = getRnd(0, N - 1);
            int water = getRnd(a, b);

            pools.get(pool).add(water);
        }

        System.out.println("Water is added again");

        for(int i=0;i<M2;++i) {
            for (IPool cur : pools) {
                cur.measure();
            }
        }

        System.out.println("End");
    }

    private static void caseBImpl(IPoolFactory factory,
                                  int N, int K, int KL, int L, int M1, int M2) {
        int a = 10;
        int b = 1000;

        System.out.println("Start");

        List<IPool> pools = new ArrayList<>();

        //operation 1: create pools
        for(int i=0;i<N;i++) {
            pools.add(factory.create());
        }

        System.out.println("Pools are created");

        Random rnd = new Random();

        //operation 2: add water
        for (IPool pool : pools) {
            int water = getRnd(a, b);
            pool.add(water);
        }

        System.out.println("Water is added");

        //operation 3: connect pools
        for (int i=0;i<K;++i) {
            int chanelLen = getRnd(KL/2, KL);
            int pool = getRnd(0, N - 1);

            for(int k=0;k<chanelLen;++k) {
                int nextPool = getRnd(0, N - 1);
                pools.get(pool).connect(pools.get(nextPool));
                pool = nextPool;
            }

            if (i%100==1)
                System.out.println("i=" + i + "/" + K);
        }

        System.out.println("Pools are connected");

        for(int i=0;i<M1;++i) {
            for (IPool cur : pools) {
                cur.measure();
            }
        }

        System.out.println("Pools are measured");

        //operation 4: add water
        for (int i=0;i<L;++i) {
            int pool = getRnd(0, N - 1);
            int water = getRnd(a, b);

            pools.get(pool).add(water);
        }

        System.out.println("Water is added again");

        for(int i=0;i<M2;++i) {
            for (IPool cur : pools) {
                cur.measure();
            }
        }

        System.out.println("End");
    }
}
