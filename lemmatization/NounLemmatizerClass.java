/*
 * This Java class is the main part of implementation of a lemmatizer (morphological analyzer) for 
 * the Sorani dialect of the Kurdish  lanuage.
 * To run this class, four other files are required: 
 * - an second Java class (VerbLemmatizerClass) which is in charge of analyzing Sorani verbs
 * - a  text file (exception.txt) containing a list of exceptional words that must stay intact 
 * - two other text files (pastBon.txt and presentBon.txt) which include the past and present 
 *   form of most common Sorani verbs.
 *
 *
 * These codes have been  developed by Shahin Salavati, a member of the Kurdish Language Processing Project (KLPP) team, 
 * at University of Kurdistan, Sanandaj, Iran.
 * For more information about KLPP, see: 
 *      http://eng.uok.ac.ir/esmaili/research/klpp/en/main.htm
 *
 *
 * @author Shahin Salavati
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author Shahin
 */
public class NounLemmatizerClass {

    static String[] beforePos = {"که", "کان", "ێک", "یش", "یه‌ت", "بوو", "تر", "ترین", "ه‌ک", "ان"};
    static String[] beforePos1 = {"که", "کان", "ێک", "یش", "یه‌ت", "بوو", "تر", "ترین", "ه‌ک"};
    static String[] possesion = {"مان", "تان", "یان", "م", "ت"};
    static String[] firstPoss = {"م", "ت"};
    static String[] definite = {"که", "ێک", "کا"};
    static String[] definiteAndPlural = {"که", "کان", "ێک", "ان", "کا"};
    static String[] plurals = {"کان", "ه‌کان", "ان"};
    static String[] adjSign = {"تر", "ترین"};
    static String[] commonVerb = {"a"};//{"بوو", "کرد"};
    static String[] verbPostfix = {"ووه", "ۆته", "ۆته‌وه", "ووه‌ته‌وه", "ووه‌ته", "وه‌ته‌وه", "ۆتوه"};
    static String zwnj = String.valueOf((char) 8204);
    static String h_end = "ە", h_zwnj = "ه" + zwnj;
    static ArrayList<String> beforePs, allPs;
    public static String[] exception = readsimple("exception.txt");
    public static String suffixlist[] = readsimple("suffix.txt");
    public static String[] pastVerb = VerbLemmatizerClass.readsimple("pastBon.txt");
    public static ArrayList verb = VerbLemmatizerClass.readToArray("pastBon.txt");
    public static ArrayList prVerb = VerbLemmatizerClass.readToArray("presentBon.txt");
    public static String[] presentVerb = VerbLemmatizerClass.readsimple("presentBon.txt");
    public static String modified_term = new String(), currentExc = new String();

    public NounLemmatizerClass() {
        beforePs = new ArrayList<String>();
        allPs = new ArrayList<String>();
        for (int i = 0; i < beforePos.length; i++) {
            beforePs.add(beforePos[i]);
        }
        for (int i = 0; i < possesion.length; i++) {
            allPs.add(possesion[i]);
        }
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
        String start = String.valueOf((char) 65279);
        if (pars.length > 0) {
            if (pars[0].startsWith(start)) {
                pars[0] = pars[0].substring(1);
            }
        }

        return pars;
    }

    public static String getMatch(String input, String[] list) {
        String max = new String();
        for (int i = 0; i < list.length; i++) {
            String term = list[i];
            if ((input.startsWith(term)) && max.length() < term.length()) {
                max = list[i];
            }
        }
        return max;

    }

    public static boolean isMakeOfSuffix(String word, String[] suffix) {


        boolean detect = true;
        while (detect) {
            detect = false;
            for (int j = 0; j < suffix.length; j++) {
                String suf = suffix[j];

                if (word.endsWith(suf)) {
                    word = word.substring(0, word.indexOf(suf));
                    detect = true;
                    if (word.length() > 0) {
                        if (word.charAt(word.length() - 1) == (char) 8204) {
                            word = word.substring(0, word.length() - 1);
                        }
                    }
                }
            }
        }
        if (word.length() == 0) {
            return true;
        }
        return false;
    }

