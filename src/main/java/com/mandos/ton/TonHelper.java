package com.mandos.ton;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.ton.block.MsgAddressInt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TonHelper {

    public static final String TON_URL = "https://mainnet-v4.tonhubapi.com";
    public static final String SMART_CONTRACT_ADDRESS = "EQCkR1cGmnsE45N4K0otPl5EnxnRakmGqeJUNua5fkWhales";
    public static final String SMART_CONTRACT_GET_STATE_ADDRESS = "EQAbSMAMUfKkSrOlp5RHRG7wLRb5a_BbzNFWKLpNGoEHSjLt";

    public static boolean isWalletAddress(String str) {
        try {
            MsgAddressInt.parse(str);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    public static JsonObject request(String urlStr) {
        StringBuilder content = new StringBuilder();
        try {
            URL url = new URL(urlStr);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int status = con.getResponseCode();

            BufferedReader in;
            if (status > 299) {
                in = new BufferedReader(
                        new InputStreamReader(con.getErrorStream()));
            } else {
                in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
            }

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Gson gson = new Gson();
        return gson.fromJson(content.toString(), JsonObject.class);
    }
}
