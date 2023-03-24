package com.mandos.investor;

import com.mandos.BotState;

public enum InvestorBotState implements BotState {

    NO_AUTH,

    AUTHENTICATED,

    INVEST_MENU,

    INVESTING,

    ACTIVE_INVESTMENTS

    ;

    @Override
    public String getStateName() {
        return this.name();
    }
}
