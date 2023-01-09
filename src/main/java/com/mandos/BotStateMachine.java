package com.mandos;

public interface BotStateMachine {

    void toState(BotState state);

    void toStartState();

    void cleanKeyboard();
}
