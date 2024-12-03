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

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAKeyGenParameterSpec;
import java.security.spec.RSAPrivateKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
public class CozeHttpClient {

    @Value("${api.publicKey}")
    private String publicKey;

    @Value("${api.appId}")
    private String appId;

    private final String GET_TOKEN_URL = "https://api.coze.cn/api/permission/oauth2/token";

    public String getToken() throws UnsupportedEncodingException {
        Map<String, Object> mapHeader = new HashMap<>();
        mapHeader.put("alg", "RS256");
        mapHeader.put("typ", "JWT");
        mapHeader.put("kid", publicKey);
        Map<String, Object> mapPayload = new HashMap<>();
        mapPayload.put("iss", appId);
        mapPayload.put("aud", "api.coze.cn");
        mapPayload.put("iat", DateUtil.currentSeconds());
        mapPayload.put("exp", DateUtil.currentSeconds() + 3600);
        mapPayload.put("jti", RandomUtil.randomString(15));
        Gson gson = new Gson();
        String headerJson = gson.toJson(mapHeader);
        String playploadJson = gson.toJson(mapPayload);
        String encodedHeader = Base64.getUrlEncoder().encodeToString(headerJson.getBytes());
        String encodedPayload = Base64.getUrlEncoder().encodeToString(playploadJson.getBytes());
        PrivateKey privateKey = getPrivateKey();
        String unsignedToken = encodedHeader + "." + encodedPayload;
        Algorithm algorithm = Algorithm.RSA256((RSAPrivateKey) privateKey);
        byte[] sign = algorithm.sign(unsignedToken.getBytes("UTF-8"));
        String encodeSign = Base64.getUrlEncoder().encodeToString(sign);
        String token = encodedHeader + "." + encodedPayload + "." + encodeSign;
        HttpRequest post = HttpUtil.createPost(GET_TOKEN_URL);
        post.header("Content-Type", "application/json");
        post.header("Authorization", "Bearer " + token);
        HttpResponse response = post.body("{\"duration_seconds\": 86399,\"grant_type\": \"urn:ietf:params:oauth:grant-type:jwt-bearer\"}").execute();
        System.out.println(token);
        if (response.isOk()) {
            String body = response.body();
            Map map = gson.fromJson(body, Map.class);
            return (String) map.get("access_token");
        }
        return null;
    }

    /**
     * 获取keyStroe下的私钥文件内容private_key.pem
     */
    private PrivateKey getPrivateKey() {
        try {
            // 从类路径下读取PEM文件
            ClassPathResource resource = new ClassPathResource("keyStore/private_key.pem");
            InputStream inputStream = resource.getStream();
            // 将PEM格式转换为字节数组
            byte[] privateKeyBytes = inputStream.readAllBytes();
            String pem = new String(privateKeyBytes);
            // 去除PEM文件开头的 "-----BEGIN PRIVATE KEY-----"
            pem = pem.replace("-----BEGIN PRIVATE KEY-----", "");
            // 去除PEM文件结尾的 "-----END PRIVATE KEY-----"
            pem = pem.replace("-----END PRIVATE KEY-----", "");
            // 去除换行符等空白字符
            pem = pem.replaceAll("\\s", "");

            KeyFactory keyStore = KeyFactory.getInstance("RSA");
            RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(RSAKeyGenParameterSpec.F4, new BigInteger(pem, 16));
            return keyStore.generatePrivate(keySpec);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
