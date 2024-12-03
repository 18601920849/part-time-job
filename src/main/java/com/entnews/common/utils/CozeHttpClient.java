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
        String encodedHeader = Base64.getUrlEncoder().withoutPadding().encodeToString(headerJson.getBytes());
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(playploadJson.getBytes());
        PrivateKey privateKey = getPrivateKey();
        String unsignedToken = encodedHeader + "." + encodedPayload;
        Algorithm algorithm = Algorithm.RSA256((RSAPrivateKey) privateKey);
        byte[] sign = algorithm.sign(unsignedToken.getBytes("utf-8"));
        // 对签名进行 Base64 编码
        String encodedSign = Base64.getUrlEncoder().withoutPadding().encodeToString(sign);
        HttpRequest post = HttpUtil.createPost(GET_TOKEN_URL);
        post.header("Content-Type", "application/json");
        post.header("Authorization", "Bearer " + encodedSign);
        HttpResponse response = post.body("{\"duration_seconds\": 86399,\"grant_type\": \"urn:ietf:params:oauth:grant-type:jwt-bearer\"}").execute();
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

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
