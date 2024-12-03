package com.entnews.rest;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.entnews.common.msg.Result;
import com.entnews.common.utils.CozeHttpClient;
import com.entnews.service.DemoService;
import com.google.gson.Gson;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/demo")
public class DemoController {

    @Resource
    private DemoService demoService;

    @Resource
    private CozeHttpClient cozeHttpClient;

    @RequestMapping("/getDemo")
    public Result<String> getDemo() {
        try {
            String token = cozeHttpClient.getToken();
            return Result.ok(token);
        } catch (UnsupportedEncodingException e) {
            return Result.fail(e.getMessage());
        }

    }
}
