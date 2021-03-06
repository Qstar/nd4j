package org.nd4j.linalg.jcublas.ops.executioner;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.nd4j.jita.conf.CudaEnvironment;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.Op;
import org.nd4j.linalg.api.ops.grid.GridDescriptor;
import org.nd4j.linalg.api.ops.grid.OpDescriptor;
import org.nd4j.linalg.api.ops.impl.accum.Max;
import org.nd4j.linalg.api.ops.impl.accum.Sum;
import org.nd4j.linalg.api.ops.impl.meta.PredicateMetaOp;
import org.nd4j.linalg.api.ops.impl.meta.ReduceMetaOp;
import org.nd4j.linalg.api.ops.impl.scalar.ScalarAdd;
import org.nd4j.linalg.api.ops.impl.scalar.ScalarMultiplication;
import org.nd4j.linalg.api.ops.impl.scalar.ScalarSubtraction;
import org.nd4j.linalg.api.ops.impl.transforms.Abs;
import org.nd4j.linalg.api.ops.impl.transforms.Set;
import org.nd4j.linalg.api.ops.impl.transforms.arithmetic.AddOp;
import org.nd4j.linalg.factory.Nd4j;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author raver119@gmail.com
 */
public class MetaOpTests {
    @Before
    public void setUp() {
        CudaEnvironment.getInstance().getConfiguration()
                .enableDebug(true);
    }


    @Ignore
    @Test
    public void testLinearMetaOp1() throws Exception {
        CudaGridExecutioner executioner = new CudaGridExecutioner();

        INDArray array = Nd4j.create(new float[]{-11f, -12f, -13f, -14f, -15f, -16f, -17f, -18f, -19f, -20f});
        INDArray exp = Nd4j.create(new float[]{1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f, 10f});
        INDArray exp2 = Nd4j.create(new float[]{11f, 12f, 13f, 14f, 15f, 16f, 17f, 18f, 19f, 20f});

        ScalarAdd opA = new ScalarAdd(array, 10f);

        Abs opB = new Abs(array);

        PredicateMetaOp metaOp = new PredicateMetaOp(opA, opB);

        executioner.prepareGrid(metaOp);

        GridDescriptor descriptor = metaOp.getGridDescriptor();

        assertEquals(2, descriptor.getGridDepth());
        assertEquals(2, descriptor.getGridPointers().size());

        assertEquals(Op.Type.SCALAR, descriptor.getGridPointers().get(0).getType());
        assertEquals(Op.Type.TRANSFORM, descriptor.getGridPointers().get(1).getType());

        long time1 = System.nanoTime();
        executioner.exec(metaOp);
        long time2 = System.nanoTime();

        System.out.println("Execution time Meta: " + ((time2 - time1) / 1));
        assertEquals(exp, array);

        time1 = System.nanoTime();
        Nd4j.getExecutioner().exec(opA);
        Nd4j.getExecutioner().exec(opB);
        time2 = System.nanoTime();

        System.out.println("Execution time Linear: " + ((time2 - time1) / 1));

        assertEquals(exp2, array);
    }

    @Ignore
    @Test
    public void testLinearMetaOp2() throws Exception {
        CudaGridExecutioner executioner = new CudaGridExecutioner();

        INDArray array = Nd4j.create(new float[]{-11f, -12f, -13f, -14f, -15f, -16f, -17f, -18f, -19f, -20f});
        INDArray exp = Nd4j.create(new float[]{21f, 22f, 23f, 24f, 25f, 26f, 27f, 28f, 29f, 30f});
        INDArray exp2 = Nd4j.create(new float[]{31f, 32f, 33f, 34f, 35f, 36f, 37f, 38f, 39f, 40f});

        Abs opA = new Abs(array);

        ScalarAdd opB = new ScalarAdd(array, 10f);

        PredicateMetaOp metaOp = new PredicateMetaOp(opA, opB);

        executioner.prepareGrid(metaOp);

        GridDescriptor descriptor = metaOp.getGridDescriptor();

        assertEquals(2, descriptor.getGridDepth());
        assertEquals(2, descriptor.getGridPointers().size());

        assertEquals(Op.Type.TRANSFORM, descriptor.getGridPointers().get(0).getType());
        assertEquals(Op.Type.SCALAR, descriptor.getGridPointers().get(1).getType());

        long time1 = System.nanoTime();
        executioner.exec(metaOp);
        long time2 = System.nanoTime();

        System.out.println("Execution time Meta: " + ((time2 - time1) / 1));
        assertEquals(exp, array);

        time1 = System.nanoTime();
        Nd4j.getExecutioner().exec(opA);
        Nd4j.getExecutioner().exec(opB);
        time2 = System.nanoTime();

        System.out.println("Execution time Linear: " + ((time2 - time1) / 1));

        assertEquals(exp2, array);
    }

