package bgu.spl.mics;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

    // For every Message type, stores a queue of the MicroServices that are subscribed to it
    private final ConcurrentHashMap<Class<? extends Message>, BlockingQueue<MicroService>> MESSAGE_SUBSCRIBERS;

    // For every MicroService, stores a queue of its messages (Events/Broadcasts)
    private final ConcurrentHashMap<MicroService, BlockingQueue<Message>> MICROSERVICE_MESSAGES;

    // For every Event, stores its promised Future
    private final ConcurrentHashMap<Event<?>, Future> EVENTS_FUTURES;

    // For every message type, stores its lock
    private final ConcurrentHashMap<Class<? extends Message>, Object> MESSAGES_LOCKS;

    private MessageBusImpl() {
        MESSAGE_SUBSCRIBERS = new ConcurrentHashMap<>();
        MICROSERVICE_MESSAGES = new ConcurrentHashMap<>();
        EVENTS_FUTURES = new ConcurrentHashMap<>();
        MESSAGES_LOCKS = new ConcurrentHashMap<>();
    }

    public static MessageBusImpl getInstance() {
        return SingeltonHolder.INSTANCE;
    }

    private void subscribe(Class<? extends Message> type, MicroService m) {
        BlockingQueue<MicroService> subscribers = new LinkedBlockingQueue<>();

        // Using the add method, the MicroService will be inserted immediately
        // We can allow this because only the current thread knows this queue
        subscribers.add(m);

        // Atomically checks if the message type exist, and associate a new lock and a MicroService queue for it if it doesn't
        MESSAGES_LOCKS.putIfAbsent(type, new Object());
        if (MESSAGE_SUBSCRIBERS.putIfAbsent(type, subscribers) != null) {
            try {
                // If it does exist, adds the MicroService to its queue
                // Using the put method, the MicroService will be added only when the queue key is available
                MESSAGE_SUBSCRIBERS.get(type).put(m);
            } catch (InterruptedException ignored) {
            }
        }
    }

    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
        subscribe(type, m);
    }

    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        subscribe(type, m);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void complete(Event<T> e, T result) {
        // Each Event is unique in a way that is has only one Future, and that only one thread can complete it
        // There's no need to sync on it, since no two threads will try to concurrently access it
        // ConcurrentHashMap enables multiple threads to concurrently read it, so no need to sync it as well
        if (EVENTS_FUTURES.containsKey(e)) {
            EVENTS_FUTURES.get(e).resolve(result);
        }
    }

    @Override
    public void sendBroadcast(Broadcast b) {
        // The ConcurrentHashMap iterator reflects the state of the map at the moment that the iterator was created
        // Afterwards, added/removed MicroServices won't be reflected
        // That's acceptable, since when sending a broadcast we want to it to arrive to all of the subscribed MicroServices at the moment of the sending
        // By definition, no MicroServices will be removed while messages are being sent, so no need to sync this with the unregister method
        if (MESSAGE_SUBSCRIBERS.containsKey(b.getClass())) {
            for (MicroService MS : MESSAGE_SUBSCRIBERS.get(b.getClass())) {
                try {
                    // Using the put method, the broadcast will be added only when the queue key is available
                    MICROSERVICE_MESSAGES.get(MS).put(b);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    @Override
    public <T> Future<T> sendEvent(Event<T> e) {

        // When sending a message, we must lock its subscribers queue to allow proper Round-Robin functionality
        if (MESSAGES_LOCKS.containsKey(e.getClass())) {
            synchronized (MESSAGES_LOCKS.get(e.getClass())) {

                // Retrieves the queue of MicroServices that are subscribed to the event type
                BlockingQueue<MicroService> subscribers = MESSAGE_SUBSCRIBERS.get(e.getClass());

                // If the subscribers queue is empty, that means that no MicroServices are subscribed to that message type
                // By definition, no MicroServices will be removed while messages are being sent, so no need to sync this with the unregister method
                if (!subscribers.isEmpty()) {

                    // Creates a new Future and associate it with the sent event
                    Future<T> future = new Future<>();
                    EVENTS_FUTURES.put(e, future);

                    // Pulls the first MicroService from the queue and pushes it back to the end of the queue
                    MicroService MS;
                    MS = subscribers.poll();
                    // Won't be null since we validated that the queue isn't empty
                    assert MS != null;
                    try {
                        // Pushing the polled MicroService to the end of the queue, for Round-Robin functionality
                        subscribers.put(MS);

                        // Adding the new message to the polled MicroService messages queue
                        // Using the put method, the event will be added only when the queue key is available
                        MICROSERVICE_MESSAGES.get(MS).put(e);
                    } catch (InterruptedException ignore) {
                    }

                    // Returns the created Future
                    return future;
                }
            }
        }
        return null;
    }

    @Override
    public void register(MicroService m) {
        // putIfAbsent is an atomic method, thus we avoid collisions
        // A new MicroService is added only if it is absent
        MICROSERVICE_MESSAGES.putIfAbsent(m, new LinkedBlockingQueue<>());
    }

    @Override
    public void unregister(MicroService m) {
        // By definition, all of the MicroServices will perform unregister at the same time, at the end of the program
        // Thus, no need to sync with the subscribing/sending methods, because no messages will be subscribed/sent while unregistering
        // Iterates over all of the currently registered message types
        for (Class<? extends Message> messageType : MESSAGE_SUBSCRIBERS.keySet()) {
            // Removes the MicroService from the message type queue, if its subscribed to it
            // Using the remove method, the Microservice will be removed only when the queue key is available
            MESSAGE_SUBSCRIBERS.get(messageType).remove(m);
        }
        MICROSERVICE_MESSAGES.remove(m);
    }

    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {
        // By definition, no MicroServices will be removed while messages are being processed, so no need to sync this with the unregister method
        if (!MICROSERVICE_MESSAGES.containsKey(m)) {
            throw new IllegalStateException(m.getName() + " was never registered");
        }
        //Using the take method, if the queue is empty it blocks and waits for a message to become available
        return MICROSERVICE_MESSAGES.get(m).take();
    }

    private static class SingeltonHolder {
        private static final MessageBusImpl INSTANCE = new MessageBusImpl();
    }
}