    public static String getMatch1(String input, String[] list) {
        String max = new String();
        for (int i = 0; i < list.length; i++) {
            String term = list[i];
            
         if(term.startsWith(zwnj))
            term=term.substring(1);
        if(term.endsWith(zwnj))
            term=term.substring(0,term.length()-1);

            if((input.equals(term)))
                return term;
            if ((input.startsWith(term))) {
                String sub = input.substring(term.length());
                if (sub.length() > 0) {

                    if (sub.charAt(0) == (char) 8204) {
                        sub = sub.substring(1);
                    }
                    if (isMakeOfSuffix(sub, suffixlist) && term.length() * 3 > input.length()) {
                        max = list[i];
                        continue;
                    }
                }
            }
            if (((input.contains(term)) && term.length() * 2 > input.length() && max.length() < term.length())) {
                max = list[i];
            }
        }
        return max;

    }

    public static boolean isEqualOf(String sub, String[] list) {
        for (int i = 0; i < list.length; i++) {
            if (sub.equals(list[i])) {
                return true;
            }
        }
        return false;
    }

    public static boolean isMemberOf(String sub, String[] list) {
        for (int i = 0; i < list.length; i++) {
            if (sub.contains(list[i])) {
                return true;
            }
        }
        return false;
    }

    public static ArrayList getAllEnds(String word, String[] exception, String[] suffixlist, String exp) {
        String original = word;
        String copy1 = word;
        boolean hasPs = false;
        modified_term = "";
        ArrayList<String> detectedSuffix = new ArrayList();
        if (word.equals(exp)) {
            return detectedSuffix;
        }
//        if (word.startsWith(exp) && exp.length() > 0) {
//            detectedSuffix.add(word.substring(exp.length()));
//            return detectedSuffix;
//        }

        String start = String.valueOf((char) 65279);



        if (word.length() < 3) {
            return detectedSuffix;
        }

        String maxSuff = new String();
        String lastSuff = new String();
        boolean detected = true;
        String currentSuff = new String();
        String[] nonSuffix = {"ستان", "خانه", "نامه", "خان", "چی", "جان", "چه","زده","پاک"};

        String nonS = new String();
        for (int i = 0; i < nonSuffix.length; i++) {

            if (word.endsWith("یستان") || word.endsWith("ێستان") && word.length() < 8) {
                nonS = "ستان";
                break;
            }
            if (word.contains(nonSuffix[i]) && !word.contains("یستان") && !word.contains("ێستان")) {

                nonS = nonSuffix[i];
                break;
            }

        }
//        if (detectedSuffix.size() > 0) {
//            word = word.substring(0, word.length() - 1);
//            if (!word.contains(exp)) {
//                word = original;
//                detectedSuffix.remove(0);
//            }
//            if (word.endsWith(exp)) {
//                return detectedSuffix;
//            }
//        }
        while (detected) {
            detected = false;
            maxSuff = "";
            for (int i = 0; i < suffixlist.length; i++) {
                original = word;
                currentSuff = suffixlist[i];
//                if (word.endsWith("ستان") || word.endsWith("خانه") || word.endsWith("نامه")
//                        || word.endsWith("خان") || word.endsWith("چی")
//                        || word.endsWith("جان") || word.endsWith("چه")) {
//                    break;
//                }
                if (word.endsWith(currentSuff) && word.length() - currentSuff.length() >= 2) {
                    String temp = word.substring(0, word.length() - currentSuff.length());

                    if ((temp.length() == 3 && temp.endsWith("ه" + zwnj)) ||
                            temp.endsWith("ئ")
                            ) {
                        continue;
                    }
                    if (currentSuff.equals("نن") || currentSuff.equals("مم")) {
                        temp = word.substring(0, word.length() - 1);
                    }
                    //  if (!temp.contains(exp) || !temp.contains(nonS)) {
                    if (!temp.contains(nonS)) {
                        temp = word;
                        continue;
                    }

                    if (currentSuff.equals("تان")) {
                        if (word.endsWith("شتان") || word.endsWith("وتان")
                                || word.endsWith("اتان") || word.endsWith("ۆتان") || word.endsWith("ختان")
                                || word.endsWith("فتان") || (word.endsWith("ه‌تان") && !word.endsWith("ه‌تان"))) {
                            continue;

                        }
                    }
                    if (currentSuff.equals("مان")) {
                        if (word.endsWith("یسمان") || word.endsWith("ێمان") || word.endsWith("ۆمان")
                                || word.endsWith("ومان") || word.endsWith("امان")
                                || (word.endsWith("ه‌مان") && word.length() <= 4)) {
                            continue;

                        }
                    }

                    if (currentSuff.equals("که") || currentSuff.equals("ه")) {
                        if (word.endsWith("ۆکه")) {
                            if (word.length() <= 5) {
                                continue;
                            }
                        }
                        if (word.endsWith("وکه") || word.endsWith("شکه")
                                || word.endsWith("چکه")) {
                            continue;
                        }
                    }

                    if (currentSuff.equals("کان")) {
                        if (word.endsWith("وکان")
                                || word.endsWith("شکان")
                                || word.endsWith("چکان")) {
                            continue;
                        }
                    }

                    if (currentSuff.equals("ان")) {
                        temp = word.substring(0, word.length() - currentSuff.length());
                         String endOfword=copy1.substring(original.lastIndexOf(currentSuff));
                        if (isEqualOf(temp + "ه", exception) && exp.length() == 0) {
                            detectedSuffix.add(currentSuff);
                            modified_term = temp + "ه";
                            modified_term+=endOfword;
//                            for (int df = detectedSuffix.size() - 1; df >= 0; df--) {
//                                modified_term += detectedSuffix.get(df);
//                            }
                            return detectedSuffix;
                        }
                        if (isEqualOf(temp + "ه", exception)) {
                            if (temp.length() + 1 >= exp.length()) {
                                currentExc = temp + "ه";
                            }
                            detectedSuffix.add(currentSuff);
                            modified_term = temp + "ه";
                             endOfword=copy1.substring(original.lastIndexOf(currentSuff));
                             modified_term+=endOfword;
//                            for (int df = detectedSuffix.size() - 1; df >= 0; df--) {
//                                modified_term += detectedSuffix.get(df);
//                            }
                            return detectedSuffix;
                        }

                    }

                    lastSuff = currentSuff;
                    if (maxSuff.length() < currentSuff.length()) {
                        maxSuff = currentSuff;
                    }

                    detected = true;
                }
            }
            if (maxSuff.length() == 0) {
                break;
            }
            if (maxSuff.equals("ه‌وه")) {

                if (word.endsWith("که‌وه") || word.endsWith("گه‌وه")) {
                    maxSuff = "وه";
                }
            }
            detectedSuffix.add(maxSuff);
            if (maxSuff.equals("نن") || maxSuff.equals("مم")) {
                word = word.substring(0, word.length() - 1);
            } else {
                word = word.substring(0, word.length() - maxSuff.length());
            }
            if (word.charAt(word.length() - 1) == (char) 8204) {
                word = word.substring(0, word.length() - 1);
            }
//
//            if (word.endsWith(exp) && exp.length() > 0) {
//                return detectedSuffix;
//            }
        }
        return detectedSuffix;
    }

