package org.javakov.algorithm;

public class PorterStemmerEn {
    /**
     * Буфер для хранения символов слова.
     */
    private char[] b;

    /**
     * Текущий индекс в буфере.
     */
    private int i;

    /**
     * Конечный индекс обработанного слова.
     */
    private int i_end;

    /**
     * Вспомогательные индексы для манипуляций с окончанием слова.
     */
    private int j, k;

    /**
     * Шаг увеличения размера буфера.
     */
    private static final int INC = 50;

    /**
     * Конструктор инициализирует буфер и переменные.
     */
    public PorterStemmerEn() {
        b = new char[INC];
        i = 0;
        i_end = 0;
    }

    /**
     * Добавляет одиночный символ в буфер.
     *
     * @param ch символ для добавления
     */
    public void add(char ch) {
        if (i == b.length) {
            char[] new_b = new char[i + INC];
            System.arraycopy(b, 0, new_b, 0, i);
            b = new_b;
        }
        b[i++] = ch;
    }

    /**
     * Добавляет массив символов в буфер.
     *
     * @param w    массив символов
     * @param wLen длина добавляемого массива
     */
    public void add(char[] w, int wLen) {
        if (i + wLen >= b.length) {
            char[] new_b = new char[i + wLen + INC];
            if (i >= 0) System.arraycopy(b, 0, new_b, 0, i);
            b = new_b;
        }
        for (int c = 0; c < wLen; c++) b[i++] = w[c];
    }

    /**
     * Преобразует буфер в строку.
     *
     * @return обработанное слово в виде строки
     */
    public String toString() {
        return new String(b, 0, i_end);
    }

    /**
     * Проверяет, является ли символ согласной.
     *
     * @param i индекс символа
     * @return true, если символ - согласная, иначе false
     */
    private boolean cons(int i) {
        return switch (b[i]) {
            case 'a', 'e', 'i', 'o', 'u' -> false;
            case 'y' -> i == 0 || !cons(i - 1);
            default -> true;
        };
    }

    /**
     * Вычисляет число последовательностей (СГ) в слове, где С - согласная, Г - гласная.
     *
     * @return количество последовательностей (СГ)
     */
    private int m() {
        int n = 0;
        int i = 0;
        while (true) {
            if (i > j) return n;
            if (!cons(i)) break;
            i++;
        }
        i++;
        while (true) {
            while (true) {
                if (i > j) return n;
                if (cons(i)) break;
                i++;
            }
            i++;
            n++;
            while (true) {
                if (i > j) return n;
                if (!cons(i)) break;
                i++;
            }
            i++;
        }
    }

    /**
     * Проверяет, содержит ли слово хотя бы одну гласную.
     *
     * @return true, если в слове есть гласная, иначе false
     */
    private boolean vowelinstem() {
        for (int i = 0; i <= j; i++) if (!cons(i)) return true;
        return false;
    }

    /**
     * Проверяет, содержит ли слово удвоенную конечную согласную.
     *
     * @param j индекс символа
     * @return true, если два последних символа одинаковы и являются согласными
     */
    private boolean doublec(int j) {
        if (j < 1) return false;
        if (b[j] != b[j - 1]) return false;
        return cons(j);
    }

    /**
     * Проверяет, является ли слово в виде CVC (согласная-гласная-согласная) с исключениями.
     *
     * @param i индекс символа
     * @return true, если слово имеет формат CVC
     */
    private boolean cvc(int i) {
        if (i < 2 || !cons(i) || cons(i - 1) || !cons(i - 2)) return false;
        int ch = b[i];
        return ch != 'w' && ch != 'x' && ch != 'y';
    }

    /**
     * Проверяет, оканчивается ли слово на заданную строку.
     *
     * @param s строка-суффикс
     * @return true, если слово оканчивается на s, иначе false
     */
    private boolean ends(String s) {
        int l = s.length();
        int o = k - l + 1;
        if (o < 0) return false;
        for (int i = 0; i < l; i++) if (b[o + i] != s.charAt(i)) return false;
        j = k - l;
        return true;
    }

    /**
     * Устанавливает новый суффикс для слова.
     *
     * @param s новая строка-суффикс
     */
    private void sett(String s) {
        int l = s.length();
        int o = j + 1;
        for (int i = 0; i < l; i++) b[o + i] = s.charAt(i);
        k = j + l;
    }

    /**
     * Заменяет окончание слова на новое, если выполняется условие m() > 0.
     *
     * @param s новая строка-суффикс
     */
    private void r(String s) {
        if (m() > 0) sett(s);
    }

    /**
     * step1() удаляет формы множественного числа и окончания -ed или -ing. Например:
     * <p>
     * caresses  ->  caress
     * ponies    ->  poni
     * ties      ->  ti
     * caress    ->  caress
     * cats      ->  cat
     * <p>
     * feed      ->  feed
     * agreed    ->  agree
     * disabled  ->  disable
     * <p>
     * matting   ->  mat
     * mating    ->  mate
     * meeting   ->  meet
     * milling   ->  mill
     * messing   ->  mess
     * <p>
     * meetings  ->  meet
     */
    private void step1() {
        if (b[k] == 's') {
            if (ends("sses")) k -= 2;
            else if (ends("ies")) sett("i");
            else if (b[k - 1] != 's') k--;
        }
        if (ends("eed")) {
            if (m() > 0) k--;
        } else if ((ends("ed") || ends("ing")) && vowelinstem()) {
            k = j;
            if (ends("at")) sett("ate");
            else if (ends("bl")) sett("ble");
            else if (ends("iz")) sett("ize");
            else if (doublec(k)) {
                k--;
                {
                    int ch = b[k];
                    if (ch == 'l' || ch == 's' || ch == 'z') k++;
                }
            } else if (m() == 1 && cvc(k)) sett("e");
        }
    }