    @Ignore
    @Test
    public void testPredicateScalarPairwise1() throws Exception {
        CudaGridExecutioner executioner = new CudaGridExecutioner();

        INDArray arrayX = Nd4j.create(new float[]{0f, 0f, 0f, 0f, 0f, 0f});
        INDArray arrayY = Nd4j.create(new float[]{2f, 2f, 2f, 2f, 2f, 2f});
        INDArray exp = Nd4j.create(new float[]{3f, 3f, 3f, 3f, 3f, 3f});

        ScalarAdd opA = new ScalarAdd(arrayX, 1.0f);

        AddOp opB = new AddOp(arrayX, arrayY, arrayX);

        PredicateMetaOp metaOp = new PredicateMetaOp(opA, opB);

        executioner.prepareGrid(metaOp);

        long time1 = System.nanoTime();
        executioner.exec(metaOp);
        long time2 = System.nanoTime();

        System.out.println("Execution time Meta: " + ((time2 - time1) / 1));

        assertEquals(exp, arrayX);
    }

    @Ignore
    @Test
    public void testPredicateScalarPairwise2() throws Exception {
        CudaGridExecutioner executioner = new CudaGridExecutioner();

        INDArray arrayX = Nd4j.create(new float[]{0f, 0f, 0f, 0f, 0f, 0f});
        INDArray arrayY = Nd4j.create(new float[]{2f, 2f, 2f, 2f, 2f, 2f});
        INDArray exp = Nd4j.create(new float[]{1f, 1f, 1f, 1f, 1f, 1f});

        ScalarSubtraction opA = new ScalarSubtraction(arrayX, 1.0f);

        AddOp opB = new AddOp(arrayX, arrayY, arrayX);

        PredicateMetaOp metaOp = new PredicateMetaOp(opA, opB);

        executioner.prepareGrid(metaOp);

        long time1 = System.nanoTime();
        executioner.exec(metaOp);
        long time2 = System.nanoTime();

        System.out.println("Execution time Meta: " + ((time2 - time1) / 1));

        assertEquals(exp, arrayX);
    }

    /**
     * This is the MOST crucial test, basically it's test for dup() + following linear op
     *
     * @throws Exception
     */
    @Test
    public void testPredicateScalarPairwise3() throws Exception {
        CudaGridExecutioner executioner = new CudaGridExecutioner();

        INDArray arrayX = Nd4j.create(new float[]{0f, 0f, 0f, 0f, 0f, 0f});
        INDArray arrayY = Nd4j.create(new float[]{1f, 2f, 3f, 4f, 5f, 6f});
        INDArray exp = Nd4j.create(new float[]{2f, 4f, 6f, 8f, 10f, 12f});

        Set opA = new Set(arrayX, arrayY, arrayX, arrayX.length());

        ScalarMultiplication opB = new ScalarMultiplication(arrayX, 2.0f);
        //ScalarAdd opB = new ScalarAdd(arrayX, 3.0f);

        PredicateMetaOp metaOp = new PredicateMetaOp(opA, opB);

        executioner.prepareGrid(metaOp);

        long time1 = System.nanoTime();
        executioner.exec(metaOp);
        long time2 = System.nanoTime();

        System.out.println("Execution time Meta: " + ((time2 - time1) / 1));

        assertEquals(exp, arrayX);
    }

    /**
     * Scalar + reduce along dimension
     *
     * @throws Exception
     */
    @Test
    public void testPredicateReduce1() throws Exception {
        CudaGridExecutioner executioner = new CudaGridExecutioner();

        INDArray arrayX = Nd4j.create(5, 5);
        INDArray exp = Nd4j.create(new float[]{2f, 2f, 2f, 2f, 2f});

        ScalarAdd opA = new ScalarAdd(arrayX, 2.0f);

        Max opB = new Max(arrayX);

        OpDescriptor a = new OpDescriptor(opA);
        OpDescriptor b = new OpDescriptor(opB, new int[]{1});

        executioner.buildZ(opB, b.getDimensions());

        ReduceMetaOp metaOp = new ReduceMetaOp(a, b);

        executioner.prepareGrid(metaOp);

        executioner.exec(metaOp);

        INDArray result = opB.z();
        assertNotEquals(null, result);
        assertEquals(exp, result);
    }

    /**
     * Predicate test for scalar + reduceScalar
     *
     * @throws Exception
     */
    @Ignore
    @Test
    public void testPredicateReduce2() throws Exception {
        CudaGridExecutioner executioner = new CudaGridExecutioner();

        INDArray arrayX = Nd4j.create(5, 5);

        ScalarAdd opA = new ScalarAdd(arrayX, 1.0f);

        Sum opB = new Sum(arrayX);

        PredicateMetaOp metaOp = new PredicateMetaOp(opA, opB);

        executioner.exec(metaOp);

        INDArray result = opB.z();

        assertNotEquals(null, result);

        assertTrue(result.isScalar());
        assertEquals(25f, result.getFloat(0), 0.1f);
    }


