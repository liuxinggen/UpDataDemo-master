package com.text.updatademo.utils;

import java.security.MessageDigest;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author duai
 * @version V1.0
 * @Title: upload
 * @Package com.xgs.net.utils
 * @Description: TODO
 * @date 2017-11-23 14:41
 */
public class SignUtils {
    private static final String hexDigits[] = { "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
    final static String PREFIX = "GANSUTUBOAPPCO.CN";
    public static String sign(String appId, Map<String,String> data){
        if (null==appId||"".equals(appId)){
            throw new RuntimeException("签名ID不能为空");
        }
        if (null==data||data.isEmpty()){
            throw new RuntimeException("签名数据不能为空");
        }
        appId = getSignKey(appId);
        Map<String,String> result = sortMapByKey(data);
        StringBuilder sb = new StringBuilder();
        for (String key:result.keySet()){
            sb.append(key+"="+result.get(key));
        }
        String token = MD5Encode(appId+sb.toString()+appId,"UTF-8").toUpperCase();
        return token;
    }
    private static String byteArrayToHexString(byte b[]) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++)
            resultSb.append(byteToHexString(b[i]));

        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0) {
            n += 256;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }
    private static String MD5Encode(String origin, String charsetname) {
        String resultString = null;
        try {
            resultString = new String(origin);
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (charsetname == null || "".equals(charsetname)) {
                resultString = byteArrayToHexString(md.digest(resultString
                        .getBytes()));
            } else {
                resultString = byteArrayToHexString(md.digest(resultString
                        .getBytes(charsetname)));
            }
        } catch (Exception exception) {
        }
        return resultString;
    }
    private static Map<String, String> sortMapByKey(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        Map<String, String> sortMap = new TreeMap<String, String>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        sortMap.putAll(map);
        return sortMap;
    }
    private static String getSignKey(String appId){
        String result = "";
        for (int i = appId.length()-1;i>=0;i--){
            result+=appId.charAt(i);
        }
        return PREFIX+result;
    }
}
