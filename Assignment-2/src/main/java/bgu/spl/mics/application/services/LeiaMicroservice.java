package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;

import java.util.LinkedList;
import java.util.List;

/**
 * LeiaMicroservices Initialized with Attack objects, and sends them as  {@link AttackEvents}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvents}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LeiaMicroservice extends MicroService {
    private final List<Future<?>> FUTURES;
    private final Diary DIARY;
    private Attack[] attacks;

    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
        this.attacks = attacks;
        FUTURES = new LinkedList<>();
        DIARY = Diary.getInstance();
    }

    @Override
    protected void initialize() {

        // Leia needs to wait until all other MicroServices are registered
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }

        // Sending Attack Events
        for (Attack attack : attacks) {
            AttackEvent attackEvent = new AttackEvent(attack);
            Future<?> future = sendEvent(attackEvent);

            // By definition, as long as Attack Events are being sent, no other MicroService will perform unsubscribe
            // Because we wait for HanSolo and C3PO to register, the future won't be null. Checking just in case
            if (future != null) {
                FUTURES.add(future);
            }
        }

        // Monitoring the attacks status
        for (Future<?> future : FUTURES) {
            future.get();
        }
        // By definition, as long as Deactivation Events are being sent, no other MicroService will perform unsubscribe
        // Because we wait for R2D2 to register, the event won't be lost
        sendEvent(new DeactivationEvent());

        // Waiting for Lando's termination message
        subscribeBroadcast(TerminateBroadcast.class, message -> terminate());
    }

    @Override
    protected void close() {
        DIARY.setLeiaTerminate(System.currentTimeMillis());
    }
}
