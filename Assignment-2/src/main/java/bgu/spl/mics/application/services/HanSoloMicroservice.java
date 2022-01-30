package bgu.spl.mics.application.services;


import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;

import java.util.List;

/**
 * HanSoloMicroservices is in charge of the handling {@link AttackEvents}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvents}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class HanSoloMicroservice extends MicroService {

    private final Ewoks EWOKS;
    private final Diary DIARY;

    public HanSoloMicroservice() {
        super("Han");
        EWOKS = Ewoks.getInstance();
        DIARY = Diary.getInstance();
    }

    @Override
    protected void initialize() {

        // Following Leia's attack orders
        subscribeEvent(AttackEvent.class, message -> {

            // Acquire the Ewoks needed for the attack
            List<Integer> requiredEwoks = message.getATTACK().getSerials();
            EWOKS.acquire(requiredEwoks);

            // Attack! (Simulate by sleeping)
            try {
                Thread.sleep(message.getATTACK().getDuration());
                DIARY.incrementTotalAttacks();
                complete(message, true);

                // Release the used Ewoks and end the attack
                EWOKS.release(requiredEwoks);
                DIARY.setHanSoloFinish(System.currentTimeMillis());
            } catch (InterruptedException ignored) {
            }
        });

        // Waiting for Lando's termination message
        subscribeBroadcast(TerminateBroadcast.class, message -> terminate());
    }

    @Override
    protected void close() {
        DIARY.setHanSoloTerminate(System.currentTimeMillis());
    }
}
