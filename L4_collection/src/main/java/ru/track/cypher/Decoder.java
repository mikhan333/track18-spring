package ru.track.cypher;

import java.util.*;

import org.jetbrains.annotations.NotNull;

public class Decoder {

    // Расстояние между A-Z -> a-z
    public static final int SYMBOL_DIST = 32;

    private Map<Character, Character> cypher;

    /**
     * Конструктор строит гистограммы открытого домена и зашифрованного домена
     * Сортирует буквы в соответствие с их частотой и создает обратный шифр Map<Character, Character>
     *
     * @param domain - текст по кторому строим гистограмму языка
     */
    public Decoder(@NotNull String domain, @NotNull String encryptedDomain) {
        Map<Character, Integer> domainHist = createHist(domain);
        Map<Character, Integer> encryptedDomainHist = createHist(encryptedDomain);

        cypher = new LinkedHashMap<>();
        Iterator<Character> iter1 = domainHist.keySet().iterator();
        Iterator<Character> iter2 = encryptedDomainHist.keySet().iterator();
        char first, second;
        while(iter1.hasNext() && iter2.hasNext()) {
            first = iter1.next();
            second = iter2.next();
            cypher.put(second, first);

        }

    }

    public Map<Character, Character> getCypher() {
        return cypher;
    }

    /**
     * Применяет построенный шифр для расшифровки текста
     *
     * @param encoded зашифрованный текст
     * @return расшифровка
     */
    @NotNull
    public String decode(@NotNull String encoded) {
        StringBuilder build = new StringBuilder();
        char c;
        for(int i=0; i<encoded.length(); i++)
        {
            c = encoded.charAt(i);
            if (c>='A' && c<='Z' || c>='a' && c<='z')
            {
                build.append(getCypher().get(Character.toLowerCase(c)));
            }
            else {
                build.append(c);
            }

        }
        return build.toString();
    }

    /**
     * Считывает входной текст посимвольно, буквы сохраняет в мапу.
     * Большие буквы приводит к маленьким
     *
     *
     * @param text - входной текст
     * @return - мапа с частотой вхождения каждой буквы (Ключ - буква в нижнем регистре)
     * Мапа отсортирована по частоте. При итерировании на первой позиции наиболее частая буква
     */
    @NotNull
    Map<Character, Integer> createHist(@NotNull String text) {
        Map<Character, Integer> map=new HashMap<>();
        char c;
        for (int i=0;i<text.length();i++)
        {
            c = text.charAt(i);
            int obgC;
            if (c>='A' && c<='Z' || c>='a' && c<='z')
            {
                c = Character.toLowerCase(c);
                if (map.containsKey(c)==true)
                {
                    obgC = map.get(c);
                    map.put(c, obgC+1);
                }
                else
                {
                    map.put(c, 1);
                }
            }

        }

        List<Map.Entry<Character,Integer>> list = new ArrayList(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<Character, Integer>>() {
            @Override
            public int compare(Map.Entry<Character, Integer> a, Map.Entry<Character, Integer> b) {
                return b.getValue() - a.getValue();
            }
        });

        Map<Character, Integer> result=new LinkedHashMap<>();
        for (int i=0;i<map.size();i++)
        {
            result.put(list.get(i).getKey(),list.get(i).getValue());
        }
        return result;
    }

}
