package pooolsImpl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import poolsImpl.GlobalStatePoolImpl;
import poolsImpl.InversedMasterPoolImpl;

public class GlobalStatePoolImplTest {
    @Test
    public void verifyAddEmptyMesh() {
        GlobalStatePoolImpl pool = new GlobalStatePoolImpl();

        Assertions.assertEquals(EMPTY, pool.measure());

        pool.add(EXTRA_WATER);

        Assertions.assertEquals(EMPTY + EXTRA_WATER, pool.measure());
    }

    @Test
    public void verifyConnectThisToThis() {
        GlobalStatePoolImpl pool = new GlobalStatePoolImpl();

        Assertions.assertEquals(NOT_CONNECTED, pool.isConnected(pool));

        pool.connect(pool);

        Assertions.assertEquals(NOT_CONNECTED, pool.isConnected(pool));
        Assertions.assertEquals(EMPTY, pool.measure());
    }

    @Test
    public void verifyConnectTwoEmptyPools() {
        GlobalStatePoolImpl poolFirst = new GlobalStatePoolImpl();
        GlobalStatePoolImpl poolSecond = new GlobalStatePoolImpl();

        Assertions.assertEquals(NOT_CONNECTED, poolFirst.isConnected(poolSecond));

        poolFirst.connect(poolSecond);

        Assertions.assertEquals(CONNECTED, poolFirst.isConnected(poolSecond));
    }

    @Test
    public void verifyConnectTwoPoolsEqualsWater() {
        GlobalStatePoolImpl poolFirst = new GlobalStatePoolImpl();
        GlobalStatePoolImpl poolSecond = new GlobalStatePoolImpl();

        poolFirst.add(EXTRA_WATER);
        poolSecond.add(EXTRA_WATER);

        Assertions.assertEquals(NOT_CONNECTED, poolFirst.isConnected(poolSecond));

        poolFirst.connect(poolSecond);

        Assertions.assertEquals(CONNECTED, poolFirst.isConnected(poolSecond));
        Assertions.assertEquals(EXTRA_WATER, poolFirst.measure());
        Assertions.assertEquals(EXTRA_WATER, poolSecond.measure());
    }

    @Test
    public void verifyAddToMesh() {
        GlobalStatePoolImpl poolFirst = new GlobalStatePoolImpl();
        GlobalStatePoolImpl poolSecond = new GlobalStatePoolImpl();

        poolFirst.add(EXTRA_WATER);
        poolSecond.add(EXTRA_WATER);

        Assertions.assertEquals(NOT_CONNECTED, poolFirst.isConnected(poolSecond));

        poolFirst.connect(poolSecond);

        poolFirst.add(EXTRA_WATER_DOUBLED);
        poolSecond.add(EXTRA_WATER);

        Assertions.assertEquals(CONNECTED, poolFirst.isConnected(poolSecond));

        final long expected = (EXTRA_WATER_DOUBLED + EXTRA_WATER + EXTRA_WATER + EXTRA_WATER) / 2;
        Assertions.assertEquals(expected, poolFirst.measure());
        Assertions.assertEquals(expected, poolSecond.measure());
    }

    @Test
    public void verifyConnectTwoPools() {
        GlobalStatePoolImpl poolFirst = new GlobalStatePoolImpl();
        GlobalStatePoolImpl poolSecond = new GlobalStatePoolImpl();

        poolFirst.add(EXTRA_WATER);
        poolSecond.add(EXTRA_WATER_DOUBLED);

        Assertions.assertEquals(NOT_CONNECTED, poolFirst.isConnected(poolSecond));
        Assertions.assertEquals(NOT_CONNECTED, poolSecond.isConnected(poolFirst));

        poolFirst.connect(poolSecond);

        Assertions.assertEquals(CONNECTED, poolFirst.isConnected(poolSecond));
        Assertions.assertEquals(CONNECTED, poolSecond.isConnected(poolFirst));


        final long expected = (EXTRA_WATER_DOUBLED + EXTRA_WATER) / 2;
        Assertions.assertEquals(expected, poolFirst.measure());
        Assertions.assertEquals(expected, poolSecond.measure());
    }

