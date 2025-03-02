package org.javakov.algorithm;

public class PorterStemmerEn {
    private StringBuilder word;

    /**
     * Проверяет, является ли символ гласной.
     */
    private boolean isVowel(char c) {
        if ("aeiou".indexOf(c) != -1) {
            return true;
        }
        if (c == 'y') {
            int index = word.indexOf(String.valueOf(c));
            if (index == 0) {
                return false; // y в начале слова — согласная
            }
            return !isVowel(word.charAt(index - 1)); // y после согласной — гласная
        }
        return false;
    }

    /**
     * Проверяет, есть ли в слове хотя бы одна гласная.
     */
    private boolean containsVowel() {
        for (int i = 0; i < word.length(); i++) {
            if (isVowel(word.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Удаляет суффиксы и приставки по правилам стеммера Портера.
     */
    public String stem(String word) {
        if (word == null || word.length() < 3) {
            return word;
        }
        this.word = new StringBuilder(word.toLowerCase());
        step1();
        step2();
        return this.word.toString();
    }

    /**
     * Применяет базовые правила удаления суффиксов (Шаг 1).
     */
    private void step1() {
        String[][] suffixRules = {
                {"ing", "3", "vowel"},   // playing -> play
                {"ed", "2", "vowel"},    // played -> play
                {"ies", "3", "y"},       // babies -> baby
                {"ly", "2", "vowel"},    // quickly -> quick
                {"less", "4", ""},       // hopeless -> hope
                {"ful", "3", ""},        // beautiful -> beauty
                {"es", "2", "esRule"},   // boxes -> box
                {"s", "1", "sRule"},     // cats -> cat
                {"able", "4", "vowel"},  // playable -> play
                {"en", "2", "vowel"},    // eaten -> eat
                {"ify", "3", "restoreE"}, // simplify -> simple
                {"ize", "3", "izeRule"}, // realize -> real
                {"ous", "3", ""},        // dangerous -> danger
                {"ive", "3", ""},        // active -> act
                {"al", "2", "restoreE"}, // natural -> nature
                {"ness", "4", ""},       // kindness -> kind
                {"ment", "4", ""},       // development -> develop
                {"ion", "3", "restoreT"}, // action -> act
                {"ity", "3", "restoreE"}, // ability -> able
                {"ism", "3", ""}         // criticism -> critic
        };

        // Проходим по каждому правилу
        for (String[] rule : suffixRules) {
            String suffix = rule[0];
            int suffixLength = Integer.parseInt(rule[1]);
            String condition = rule[2];

            if (word.length() > suffixLength && word.toString().endsWith(suffix)) {
                switch (condition) {
                    case "vowel":
                        if (containsVowel()) {
                            word.setLength(word.length() - suffixLength);
                            removeDoubleConsonant();
                        }
                        break;
                    case "y":
                        if (containsVowel()) {
                            word.setLength(word.length() - suffixLength);
                            word.append('y');
                        }
                        break;
                    case "esRule":
                        char lastCharBeforeEs = word.charAt(word.length() - 3);
                        if ("sxz".indexOf(lastCharBeforeEs) != -1 ||
                                (lastCharBeforeEs == 'h' && "cs".indexOf(word.charAt(word.length() - 4)) != -1)) {
                            word.setLength(word.length() - suffixLength);
                        }
                        break;
                    case "sRule":
                        char lastCharBeforeS = word.charAt(word.length() - 2);
                        if (!isVowel(lastCharBeforeS) && lastCharBeforeS != 's' && lastCharBeforeS != 'x' && lastCharBeforeS != 'z') {
                            word.setLength(word.length() - suffixLength);
                        }
                        break;
                    case "restoreE":
                        word.setLength(word.length() - suffixLength);
                        if (word.length() > 1 && !isVowel(word.charAt(word.length() - 1)) &&
                                word.charAt(word.length() - 1) != 'x' && word.charAt(word.length() - 1) != 'w') {
                            word.append('e');
                        }
                        break;
                    case "restoreT":
                        word.setLength(word.length() - suffixLength);
                        if (word.length() > 1 && word.charAt(word.length() - 1) == 't') {
                            break;
                        }
                        break;
                    case "izeRule":
                        if (word.toString().endsWith("ize")) {
                            word.setLength(word.length() - 3);
                            if (!word.isEmpty() && !isVowel(word.charAt(word.length() - 1))) {
                                if (word.length() > 1 && !isVowel(word.charAt(word.length() - 2))) {
                                    word.append('e');
                                }
                            }
                        }
                        break;
                    default:
                        word.setLength(word.length() - suffixLength);
                        break;
                }
            }
        }

        // Обработка суффикса "able" с восстановлением исходного слова
        if (word.length() > 5 && word.toString().endsWith("able")) {
            int originalLength = word.length();
            word.setLength(word.length() - 4);
            if (!containsVowel()) {
                word.setLength(originalLength);
            }
        }
    }

    /**
     * Дополнительная обработка для восстановления основы (Шаг 2).
     */
    private void step2() {
        // Если слово заканчивается на "i" и перед ним согласная, заменяем "i" на "y" (studied -> study)
        if (word.length() > 2 && word.charAt(word.length() - 1) == 'i') {
            if (!isVowel(word.charAt(word.length() - 2))) {
                word.setCharAt(word.length() - 1, 'y');
            }
        }
    }

    /**
     * Удаляет удвоенную согласную в конце слова, если она есть (hopping -> hop)
     */
    private void removeDoubleConsonant() {
        if (word.length() > 1 && word.charAt(word.length() - 1) == word.charAt(word.length() - 2)) {
            word.setLength(word.length() - 1);
        }
    }
}