    public static String getLemOfWord(String word, ArrayList detectedSuffix, String exp) {
        if (modified_term.length() > 0) {
            word = modified_term;
        }
        String toCount = word.replaceAll(h_zwnj, h_end);
        String currentSuffix = new String();
        String original = word;
        String std=word;
        ArrayList<String> removedSuffix = new ArrayList<String>();

        boolean canDelete = true;
        boolean canBreak = false;
        boolean poss_deleted = false;
        for (int i = 0; i < detectedSuffix.size(); i++) {


            currentSuffix = detectedSuffix.get(i).toString();
            canDelete = true;
            canBreak = false;

            //-------------------------Plural--------------------------------
            if (isMemberOf(currentSuffix, plurals) && !isMemberOf(currentSuffix, possesion)) {
                if (removedSuffix.size() > 0) {
                    if (currentSuffix.equals("کان") && removedSuffix.get(removedSuffix.size() - 1).equals("ێک")) {
                        currentSuffix = "ان";
                    }
                }
                for (int j = 0; j < removedSuffix.size(); j++) {
                    String removed = removedSuffix.get(j);


                    if (currentSuffix.equals("ان") && removed.equals("ێک")) {
                        canDelete = true;
                        break;
                    }

                    if (!isEqualOf(removed, possesion)) {
                        if (isMemberOf(removed, adjSign) || isMemberOf(removed, plurals)
                                || isMemberOf(removed, definite)) {
                            if(std.contains("انه‌تر") || std.contains("انه‌ترین") )
                            {
                            canBreak=true;
                            break;
                            }
                            else{ canBreak = true;
                            canDelete = false;
                            break;
                            }

                        }
                    }
                }

                if (canDelete) {
                    if (currentSuffix.equals("ان")) {
                        if (word.endsWith("خان") || word.endsWith("بان")) {
                            canBreak = true;
                            canDelete = false;
                        } else if (word.endsWith("زان")) {
                            canDelete = false;
                            canBreak = true;
                            if (word.endsWith("سازان") || word.endsWith("وازان") || word.endsWith("وزان")) {
                                canDelete = true;
                                canBreak = false;
                            }
                        }
                        if (word.endsWith("لان") && !word.endsWith("الان")) {
                            canDelete = false;
                            canBreak = true;
                        }
                        if (word.endsWith("ڵان") && !word.endsWith("اڵان")) {
                            canDelete = false;
                            canBreak = true;
                        }
                        if (word.endsWith("وان") && !word.endsWith("ووان") && !word.endsWith("اوان")) {
                            canBreak = true;
                            canDelete = false;

                        }
                    }
                }

            } //-------------------------Indefinite-----------------------------------
            else if (isMemberOf(currentSuffix, definite)) {
                for (int j = 0; j < removedSuffix.size(); j++) {
                    String removed = removedSuffix.get(j);
                    if (!isMemberOf(removed, possesion)) {
                        if (isMemberOf(removed, adjSign) || isMemberOf(removed, plurals)
                                || isMemberOf(removed, definite)
                                || isMemberOf(removed, commonVerb)
                                || (currentSuffix.contains("ێک") && removed.contains("یه‌ت"))) {

                            if((isMemberOf(removed, plurals) || isMemberOf(removed, definite))
                                && currentSuffix.endsWith("ه")){
                                currentSuffix="ه";
                                canBreak = true;
                            break;
                            }
                         
                            canBreak = true;
                            canDelete = false;
                            break;

                        }

                    }
                }

                if (currentSuffix.equals("که")) {
                    //toCount = word.replaceAll(h_zwnj, h_end);
                      if (word.endsWith("ۆکه")) {
                    if (word.length() <= 6) {
                    canDelete = false;
                    canBreak = true;
                    }} /*else if (toCount.length() == 6) {
                    currentSuffix = "ه";
                    canDelete = true;
                    canBreak = true;
                    }
                    }
                    if (word.endsWith("وکه")) {
                    currentSuffix = "ه";
                    canDelete = canBreak = true;
                    }
                    if (word.endsWith("چکه")) {
                    canDelete = false;
                    canBreak = true;
                    }*/
                }

            } //-------------------------Possession--------------------------------
            else if (currentSuffix.equals("مم") || currentSuffix.equals("تت")) {
                currentSuffix = currentSuffix.substring(1);
            } else if (currentSuffix.equals("ت")) {
                if (word.endsWith("ست") || word.endsWith("شت") || word.endsWith("وت")
                        || word.endsWith("ات") || word.endsWith("ۆت") || word.endsWith("خت")
                        || word.endsWith("فت") || (word.endsWith("ه‌ت") && !word.endsWith("یه‌ت"))) {
                    canDelete = false;
                    canBreak = true;

                }

            } else if (currentSuffix.equals("م")) {
                if (word.endsWith("یسم") || word.endsWith("ێم") || word.endsWith("ۆم")
                        || word.endsWith("وم") || word.endsWith("ام")
                        || (word.endsWith("ه‌م") && word.length() <= 4)) {
                    canDelete = false;
                    canBreak = true;

                }

            } else if (isMemberOf(currentSuffix, possesion)) {
//                if (word.length() > 7 && detectedSuffix.size() > (i + 1)) {
//
//                    if (word.endsWith("ه‌تان")
//                            && detectedSuffix.get(i + 1).toString().equals("ه")) {
//                        currentSuffix = "ان";
//                        canBreak = true;
//                    }
//                }
//                if (word.endsWith("اتان")) {
//                    currentSuffix = "ان";
//                    canBreak = true;
//                }

                for (int j = 0; j < removedSuffix.size(); j++) {
                    String removed = removedSuffix.get(j);
                    if (isMemberOf(removed, beforePos1) ) {//&& word.length() < 5
                       if(isEqualOf(removed, adjSign) && currentSuffix.endsWith("ان")){
                        if(removedSuffix.get(removedSuffix.size()-1).equals("ه"))
                        {currentSuffix="ان";
                         canBreak = true;
                        canDelete = true;
                        break;
                        }  
                       }

                        canBreak = true;
                        canDelete = false;
                        break;
                    }
                }





                if (canDelete) {
                    if (poss_deleted) {
                        boolean valid = false;
                        for (int j = i + 1; j < detectedSuffix.size(); j++) {
                            String temp = detectedSuffix.get(j).toString();
                            if (isMemberOf(temp, beforePos1)) {
                                valid = true;
                            }
                        }
                        if (!valid) {
                            canDelete = false;
                            canBreak = true;
                        }
                    }



                }
                if (canDelete) {
                    if (word.endsWith("و" + currentSuffix)) {
                        canDelete = false;
                        canBreak = true;
                        poss_deleted = false;
                        if (word.endsWith("او" + currentSuffix) || word.endsWith("یو" + currentSuffix)
                                || word.endsWith(h_zwnj + "و" + currentSuffix) || word.endsWith("وو" + currentSuffix)) {
                            //  word = word.substring(0, word.length()-word.length()-possesion[j].length());
                            canBreak = canDelete = true;

                        }
                        if (word.endsWith("ووتان")) {
                            canBreak = true;
                            canDelete = false;
                            poss_deleted = false;
                        }
                    }
                }
                if (word.endsWith("ه‌یمان") || word.endsWith("ه‌یتان")) {
                    canBreak = true;
                    canDelete = false;

                }

                if (currentSuffix.equals("تان") && (word.endsWith("یستان")
                        || word.endsWith("ێستان"))) {
                    currentSuffix = "ان";
                    canBreak = true;
                    canDelete = true;
                }



            } //--------------------------ish--------------------------------------
            else if (currentSuffix.equals("یش")) {
                for (int j = 0; j < removedSuffix.size(); j++) {
                    String removed = removedSuffix.get(j);
                    if (!isMemberOf(removed, possesion)) {
                        if (isMemberOf(removed, adjSign) || isMemberOf(removed, plurals)
                                || isMemberOf(removed, definite)) {
                            canBreak = true;
                            canDelete = false;
                            break;

                        }
                    }
                }

            } //-----------------------------------------h---------------------------------------
            else if (currentSuffix.equals("ه")) {
                toCount = word.replaceAll(h_zwnj, h_end);
                if (word.endsWith("نه")) {
                    if (toCount.length() <= 4) {
                        canDelete = false;
                        canBreak = true;
                    } else {
                        canDelete = true;
                    }
                }
                if (word.endsWith("گه")) {
                    canDelete = false;
                    canBreak = true;
                    if (word.endsWith("نگه")) {
                        toCount = word.replaceAll(h_zwnj, h_end);
                        if (toCount.indexOf("نگە" + currentSuffix) < 5) {
                            currentSuffix = "ه";
                            canDelete = true;
                        }
                    }
                }

                if (word.endsWith("نده")) {

                    if (!word.endsWith("ه‌نده") || word.endsWith("هه‌نده")) {
                        canDelete = false;
                        canBreak = true;
                    }
                }


                if (word.endsWith("ممه") || word.endsWith("غه")) {
                    canDelete = false;
                    canBreak = true;
                }

                if (word.endsWith("جه")) {
                    canDelete = false;
                    canBreak = true;
                    if (word.endsWith("نجه") && word.length() <= 8) {
                        canDelete = true;

                    }


                }

                if (removedSuffix.size() > 0) {
                    if (removedSuffix.get(i - 1).toString().equals("یه‌م")) {
                        canBreak = true;
                        canDelete = false;
                    }

                }


            } //-------------------------------------i----------------------------------
            else if (currentSuffix.equals("ی")) {
                if (word.endsWith("ینی") || word.endsWith("ێنی")) {
                    canBreak = true;
                }
                if(word.endsWith("هی")){
                canBreak=true;
                }
            } else if (currentSuffix.equals("ێ")) {
                if (removedSuffix.size() > 0) {
                    if (isMemberOf(removedSuffix.get(removedSuffix.size() - 1), possesion)) {
                        canBreak = true;
                        canDelete = false;
                    }
                }
            } else if (currentSuffix.equals("یه‌م")) {
                toCount = word.replaceAll(h_zwnj, h_end);
                char ch = word.charAt(word.indexOf("یه‌م") - 1);
                if (toCount.length() <= 6 && ch != 'ۆ' && ch != 'ێ') {
                    canBreak = true;
                    canDelete = false;
                }

            } //-------------------------------------------------------------
            //------------------------------da------------------------------------------
            else if (currentSuffix.equals("دا")) {

                for (int j = 0; j < removedSuffix.size(); j++) {
                    String removed = removedSuffix.get(j);
                    if (isMemberOf(currentSuffix, possesion)
                            || isMemberOf(removed, beforePos)
                            || isMemberOf(removed, definite)) {
                        canBreak = true;
                        canDelete = false;
                        break;
                    }
                }

            } //----------------------------------------------------------------------------
            else if (currentSuffix.equals("نن")) {
                currentSuffix = "ن";
            }

            else if (currentSuffix.equals("وه") && word.endsWith("ووه")) {
                currentSuffix = "ە";
            }
//----------------------------------------------------------------------------
            if (canDelete) {
                String preWord = word;

                removedSuffix.add(currentSuffix);

                word = word.substring(0, word.length() - currentSuffix.length());
                if (word.length() > 0) {
                    if (word.charAt(word.length() - 1) == (char) 8204) {
                        word = word.substring(0, word.length() - 1);
                    }
                }
//             if (!word.contains(exp)) {
//                word = preWord;
//                canBreak=false;
//            }
//            if (word.equals(exp)) {
//                canBreak=false;
//            }

                if (isEqualOf(currentSuffix, possesion)) {
                    poss_deleted = true;
                }
            }
            if (canBreak) {
                break;
            }
        }
        return word;
    }

