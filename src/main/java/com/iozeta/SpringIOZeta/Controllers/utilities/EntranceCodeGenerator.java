package com.iozeta.SpringIOZeta.Controllers.utilities;

import java.util.Random;

public class EntranceCodeGenerator {
    private final int codeLength = 6;
    private final String chars = "23456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijmnopqrstuvwxyz";


    public String generateCode() {
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < codeLength; i++)
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        return sb.toString();
    }
}