package poolsImpl;

import interfaces.IPool;

import java.util.HashSet;
import java.util.Set;

/**
 * Базовая имплементация {@code IPool}, производительность данного подхода ниже, чем {@code ConnectAndAddHighPerfomancePoolImpl},
 * но данный подход принципиально проще с точки зрения поддержки
 *
 *  Фактически вся задача сводится к разрыву рекурсии при балансировке сети бассейнов,
 *  применяется подход, при котором балансировкой воды фактически занимается 1 master бассейн
 *  master бассейном считается тот, в который доливают воды или тот, на котором конечный пользователь вызывает conect
 *
 *  для реализации такого подхода к балансировке необходимо, чтобы master бассейн знал о всех бассейнах в сети
 *  так как любой бассейн в разные периоды времени может выполнять функции master бассейна, то
 *  фактически с точки зрения внутренего представления, сеть бассейнов рассматривается как полный граф,
 *  где {@code connectedSet} есть множество вершин графа
 */
public final class FullGraphPoolImpl implements IPool {

    /** Сеть объедененных каналми бассейнов */
    private final Set<IPool> connectedPools = new HashSet<>();

    /** Колличество воды в бассейне в данный момент */
    private long amountOfWater = 0;

    /**
     * @inheritDoc
     */
    @Override
    public long measure() {
        return amountOfWater;
    }

    /**
     * @inheritDoc В силу того, что сложности различить ситуации добавления нового бассейна в сеть и процесс мерджа двух сетей бассейнов
     * первый {@code measure()} будет расчитывать колличество воды в бассейнах
     */
    @Override
    public void connect(IPool pool) {
        if (pool == this) return;
        if (connectedPools.contains(pool)) return;

        boolean isFirstConnection = connectedPools.stream().noneMatch((IPool itPool) -> itPool.isConnected(pool));

        connectedPools.add(pool);

        if (isFirstConnection && !pool.isConnected(this)) {

            //данная секция вызывается при вызове connect конечным пользователем для новой пары бассейнов

            pool.connect(this);

            connectedPools.forEach((IPool firstIter)->firstIter.connect(this) );
            connectedPools.forEach((IPool firstIter) -> connectedPools.forEach(firstIter::connect));

            balanceWater();
        } else if (isFirstConnection) {

            //данная секция вызывается, когда происходит внутренее соединение двух сетей
            //фактически это значит, что на уровень выше по стеку вызовов можно обноружить вызов вида {@code pool.connect(this)}
            //из пользовательского кода

            connectedPools.forEach(pool::connect);

        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public void add(long water) {
        if (water == 0) return;

        //если вода сбалансированна то тогда распределяем по всем бассейнам одинаковое колличество воды
        if (isWaterBalanced()) {
            //считаем что вода может равномерно распределиться по басейнам
            final long diff = water / poolMeshSize();
            amountOfWater += diff;
            connectedPools.forEach((IPool pool) -> pool.add(diff));
        } else {
            //если бассейны не сбалансированны то значит мы получили объем для балансировки
            amountOfWater += water;
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public Boolean isConnected(IPool pool) {
        return connectedPools.contains(pool);
    }


    /**
     * Метод балансировки воды в сети бассейнов, если в сети бассейнов разное колличество воды,
     * то данный метод сбалансирует объемы
     */
    private void balanceWater() {
        if (isWaterBalanced()) return;
        final long meshSize = poolMeshSize();
        long expectedAmountOfWater = amountOfWater/meshSize + connectedPools
                        .stream()
                        .map((IPool pool) -> pool.measure() / meshSize)
                        .reduce(0L, Long::sum);

        amountOfWater += expectedAmountOfWater - amountOfWater;
        connectedPools.forEach((IPool pool) -> pool.add(expectedAmountOfWater - pool.measure()));
    }

    /**
     * Метод проверки налисия изменений баланса воды в соедененных бассейнах, если метод вернул true
     * это означает что вода сбалансированна в соедененных бассейнах, если false то происходит процесс балансировки
     *
     * @return возвращает true, если объем воды во всех бассейнах одинаковый, иначе возвращает false
     */
    private boolean isWaterBalanced() {
        return connectedPools.stream().noneMatch((IPool pool) -> pool.measure() != amountOfWater);
    }

    /**
     * Колличество бассейнов в сети
     *
     * @return колличество бассейнов соеденных каналами,
     * по факту это есть размер коллекции сети с учетом данного бассейна
     */
    private int poolMeshSize() {
        return connectedPools.size() + 1;
    }
}