    public static String getLem(String word, String[] exception, String[] suffixlist,
            String[] pastVerb, ArrayList verb,
            String[] presentVerb, ArrayList prVerb) {

        String exp = getMatch1(word, exception);

        if (word.equals(exp)) {
            return word;
        }
        currentExc = exp;
         ArrayList suffix = getAllEnds(word, exception, suffixlist, exp);
        exp = currentExc;
         boolean isNoun=false;
         for(int i=0;i<suffix.size();i++){
             String t=suffix.get(i).toString();
             if(t.equals("که") || t.contains("ێک") || t.contains("کان"))
             {    isNoun=true;
             break;
             }
         }
         String stem="";
         if(isNoun==false){
         stem = VerbLemmatizerClass.getLemVerb(word, pastVerb, verb, presentVerb, prVerb);
        if (!stem.equals(word) || VerbLemmatizerClass.isVerb) {
           // if(exp.length()<=word.length()/2)
            return stem;
        }
         }
//        currentExc = exp;
//         suffix = getAllEnds(word, exception, suffixlist, exp);
//        exp = currentExc;
        stem = getLemOfWord(word, suffix, exp);
        if (!stem.contains(exp) && exp.length()>0) {
            String start = word.substring(0, word.indexOf(exp));
            stem = start + exp;

        }
        return stem;


    }