    @Test
    public void verifyConnectPoolToPoolMesh() {
        GlobalStatePoolImpl poolFirst = new GlobalStatePoolImpl();
        GlobalStatePoolImpl poolSecond = new GlobalStatePoolImpl();
        GlobalStatePoolImpl poolThird = new GlobalStatePoolImpl();

        poolFirst.add(EXTRA_WATER);
        poolSecond.add(EXTRA_WATER_DOUBLED);
        poolThird.add(EXTRA_WATER_PLUS);

        Assertions.assertEquals(NOT_CONNECTED, poolFirst.isConnected(poolSecond));
        Assertions.assertEquals(NOT_CONNECTED, poolFirst.isConnected(poolThird));
        Assertions.assertEquals(NOT_CONNECTED, poolSecond.isConnected(poolFirst));
        Assertions.assertEquals(NOT_CONNECTED, poolSecond.isConnected(poolThird));
        Assertions.assertEquals(NOT_CONNECTED, poolThird.isConnected(poolFirst));
        Assertions.assertEquals(NOT_CONNECTED, poolThird.isConnected(poolSecond));

        poolFirst.connect(poolSecond);
        poolThird.connect(poolFirst);

        Assertions.assertEquals(CONNECTED, poolFirst.isConnected(poolSecond));
        Assertions.assertEquals(CONNECTED, poolFirst.isConnected(poolThird));
        Assertions.assertEquals(CONNECTED, poolSecond.isConnected(poolFirst));
        Assertions.assertEquals(CONNECTED, poolSecond.isConnected(poolThird));
        Assertions.assertEquals(CONNECTED, poolThird.isConnected(poolFirst));
        Assertions.assertEquals(CONNECTED, poolThird.isConnected(poolSecond));

        final long expected = (EXTRA_WATER + EXTRA_WATER_DOUBLED + EXTRA_WATER_PLUS) / 3;
        Assertions.assertEquals(expected, poolFirst.measure());
        Assertions.assertEquals(expected, poolSecond.measure());
        Assertions.assertEquals(expected, poolThird.measure());
    }

    @Test
    public void verifyConnectPoolMeshToPool() {
        GlobalStatePoolImpl poolFirst = new GlobalStatePoolImpl();
        GlobalStatePoolImpl poolSecond = new GlobalStatePoolImpl();
        GlobalStatePoolImpl poolThird = new GlobalStatePoolImpl();

        poolFirst.add(EXTRA_WATER);
        poolSecond.add(EXTRA_WATER_DOUBLED);
        poolThird.add(EXTRA_WATER_PLUS);

        Assertions.assertEquals(NOT_CONNECTED, poolFirst.isConnected(poolSecond));
        Assertions.assertEquals(NOT_CONNECTED, poolFirst.isConnected(poolThird));
        Assertions.assertEquals(NOT_CONNECTED, poolSecond.isConnected(poolFirst));
        Assertions.assertEquals(NOT_CONNECTED, poolSecond.isConnected(poolThird));
        Assertions.assertEquals(NOT_CONNECTED, poolThird.isConnected(poolFirst));
        Assertions.assertEquals(NOT_CONNECTED, poolThird.isConnected(poolSecond));

        poolFirst.connect(poolSecond);
        poolFirst.connect(poolThird);

        Assertions.assertEquals(CONNECTED, poolFirst.isConnected(poolSecond));
        Assertions.assertEquals(CONNECTED, poolFirst.isConnected(poolThird));
        Assertions.assertEquals(CONNECTED, poolSecond.isConnected(poolFirst));
        Assertions.assertEquals(CONNECTED, poolSecond.isConnected(poolThird));
        Assertions.assertEquals(CONNECTED, poolThird.isConnected(poolFirst));
        Assertions.assertEquals(CONNECTED, poolThird.isConnected(poolSecond));

        final long expected = (EXTRA_WATER + EXTRA_WATER_DOUBLED + EXTRA_WATER_PLUS) / 3;
        Assertions.assertEquals(expected, poolFirst.measure());
        Assertions.assertEquals(expected, poolSecond.measure());
        Assertions.assertEquals(expected, poolThird.measure());
    }

