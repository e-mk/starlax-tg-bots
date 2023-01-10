package com.mandos;

public interface BotStateMachine<T extends BotState> {

    void toState(Long chatId, T state);

    void toStartState(Long chatId);

    void cleanKeyboard(Long chatId, String txt);

}
