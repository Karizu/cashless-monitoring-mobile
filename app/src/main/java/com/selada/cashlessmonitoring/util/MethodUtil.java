package com.selada.cashlessmonitoring.util;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.selada.cashlessmonitoring.R;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Dhimas on 9/29/17.
 */

public class MethodUtil extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        MethodUtil.context = getApplicationContext();
    }

    public static String toCurrencyFormat(final String value) {

        if (!TextUtils.isEmpty(value)) {
            String formattedPrice = value.replaceAll("[^\\d]", "");

            String reverseValue = new StringBuilder(formattedPrice).reverse().toString();
            StringBuilder finalValue = new StringBuilder();
            for (int i = 1; i <= reverseValue.length(); i++) {
                char val = reverseValue.charAt(i - 1);
                finalValue.append(val);
                if (i % 3 == 0 && i != reverseValue.length() && i > 0) {
                    finalValue.append(".");
                }
            }

            return finalValue.reverse().toString();
        }

        return StringUtils.EMPTY;
    }

    public static String toDateFormat(final String value) {
        if (!TextUtils.isEmpty(value)) {
            String formattedPrice = value.replaceAll("[^\\d]", "");

            String reverseValue = new StringBuilder(formattedPrice).reverse().toString();
            StringBuilder finalValue = new StringBuilder();
            for (int i = 1; i <= reverseValue.length(); i++) {
                char val = reverseValue.charAt(i - 1);
                finalValue.append(val);
                if (i % 2 == 0 && i != reverseValue.length() && i > 0) {
                    finalValue.append("/");
                }
            }

            return finalValue.reverse().toString();
        }

        return StringUtils.EMPTY;
    }

    public static String strToDateFormat(final String value) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat fromUser = new SimpleDateFormat("yyyyMM");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat myFormat = new SimpleDateFormat("MMM yy");

        try {
            return myFormat.format(fromUser.parse(value));
        } catch (ParseException e) {
            e.printStackTrace();
            return value;
        }
    }

    public static String formatTokenNumber(final String number) {
        String cleanString = number.replace(" ", "");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < cleanString.length(); i++) {
            if (i % 4 == 0 && i != 0) {
                result.append("-");
            }

            result.append(cleanString.charAt(i));
        }

        return result.toString();
    }

    public static String formatCardNumber(final String number) {
        String cleanString = number.replace(" ", "");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < cleanString.length(); i++) {
            if (i % 4 == 0 && i != 0) {
                result.append(" ");
            }

            result.append(cleanString.charAt(i));
        }

        return result.toString();
    }

    public static String[] formatDateAndTime(String dateTime) {
        String[] tempDateTime = new String[2];
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", new Locale("id")).parse(dateTime);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale("id","ID"));
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH : mm : ss");
            tempDateTime[0] = dateFormat.format(date);
            tempDateTime[1] = timeFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return tempDateTime;
    }

    public static String formatDateCreditcard(String date) {
        String cleanDate = date.trim();
        StringBuilder result = new StringBuilder();
        for (int i =0; i < cleanDate.length() ;i++) {
            if (i % 2 == 0 && i != 0) {
                result.append("/");
            }
            result.append(cleanDate.charAt(i));
        }
        return result.toString();

    }

    public static String getResponseError(String json) throws JSONException {
        JSONObject jObjError = new JSONObject(json);
        return jObjError.getString("error");
    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] stringToBytes(String plaintext) {
        return plaintext.getBytes(StandardCharsets.UTF_8);
    }

    public static String encodeToString(byte[] bytes) {
//        String credentials = new String(plaintext, StandardCharsets.UTF_8);
//        String credentials =  Base64.encodeToString(plaintext, Base64.DEFAULT);
//        return Base64.encodeToString(plaintext, Base64.NO_WRAP);

        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] IV() {
        byte[] IV = new byte[16];
        SecureRandom random;
        random = new SecureRandom();
        random.nextBytes(IV);
        return IV;
    }

    public static byte[] encrypt(byte[] plaintext, byte[] key, byte[] IV) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
//        IvParameterSpec ivSpec = new IvParameterSpec(IV);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        return cipher.doFinal(plaintext);
    }

    public static String decrypt(byte[] cipherText, byte[] key, byte[] IV) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
//            IvParameterSpec ivSpec = new IvParameterSpec(IV);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decryptedText = cipher.doFinal(cipherText);
            return new String(decryptedText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String doActivatePin(String plainText, String member_id, String created_at) {
        try {
            String date = created_at.replace("-","");
            date = date.replace(":","");
            date = date.replace(" ", "");
            date = date + "SL";
            String key = member_id.substring(0, 16)+date;

            try {
                byte[] keyGen = encrypt(stringToBytes(plainText), stringToBytes(key), IV());
                Log.d("KEYGENSEC", bytesToHex(keyGen));
                return bytesToHex(keyGen);
//                byte[] decKeyGen = hexStringToByteArray(encKeyGen);
//                Log.d("DECRYPT: ", decrypt(decKeyGen, stringToBytes(key), IV()));

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }
}
