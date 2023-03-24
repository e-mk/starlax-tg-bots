package com.mandos.slots;

import com.mandos.BotState;

public enum SlotsBotState implements BotState {

    NO_AUTH,

    PLAYING,

    WAITING_FOR_RESULT,

    ;

    @Override
    public String getStateName() {
        return this.name();
    }
}
