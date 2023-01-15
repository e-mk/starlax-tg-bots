package com.mandos.investor;

import com.mandos.BotCommunicationHelper;
import com.mandos.BotStateMachine;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvestorBot extends TelegramLongPollingBot implements BotStateMachine<InvestorBotState> {

    private static final String BOT_NAME = "investor_bot";
    private static final String BOT_TOKEN = "5987382692:AAFpbeiQjkt4sHaMdnkPFqDuHZQtGw3WjyM";
    public static final String TEXT_WELCOME = "<b>Welcome bitch!!</b>";
    public static final String COMMAND_START = "/start";
    public static final String MSG_INVEST = "Invest";
    public static final String MSG_ACTIVE_INVESTMENTS = "Active Investments";
    public static final String TEXT_START = "<b>Please, select to invest for new investments, or check your portfolio.</b>";
    public static final String TEXT_INVESTMENT_MENU_HEADER = "<b>Games to invest</b>";
    public static final String TEXT_INVESTMENT_MENU = "Please, select the game you want to invest in";
    public static final String TEXT_ACTIVE_INVESTMENTS_NO_INVESTMENTS = "You don't have active investments yet. Please go to Invest menu and select a game to invest in";

    private final BotCommunicationHelper botCommunicationHelper = new BotCommunicationHelper(this);

    private InvestorBotState botState = InvestorBotState.START;

    private final Map<String, String> investmentGamesMap;

    private final InlineKeyboardMarkup keyboardInvest;

    {
        investmentGamesMap = new HashMap<>();
        investmentGamesMap.put("invest_game_1", "Game 1");
        investmentGamesMap.put("invest_game_2", "Game 2");
        investmentGamesMap.put("invest_game_3", "Game 3");

        var keyboardInvestBuilder = InlineKeyboardMarkup.builder();
        investmentGamesMap.forEach((key, name) ->  {
            keyboardInvestBuilder.keyboardRow(List.of(InlineKeyboardButton.builder()
                    .callbackData(key)
                    .text(name)
                    .build()));
        });

        keyboardInvest = keyboardInvestBuilder.build();
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

    private void callbackQueryHandler(CallbackQuery callbackQuery) {
        String queryId = callbackQuery.getId();
        Long chatId = callbackQuery.getFrom().getId();
        String data = callbackQuery.getData();
        int msgId = callbackQuery.getMessage().getMessageId();

        if (data.startsWith("invest_game_")) {
            botCommunicationHelper.sendText(chatId, generateGameSelectedTxt(investmentGamesMap.get(data)));
            toState(chatId, InvestorBotState.INVESTING);
        } else {
            botCommunicationHelper.sendText(chatId, "Game not found");
        }

        botCommunicationHelper.sendCallbackQueryClose(queryId);
    }

    private void messageHandler(Message msg) {
        var chatId = msg.getChatId();

        if(msg.isCommand()){
            if (msg.getText().equals(COMMAND_START)) {
                toState(chatId, InvestorBotState.START);
            }
        } else if (msg.getText().equals(MSG_INVEST)) {
            toState(chatId, InvestorBotState.INVEST_MENU);
        } else if (msg.getText().equals(MSG_ACTIVE_INVESTMENTS)) {
            toState(chatId, InvestorBotState.ACTIVE_INVESTMENTS);
        }
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void toState(Long chatId, InvestorBotState newState) {
        if (newState == InvestorBotState.START) {
            toStartState(chatId);
        } else if(newState == InvestorBotState.INVEST_MENU) {
            cleanKeyboard(chatId, TEXT_INVESTMENT_MENU);
            botCommunicationHelper.sendMenu(chatId, TEXT_INVESTMENT_MENU_HEADER, keyboardInvest);
        } else if (newState == InvestorBotState.INVESTING) {

        } else if (newState == InvestorBotState.ACTIVE_INVESTMENTS) {
            cleanKeyboard(chatId, TEXT_ACTIVE_INVESTMENTS_NO_INVESTMENTS);
        }
        botState = newState;
    }

    @Override
    public void toStartState(Long chatId) {
        KeyboardRow startKeyboardRaw = new KeyboardRow();
        startKeyboardRaw.add(MSG_INVEST);
        startKeyboardRaw.add(MSG_ACTIVE_INVESTMENTS);

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(startKeyboardRaw);
        botCommunicationHelper.sendKeyboard(chatId, TEXT_START, keyboard);
    }

    @Override
    public void cleanKeyboard(Long chatId, String txt) {
        botCommunicationHelper.sendRemoveKeyboard(chatId, txt);
    }

    public String generateGameSelectedTxt(String gameName) {
        return String.format("You have selected game: %s", gameName);
    }
}
