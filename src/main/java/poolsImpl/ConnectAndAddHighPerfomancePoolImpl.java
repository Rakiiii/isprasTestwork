package poolsImpl;

import interfaces.IPool;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

/**
 * Производительная имплементация {@code IPool}
 *
 *  Фактически вся задача сводится к разрыву рекурсии при балансировке сети бассейнов,
 *  применяется подход, при котором балансировкой воды фактически занимается 1 master бассейн,
 *
 *  для реализации такого подхода к балансировке необходимо, чтобы master бассейн знал о всех бассейнах в сети
 *  так как master бассейн в каждый период времени 1 то фатически получаем топологиию типа звезда, где master бассейн
 *  есть центральная нода, таким образом задача сводиться к соединению 2 топлогий типа звезда при вызове connect
 */
public class ConnectAndAddHighPerfomancePoolImpl implements IPool {

    /** Сеть объедененных каналми бассейнов */
    private final Set<IPool> connectedPools = new HashSet<>();

    /** Ссылка на центральный бассейн с точки зрения топологии, если {@code null} то данный бассейн является центральным */
    private IPool masterPool = null;

    /** Флаг для разрыва рекурсии при соединении двух сетей */
    private boolean isInDomination = false;

    /** Колличество воды в бассейне в данный момент */
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
        if (pool == this) return;
        if(pool == masterPool)return;
        if (connectedPools.contains(pool)) return;
        // Если пользователь  добавляет новый бассейн не к master бассейну то переадресуем добавление
        if (masterPool != null && !pool.isConnected(masterPool)){
            pool.connect(masterPool);
            return;
        }

        final boolean isConnected =
                //елси пользователь вызвал присоедение с master бассейном то это true
                pool.isConnected(this) ||
                        //если пользователь вызвал соединение не с master бассейном то это true
                        connectedPools.stream().anyMatch(pool::isConnected);

        if(masterPool == null && !isConnected && !isInDomination) {
            //секция 1: вызывается при добавлении новой сети пользователем
            isInDomination = true;
            final long newMeshMeasure = pool.measure();
            final long oldSize = connectedPools.size();

            connectedPools.add(pool);
            pool.connect(this);

            final long sizeDif = connectedPools.size() - oldSize;

             amountOfWater = calculateNewAmount(sizeDif,oldSize,newMeshMeasure);

            isInDomination = false;
        } else if (masterPool == null && !isInDomination) {
            //секция 2:вызываемая для старого master бассейна в сети
            //по факту происходит пересоединения всех старых листов к новому центру
            masterPool = pool;

            connectedPools.forEach(pool::connect);

            connectedPools.clear();
            amountOfWater = 0;
        } else if (masterPool == null){
            //секция 3: вызываемая когда пытаемся добавить новый лист от старого master бассейна
            connectedPools.add(pool);
            pool.connect(this);
        } else if(isConnected && !masterPool.isConnected(pool)) {
            //секуция 4: вызывается если в секции 1 был не master бассейн
            //просто переадресует вызов в секцию 2 для старого master бассейна
            masterPool.connect(pool);
        }else{
            masterPool = pool;
        }
    }

    /**
     * Расчитывает новое значени объема воды с учетом возможных переполнений лонга в процессе
     *
     * @param sizeDiff размер добавленной сети
     * @param oldSize размер сети до добавления нового бассейна
     * @param newMeasure объем воды в старой сети
     *
     * @return возвращает новый объем воды для сети
     */
    private long calculateNewAmount(long sizeDiff,long oldSize, long newMeasure) {
        final long meshSize = connectedPools.size()+1;
        try {
            return Math.addExact(Math.multiplyExact(amountOfWater,oldSize+1),  Math.multiplyExact(newMeasure,sizeDiff))/meshSize;
        } catch (ArithmeticException exception) {
            //если лонги перепонились то только BigInt
            BigInteger amountOfWaterBI = new BigInteger(String.valueOf(amountOfWater));
            BigInteger newMeasureBI = new BigInteger(String.valueOf(newMeasure));
            BigInteger oldSizeBI = new BigInteger(String.valueOf(oldSize+1));
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
            } catch (ArithmeticException exception){
                amountOfWater = Long.MAX_VALUE;
            }
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean isConnected(IPool pool) {
        return pool == masterPool || ((masterPool == null ) ? connectedPools.contains(pool) : masterPool.isConnected(pool)) ;
    }
}