    /**
     * step2() заменяет конечную y на i, если в корне есть другая гласная.
     */
    private void step2() {
        if (ends("y") && vowelinstem()) b[k] = 'i';
    }

    /**
     * step3() сокращает двойные суффиксы до одиночных. Например, -ization
     * ( = -ize + -ation) превращается в -ize и т. д.
     * Перед суффиксом m() должно быть > 0.
     */
    private void step3() {
        if (k == 0) return;
        switch (b[k - 1]) {
            case 'a':
                if (ends("ational")) {
                    r("ate");
                    break;
                }
                if (ends("tional")) {
                    r("tion");
                    break;
                }
                break;
            case 'c':
                if (ends("enci")) {
                    r("ence");
                    break;
                }
                if (ends("anci")) {
                    r("ance");
                    break;
                }
                break;
            case 'e':
                if (ends("izer")) {
                    r("ize");
                    break;
                }
                break;
            case 'l':
                if (ends("bli")) {
                    r("ble");
                    break;
                }
                if (ends("alli")) {
                    r("al");
                    break;
                }
                if (ends("entli")) {
                    r("ent");
                    break;
                }
                if (ends("eli")) {
                    r("e");
                    break;
                }
                if (ends("ousli")) {
                    r("ous");
                    break;
                }
                break;
            case 'o':
                if (ends("ization")) {
                    r("ize");
                    break;
                }
                if (ends("ation")) {
                    r("ate");
                    break;
                }
                if (ends("ator")) {
                    r("ate");
                    break;
                }
                break;
            case 's':
                if (ends("alism")) {
                    r("al");
                    break;
                }
                if (ends("iveness")) {
                    r("ive");
                    break;
                }
                if (ends("fulness")) {
                    r("ful");
                    break;
                }
                if (ends("ousness")) {
                    r("ous");
                    break;
                }
                break;
            case 't':
                if (ends("aliti")) {
                    r("al");
                    break;
                }
                if (ends("iviti")) {
                    r("ive");
                    break;
                }
                if (ends("biliti")) {
                    r("ble");
                    break;
                }
                break;
            case 'g':
                if (ends("logi")) {
                    r("log");
                    break;
                }
        }
    }

    /**
     * step4() обрабатывает окончания -ic-, -full, -ness и т. д.
     * Используется аналогичная стратегия, как в step3.
     */
    private void step4() {
        switch (b[k]) {
            case 'e':
                if (ends("icate")) {
                    r("ic");
                    break;
                }
                if (ends("ative")) {
                    r("");
                    break;
                }
                if (ends("alize")) {
                    r("al");
                    break;
                }
                break;
            case 'i':
                if (ends("iciti")) {
                    r("ic");
                    break;
                }
                break;
            case 'l':
                if (ends("ical")) {
                    r("ic");
                    break;
                }
                if (ends("ful")) {
                    r("");
                    break;
                }
                break;
            case 's':
                if (ends("ness")) {
                    r("");
                    break;
                }
                break;
        }
    }

    /**
     * step5() удаляет окончания -ant, -ence и другие в контексте <c>vcvc<v>.
     */
    private void step5() {
        if (k == 0) return;
        switch (b[k - 1]) {
            case 'a':
                if (ends("al")) break;
                return;
            case 'c':
                if (ends("ance")) break;
                if (ends("ence")) break;
                return;
            case 'e':
                if (ends("er")) break;
                return;
            case 'i':
                if (ends("ic")) break;
                return;
            case 'l':
                if (ends("able")) break;
                if (ends("ible")) break;
                return;
            case 'n':
                if (ends("ant")) break;
                if (ends("ement")) break;
                if (ends("ment")) break;
                if (ends("ent")) break;
                return;
            case 'o':
                if (ends("ion") && j >= 0 && (b[j] == 's' || b[j] == 't')) break;
                if (ends("ou")) break;
                return;
            case 's':
                if (ends("ism")) break;
                return;
            case 't':
                if (ends("ate")) break;
                if (ends("iti")) break;
                return;
            case 'u':
                if (ends("ous")) break;
                return;
            case 'v':
                if (ends("ive")) break;
                return;
            case 'z':
                if (ends("ize")) break;
                return;
            default:
                return;
        }
        if (m() > 1) k = j;
    }

    /**
     * step6() удаляет конечную -e, если m() > 1.
     */
    private void step6() {
        j = k;
        if (b[k] == 'e') {
            int a = m();
            if (a > 1 || a == 1 && !cvc(k - 1)) k--;
        }
        if (b[k] == 'l' && doublec(k) && m() > 1) k--;
    }

    public void stem() {
        k = i - 1;
        if (k > 1) {
            step1();
            step2();
            step3();
            step4();
            step5();
            step6();
        }
        i_end = k + 1;
        i = 0;
    }
}
