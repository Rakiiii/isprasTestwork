package utils;

import interfaces.IPool;

import java.lang.ref.WeakReference;
import java.math.BigInteger;
import java.util.LinkedList;

/**
 * Представление воды в сети бассейнов соеденненых каналами
 */
public class PoolBucket {

    /**
     * Для оптимизации {@code add}
     */
    private long addWaterBuffer = 0;

    /**
     * Колличество воды в сети
     */
    private long amountOfWater;

    /**
     * Колличество бассейнов в сети
     */
    private long amountOfPools = 1;

    private final LinkedList<WeakReference<IPool>> poolList = new LinkedList<>();

    public LinkedList<WeakReference<IPool>> getPoolList() {
        return poolList;
    }

    public PoolBucket(long water) {
        amountOfWater = water;
    }

    public void addPool(IPool pool) {
        poolList.add(new WeakReference<>(pool));
    }

    public int sizeOfPoolList() {
        return poolList.size();
    }

    /**
     * Геттер для {@code amountOfWater}
     */
    public long getAmountOfWater() {
        if (addWaterBuffer != 0) {
            realAddWater(addWaterBuffer);
            addWaterBuffer = 0;
        }
        return amountOfWater;
    }

    /**
     * Добавить в сеть бассейн для которого не создавался PoolBucket
     */
    public void addWaterWithPool(long water) {
        amountOfWater = calculateNewAmount(1, water);
        amountOfPools++;
    }

    /**
     * Добавить воды в сеть
     */
    public void addWater(long water) {
        try {
            addWaterBuffer = Math.addExact(addWaterBuffer, water);
        } catch (ArithmeticException exception) {
            realAddWater(addWaterBuffer);
            realAddWater(water);
            addWaterBuffer = 0;
        }
    }

    private void realAddWater(long water) {
        try {
            amountOfWater = Math.addExact(amountOfWater, water / amountOfPools);
        } catch (ArithmeticException exception) {
            amountOfWater = Long.MAX_VALUE;
        }
    }

    /**
     * Присоеденить {@code bucket} к текущей сети
     */
    public void mergeBuckets(PoolBucket bucket) {
        amountOfWater = calculateNewAmount(bucket.amountOfPools, bucket.amountOfWater);
        amountOfPools += bucket.amountOfPools;
    }

    /**
     * Пересчитать объем воды в сети после присоеденения еще 1 сети
     *
     * @param newBucketSize          размер присоедененной сети
     * @param newBucketAmountOfWater объем воды в присоедененной сети
     * @return объем воды в объедененной сети
     */
    private long calculateNewAmount(long newBucketSize, long newBucketAmountOfWater) {

        final long meshSize = amountOfPools + newBucketSize;

        try {

            return Math.addExact(Math.multiplyExact(amountOfWater, amountOfPools), Math.multiplyExact(newBucketAmountOfWater, newBucketSize)) / meshSize;

        } catch (ArithmeticException exception) {

            //если лонги перепонились то только BigInt
            BigInteger amountOfWaterBI = new BigInteger(String.valueOf(amountOfWater));
            BigInteger newMeasureBI = new BigInteger(String.valueOf(newBucketAmountOfWater));
            BigInteger oldSizeBI = new BigInteger(String.valueOf(amountOfPools));
            BigInteger sizeDiffBI = new BigInteger(String.valueOf(newBucketSize));
            BigInteger meshSizeBI = new BigInteger(String.valueOf(meshSize));

            //тут переполнение невозможно исходя из семантики, какие 2 сети бассейнов не соеденяй переполнения получить не выйдет
            return (
                    (amountOfWaterBI
                            .multiply(oldSizeBI)
                            .divide(meshSizeBI)
                    ).add(
                            newMeasureBI
                                    .multiply(sizeDiffBI)
                                    .divide(meshSizeBI)
                    )
            ).longValue();
        }
    }

}
