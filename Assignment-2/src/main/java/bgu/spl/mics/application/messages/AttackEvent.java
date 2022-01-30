package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Attack;

public class AttackEvent implements Event<Boolean> {

    final Attack ATTACK;

    public AttackEvent(Attack attack) {
        ATTACK = attack;
    }

    public Attack getATTACK() {
        return ATTACK;
    }
}