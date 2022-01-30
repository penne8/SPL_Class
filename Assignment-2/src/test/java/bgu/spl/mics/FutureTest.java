package bgu.spl.mics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class FutureTest {

    private Future<Integer> future;

    @BeforeEach
    public void setUp() {
        future = new Future<>();
    }

    @Test
    public void testGet() {
        assertFalse(future.isDone());
        future.resolve(5);
        future.get();
        assertTrue(future.isDone());
    }

    @Test
    public void testResolve() {
        Integer intg = 88;
        future.resolve(intg);
        assertTrue(future.isDone());
        assertEquals(future.get(), 88);
    }

    @Test
    public void testIsDone() {
        Integer intg = 420;
        assertFalse(future.isDone());
        future.resolve(intg);
        assertTrue(future.isDone());
    }

    @Test
    public void testGetWithTimeOut() throws InterruptedException {
        assertFalse(future.isDone());
        future.get(100, TimeUnit.MILLISECONDS);
        assertFalse(future.isDone());
        future.resolve(2020);
        assertEquals(2020, future.get(100, TimeUnit.MILLISECONDS));
    }
}