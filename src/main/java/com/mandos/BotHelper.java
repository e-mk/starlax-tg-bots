package com.mandos;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

public class BotHelper {

    public void sendText(Long chatId, String what, TelegramLongPollingBot tgLongPollingBot){
        SendMessage sm = SendMessage.builder()
                .chatId(chatId.toString()) //Who are we sending a message to
                .text(what).build();    //Message content
        try {
            tgLongPollingBot.execute(sm);                        //Actually sending the message
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);      //Any error will be printed here
        }
    }

    public void sendMenu(Long chatId, String txt, ReplyKeyboard kb, TelegramLongPollingBot tgLongPollingBot){
        SendMessage sm = SendMessage.builder()
                .chatId(chatId.toString())
                .parseMode("HTML")
                .replyMarkup(kb)
                .text(txt)
                .build();

        try {
            tgLongPollingBot.execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }


    public void sendKeyBoard(Long chatId, String txt, List<KeyboardRow> keyboard, TelegramLongPollingBot tgLongPollingBot) {
        ReplyKeyboardMarkup keyboardMarkup = ReplyKeyboardMarkup.builder()
                .oneTimeKeyboard(true)
                .keyboard(keyboard)
                .resizeKeyboard(true)
                .build();

        sendMenu(chatId, txt, keyboardMarkup, tgLongPollingBot);
    }
}
