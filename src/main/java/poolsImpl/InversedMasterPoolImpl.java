package poolsImpl;

import interfaces.IPool;


import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

/**
 * Алгоримт в этой имплементации такой
 * бассейн на котором вызвали connect передает все свои дочерние элементы в присоеденяемыйй бассейн
 */
public class InversedMasterPoolImpl implements IPool {

    /**
     * Сеть объедененных каналми бассейнов
     */
    private final Set<IPool> connectedPools = new HashSet<>();

    /**
     * Ссылка на центральный бассейн с точки зрения топологии, если {@code null} то данный бассейн является центральным
     */
    private IPool masterPool = null;

    /**
     * Флаг для разрыва рекурсии при соединении двух сетей
     */
    private long oldSize = 0;

    private IPool oldMasterPool = null;

    private IPool transitionMasterPool = null;

    /**
     * Колличество воды в бассейне в данный момент
     */
    private long amountOfWater = 0;

    /**
     * @inheritDoc
     */
    @Override
    public long measure() {
        return masterPool == null ? amountOfWater : masterPool.measure();
    }

    /**
     * @inheritDoc
     */
    @Override
    public void connect(IPool pool) {
        if (pool == null || pool == this || pool == masterPool) return;
        if (masterPool == null && connectedPools.contains(pool)) return;

        if (masterPool == null) {
            masterConnect(pool);
        } else {
            slaveConnect(pool);
        }
    }

    private void slaveConnect(IPool pool) {
        final Boolean isMasterPoolConnected = masterPool.isConnected(pool);

        //Возврат null в методе isConnect на мастер бассейне означает, что сеть в процессе слияния
        if (isMasterPoolConnected == null) {
            masterPool = pool;
        } else if (isMasterPoolConnected.equals(Boolean.FALSE)) {
            masterPool.connect(pool);
        }
    }

    private void masterConnect(IPool pool) {

        if (transitionMasterPool != null) {
            masterPool = pool;
        } else if (oldMasterPool != null && pool != oldMasterPool) {
            connectedPools.add(pool);
            pool.connect(this);
        } else if (oldMasterPool == pool) {
            stopStarTransition(pool);
        } else if (pool.isConnected(this) == null) {
            oldSize = connectedPools.size();
            oldMasterPool = pool;
        } else {
            newMasterPoolConnect(pool);
        }
    }

    /**
     * Метод вызываемый для присоединения старой сети бассейнов к новой, вызывается только на новом master бассейне
     *
     * @param pool бассейн для присоедениениея
     */
    private void newMasterPoolConnect(IPool pool) {
        transitionMasterPool = pool;

        pool.connect(this);


        connectedPools.forEach((IPool p)->pool.connect(p));

        connectedPools.clear();

        pool.connect(this);

        transitionMasterPool = null;
        amountOfWater = 0L;
    }

    /**
     * Метод вызываемый для переприсоединения старых вершин звезды к новому master бассейну
     * {@code this} всегда старый master бассейн
     *
     * @param pool бассейн для присоедениениея, новый master бассейн
     */
    private void stopStarTransition(IPool pool) {
        pool.connect(this);
        oldMasterPool = null;

        connectedPools.add(pool);

        final long newMeshMeasure = pool.measure();
        final long sizeDif = connectedPools.size() - oldSize;

        amountOfWater = calculateNewAmount(sizeDif, oldSize, newMeshMeasure);
    }

    /**
     * Расчитывает новое значени объема воды с учетом возможных переполнений лонга в процессе
     *
     * @param sizeDiff   размер добавленной сети
     * @param oldSize    размер сети до добавления нового бассейна
     * @param newMeasure объем воды в старой сети
     * @return возвращает новый объем воды для сети
     */
    private long calculateNewAmount(long sizeDiff, long oldSize, long newMeasure) {

        final long meshSize = connectedPools.size() + 1;

        try {

            return Math.addExact(Math.multiplyExact(amountOfWater, oldSize + 1), Math.multiplyExact(newMeasure, sizeDiff)) / meshSize;

        } catch (ArithmeticException exception) {

            //если лонги перепонились то только BigInt
            BigInteger amountOfWaterBI = new BigInteger(String.valueOf(amountOfWater));
            BigInteger newMeasureBI = new BigInteger(String.valueOf(newMeasure));
            BigInteger oldSizeBI = new BigInteger(String.valueOf(oldSize + 1));
            BigInteger sizeDiffBI = new BigInteger(String.valueOf(sizeDiff));
            BigInteger meshSizeBI = new BigInteger(String.valueOf(meshSize));

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

    /**
     * @inheritDoc
     */
    @Override
    public void add(long water) {

        if (water == 0) return;

        if (masterPool != null) {
            masterPool.add(water);
        } else {
            try {
                amountOfWater = Math.addExact(amountOfWater, water / (connectedPools.size() + 1));
            } catch (ArithmeticException exception) {
                amountOfWater = Long.MAX_VALUE;
            }
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public Boolean isConnected(IPool pool) {
        if (pool == null) return Boolean.FALSE;
        if (transitionMasterPool != null) {
            return null;
        } else if (pool == masterPool) {
            return Boolean.TRUE;
        } else {
            return internalIsConnect(pool);
        }
    }

    private Boolean internalIsConnect(IPool pool) {
        if (masterPool != null) {
            return masterPool.isConnected(pool);
        } else {
            return connectedPools.contains(pool);
        }
    }
}

