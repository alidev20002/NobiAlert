package com.example.nobialert;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class NobiUtils {

    public static int getIcon(String name) {
        switch (name) {
            case "BTC":
                return R.drawable.btc;
            case "ETH":
                return R.drawable.eth;
            case "LTC":
                return R.drawable.ltc;
            case "USDT":
                return R.drawable.usdt;
            case "XRP":
                return R.drawable.xrp;
            case "BCH":
                return R.drawable.bch;
            case "BNB":
                return R.drawable.bnb;
            case "EOS":
                return R.drawable.eos;
            case "XLM":
                return R.drawable.xlm;
            case "ETC":
                return R.drawable.etc;
            case "TRX":
                return R.drawable.trx;
            case "DOGE":
                return R.drawable.doge;
            case "UNI":
                return R.drawable.uni;
            case "DAI":
                return R.drawable.dai;
            case "LINK":
                return R.drawable.link;
            case "DOT":
                return R.drawable.dot;
            case "AAVE":
                return R.drawable.aave;
            case "ADA":
                return R.drawable.ada;
            case "SHIB":
                return R.drawable.shib;
        }
        return 0;
    }

    public static long getPrice(String ARZ_NAME) {
        long price = 0;
        String API = "https://api.nobitex.ir/market/stats?";
        StringBuilder sb = new StringBuilder();
        try
        {
            URL url = new URL(API + "srcCurrency=" + ARZ_NAME + "&dstCurrency=rls");
            try
            {
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    sb.append(inputLine);
                in.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        try
        {
            JSONObject jo = new JSONObject(sb.toString());
            JSONObject stats = jo.getJSONObject("stats");
            JSONObject rls = stats.getJSONObject(ARZ_NAME + "-rls");
            String latest = rls.getString("latest");
            double pd = Double.parseDouble(latest);
            String formatted = new BigDecimal(pd).toString();
            price = Long.parseLong(formatted)/10;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return price;
    }

    public static ArrayList<Long> getPrice2(ArrayList<Coin> coins) {
        ArrayList<String> names = new ArrayList<>();
        for (Coin c: coins)
            names.add(c.getName());
        String link = "https://api.nobitex.ir/market/stats?srcCurrency=";
        ArrayList<Long> prices = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            link += names.get(i);
            if (i < names.size() - 1)
                link += ",";
        }
        link += "&dstCurrency=rls";
        StringBuilder sb = new StringBuilder();
        try
        {
            URL url = new URL(link);
            try
            {
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    sb.append(inputLine);
                in.close();
                try
                {
                    JSONObject jo = new JSONObject(sb.toString());
                    JSONObject stats = jo.getJSONObject("stats");

                    for (int i = 0; i < names.size(); i++) {
                        JSONObject rls = stats.getJSONObject(names.get(i) + "-rls");
                        String latest = rls.getString("latest");
                        double pd = Double.parseDouble(latest);
                        String formatted = new BigDecimal(pd).toString();
                        prices.add(Long.parseLong(formatted)/10);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return prices;
    }

}
