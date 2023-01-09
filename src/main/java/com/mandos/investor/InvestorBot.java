package com.mandos.investor;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class InvestorBot extends TelegramLongPollingBot {



    @Override
    public void onUpdateReceived(Update update) {
        System.out.println(update.toString());

        if (update.hasMessage()) {
            messageHandler(update.getMessage());
        } else if (update.hasCallbackQuery()){
            callbackQueryHandler(update.getCallbackQuery());
        }
    }

    private void callbackQueryHandler(CallbackQuery callbackQuery) {

    }

    private void messageHandler(Message message) {

    }

    @Override
    public String getBotUsername() {
        return "investor_bot";
    }

    @Override
    public String getBotToken() {
        return "5987382692:AAFpbeiQjkt4sHaMdnkPFqDuHZQtGw3WjyM";
    }

}