    public static String getLemShort(String term){
    	return getLem(term.trim(), exception, suffixlist, pastVerb, verb, presentVerb, prVerb);
    }
    public static void main(String[] args) {
        String word = "ئاراسته‌وه";
        word = "ئاماژه‌شیاندا";
        word = "ده‌یکوژن";
        word = "ده‌ربڕی";
        word = "‌نزینخانه‌یه‌کی";
        word = "ببه‌خشین";

        word = "فروکه";
        //word = "فروکان";

        word = "مانخانه‌وه";
        word = "ده‌مانیانبرد";
        word = "ئیواران";
        word = "ئه‌لبووم";

        word = "پیشانیداوه";
        word = "ئابوریه‌کانی";
        word = "کێشانه‌وه‌ی";
        word = "به‌هه‌ڵبژاردن";

        word = "هه‌ڵبژارده‌که‌مان";

        word = "له‌هه‌ڵبژاردندا";
        word = "هه‌ڵماننه‌بژارد";
        word = "دوورده‌که‌ویته‌وه";
        word = "نوێژقه‌ڵاکردنه‌وه‌";
        word = "کوژرا";
        word = "چوارچرا";
        word = "کوردستانن";
        
        word = "چاکبوو";
        word = "په‌رله‌مانی";
        word = "به‌ش";
        word = "چاوپێکه‌وتنێکدا";
        word="ڕاپۆرتێکمانیش";
        word="دوازده‌می";
        word="کتێبێکمانمان";
      //  word="دڕندانه‌ترین";
        //word="وه‌حشیانه‌ترین";
    

      System.out.println(getLem(word.trim(), exception, suffixlist, pastVerb, verb, presentVerb, prVerb));


      
    }
}
