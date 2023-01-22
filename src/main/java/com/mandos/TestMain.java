package com.mandos;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.ton.cell.Cell;
import org.ton.cell.CellBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TestMain {

//    "https://sandbox-v4.tonhubapi.com/block/%s/EQCkR1cGmnsE45N4K0otPl5EnxnRakmGqeJUNua5fkWhales/run/<method>/<args?>"

    public static final String TON_URL = "https://mainnet-v4.tonhubapi.com";
    public static final String SMART_CONTRACT_ADDRESS = "EQCkR1cGmnsE45N4K0otPl5EnxnRakmGqeJUNua5fkWhales";

    public static void main(String[] args) {

        String latestBlockUrlStr = String.format("%s/block/latest", TON_URL);
        JsonObject latestBlockResponse = request(latestBlockUrlStr);
        String seqno = latestBlockResponse.get("last").getAsJsonObject().get("seqno").getAsString();
        System.out.println(seqno);

        Cell cell = CellBuilder.beginCell(2).endCell();

        String memberBalanceUrlStr = String.format("%s/block/%s/%s/run/get_member_balance/te6ccgEBAwEAMgACDwAAAQQAELAgAgEAQ4AGQBhaP4EBAeSASUr7jsCFl42g1exLLB6txV689EeaN1AAAA==", TON_URL, seqno, SMART_CONTRACT_ADDRESS);

        JsonObject memberBalanceResponse = request(memberBalanceUrlStr);
        System.out.println(memberBalanceResponse);
    }

    static JsonObject request(String urlStr) {
        StringBuilder content = new StringBuilder();;
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
