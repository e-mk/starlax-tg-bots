package com.mandos;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class InvestorBot1 extends TelegramLongPollingBot {

    private InlineKeyboardButton invest = InlineKeyboardButton.builder()
            .text("Invest").callbackData("invest")
            .build();

    private InlineKeyboardButton myInvestments = InlineKeyboardButton.builder()
            .text("My Investments").callbackData("my_investments")
            .build();

    private InlineKeyboardButton back = InlineKeyboardButton.builder()
            .text("Back").callbackData("back")
            .build();

    private InlineKeyboardMarkup keyboardStart = InlineKeyboardMarkup.builder()
            .keyboardRow(List.of(invest))
            .keyboardRow(List.of(myInvestments))
            .build();

    private InlineKeyboardMarkup keyboardInvest = InlineKeyboardMarkup.builder()
            .keyboardRow(List.of(back))
            .build();

    @Override
    public String getBotUsername() {
        return "investor_bot";
    }

    @Override
    public String getBotToken() {
        return "5987382692:AAFpbeiQjkt4sHaMdnkPFqDuHZQtGw3WjyM";
    }

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println(update.toString());

        if (update.hasMessage()) {
            messageHandler(update.getMessage());
        } else if (update.hasCallbackQuery()){
            callbackQueryHandler(update.getCallbackQuery());
        }
    }

    private void messageHandler(Message msg) {

        var chatId = msg.getChatId();
        var user = msg.getFrom();
        var id = user.getId();

        if(msg.isCommand()){
            if (msg.getText().equals("/start")) {
                sendMenu(chatId, "<b>Main Menu</b>", keyboardStart);
            } else if(msg.getText().equals("invest")) {
                sendText(chatId, "Invest");
            } else if (msg.getText().equals("my_investments")) {
                sendText(chatId, "Your Investment");
            }
        } else if (msg.getText().equals("Button 1")) {
            sendText(chatId, "Hello button1");
        }
    }

    private void callbackQueryHandler(CallbackQuery callbackQuery){
        try {
            buttonTap(callbackQuery.getFrom().getId(), callbackQuery.getId(),
                    callbackQuery.getData(), callbackQuery.getMessage().getMessageId());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void buttonTap(Long id, String queryId, String data, int msgId) throws TelegramApiException {

        EditMessageText newTxt = EditMessageText.builder()
                .chatId(id.toString())
                .messageId(msgId).text("").build();

        EditMessageReplyMarkup newKb = EditMessageReplyMarkup.builder()
                .chatId(id.toString()).messageId(msgId).build();

        if (data.equals("invest")) {
            newTxt.setText("Investment Menu");
            newKb.setReplyMarkup(keyboardInvest);

            AnswerCallbackQuery close = AnswerCallbackQuery.builder()
                    .callbackQueryId(queryId).build();

            execute(close);
            execute(newTxt);
            execute(newKb);
        } else if(data.equals("back")) {
            newTxt.setText("Main Menu");
            newKb.setReplyMarkup(keyboardStart);


            execute(newTxt);
            execute(newKb);
        } else if(data.equals("my_investments")) {

            KeyboardRow testKeyboard = new KeyboardRow();
            testKeyboard.add("Button 1");
            testKeyboard.add("Button 2");

            List<KeyboardRow> keyboard = new ArrayList<>();
            keyboard.add(testKeyboard);
            keyboard.add(testKeyboard);
            ReplyKeyboardMarkup keyboardMarkup = ReplyKeyboardMarkup.builder()
                    .oneTimeKeyboard(true)
                    .keyboard(keyboard)
                    .resizeKeyboard(true)
                    .build();

            sendMenu(id, "Active Investments", keyboardMarkup);
        }

        AnswerCallbackQuery close = AnswerCallbackQuery.builder()
                .callbackQueryId(queryId).build();

        execute(close);
    }

    public void sendText(Long who, String what){
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString()) //Who are we sending a message to
                .text(what).build();    //Message content
        try {
            execute(sm);                        //Actually sending the message
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);      //Any error will be printed here
        }
    }

    public void sendMenu(Long who, String txt, ReplyKeyboard kb){
        SendMessage sm = SendMessage.builder().chatId(who.toString())
                .parseMode("HTML").text(txt)
                .replyMarkup(kb).build();

        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}