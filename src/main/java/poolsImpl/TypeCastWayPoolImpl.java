package poolsImpl;

import interfaces.IPool;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

/**
 * Имплементация на основе typecast {@code IPool} под капотом до TypeCastWayPoolImpl
 */
public class TypeCastWayPoolImpl implements IPool {

    /**
     * Сеть объедененных каналми бассейнов
     */
    private final Set<TypeCastWayPoolImpl> connectedPools = new HashSet<>();

    /**
     * Ссылка на центральный бассейн с точки зрения топологии, если {@code null} то данный бассейн является центральным
     */
    private TypeCastWayPoolImpl masterPool = null;

    /**
     * Колличество воды в бассейне в данный момент
     */
    private long amountOfWater = 0;

    /**
     * @inheritDoc
     */
    @Override
    public long measure() {
        return masterPool == null ? amountOfWater : masterPool.amountOfWater;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void connect(IPool pool) {
        if (pool == this || pool == masterPool || !(pool instanceof TypeCastWayPoolImpl)) return;

        realConnect((TypeCastWayPoolImpl) pool);
    }

    /**
     * Добавление бассена с резолвленного типа
     *
     * @param pool бассейн
     */
    private void realConnect(TypeCastWayPoolImpl pool) {
        if (masterPool == null && connectedPools.contains(pool)) return;

        if (masterPool == null) {
            if (pool.masterPool == null) {
                masterConnect(pool);
            } else {
                realConnect(pool.masterPool);
            }
        } else {
            masterPool.realConnect(pool);
        }
    }

    private void masterConnect(TypeCastWayPoolImpl pool) {
        final long oldSize = connectedPools.size();
        pool.connectedPools.forEach((TypeCastWayPoolImpl p) -> {
            p.masterPool = this;
            connectedPools.add(p);
        });
        connectedPools.add(pool);
        pool.masterPool = this;
        amountOfWater = calculateNewAmount(pool.connectedPools.size() + 1, oldSize, pool.amountOfWater);
        pool.connectedPools.clear();
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
            masterPool.realAdd(water);
        } else {
            realAdd(water);
        }
    }

    private void realAdd(long water) {
        try {
            amountOfWater = Math.addExact(amountOfWater, water / (connectedPools.size() + 1));
        } catch (ArithmeticException exception) {
            amountOfWater = Long.MAX_VALUE;
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public Boolean isConnected(IPool pool) {
        if (pool instanceof TypeCastWayPoolImpl)
            return pool == masterPool || (masterPool == null ? connectedPools.contains(pool) : masterPool.isConnected(pool));
        return false;
    }

}