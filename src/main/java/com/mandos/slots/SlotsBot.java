package com.mandos.slots;

import com.mandos.BotCommunicationHelper;
import com.mandos.BotStateMachine;
import com.mandos.ton.TonHelper;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public class SlotsBot extends TelegramLongPollingBot implements BotStateMachine<SlotsBotState> {

    private static final String BOT_NAME = "slots_bot";
    private static final String BOT_TOKEN = "5843227964:AAFss6DUzFoprTAlXeZeex8wCLa6rWn3jM4";

    public static final String COMMAND_START = "/start";
    public static final String COMMAND_SPIN = "/spin";
    public static final String CALLBACK_DONE = "callback_done";
    public static final String CALLBACK_CANCEL = "callback_cancel";
    public static final String TEXT_ENTER_WALLET = "<b>Please, enter your wallet address</b>";
    public static final String TEXT_NEED_AUTH = "Please, Log in with your wallet address to play";
    public static final String TEXT_SPINNING = "Spinning...";
    public static final String TEXT_YOU_WON = "YOU WON!!!!";
    public static final String TEXT_AUTH_WALLET_IS = "Authenticated with wallet: ";
    public static final String TEXT_DONE = "Done";
    public static final String TEXT_CANCEL = "Cancel";
    public static final String TEXT_BET_WITH_WALLET = "Please, transfer your bet to STARLAX_WALLET from your authenticated wallet and press DONE";

    private final BotCommunicationHelper botCommunicationHelper = new BotCommunicationHelper(this);

    private SlotsBotState botState = SlotsBotState.NO_AUTH;

    private String authenticatedWallet = "";

    private final InlineKeyboardMarkup keyboardDone = InlineKeyboardMarkup.builder()
            .keyboardRow(List.of(InlineKeyboardButton.builder()
                    .callbackData(CALLBACK_DONE)
                    .text(TEXT_DONE)
                    .build()))
            .keyboardRow(List.of(InlineKeyboardButton.builder()
                    .callbackData(CALLBACK_CANCEL)
                    .text(TEXT_CANCEL)
                    .build()))
            .build();

    private int latestMenuMessageId;

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
        String queryId = callbackQuery.getId();
        Long chatId = callbackQuery.getFrom().getId();
        String data = callbackQuery.getData();
        botCommunicationHelper.sendEditMessage(chatId, latestMenuMessageId);
        if (CALLBACK_DONE.equals(data)) {
            toState(chatId, SlotsBotState.WAITING_FOR_RESULT);
        } else if (CALLBACK_CANCEL.equals(data)) {

        }
        botCommunicationHelper.sendCallbackQueryClose(queryId);
    }

    private void messageHandler(Message msg) {
        var chatId = msg.getChatId();

        if(msg.isCommand()){
            if (msg.getText().equals(COMMAND_START)) {
                toState(chatId, SlotsBotState.NO_AUTH);
            }
        } else {
            if (botState == SlotsBotState.PLAYING) {
//            addBet(chatId, msg.getText());
                toState(chatId, SlotsBotState.PLAYING);
            } else if (botState == SlotsBotState.NO_AUTH) {
                if (TonHelper.isWalletAddress(msg.getText())) {
                    doAuthenticate(chatId, msg.getText());
                } else {
                    botCommunicationHelper.sendText(chatId, TEXT_NEED_AUTH);
                }
            }
        }
    }

    private void doAuthenticate(Long chatId, String walletAddress) {
        authenticatedWallet = walletAddress;
        toState(chatId, SlotsBotState.PLAYING);
    }

    @Override
    public void toState(Long chatId, SlotsBotState newState) {
        System.out.printf("STATE changed to: %s", newState.getStateName());
        System.out.println(" ");
        botState = newState;
        if (SlotsBotState.NO_AUTH == newState) {
            if (authenticatedWallet.isEmpty()) {
                botCommunicationHelper.sendText(chatId, TEXT_ENTER_WALLET);
            } else {
                botCommunicationHelper.sendText(chatId, TEXT_AUTH_WALLET_IS + authenticatedWallet);
                toState(chatId, SlotsBotState.PLAYING);
            }
        } else if (SlotsBotState.PLAYING == newState) {
            latestMenuMessageId = botCommunicationHelper.sendMenu(chatId, TEXT_BET_WITH_WALLET, keyboardDone);
        } else if (SlotsBotState.WAITING_FOR_RESULT == newState) {
            botCommunicationHelper.sendText(chatId, TEXT_SPINNING);
            requestResult(chatId);

        }
    }

    private void requestResult(Long chatId) {
        goSleep(3000);
        botCommunicationHelper.sendText(chatId, TEXT_YOU_WON);
        toState(chatId, SlotsBotState.PLAYING);
    }

    private void goSleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void toStartState(Long chatId) {

    }

    @Override
    public void cleanKeyboard(Long chatId, String txt) {
        botCommunicationHelper.sendRemoveKeyboard(chatId, txt);
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }
}
