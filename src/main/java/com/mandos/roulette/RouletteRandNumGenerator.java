package com.mandos.roulette;

public class RouletteRandNumGenerator {

    public static int generateNumber() {
        int min = 0;
        int max = 36;
        // Generate random int value from min to max
        int randNum = (int) Math.floor(Math.random() * (max - min + 1) + min);
        // Printing the generated random numbers
        System.out.println(randNum);

        return randNum;
    }
}
