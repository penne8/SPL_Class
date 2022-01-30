package bgu.spl.mics;

import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.services.HanSoloMicroservice;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusImplTest {

    private MessageBus messageBus;
    private AttackEvent attackEvent;
    private MicroService microService;

    @BeforeEach
    void setUp() {

        // Init the MessageBus
        messageBus = MessageBusImpl.getInstance();

        // Create an event
        List<Integer> attacks = new LinkedList<>();
        attacks.add(1);
        attackEvent = new AttackEvent(new Attack(attacks, 100));

        // Create a MicroService
        microService = new HanSoloMicroservice();
    }

    @AfterEach
    void tearUp() {

        // Must provide a clean plate for the next test
        messageBus.unregister(microService);
    }

    @Test
    void subscribeEvent() {
        // Send an event without subscribers --> Expect null
        assertNull(messageBus.sendEvent(attackEvent));

        // Register a Microservice, and subscribe it to the event
        messageBus.register(microService);
        messageBus.subscribeEvent(AttackEvent.class, microService);

        // Send the event again --> Expect a Future
        assertNotNull(messageBus.sendEvent(attackEvent));
    }

    @Test
    void complete() {
        // Register a Microservice, and subscribe it to the event
        messageBus.register(microService);
        messageBus.subscribeEvent(AttackEvent.class, microService);

        // Send the event --> Expect a Future
        Future<?> future = messageBus.sendEvent(attackEvent);

        // The event wasn't processed yet --> Expect false
        assertFalse(future.isDone());

        // Complete the event
        messageBus.complete(attackEvent, true);

        // The event completed --> Expect true
        assertTrue(future.isDone());
    }

    @Test
        // Also tests the subscribeBroadcast method
    void sendBroadcast() throws InterruptedException {
        // Create a new broadcast
        TerminateBroadcast terminateBroadcast = new TerminateBroadcast();

        // Register a Microservice, and subscribe it to the broadcast
        messageBus.register(microService);
        messageBus.subscribeBroadcast(TerminateBroadcast.class, microService);

        // Send the broadcast
        messageBus.sendBroadcast(terminateBroadcast);

        // Validate that the broadcast arrived
        assert (messageBus.awaitMessage(microService).equals(terminateBroadcast));
    }

    @Test
    void sendEvent() throws InterruptedException {
        // Register a Microservice, and subscribe it to the event
        messageBus.register(microService);
        messageBus.subscribeEvent(AttackEvent.class, microService);

        // Send the event
        messageBus.sendEvent(attackEvent);

        // Validate that the event arrived
        assert (messageBus.awaitMessage(microService).equals(attackEvent));
    }

    @Test
    void register() {
        // Subscribe a Microservice to an event
        messageBus.subscribeEvent(AttackEvent.class, microService);

        // Send an event, there's a subscribed MicroService but its not registered --> Expect an exception
        assertThrows(NullPointerException.class, () -> messageBus.sendEvent(attackEvent));

        // Register the MicroService
        messageBus.register(microService);

        // Send the event again --> Expect a Future
        assertNotNull(messageBus.sendEvent(attackEvent));
    }

    @Test
    void unregister() {

        // Register a Microservice, and subscribe it to the event
        messageBus.register(microService);
        messageBus.subscribeEvent(AttackEvent.class, microService);

        // Send the event, there's a registered and subscribed MicroService --> Expect a Future
        assertNotNull(messageBus.sendEvent(attackEvent));

        // Unregister the MicroService
        messageBus.unregister(microService);

        // Send the event again, no one is subscribed to the event --> Expect null
        assertNull(messageBus.sendEvent(attackEvent));
    }

    @Test
    void awaitMessage() throws InterruptedException {

        // Pull a message for an unregistered MicroService, expect an exception
        assertThrows(IllegalStateException.class, () -> messageBus.awaitMessage(microService));

        // Register a Microservice, and subscribe it to the event
        messageBus.register(microService);
        messageBus.subscribeEvent(AttackEvent.class, microService);

        // Send the event
        messageBus.sendEvent(attackEvent);

        // Pull again --> Expect the event
        assert (messageBus.awaitMessage(microService).equals(attackEvent));
    }
}
