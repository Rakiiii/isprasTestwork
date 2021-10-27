package cases;

import interfaces.IPool;
import interfaces.IPoolFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Класс с кодом из письма
 */
public final class CaseClass {

    private static final Random rnd = new Random();

    private static int getRnd(int from, int to) {
        int bound = to - from;
        return from + (rnd.nextInt(bound));
    }

    private static int getRndOdd(int from, int to) {
        int bound = to - from;
        int value = from + (rnd.nextInt(bound));
        return value + (value % 2);
    }

    /**
     * Метод базовой проверки
     * @param factory фабрика бассейнов
     * @param N колличество бассейнов для создания
     * @param K колличество каналов
     * @param L колличество добавлений воды
     * @param M1 колличетво измерений первой итерации
     * @param M2 колличество измерений второй итерации
     */
    public void caseImpl(IPoolFactory factory,
                  int N, int K, int L, int M1, int M2) {
        int a = 10;
        int b = 1000;

        //note: measure number is (M + measure1?K:0 + measure2?L:0).

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

    public void harcodeDebug(IPoolFactory factory) {
        List<IPool> pools = new ArrayList<>();

        //operation 1: create pools
        for(int i=0;i<6;i++) {
            pools.add(factory.create());
        }

        //operation 2: add water
        for (IPool pool : pools) {
            int water = getRndOdd(1, 10);
            pool.add(water);
        }

        pools.get(0).connect(pools.get(1));
        pools.get(2).connect(pools.get(3));
        pools.get(2).connect(pools.get(4));
        pools.get(0).connect(pools.get(2));
        pools.get(0).connect(pools.get(5));

        for (IPool cur : pools) {
            System.out.println(cur.measure());
        }

        pools.get(0).add(0);
        for (IPool pool : pools) {
            final long water = getRnd(1,10);
            pool.add(water);
        }

        for (IPool cur : pools) {
            System.out.println(cur.measure());
        }
    }

    private static final long NOT_INITED = -1;
}