    @Test
    public void verifyConnectPoolMeshToPoolMeshMasterToMaster() {
        GlobalStatePoolImpl poolFirst = new GlobalStatePoolImpl();
        GlobalStatePoolImpl poolSecond = new GlobalStatePoolImpl();
        GlobalStatePoolImpl poolThird = new GlobalStatePoolImpl();
        GlobalStatePoolImpl poolFourth = new GlobalStatePoolImpl();

        poolFirst.add(EXTRA_WATER);
        poolSecond.add(EXTRA_WATER_DOUBLED);
        poolThird.add(EXTRA_WATER);
        poolFourth.add(EXTRA_WATER_DOUBLED);

        poolFirst.connect(poolSecond);
        poolThird.connect(poolFourth);
        poolSecond.connect(poolFourth);

        Assertions.assertEquals(CONNECTED, poolFirst.isConnected(poolSecond));
        Assertions.assertEquals(CONNECTED, poolFirst.isConnected(poolThird));
        Assertions.assertEquals(CONNECTED, poolFirst.isConnected(poolFourth));
        Assertions.assertEquals(CONNECTED, poolSecond.isConnected(poolFirst));
        Assertions.assertEquals(CONNECTED, poolSecond.isConnected(poolThird));
        Assertions.assertEquals(CONNECTED, poolSecond.isConnected(poolFourth));
        Assertions.assertEquals(CONNECTED, poolThird.isConnected(poolFirst));
        Assertions.assertEquals(CONNECTED, poolThird.isConnected(poolSecond));
        Assertions.assertEquals(CONNECTED, poolThird.isConnected(poolFourth));
        Assertions.assertEquals(CONNECTED, poolFourth.isConnected(poolFirst));
        Assertions.assertEquals(CONNECTED, poolFourth.isConnected(poolSecond));
        Assertions.assertEquals(CONNECTED, poolFourth.isConnected(poolThird));

        final long expected = (EXTRA_WATER + EXTRA_WATER_DOUBLED + EXTRA_WATER + EXTRA_WATER_DOUBLED) / 4;
        Assertions. assertEquals(expected, poolFirst.measure());
        Assertions.assertEquals(expected, poolSecond.measure());
        Assertions.assertEquals(expected, poolThird.measure());
        Assertions.assertEquals(expected, poolFourth.measure());
    }

    @Test
    public void verifyConnectPoolMeshToPoolMeshMasterToSlave() {
        GlobalStatePoolImpl poolFirst = new GlobalStatePoolImpl();
        GlobalStatePoolImpl poolSecond = new GlobalStatePoolImpl();
        GlobalStatePoolImpl poolThird = new GlobalStatePoolImpl();
        GlobalStatePoolImpl poolFourth = new GlobalStatePoolImpl();

        poolFirst.add(EXTRA_WATER);
        poolSecond.add(EXTRA_WATER_DOUBLED);
        poolThird.add(EXTRA_WATER);
        poolFourth.add(EXTRA_WATER_DOUBLED);

        poolFirst.connect(poolSecond);
        poolThird.connect(poolFourth);
        poolSecond.connect(poolThird);

        Assertions.assertEquals(CONNECTED, poolFirst.isConnected(poolSecond));
        Assertions.assertEquals(CONNECTED, poolFirst.isConnected(poolThird));
        Assertions.assertEquals(CONNECTED, poolFirst.isConnected(poolFourth));
        Assertions.assertEquals(CONNECTED, poolSecond.isConnected(poolFirst));
        Assertions.assertEquals(CONNECTED, poolSecond.isConnected(poolThird));
        Assertions.assertEquals(CONNECTED, poolSecond.isConnected(poolFourth));
        Assertions.assertEquals(CONNECTED, poolThird.isConnected(poolFirst));
        Assertions.assertEquals(CONNECTED, poolThird.isConnected(poolSecond));
        Assertions.assertEquals(CONNECTED, poolThird.isConnected(poolFourth));
        Assertions.assertEquals(CONNECTED, poolFourth.isConnected(poolFirst));
        Assertions.assertEquals(CONNECTED, poolFourth.isConnected(poolSecond));
        Assertions.assertEquals(CONNECTED, poolFourth.isConnected(poolThird));

        final long expected = (EXTRA_WATER + EXTRA_WATER_DOUBLED + EXTRA_WATER + EXTRA_WATER_DOUBLED) / 4;
        Assertions. assertEquals(expected, poolFirst.measure());
        Assertions.assertEquals(expected, poolSecond.measure());
        Assertions.assertEquals(expected, poolThird.measure());
        Assertions.assertEquals(expected, poolFourth.measure());
    }

