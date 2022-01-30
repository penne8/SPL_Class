package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * LandoMicroservice
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LandoMicroservice extends MicroService {

    private final long DURATION;
    private final Diary DIARY;

    public LandoMicroservice(long duration) {
        super("Lando");
        DURATION = duration;
        DIARY = Diary.getInstance();
    }

    @Override
    protected void initialize() {

        // Waits for deactivation of the shield generator and proceed with bombing the star destroyer
        subscribeEvent(BombDestroyerEvent.class, message -> {
            try {
                Thread.sleep(DURATION);
                sendBroadcast(new TerminateBroadcast());
            } catch (InterruptedException ignored) {
            }
        });

        // Waiting for own termination, to allow all MicroServices terminate at the same time
        subscribeBroadcast(TerminateBroadcast.class, message -> terminate());
    }

    @Override
    protected void close() {
        DIARY.setLandoTerminate(System.currentTimeMillis());
    }
}
