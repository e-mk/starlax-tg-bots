package com.mandos.roulette;

import com.mandos.BotCommunicationHelper;
import com.mandos.BotStateMachine;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class RouletteBot extends TelegramLongPollingBot implements BotStateMachine<RouletteBotState> {

    private static final String BOT_NAME = "roulette_bot";
    private static final String BOT_TOKEN = "5839464964:AAEAZZASeHOHXqhHbDoO1hbt2l0QpaaiOqc";
    public static final String COMMAND_START = "/start";
    public static final String TEXT_START = "<b>Please, make your bets</b>";
    public static final String TEXT_CANT_BET = "You cant bet now";
    public static final String TEXT_YOUR_BET = "Your bet is: ";
    public static final String TEXT_WINNING_NUMBER = "THE WINNING NUMBER IS: ";

    private final BotCommunicationHelper botCommunicationHelper = new BotCommunicationHelper(this);

    private final static List<List<String>> rouletteTable = new ArrayList<>(15);
    private static final List<String> rouletteBetsStrList;
    private List<String> userBets = new ArrayList<>();

    static {
        rouletteTable.add(new ArrayList<>(List.of("0 to 3", "0")));

//        rouletteTable.add(new ArrayList<>(List.of("1 to 3", "1", "2", "3", "1 to 6")));
//        rouletteTable.add(new ArrayList<>(List.of("4 to 6", "4", "5", "6", "1 to 6")));
//        rouletteTable.add(new ArrayList<>(List.of("1 to 3", "1", "2", "3", "1 to 6")));
//        rouletteTable.add(new ArrayList<>(List.of("1 to 3", "1", "2", "3", "1 to 6")));

        for (int i = 0; i < 12; i++) {
            var firstButtonTxt = String.format("%d to %d", i * 3 + 1, (i + 1) * 3);
            var numberButtonTxt1 = String.valueOf(i * 3 + 1);
            var numberButtonTxt2 = String.valueOf(i * 3 + 2);
            var numberButtonTxt3 = String.valueOf(i * 3 + 3);
            var lastButtonTxt = String.format("%d to %d", i * 3 + 1, (i + 2) * 3);
            if ((i + 1) % 4 == 0) {
                lastButtonTxt =  String.format("%d to %d", (i - 3) * 3 + 1, (i + 1) * 3);
            }
            rouletteTable.add(new ArrayList<>(List.of(firstButtonTxt, numberButtonTxt1, numberButtonTxt2, numberButtonTxt3, lastButtonTxt)));
        }

        rouletteTable.add(new ArrayList<>(List.of("1 to 18", "col 1", "col 2", "col 3", "19 to 36")));
        rouletteTable.add(new ArrayList<>(List.of("EVEN", "RED", "BLACK", "ODD")));

        rouletteBetsStrList = rouletteTable.stream()
                        .flatMap(List::stream)
                        .collect(Collectors.toList());
    }

    private RouletteBotState botState = RouletteBotState.START;

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println(update.toString());

        if (update.hasMessage()) {
            messageHandler(update.getMessage());
        }
    }

    private void messageHandler(Message msg) {
        var chatId = msg.getChatId();

        if(msg.isCommand()){
            if (msg.getText().equals(COMMAND_START)) {
                toState(chatId, RouletteBotState.START);
            }
        } else if (isBetTxt(msg.getText())) {
            if (botState == RouletteBotState.BETTING) {
                addBet(chatId, msg.getText());
                toState(chatId, RouletteBotState.ROLLING);
            } else {
                botCommunicationHelper.sendText(chatId, TEXT_CANT_BET);
            }
        }
    }

    private void addBet(Long chatId, String betText) {
        userBets.add(betText);
        botCommunicationHelper.sendText(chatId, TEXT_YOUR_BET + betText);
    }

    @Override
    public void toState(Long chatId, RouletteBotState newState) {
        System.out.printf("STATE changed to: %s", newState.getStateName());
        System.out.println(" ");
        botState = newState;
        if (RouletteBotState.START == newState) {
            toStartState(chatId);
        } else if (RouletteBotState.ROLLING == newState) {
            int number = RouletteRandNumGenerator.generateNumber();
            botCommunicationHelper.sendText(chatId, TEXT_WINNING_NUMBER + number);
        }
    }

    @Override
    public void toStartState(Long chatId) {
        botCommunicationHelper.sendKeyboard(chatId, TEXT_START, buildRouletteKeyboard());
        toState(chatId, RouletteBotState.BETTING);
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

    private List<KeyboardRow> buildRouletteKeyboard() {
        List<KeyboardRow> keyboard = new ArrayList<>();
        rouletteTable.forEach(row -> {
            KeyboardRow keyboardRow = new KeyboardRow();
            row.forEach(keyboardRow::add);
            keyboard.add(keyboardRow);
        });

        return keyboard;
    }

    private boolean isBetTxt(String txt) {
        return rouletteBetsStrList.contains(txt);
    }

    public static boolean isInteger(String s, int radix) {
        Scanner sc = new Scanner(s.trim());
        if(!sc.hasNextInt(radix)) return false;
        // we know it starts with a valid int, now make sure
        // there's nothing left!
        sc.nextInt(radix);
        return !sc.hasNext();
    }
}