    @Test
    public void verifyConnectPoolMeshToPoolMeshSlaveToMaster() {
        GlobalStatePoolImpl poolFirst = new GlobalStatePoolImpl();
        GlobalStatePoolImpl poolSecond = new GlobalStatePoolImpl();
        GlobalStatePoolImpl poolThird = new GlobalStatePoolImpl();
        GlobalStatePoolImpl poolFourth = new GlobalStatePoolImpl();

        poolFirst.add(EXTRA_WATER);
        poolSecond.add(EXTRA_WATER_DOUBLED);
        poolThird.add(EXTRA_WATER);
        poolFourth.add(EXTRA_WATER_DOUBLED);

        poolFirst.connect(poolSecond);
        poolThird.connect(poolFourth);
        poolFirst.connect(poolFourth);

        Assertions.assertEquals(CONNECTED, poolFirst.isConnected(poolSecond));
        Assertions.assertEquals(CONNECTED, poolFirst.isConnected(poolThird));
        Assertions.assertEquals(CONNECTED, poolFirst.isConnected(poolFourth));
        Assertions.assertEquals(CONNECTED, poolSecond.isConnected(poolFirst));
        Assertions.assertEquals(CONNECTED, poolSecond.isConnected(poolThird));
        Assertions.assertEquals(CONNECTED, poolSecond.isConnected(poolFourth));
        Assertions.assertEquals(CONNECTED, poolThird.isConnected(poolFirst));
        Assertions.assertEquals(CONNECTED, poolThird.isConnected(poolSecond));
        Assertions.assertEquals(CONNECTED, poolThird.isConnected(poolFourth));
        Assertions.assertEquals(CONNECTED, poolFourth.isConnected(poolFirst));
        Assertions.assertEquals(CONNECTED, poolFourth.isConnected(poolSecond));
        Assertions.assertEquals(CONNECTED, poolFourth.isConnected(poolThird));

        final long expected = (EXTRA_WATER + EXTRA_WATER_DOUBLED + EXTRA_WATER + EXTRA_WATER_DOUBLED) / 4;
        Assertions. assertEquals(expected, poolFirst.measure());
        Assertions.assertEquals(expected, poolSecond.measure());
        Assertions.assertEquals(expected, poolThird.measure());
        Assertions.assertEquals(expected, poolFourth.measure());
    }

    @Test
    public void verifyConnectPoolMeshToPoolMeshSlaveToSlave() {
        GlobalStatePoolImpl poolFirst = new GlobalStatePoolImpl();
        GlobalStatePoolImpl poolSecond = new GlobalStatePoolImpl();
        GlobalStatePoolImpl poolThird = new GlobalStatePoolImpl();
        GlobalStatePoolImpl poolFourth = new GlobalStatePoolImpl();

        poolFirst.add(EXTRA_WATER);
        poolSecond.add(EXTRA_WATER_DOUBLED);
        poolThird.add(EXTRA_WATER);
        poolFourth.add(EXTRA_WATER_DOUBLED);

        poolFirst.connect(poolSecond);
        poolThird.connect(poolFourth);
        poolFirst.connect(poolThird);

        Assertions.assertEquals(CONNECTED, poolFirst.isConnected(poolSecond));
        Assertions.assertEquals(CONNECTED, poolFirst.isConnected(poolThird));
        Assertions.assertEquals(CONNECTED, poolFirst.isConnected(poolFourth));
        Assertions.assertEquals(CONNECTED, poolSecond.isConnected(poolFirst));
        Assertions.assertEquals(CONNECTED, poolSecond.isConnected(poolThird));
        Assertions.assertEquals(CONNECTED, poolSecond.isConnected(poolFourth));
        Assertions.assertEquals(CONNECTED, poolThird.isConnected(poolFirst));
        Assertions.assertEquals(CONNECTED, poolThird.isConnected(poolSecond));
        Assertions.assertEquals(CONNECTED, poolThird.isConnected(poolFourth));
        Assertions.assertEquals(CONNECTED, poolFourth.isConnected(poolFirst));
        Assertions.assertEquals(CONNECTED, poolFourth.isConnected(poolSecond));
        Assertions.assertEquals(CONNECTED, poolFourth.isConnected(poolThird));

        final long expected = (EXTRA_WATER + EXTRA_WATER_DOUBLED + EXTRA_WATER + EXTRA_WATER_DOUBLED) / 4;
        Assertions. assertEquals(expected, poolFirst.measure());
        Assertions.assertEquals(expected, poolSecond.measure());
        Assertions.assertEquals(expected, poolThird.measure());
        Assertions.assertEquals(expected, poolFourth.measure());
    }

    private static final long EMPTY = 0;
    private static final boolean NOT_CONNECTED = false;
    private static final boolean CONNECTED = true;
    private static final long EXTRA_WATER = 10;
    private static final long EXTRA_WATER_PLUS = 12;
    private static final long EXTRA_WATER_DOUBLED = 20;

}

