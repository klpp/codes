/*
 * This Java class is the second  part of implementation of a lemmatizer (morphological analyzer) for 
 * the Sorani dialect of the Kurdish  lanuage.
 *
 * It has been  developed by Shahin Salavati, a member of the Kurdish Language Processing Project (KLPP) team, 
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
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Shahin
 */
public class VerbLemmatizerClass {

    static String zwnj = String.valueOf((char) 8204);
    static String[] verbPrefix = {"ڕا", "دا", "تێ", "هه‌ڵ", "وه‌ر", "سه‌ر", "ده‌ر", "جێ"};
    static boolean isVerb = true;
    static String[] nonJoint = {"ا", "ر", "ژ", "ز", "د", "و", "ۆ", "ڕ", zwnj};

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
        if (pars[0].startsWith(start)) {
            pars[0] = pars[0].substring(1);
        }
        return pars;
    }

    public static boolean isMemberOf(String input, String[] list) {
        for (int i = 0; i < list.length; i++) {
            if (list[i].equals(input)) {
                return true;
            }
        }
        return false;
    }

    public static String getMatch(String input, String[] list) {
        for (int i = 0; i < list.length; i++) {
            if (input.startsWith(list[i])) {
                return list[i];
            }
        }
        return "";
    }

    public static String kraVerb(String input) {
        Pattern pat = Pattern.compile("کرا");
        pat = Pattern.compile("کرا" + "[و,ن,ب]");
        Matcher match = pat.matcher(input);
        int startInd = -1;
        String subSt = new String();

        while (match.find()) {
            startInd = match.start();
            subSt = input.substring(0, startInd);
            if (subSt.length() < 4 && prefixHandler(subSt,"کرا").length() > 0) {//&&!input.startsWith(subSt)
                startInd = -1;
                continue;
            }
        }

        if (input.endsWith("کرا")) {
            startInd = input.lastIndexOf("کرا");
            subSt = input.substring(0, startInd);
            if (subSt.length() < 4 && prefixHandler(subSt,"کرا").length() > 0) {//&&!input.startsWith(subSt)
                startInd = -1;

            }
        }

        if (startInd >= 0) {
            return prefixHandler(subSt,"کرا") + "کردن";
        }

        return input;

    }

    public static String krdVerb(String input) {
        Pattern pat = Pattern.compile("کرد" + "[ن,و,ی,ت,ۆ,ه,ا]");
        Matcher match = pat.matcher(input);
        int startInd = -1;
        String subSt = new String();
        while (match.find()) {
            startInd = match.start();
            subSt = input.substring(0, startInd);
        }
        if (startInd >= 0) {
            return prefixHandler(subSt,"کرا") + "کردن";
        }
        isVerb = false;
        return input;

    }

    public static String booVerb(String input, String[] pastBon) {
        isVerb = true;
        if (input.contains("بوو")) {
            for (int i = 0; i < pastBon.length; i++) {
                String pattern = pastBon[i] + "بوو";
                if (input.startsWith(pattern)) {
                    return pastBon[i] + "ن";
                }
                if (input.contains(pattern)) {
                    int ind = input.indexOf(pattern);
                    String startS = input.substring(0, ind);
                    return prefixHandler(startS,"بوو") + pastBon[i] + "ن";

                }
//                Pattern pat = Pattern.compile("^" + pastBon[i] + ".{0,3}" + "بوو");
//                Matcher m = pat.matcher(input);
//                if (m.find()) {
//                    isVerb = true;
//                    return pastBon[i] + "ن";
//                }


            }
            
            
        }
        isVerb = false;
        return input;

    }

    public static ArrayList readToArray(String path) { //read a file into an String araay
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
        return terms;
    }

    public static String helperVerb(String word, String root, String[] pastBon) {
    isVerb=true;

        if (word.endsWith(root)) {
            String subStart = word.substring(0, word.indexOf(root));
             if(!root.endsWith("ن"))
            root+="ن";
            return prefixHandler(subStart,root) + root ;
        }





        Pattern pat = Pattern.compile(root + "[و,ن,ب,ه,ی,م,,]");
        Matcher m = pat.matcher(word);
        if (!m.find()) {
            isVerb = false;
            return word;
        }

        String subStart = word.substring(0, word.indexOf(root));
        isVerb = true;

        if(!root.endsWith("ن"))
            root+="ن";
        return prefixHandler(subStart,root) + root ;



    }

    public static String pastVerbLem(String word, String[] pastVerb, ArrayList verb) {

        isVerb = true;
        String[] transitiveSign = {"مان", "تان", "یان", "م", "ت", "ی",""};
        String[] objectSign = {"مان", "تان", "یان", "م", "ت", "ی",""};

        String[] inTransitiveSign = {"م", "ی", "", "ین", "ن"};
        String[] verbPre = {"ڕا", "دا", "تێ", "پێ", "هه‌ڵ", "له", 
            "وه‌ر", "سه‌ر", "ده‌ر", "لێ", "ده‌ست"};
        ArrayList verbPrefixList = new ArrayList();
        for (int i = 0; i < verbPrefix.length; i++) {
            verbPrefixList.add(verbPrefix[i]);
        }
        String negative = "نه" + zwnj, de = "ده" + zwnj;
        //String temp = new String(),negTemp=new String();
        String roots[] = gussRoot(word, pastVerb);


        root:
        for (int r = 0; r < roots.length; r++) {
            String root = roots[r];

//            if (root.equals("کرد")) {
//                isVerb = false;
//                return word;
//            }
            String[] verbTense = new String[14];
            for (int obj = 0; obj < objectSign.length; obj++) {
                String objectS = objectSign[obj];
                for (int i = 0; i < transitiveSign.length; i++) {


                    String sign = transitiveSign[i];
                    verbTense[0] = root + sign + objectS;//simple mota'di
                    verbTense[1] = negative + objectS + sign + root;//negative simple

                    verbTense[2] = de + objectS + sign + root;//continues poss
                    verbTense[3] = negative + objectS + sign + de + root; //Negative Continues

                    verbTense[4] = "ب" + objectS + sign + root + "با";
                    verbTense[5] = negative + objectS + sign + root + "با";

                    verbTense[6] = root + "بوو" + objectS + sign;
                    verbTense[7] = negative + objectS + sign + root + "بوو";

                    verbTense[8] = root + "بێت" + objectS + sign;
                    verbTense[9] = negative + objectS + sign + root + "بێت";

                    verbTense[10] = root + "وو" + objectS + sign + "ه";
                    verbTense[11] = negative + objectS + sign + root + "ووه";

                    verbTense[12] = root + "وه";
                    verbTense[13] = negative + objectS + sign + root + "وه";

                    for (int j = 0; j < verbTense.length; j++) {
                       String tempVerb = verbTense[j];
                            if (word.equals(tempVerb)) {
                                // System.out.println("Transitive Verb: " + );
                                return root + "ن";
                            }                   
                    }
                    
                     for (int j = 0; j < verbTense.length; j++) {

                        String tempVerb = verbTense[j];
                       if (word.endsWith(tempVerb)) {
                                String subStart = word.substring(0, word.indexOf(tempVerb));
                                if (isMemberOf(subStart, verbPre) || subStart.length() == 0) {
                                    return subStart + root + "ن";
                                }
                            }
                             
                    
                     }
                        
                }

               

            }

            
            
            
            verbTense = new String[10];

            for (int i = 0; i < inTransitiveSign.length; i++) {

                String sign = inTransitiveSign[i];
                verbTense[0] = root + sign;//Positive Simple Tense
                verbTense[1] = negative + verbTense[0];//Negative Simple Tense

                verbTense[2] = de + verbTense[0];//Positive Contiues
                verbTense[3] = negative + verbTense[2];//Negative Continues

                verbTense[4] = "ب" + root + "با" + sign;//Positive Absolute
                verbTense[5] = negative + root + "با" + sign;//Negative Absolute

                verbTense[6] = root + "بوو" + sign;//
                verbTense[7] = negative + verbTense[6];//

                verbTense[8] = root + "وو" + sign + "ه";// naghli
                verbTense[9] = negative + verbTense[8];//neg-naghli

                for (int v = 0; v < verbTense.length; v++) {
                    String tempVerb = verbTense[v];


                    if (word.equals(tempVerb)) {
                        return root + "ن";
                    }
                }
                
                
                 for (int j = 0; j < verbTense.length; j++) {

                        String tempVerb = verbTense[j];
                    if (word.endsWith(tempVerb)) {
                        String subStart = word.substring(0, word.indexOf(tempVerb));
                        if (isMemberOf(subStart, verbPre) || subStart.length() == 0) {
                            return subStart + root + "ن";
                        }


                       
                    }
                }
            }


              if (root.equals("گرت") || root.equals("برد")
                              || root.equals("بوو") || root.equals("کرد")) {

                                    String stem=helperVerb(word, root, pastVerb);
                                    if(isVerb)
                                        return stem;

                              }
        }
        //    System.out.println("The word isn't a Past Tense of Verb");

        isVerb = false;
        return word;
    }

    public static String presentVerbLem(String word, String[] pastVerb, String[] presentVerb, ArrayList presentList) {

        presentList.set(0, presentVerb[0]);
        String[] personSign = {"ین", "ن", "ێت", "م", "ات", "ی", "ێ"};
        String[] objectSign = {"", "مان", "تان", "یان", "م", "ت", "ی"};
        String[] imperativeSign = {"ه", "ین", "ن", "ۆ", ""};

        String negative = "نه" + zwnj, de = "ده" + zwnj, neg = "نا";
        String ma = "مه" + zwnj;
        //String temp = new String(),negTemp=new String();
        String[] temp = new String[6];
        String roots[] = gussRoot(word, presentVerb);
        for (int r = 0; r < roots.length; r++) {
            String root = roots[r];

            String pastRoot = pastVerb[presentList.indexOf(root)];
            if (pastRoot.equals("بوو")) {
                temp = new String[9];

            }


            for (int i = 0; i < personSign.length; i++) {
                String sign = personSign[i];
//                if (root.endsWith("ه" + zwnj) && sign.equals("ێ")) {
//                    root = root.substring(0, root.length() - 2);
//                    sign = "ا";
//                }

                for (int j = 0; j < objectSign.length; j++) {
                    String objS = objectSign[j];
                    String simpleP = temp[0] = de + objS + root + sign;//Positive Simple Tense
                    String simpleN = temp[1] = neg + objS + simpleP;//Negative Simple Tense

                    String mustP = temp[2] = "ب" + objS + objS + root + sign;//Positive Absolute
                    String mustN = temp[3] = negative + objS + mustP;//Negative Absolute

                    temp[4] = "ئه" + zwnj + root + sign;
                    temp[5] = "نا" + root + sign;



                    if (pastRoot.equals("بوو")) {
                        temp[6] = root + sign;
                        temp[7] = neg + root + sign;
                        temp[8] = "نه" + zwnj + root + sign;
                    }
                    for (int l = 0; l < temp.length; l++) {

                        if (word.equals(temp[l])) {
                            return pastRoot + "ن";
                        }
                        if (temp[l].equals("هه‌م")) {
                            return word;
                        }

                        if (pastRoot.equals("بوو")) {
                            if (word.contains(temp[l])) {
                                String st = word.substring(0, word.indexOf(temp[l]));
                                if (!isMemberOf(st, verbPrefix)) {
                                    return word;
                                }
                            }


                        }

                        if (word.endsWith(temp[l])) {
                            if (temp[l].equals("بێ") && !word.endsWith("بێ")) {
                                isVerb = false;
                                return word;
                            }
                            return word.substring(0, word.indexOf(temp[l])) + pastRoot + "ن";
                        }

                        if (word.startsWith(temp[l])) {

                            String post = word.substring(temp[l].length(), word.length());
                            if (NounLemmatizerClass.isMakeOfSuffix(post, NounLemmatizerClass.suffixlist)) {
                                isVerb = true;


                                return pastRoot + "ن";
                            }
                        }


                    }
                }

                for (int j = 0; j < temp.length; j++) {
                    if (word.endsWith(temp[j])) {
                        if (temp[j].contains("بێت")) {
                            String root1 = booVerb(word.replace(temp[j], "بوو"), pastVerb);
                            return root1;
                        } else {

                            String prefix = word.substring(0, word.indexOf(temp[j]));

                            return prefixHandler(prefix,root) + pastRoot + "ن";
                        }
                        //System.out.println("In Active tense: " + root1);
                        //return true;
                    }
                }

            }
            root = roots[r];
            for (int i = 0; i < imperativeSign.length; i++) {
                String sign = imperativeSign[i];
                String imp = "ب" + root + sign;
                String impN = ma + root + sign;
                String impNSp = negative + root + "ین";

                if (word.equals(imp) || word.equals(impN) || word.equals(impNSp)) {
                    // System.out.println("In Active tense: " + pastRoot + "ن");
                    return pastRoot + "ن";
                }
            }
        }


        //    System.out.println("The word isn't a Present Tense of Verb");
        isVerb=false;
        return word;
    }

    public static String hasVerb(String word) {
        String[] transitiveSign = {"مان", "تان", "یان", "م", "ت", "ی"};

        String root = "هه‌";
        for (int i = 0; i < transitiveSign.length; i++) {
            String sign = transitiveSign[i];
            String verb = root + sign + "ه";
            if (word.equals(verb)) {
                return "بوون";
            }
            if (word.endsWith(verb)) {
                return prefixHandler(word.substring(0, word.indexOf(verb)),root) + "بوون";
            }

        }


        return word;
    }

    public static String specialTagger(String word, String[] pastVerb, String[] presentVerb, ArrayList verbList) {
    isVerb=true;
        String[] verbPostfix = {"ووه", "ۆته", "ۆته‌وه", "ووه‌ته‌وه", "ووه‌ته", "وه‌ته‌وه", "ۆتوه"};
        verbList.set(0, presentVerb[0]);

        String[] pastStems = gussRoot(word, pastVerb);
        String[] presentStem = gussRoot(word, presentVerb);
        //----------past--------------------------
        for (int r = 0; r < pastStems.length; r++) {
            String root = pastStems[r];

            for (int i = 0; i < verbPostfix.length; i++) {
                String postfix = verbPostfix[i];
                String verb = root + postfix;

                if (word.contains(verb)) {
                    int ind = word.indexOf(verb);
                    word = prefixHandler(word.substring(0, ind),root) + root + "ن";
                    // System.out.println("Wors is a verb:" + word);
                    return word;
                }

            }
        }
        //----------------Presnet--------------------
        for (int r = 0; r < presentStem.length; r++) {
            String root = presentStem[r];

            String pastRoot = pastVerb[verbList.indexOf(root)];
            if (word.endsWith("را")) {

                if (word.equals(root + "را")) {
                    isVerb = true;
                    return pastRoot + "ن";
                }

            }
            for (int i = 0; i < verbPostfix.length; i++) {
                String postfix = verbPostfix[i];
                String verb = root + postfix;

                if (word.contains(verb)) {
                    int ind = word.indexOf(verb);
                    word = prefixHandler(word.substring(0, ind),root) + pastRoot + "ن";
                    //   System.out.println("Wors is a verb:" + word);
                    return word;
                }

            }
        }
        isVerb=false;
        return word;
    }
    //--------------------------------------------------------------

    public static String prefixHandler(String prefixSub,String root) {
        String[] transitiveSign = {"مان", "تان", "یان", "م", "ت", "ی", ""};
        String[] verbPre = {"ڕا", "دا", "تێ", "پێ", "هه‌ڵ", "له"+zwnj, "ب",
            "وه‌ر", "سه‌ر", "ده‌ر", "لێ", "ده‌ست", "ده" + zwnj, "نا", "ئه" + zwnj,"نه" + zwnj};
        String[] candeleted = { "نه"+zwnj,"نه","نا",  "ب", "له","له"+zwnj, "به","به"+zwnj,"ده","ده"+zwnj,"ئه",""+zwnj};
        String[] candeleted1 = { "نه"+zwnj,"نا",  "ب","له"+zwnj,"ده"+zwnj,"ئه"+zwnj};
        if (prefixSub.endsWith(zwnj)) {
            prefixSub = prefixSub.substring(0, prefixSub.length() - 1);
        }
        String[] cantDeleted = {"ڕا", "دا", "تێ", "پێ", "هه‌ڵ", "وه‌ر", "سه‌ر", "ده‌ر", "لێ", "ده‌ست","له‌ده‌ست"};

        String[] verbPre1 = {"ڕا", "دا", "تێ", "پێ", "هه‌ڵ", "له", "ب",
            "وه‌ر", "سه‌ر", "ده‌ر", "لێ", "ده", "نه", "نا", "ئه", "ده‌ست"};


          String exp = NounLemmatizerClass.getMatch1(prefixSub, NounLemmatizerClass.exception);
        if(prefixSub.equals(exp) || (prefixSub.endsWith(exp) && exp.length()>prefixSub.length()/2) )
            return prefixSub+zwnj;
        if (isMemberOf(prefixSub, candeleted) || prefixSub.length() == 0) {
            return "";
        }

        String neg = "نه" + zwnj;
        if (isMemberOf(prefixSub, cantDeleted)) {
            return prefixSub;
        }
        for (int i = 0; i < verbPre.length; i++) {
            String prefix = verbPre[i];
            for (int j = 0; j < transitiveSign.length; j++) {
                String pat1 = prefix + transitiveSign[j];
                String pat2 = prefix + transitiveSign[j] + neg;
                if (prefixSub.equals(pat1) || prefixSub.equals(pat2)) {
                    if (isMemberOf(prefix, candeleted1)) {
                        return "";
                    }
                    return prefix;
                }
            }
        }

        if (NounLemmatizerClass.isMakeOfSuffix(prefixSub, verbPre1)) {
            for (int i = 0; i < candeleted.length; i++) {
                prefixSub = prefixSub.replace(candeleted[i], "");
            }
            if (prefixSub.length() > 0) {
                if (prefixSub.startsWith(zwnj)) {
                    prefixSub = prefixSub.substring(1);
                }
                if (prefixSub.endsWith(zwnj)) {
                    prefixSub = prefixSub.substring(0, prefixSub.length() - 1);
                }
            }

            return prefixSub;
        }


        int count=candeleted.length;
        if(root.equals("بوو"))
            count=candeleted.length-3;

        for (int i = 0; i < count; i++) {
            if (prefixSub.endsWith(candeleted[i])) {
                prefixSub = prefixSub.replace(candeleted[i], "");
            }
        }

        for (int i = 0; i < cantDeleted.length; i++) {
            if (prefixSub.endsWith(cantDeleted[i])) {
                return prefixSub + zwnj;
            }
        }
      

         if( prefixSub.equals(exp+"ش") ||
                prefixSub.equals(exp+zwnj+"ش")){
         return prefixSub.substring(0,prefixSub.length()-1)+zwnj;
         }


        ArrayList list = NounLemmatizerClass.getAllEnds(prefixSub, NounLemmatizerClass.exception, NounLemmatizerClass.suffixlist, exp);

        boolean check = false;
        String s = new String();

        for (int i = 0; i < list.size(); i++) {
            String t = list.get(i).toString();
            if (check && isMemberOf(t, transitiveSign)) {
                list.clear();
                list.add(s);
                break;
            }
            if (isMemberOf(t, transitiveSign)) {
                s = t;
                check = true;
            }

        }
       String prefixStem = NounLemmatizerClass.getLemOfWord(prefixSub, list, exp);
       
        if (!prefixStem.contains(exp) && exp.length()>0) {
            int ind=prefixSub.indexOf(exp);
            if(ind>=0){
            String start = prefixSub.substring(0,ind );
            prefixSub = start + exp;
            }
        }
        else
       prefixSub=prefixStem;
        for (int i = 0; i < nonJoint.length; i++) {
            if (prefixSub.endsWith(nonJoint[i])) {
                return prefixSub;
            }
        }


        return prefixSub + zwnj;

    }

    //--------------------------------------------------------------
    public static String[] gussRoot(String word, String[] verbs) {

        ArrayList list = new ArrayList();
        for (int i = 0; i < verbs.length; i++) {
            if (word.contains(verbs[i]) && !list.contains(verbs[i])) {
                list.add(verbs[i]);
                //if(!detected.equals("بوو"))

            }
        }
        boolean detect = false;
        while (detect) {
            String current = new String(), maxroot = new String();
            detect = false;
            for (int i = 0; i < verbs.length; i++) {
                if (word.contains(verbs[i]) && !list.contains(verbs[i])) {
                    current = verbs[i];
                    if (maxroot.length() < current.length()) {
                        maxroot = current;
                        detect = true;
                    }
                }


            }
            list.add(maxroot);

        }
        String detected[] = new String[list.size()];

        for (int i = 0; i < list.size(); i++) {
            detected[i] = list.get(i).toString();
        }

        return detected;

    }

    public static String raVerb(String word, ArrayList prVerb, ArrayList pastBon) {
        isVerb = false;
        if (word.endsWith("را")) {
            String verb = word.substring(word.length() - 3, word.length() - 2);
            int ind = prVerb.indexOf(verb + "ه" + zwnj);
            if (ind < 0) {
                return word;
            }
            String past = pastBon.get(ind).toString();
            isVerb = true;
            return word.substring(0, word.lastIndexOf(verb + "را")) + past + "ن";


        }
        return word;

    }

    public static String exceptionalVerb(String word, ArrayList prVerb, ArrayList pastBon) {
        String stem = raVerb(word, prVerb, pastBon);
        if (isVerb) {
            return stem;
        }
        Pattern pat = Pattern.compile("[ب,ده‌,نه‌]" + "[مان,تان,یان,م,ت,ی]*" + ".و" + "ات");

        //Pattern pat=Pattern.compile("");
        Matcher m1 = pat.matcher(word);
        if (m1.find()) {
            int ind = word.lastIndexOf("ا");
            String presentB = word.substring(ind - 2, ind);
            presentB = presentB.replace("و", "ۆ");
            if (prVerb.contains(presentB)) {
                String sub = word.substring(0, m1.start());
                if (sub.endsWith("ب")) {
                    sub = sub.substring(0, sub.length() - 1);
                } else if (sub.endsWith("ده") || sub.endsWith("نه")) {
                    sub = sub.substring(0, sub.length() - 2);
                }

                return sub + pastBon.get(prVerb.indexOf(presentB)) + "ن";
            }
        }
        pat = Pattern.compile("[ب,ده‌,نه‌]" + "[مان,تان,یان,م,ت,ی]*" + "." + "ات");
        m1 = pat.matcher(word);
        if (m1.find()) {
            int ind = word.lastIndexOf("ا");
            String presentB = word.substring(ind - 1, ind);
            presentB += "ه" + zwnj;
            if (prVerb.contains(presentB)) {
                String sub = word.substring(0, m1.start());
                if (sub.endsWith("ب")) {
                    sub = sub.substring(0, sub.length() - 1);
                } else if (sub.endsWith("ده") || sub.endsWith("نه")) {
                    sub = sub.substring(0, sub.length() - 2);
                }

                return sub + pastBon.get(prVerb.indexOf(presentB)) + "ن";
            }
        }

        return word;
    }

    public static String condiderMasdar(String word, String[] pastVerb) {
        isVerb = false;
        String negative = "نه" + zwnj, de = "ده" + zwnj, neg = "نا";
        for (int i = 0; i < pastVerb.length; i++) {
            String masdar = pastVerb[i] + "ن";
            String[] v1 = new String[4];
            v1[0]=masdar;
            v1[1] = de + masdar;
            v1[2] = negative + masdar;
            v1[3] = neg + masdar;
            for (int j = 0; j < v1.length; j++) {
                //if (masdar.equals("مان") || masdar.equals("دا")) { }
                    if (word.equals(v1[j])) {
                        isVerb = true;
                        return masdar;
                    }
               
                    if (word.contains(v1[j])) {
                        //isVerb = true;
                        //return prefixHandler(word.substring(0, word.indexOf(v1[j])),masdar) + masdar;

                         if (masdar.equals("گرتن") || masdar.equals("بردن")
                              || masdar.equals("بوون") || masdar.equals("کردن")) {

                                    String stem=helperVerb(word, masdar, pastVerb);
                                    if(isVerb)
                                        return stem;

                                }
                    
                }
            }


        }

        return word;
    }

    public static String getLemVerb(String word, String[] pastVerb, ArrayList verb,
            String[] presentVerb, ArrayList prVerb) {

        String res = specialTagger(word, pastVerb, presentVerb, prVerb);
        if (!res.equals(word)) {
            return res;
        }
        res = kraVerb(word);
        if (!res.equals(word)) {
            return res;
        }
        res = krdVerb(word);
        if (!res.equals(word) || isVerb) {
            return res;
        }
        res = booVerb(word, pastVerb);
        if (!res.equals(word) || isVerb) {
            return res;
        }
        res = hasVerb(word);
        if (!res.equals(word)) {
            return res;
        }
        res = condiderMasdar(word, pastVerb);
        if (!res.equals(word) || isVerb) {
            return res;
        }


        res = pastVerbLem(word, pastVerb, verb);
        if (!res.equals(word) || isVerb) {
            return res;
        }
        res = exceptionalVerb(word, prVerb, verb);
        if (!res.equals(word) || isVerb) {
            return res;
        }
        res = presentVerbLem(word, pastVerb, presentVerb, prVerb);
        return res;
    }
}
