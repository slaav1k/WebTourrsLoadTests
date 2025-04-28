package ru.slaav1k.items;

import java.util.Random;

public class DataGenerator {

    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String SPECIAL = "!@#$%^&*";

    // Вспомогательный метод для генерации случайной строки из букв
    public static String generateRandomString(Random rand, int minLength, int maxLength) {
        int length = rand.nextInt(maxLength - minLength + 1) + minLength;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(LETTERS.charAt(rand.nextInt(LETTERS.length())));
        }
        return sb.toString();
    }

    // Вспомогательный метод для генерации пароля
    public static String generatePassword(Random rand) {
        String all = UPPER + LOWER + DIGITS + SPECIAL;

        StringBuilder password = new StringBuilder();
        // Гарантируем минимум 1 символ каждого типа
        password.append(UPPER.charAt(rand.nextInt(UPPER.length())));      // 1 большая
        password.append(LOWER.charAt(rand.nextInt(LOWER.length())));      // 1 маленькая
        password.append(DIGITS.charAt(rand.nextInt(DIGITS.length())));    // 1 цифра
        password.append(SPECIAL.charAt(rand.nextInt(SPECIAL.length())));  // 1 спецсимвол

        // Заполняем оставшиеся 6 символов
        for (int i = 4; i < 10; i++) {
            password.append(all.charAt(rand.nextInt(all.length())));
        }

        // Перемешиваем
        char[] chars = password.toString().toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        return new String(chars);
    }
}
