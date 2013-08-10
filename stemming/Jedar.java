/*
 * This Java class is the implementation of  Jedar, a rule-based stemmer for 
 * both dialects of the Kurdish  lanuage (i.e. Sorani and Kurmanji).
 *
 * Jedar was developed by Shahin Salavati, a member of the Kurdish Language Processing Project (KLPP) team, 
 * at University of Kurdistan, Sanandaj, Iran.
 * For more information about KLPP, see: 
 *      http://eng.uok.ac.ir/esmaili/research/klpp/en/main.htm
 *
 *
 ********************** Citation information ****************************
 * S. Salavati, K. Sheykh Esmaili and F. Akhlaghian, "Stemming for Kurdish Information Retrieval", 
 * In the Proceedings of the 9th Asia Information Retrieval Societies Conference (AIRS'13),
 * Singapore, December 2013.
 ************************************************************************
 *
 * @author Shahin Salavati
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

public class Jedar {

    public static HashMap suffixRemover(String word, String suffixlist[]) {//Crate a candidate list before any stemming rules
        ArrayList<String> detectedSuffix = new ArrayList<String>();
        HashMap<String, Integer> candidate = new HashMap<String, Integer>();
        candidate.put(word, word.length());
        ArrayList<String> temp = new ArrayList<String>();
        temp.add(word);
        for (int iter = 0; iter < temp.size(); iter++) {
            word = temp.get(iter);
            for (int i = 0; i < suffixlist.length; i++) {
                if (word.endsWith("ستان")) {
                    continue;
                }
                if (word.endsWith(suffixlist[i]) && !detectedSuffix.contains(suffixlist[i])) {
                    detectedSuffix.add(suffixlist[i]);
                    String cut = word.substring(0, word.length() - suffixlist[i].length());
                    if (temp.indexOf(cut) < 0 && cut.length() > 1) {
                        if (cut.length() > 0) {
                            if (cut.charAt(cut.length() - 1) == (char) 8204) {
                                cut = cut.substring(0, cut.length() - 1);
                            }
                        }
                        candidate.put(cut, cut.length());
                        temp.add(cut);
                    }
                }
            }
        }
        return candidate;

    }

    public static String[] readsimple(String path) { //read a file into an String araay
        File f = new File(path);
        String[] pars;
        ArrayList<String> terms = new ArrayList<String>();
        try {
            Scanner scan = new Scanner(f);
            while (scan.hasNext()) {
                terms.add(scan.nextLine());
            }
            scan.close();
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        }
        pars = new String[terms.size()];
        for (int i = 0; i < pars.length; i++) {
            pars[i] = terms.get(i).toString();
        }
        return pars;
    }

    public static String selectStem(String word, HashMap candid, int minLen) {//select best candidate by done rules and minimum length


        String[] ignoredMinLength = {"گرتنی", "گه‌ر", "بوو", "بوون",
            "بووه", "بووبن", "بێت", "بووم"};
        String[] possessionPerson = {"مانیان", "مانتان", "یانمان", "یانتان", "تانمان", "تانمان",
            "مانمان", "تانتان", "یانیان"};

        if (word.length() < minLen) {
            return word;
        }

        String stem = new String();
        for (int i = 0; i < ignoredMinLength.length; i++) {
            if (word.contains(ignoredMinLength[i])) {
                minLen = 4;
            }
        }
        String currentPS = new String();
        for (int i = 0; i < possessionPerson.length; i++) {
            if (word.contains(possessionPerson[i])) {
                currentPS = possessionPerson[i];
            }
        }

        Iterator it = candid.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            int len = Integer.valueOf(pairs.getValue().toString());
            String term = pairs.getKey().toString();
            if (len < minLen) {
                it.remove();
            }
        }
        int min = 50;
        stem = "";
        Iterator it1 = candid.entrySet().iterator();
        while (it1.hasNext()) {
            Map.Entry pairs = (Map.Entry) it1.next();
            String candidstem = pairs.getKey().toString();
            if (currentPS.length() > 0 && (!candidstem.contains("مان")
                    && !candidstem.contains("یان") && !candidstem.contains("تان"))) {
                if (!word.contains("که") && !word.contains("کان") && !word.contains("ێک")) {
                    continue;
                }
            }
            if (min > Integer.valueOf(pairs.getValue().toString())) {
                min = Integer.valueOf(pairs.getValue().toString());
                stem = pairs.getKey().toString();
            }
        }
        return stem;
    }

    public static String getStem(String word, int minLength) {
        String suffix[] = readsimple("suffixes.txt");
        String start = String.valueOf((char) 65279);
        if (suffix[0].startsWith(start)) {
            suffix[0] = suffix[0].substring(1);
        }
        HashMap m = suffixRemover(word, suffix);
        return selectStem(word, m, minLength);

    }

    public static void main(String[] args) throws FileNotFoundException {
        String word = "کتیوێک";//Input word
        int minLength = 3;
        String stem = getStem(word, minLength);
        System.out.println("Stem of " + word + " is:\t" + stem);
    }
}