    @Test
    public void testPerformance1() throws Exception {
        CudaGridExecutioner executioner = new CudaGridExecutioner();

/*        INDArray array = Nd4j.create(new float[]{-11f, -12f, -13f, -14f, -15f, -16f, -17f, -18f, -19f, -20f});
        INDArray exp = Nd4j.create(new float[]{1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f, 10f});
        INDArray exp2 = Nd4j.create(new float[]{11f, 12f, 13f, 14f, 15f, 16f, 17f, 18f, 19f, 20f});
        */
        INDArray arrayX = Nd4j.create(65536);
        INDArray arrayY = Nd4j.create(65536);
        INDArray exp2 = Nd4j.create(65536);

        Set opA = new Set(arrayX, arrayY, arrayX, arrayX.length());

        ScalarMultiplication opB = new ScalarMultiplication(arrayX, 2.0f);
        //ScalarAdd opB = new ScalarAdd(arrayX, 3.0f);

        PredicateMetaOp metaOp = new PredicateMetaOp(opA, opB);

        executioner.prepareGrid(metaOp);

        GridDescriptor descriptor = metaOp.getGridDescriptor();

        assertEquals(2, descriptor.getGridDepth());
        assertEquals(2, descriptor.getGridPointers().size());

        assertEquals(Op.Type.PAIRWISE, descriptor.getGridPointers().get(0).getType());
        assertEquals(Op.Type.SCALAR, descriptor.getGridPointers().get(1).getType());

        long time1 = System.nanoTime();
        for (int x = 0; x < 1000000; x++) {
            executioner.exec(metaOp);
        }
        long time2 = System.nanoTime();

        System.out.println("Execution time Meta: " + ((time2 - time1) / 1000000));
      //  assertEquals(exp, array);

        time1 = System.nanoTime();
        for (int x = 0; x < 1000000; x++) {
            Nd4j.getExecutioner().exec(opA);
            Nd4j.getExecutioner().exec(opB);
        }
        time2 = System.nanoTime();

        System.out.println("Execution time Linear: " + ((time2 - time1) / 1000000));


      //  assertEquals(exp2, array);

    }

    @Test
    public void testEnqueuePerformance1() throws Exception {
        CudaGridExecutioner executioner = new CudaGridExecutioner();

        INDArray arrayX = Nd4j.create(65536);
        INDArray arrayY = Nd4j.create(65536);

        Set opA = new Set(arrayX, arrayY, arrayX, arrayX.length());

        ScalarMultiplication opB = new ScalarMultiplication(arrayX, 2.0f);
        //ScalarAdd opB = new ScalarAdd(arrayX, 3.0f);

        PredicateMetaOp metaOp = new PredicateMetaOp(opA, opB);

        executioner.prepareGrid(metaOp);

        GridDescriptor descriptor = metaOp.getGridDescriptor();

        assertEquals(2, descriptor.getGridDepth());
        assertEquals(2, descriptor.getGridPointers().size());

        assertEquals(Op.Type.PAIRWISE, descriptor.getGridPointers().get(0).getType());
        assertEquals(Op.Type.SCALAR, descriptor.getGridPointers().get(1).getType());

        long time1 = System.nanoTime();
        for (int x = 0; x < 10000000; x++) {
            executioner.exec(opA);
            executioner.purgeQueue();
        }
        long time2 = System.nanoTime();

        System.out.println("Enqueue time: " + ((time2 - time1) / 10000000));
    }


    @Ignore
    @Test
    public void testPerformance2() throws Exception {
        CudaGridExecutioner executioner = new CudaGridExecutioner();

        INDArray arrayX = Nd4j.create(1024);
        INDArray arrayY = Nd4j.create(1024);
        INDArray exp = Nd4j.create(1024);

        ScalarAdd opA = new ScalarAdd(arrayX, 1.0f);

        AddOp opB = new AddOp(arrayX, arrayY, arrayX);

        PredicateMetaOp metaOp = new PredicateMetaOp(opA, opB);

        executioner.prepareGrid(metaOp);

        long time1 = System.nanoTime();
        for (int x = 0; x < 100000; x++) {
            executioner.exec(metaOp);
        }
        long time2 = System.nanoTime();

        System.out.println("Execution time Meta: " + ((time2 - time1) / 100000));

        time1 = System.nanoTime();
        for (int x = 0; x < 100000; x++) {
            Nd4j.getExecutioner().exec(opA);
            Nd4j.getExecutioner().exec(opB);
        }
        time2 = System.nanoTime();

        System.out.println("Execution time Meta: " + ((time2 - time1) / 100000));
    }
}
