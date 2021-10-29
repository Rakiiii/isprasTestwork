package utils;

import interfaces.IPool;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.WeakHashMap;

/**
 * Оркестратор сетей бассейнов, предполагается что данная сущность хранит всю информаицю о бассейнах соеденненных каналами
 */
public final class PoolsOrchestrator {
    /**
     * Мапа бассейнов к бакетам с водой
     */
    private final WeakHashMap<IPool, PoolBucket> poolMap = new WeakHashMap<>();

    /**
     * Соеденить две сети бассейнов
     */
    public void mergePools(IPool poolFirst, IPool poolSecond) {
        if (poolFirst == poolSecond) {
            if(poolMap.get(poolFirst) != null) return;
            addNewPool(poolFirst);
        }

        final PoolBucket firstBucket = poolMap.get(poolFirst);
        final PoolBucket secondBucket = poolMap.get(poolSecond);

        if (firstBucket == null && secondBucket != null) {
            addNewPoolToOld(poolFirst, secondBucket);
        } else if (secondBucket == null && firstBucket != null) {
            addNewPoolToOld(poolSecond, firstBucket);
            //хак для обноаления флага состояния во втором бассейне
            poolSecond.connect(poolSecond);
        } else if (firstBucket == null) {
            addTwoNewPools(poolFirst, poolSecond);
            //хак для обноаления флага состояния во втором бассейне
            poolSecond.connect(poolSecond);
        } else {
            if (firstBucket == secondBucket) return;

            mergeOldPools(firstBucket, secondBucket);
        }
    }

    private void addNewPool(IPool pool) {
        PoolBucket poolBucket = new PoolBucket(pool.measure());
        poolBucket.addPool(pool);
        poolMap.put(pool, poolBucket);
    }

    /**
     * Соединение 2 неизвестных бассейнов
     */
    private void addTwoNewPools(IPool poolFirst, IPool poolSecond) {
        PoolBucket poolBucket = new PoolBucket(poolFirst.measure());
        poolBucket.addWaterWithPool(poolSecond.measure());
        poolBucket.addPool(poolFirst);
        poolBucket.addPool(poolSecond);
        poolMap.put(poolFirst, poolBucket);
        poolMap.put(poolSecond, poolBucket);
    }

    /**
     * Присоединение нового бассейна к сети
     */
    private void addNewPoolToOld(IPool newPool, PoolBucket oldBucket) {
        oldBucket.addPool(newPool);
        poolMap.put(newPool, oldBucket);
        oldBucket.addWaterWithPool(newPool.measure());
    }

    /**
     * Соединение 2 сетей бассейнов
     */
    private void mergeOldPools(PoolBucket firstBucket, PoolBucket secondBucket) {
        final LinkedList<WeakReference<IPool>> secondBucketArray = secondBucket.getPoolList();
        final LinkedList<WeakReference<IPool>> firstBucketArray = firstBucket.getPoolList();

        if (secondBucketArray.size() > firstBucketArray.size()) {
            optimizedMergePool(firstBucket, secondBucket, firstBucketArray, secondBucketArray);
        } else {
            optimizedMergePool(secondBucket, firstBucket, secondBucketArray, firstBucketArray);
        }
    }

    /**
     * Оптимизированное соединение бассейнов
     *
     * @param smallerBucket бакет соответствующий меньшему числу бассейнов
     * @param biggerBucket  бакет соответствующий большему числу бассейнов
     * @param smallerArray  список бассейнов {@code smallerBucket}
     * @param biggerArray   список бассейнов {@code smallerBucket}
     */
    private void optimizedMergePool(
            PoolBucket smallerBucket,
            PoolBucket biggerBucket,
            LinkedList<WeakReference<IPool>> smallerArray,
            LinkedList<WeakReference<IPool>> biggerArray
    ) {
        biggerBucket.mergeBuckets(smallerBucket);

        for (WeakReference<IPool> pool : smallerArray) {
            biggerArray.add(pool);
            IPool poolRef = pool.get();
            if (poolRef != null) poolMap.put(poolRef, biggerBucket);
        }

        smallerArray.clear();
    }

    /**
     * Получить колличество воды в бассейне
     */
    public long measurePool(IPool pool) {
        return poolMap.get(pool).getAmountOfWater();
    }

    /**
     * Проверить соеденены ли бассейны каналами
     */
    public Boolean arePoolConnected(IPool poolFirst, IPool poolSecond) {
        return poolMap.get(poolFirst) == poolMap.get(poolSecond);
    }

    /**
     * Добавить воды в бассейн
     */
    public void addWaterToPool(IPool pool, long water) {
        poolMap.get(pool).addWater(water);
    }
}
