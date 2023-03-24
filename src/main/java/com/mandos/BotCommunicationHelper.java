package com.mandos;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

public class BotCommunicationHelper {

    private TelegramLongPollingBot tgLongPollingBot;

    public BotCommunicationHelper(TelegramLongPollingBot tgLongPollingBot) {
        this.tgLongPollingBot = tgLongPollingBot;
    }

    public void sendText(Long chatId, String txt){
        SendMessage sm = SendMessage.builder()
                .chatId(chatId.toString()) //Who are we sending a message to
                .parseMode("HTML")
                .text(txt)
                .build();    //Message content
        try {
            tgLongPollingBot.execute(sm);                        //Actually sending the message
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);      //Any error will be printed here
        }
    }

    public Integer sendMenu(Long chatId, String txt, ReplyKeyboard kb){
        SendMessage sm = SendMessage.builder()
                .chatId(chatId.toString())
                .parseMode("HTML")
                .replyMarkup(kb)
                .text(txt)
                .build();

        try {
            Message a = tgLongPollingBot.execute(sm);
            return a.getMessageId();
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }


    public void sendKeyboard(Long chatId, String txt, List<KeyboardRow> keyboard) {
        ReplyKeyboardMarkup keyboardMarkup = ReplyKeyboardMarkup.builder()
                .oneTimeKeyboard(true)
                .keyboard(keyboard)
                .resizeKeyboard(true)
                .build();

        sendMenu(chatId, txt, keyboardMarkup);
    }

    public void sendRemoveKeyboard(Long chatId, String txt) {
        ReplyKeyboardRemove replyKeyboardRemove = ReplyKeyboardRemove.builder()
                .removeKeyboard(true)
                .build();

        sendMenu(chatId, txt, replyKeyboardRemove);
    }

    public void sendCallbackQueryClose(String queryId) {
        AnswerCallbackQuery close = AnswerCallbackQuery.builder()
                .callbackQueryId(queryId)
                .build();

        try {
            tgLongPollingBot.execute(close);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendEditMessage(Long chatId, Integer messageId) {
        EditMessageReplyMarkup clearInlineKb = EditMessageReplyMarkup.builder()
                .messageId(messageId)
                .chatId(chatId.toString())
                .replyMarkup(null)
                .build();

        try {
            tgLongPollingBot.execute(clearInlineKb);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }


}
