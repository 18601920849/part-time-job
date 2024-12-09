package com.entnews.common.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;

@Component
public class CozeHttpClient {

    @Value("${api.publicKey}")
    private String publicKey;

    @Value("${api.appId}")
    private String appId;

    @Value("${api.botId}")
    private String botId;

    private final String GET_TOKEN_URL = "https://api.coze.cn/api/permission/oauth2/token";

    private final String CHAT_URL = "https://api.coze.cn/v1/conversation/create";

    private final String SEND_MSG_URL = "https://api.coze.cn/v3/chat";

    private final String RETRIEVE_URL = "https://api.coze.cn/v3/chat/retrieve";

    private final String GET_MSG_URL = "https://api.coze.cn/v3/chat/message/list";

    public String getToken() throws UnsupportedEncodingException {
        Map<String, Object> mapHeader = new HashMap<>();
        mapHeader.put("alg", "RS256");
        mapHeader.put("typ", "JWT");
        mapHeader.put("kid", "IztsDCjVvMkZdOMpFOnaqY60wgXmru05fSCvmHpHrSw");
        Map<String, Object> mapPayload = new HashMap<>();
        mapPayload.put("iss", "1169482334402");
        mapPayload.put("aud", "api.coze.cn");
        mapPayload.put("iat", DateUtil.currentSeconds());
        mapPayload.put("exp", DateUtil.currentSeconds() + 3600);
        mapPayload.put("jti", RandomUtil.randomString(15));
        // 将 mapHeader 和 mapPayload 转换为 JSON 字符串
        Gson gson = new Gson();
        String headerJson = gson.toJson(mapHeader);
        String payloadJson = gson.toJson(mapPayload);

        // 对 JSON 字符串进行 Base64 编码
        String encodedHeader = Base64.getUrlEncoder().withoutPadding().encodeToString(headerJson.getBytes());
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(payloadJson.getBytes());

        PrivateKey rsaPrivateKey = readPrivateKey();
        Algorithm algorithm = Algorithm.RSA256((RSAPrivateKey) rsaPrivateKey);
        byte[] sign = algorithm.sign((encodedHeader + "." + encodedPayload).getBytes("utf-8"));
        // 对签名进行 Base64 编码
        String encodedSign = Base64.getUrlEncoder().withoutPadding().encodeToString(sign);
        System.out.println(encodedSign);
        HttpRequest post = HttpUtil.createPost(GET_TOKEN_URL);
        post.header("Content-Type", "application/json");
        post.header("Authorization", "Bearer " + encodedHeader + "." + encodedPayload + "." + encodedSign);
        HttpResponse response = post.body("{\"duration_seconds\": 86399,\"grant_type\": \"urn:ietf:params:oauth:grant-type:jwt-bearer\"}").execute();
        if (response.isOk()) {
            String body = response.body();
            Map map = gson.fromJson(body, Map.class);
            return (String) map.get("access_token");
        }
        return null;
    }

    // 从文件中读取私钥的方法
    private static PrivateKey readPrivateKey() {
        // 读取密钥文件
        // 从类路径下读取PEM文件
        ClassPathResource resource = new ClassPathResource("keyStore/private_key.pem");
        InputStream inputStream = resource.getStream();
        // 将PEM格式转换为字节数组
        byte[] privateKeyBytes = null;
        try {
            privateKeyBytes = inputStream.readAllBytes();
            String privateKeyContent = new String(privateKeyBytes);

            // 去掉头尾标识
            String privateKeyPEM = privateKeyContent.replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");

            // Base64解码
            byte[] encodedKey = Base64.getDecoder().decode(privateKeyPEM);

            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(encodedKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(spec);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }


    public String createChat(String token) {
        HttpRequest post = HttpUtil.createPost(CHAT_URL);
        post.header("Content-Type", "application/json");
        post.header("Authorization", "Bearer " + token);
        HttpResponse response = post.execute();
        Gson gson = new Gson();
        if (response.isOk()) {
            String body = response.body();
            Map map = gson.fromJson(body, Map.class);
            return gson.toJson(map.get("data"));
        }
        return null;
    }

    public String sendMsg(String token, String conversationId, String message) {
        Gson gson = new Gson();
        HttpRequest post = HttpUtil.createPost(SEND_MSG_URL + "?conversation_id=" + conversationId);
        post.header("Content-Type", "application/json");
        post.header("Authorization", "Bearer " + token);
        Map<String, Object> mapMsg = new HashMap();
        mapMsg.put("bot_id", botId);
        mapMsg.put("user_id", "entnews");
        mapMsg.put("stream", false);
        mapMsg.put("auto_save_history", true);
        List<Map<String, Object>> messages = new ArrayList();
        Map messag = new HashMap();
        messag.put("role", "user");
        messag.put("content", message);
        messag.put("content_type", "text");
        messag.put("type", "answer");
        messages.add(messag);
        mapMsg.put("additional_messages", messages);
        HttpResponse response = post.body(gson.toJson(mapMsg)).execute();
        if (response.isOk()) {
            String body = response.body();
            Map map = gson.fromJson(body, Map.class);
            Map data = (Map) map.get("data");
            System.out.println(data);
            return (String) data.get("id");
        }
        return null;
    }

    public String getRetrieve(String token, String chatId, String conversationId) {
        Gson gson = new Gson();
        HttpRequest get = HttpUtil.createGet(RETRIEVE_URL+"?chat_id="+chatId+"&conversation_id="+conversationId);
        get.header("Content-Type", "application/json");
        get.header("Authorization", "Bearer " + token);
        HttpResponse response = get.execute();
        if (response.isOk()) {
            String body = response.body();
            Map map = gson.fromJson(body, Map.class);
            Map data = (Map) map.get("data");
            return (String) data.get("status");
        }
        return null;
    }

    public String getMsg(String token, String chatId, String conversationId) {
        HttpRequest get = HttpUtil.createGet(GET_MSG_URL+"?chat_id="+chatId+"&conversation_id="+conversationId);
        get.header("Content-Type", "application/json");
        get.header("Authorization", "Bearer " + token);
        HttpResponse response = get.execute();
        if (response.isOk()) {
            return response.body();
        }
        return null;
    }

}
