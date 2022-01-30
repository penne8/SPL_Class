package bgu.spl.mics.application.passiveObjects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EwokTest {

    private Ewok ewok;

    @BeforeEach
    void setUp() {
        ewok = new Ewok(1);
    }

    @Test
    void acquire() {
        ewok.available = true;
        ewok.acquire();
        assertFalse(ewok.available);
    }

    @Test
    void release() {
        ewok.available = false;
        ewok.release();
        assertTrue(ewok.available);
    }
}