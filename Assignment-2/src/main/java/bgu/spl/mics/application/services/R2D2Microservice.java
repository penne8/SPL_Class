package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * R2D2Microservices is in charge of the handling {@link DeactivationEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link DeactivationEvent}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class R2D2Microservice extends MicroService {

    private final long DURATION;
    private final Diary DIARY;

    public R2D2Microservice(long duration) {
        super("R2D2");
        DURATION = duration;
        DIARY = Diary.getInstance();
    }

    @Override
    protected void initialize() {

        // Waits for all of the attacks to be over, and proceed with deactivating the shield generator
        subscribeEvent(DeactivationEvent.class, message -> {
            try {
                Thread.sleep(DURATION);
                DIARY.setR2D2Deactivate(System.currentTimeMillis());
                // By definition, as long as BombDestroyer Events are being sent, no other MicroService will perform unsubscribe
                // Because we wait for all MicroServices to register before sending attack events, this event won't be lost
                sendEvent(new BombDestroyerEvent());
            } catch (InterruptedException ignored) {
            }
        });

        // Waiting for Lando's termination message
        subscribeBroadcast(TerminateBroadcast.class, message -> terminate());
    }

    @Override
    protected void close() {
        DIARY.setR2D2Terminate(System.currentTimeMillis());
    }
}
