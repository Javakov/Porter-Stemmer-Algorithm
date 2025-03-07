package org.javakov.algorithm;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class PorterStemmerRu {
    /**
     * Регулярное выражение для поиска совершенных глаголов и их форм.
     */
    private static final Pattern perfectiveGroundPattern =
            Pattern.compile("((ив|ивши|ившись|ыв|ывши|ывшись)|((?<=[ая])(в|вши|вшись)))$");

    /**
     * Регулярное выражение для поиска прилагательных.
     */
    private static final Pattern adjectivePattern =
            Pattern.compile("(ее|ие|ые|ое|ими|ыми|ей|ий|ый|ой|ем|им|ым|ом|его|ого|ему|ому|их|ых|ую|юю|ая|яя|ою|ею)$");

    /**
     * Регулярное выражение для поиска причастий.
     */
    private static final Pattern participlePattern =
            Pattern.compile("((ивш|ывш|ующ)|((?<=[ая])(ем|нн|вш|ющ|щ)))$");

    /**
     * Регулярное выражение для поиска глаголов.
     */
    private static final Pattern verbPattern =
            Pattern.compile("((ила|ыла|ена|ейте|уйте|ите|или|ыли|ей|уй|ил|ыл|им|ым|ен|ило|ыло|ено|ят|ует|уют|ит|ыт|ены|ить|ыть|ишь|ую|ю)|((?<=[ая])(ла|на|ете|йте|ли|й|л|ем|н|ло|но|ет|ют|ны|ть|ешь|нно)))$");

    /**
     * Регулярное выражение для поиска существительных.
     */
    private static final Pattern nounPattern =
            Pattern.compile("(а|ев|ов|ие|ье|е|иями|ями|ами|еи|ии|и|ией|ей|ой|ий|й|иям|ям|ием|ем|ам|ом|о|у|ах|иях|ях|ы|ь|ию|ью|ю|ия|ья|я)$");

    /**
     * Регулярное выражение для поиска возвратных глаголов.
     */
    private static final Pattern reflexivePattern =
            Pattern.compile("(с[яь])$");

    /**
     * Регулярное выражение для выделения корня (RV) и остаточной части слова.
     */
    private static final Pattern rootPattern =
            Pattern.compile("^(.*?[аеиоуыэюя])(.*)$");

    /**
     * Регулярное выражение для проверки наличия производных форм слова.
     */
    private static final Pattern derivationalPattern =
            Pattern.compile(".*[^аеиоуыэюя]+[аеиоуыэюя].*ость?$");

    /**
     * Регулярное выражение для удаления суффикса "ость".
     */
    private static final Pattern derivativeSuffixPattern =
            Pattern.compile("ость?$");

    /**
     * Регулярное выражение для поиска превосходной степени.
     */
    private static final Pattern superlativePattern =
            Pattern.compile("(ейше|ейш)$");

    /**
     * Регулярное выражение для поиска суффикса "и".
     */
    private static final Pattern iSuffixPattern =
            Pattern.compile("и$");

    /**
     * Регулярное выражение для поиска суффикса "ь".
     */
    private static final Pattern softSignPattern =
            Pattern.compile("ь$");

    /**
     * Регулярное выражение для поиска удвоенного "нн".
     */
    private static final Pattern doubleNPattern =
            Pattern.compile("нн$");

    /**
     * Метод для стеммирования слова.
     * Применяет различные регулярные выражения для удаления суффиксов и редукции
     * слова до его основы.
     *
     * @param word слово, которое требуется привести к основе
     * @return основанное слово
     */
    public static String stem(String word) {
        word = word.toLowerCase().replace('ё', 'е');

        // Сопоставляем слово с регулярным выражением, которое разделяет слово на приставку и корень
        Matcher matcher = rootPattern.matcher(word);
        if (matcher.matches()) {
            // Извлекаем приставку (первую часть) и корень (вторую часть) из строки
            String prefix = matcher.group(1);
            String root = matcher.group(2);
            if (root.isEmpty()) return word;

            // Удаляем суффиксы совершенного вида (например, "ив", "ивши", "вши", и т.д.)
            String temp = perfectiveGroundPattern.matcher(root).replaceFirst("");
            if (temp.equals(root)) {
                // Удаляем рефлексивные окончания (например, "сь", "ся")
                root = reflexivePattern.matcher(root).replaceFirst("");

                // Удаляем прилагательные суффиксы (например, "ее", "ий", "ая", и т.д.)
                temp = adjectivePattern.matcher(root).replaceFirst("");
                if (!temp.equals(root)) {
                    root = temp;
                    // Удаляем причастия (например, "ющий", "вший", и т.д.)
                    root = participlePattern.matcher(root).replaceFirst("");
                } else {
                    // Если прилагательное не найдено, проверяем на глаголы
                    temp = verbPattern.matcher(root).replaceFirst("");
                    if (temp.equals(root)) {
                        // Если глагол не найден, проверяем на существительные
                        root = nounPattern.matcher(root).replaceFirst("");
                    } else {
                        root = temp;
                    }
                }
            } else {
                // Если суффикс совершенного вида был удален, обновляем корень
                root = temp;
            }

            // Удаляем суффикс "и", если он есть
            root = iSuffixPattern.matcher(root).replaceFirst("");

            // Проверяем, является ли слово производным (например, "дружбы" -> "дружб")
            Matcher derivMatcher = derivationalPattern.matcher(root);
            if (derivMatcher.find()) {
                // Удаляем производные суффиксы (например, "ость", "ность")
                root = derivativeSuffixPattern.matcher(root).replaceFirst("");
            }

            // Удаляем суффикс "ь", если он есть
            root = softSignPattern.matcher(root).replaceFirst("");
            // проверяем на превосходную степень
            root = superlativePattern.matcher(root).replaceFirst("");
            // заменяем "нн" на "н"
            root = doubleNPattern.matcher(root).replaceFirst("н");

            // Восстанавливаем полное слово: приставка + корень
            word = prefix + root;
        }

        return word;
    }
}
