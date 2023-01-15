package com.mandos.roulette;

import com.mandos.BotState;

public enum RouletteBotState implements BotState {

    START,

    BETTING,

    ROLLING,

    RESULT

    ;

    @Override
    public String getStateName() {
        return this.name();
    }
}
