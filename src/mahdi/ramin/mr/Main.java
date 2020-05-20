package mahdi.ramin.mr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class Main {
    private final Scanner scanner = new Scanner(System.in);
    private boolean bool = false;
    private final Map<String, String> cards = new TreeMap<>();
    private final Map<String, Integer> mistakes = new TreeMap<>();
    private static String importFileName;
    private static String exportFileName;
    private File file;
    private int numberOfCards;
    private String termOfTheCards;
    private final String theCard = "The card:";
    private final String fileNameText = "File name:";

    private final ArrayList<String> logArrayList = new ArrayList<>();
    private static boolean firstSave = false;
    private static boolean changed = false;

    Main() {
        importFileName = null;
        exportFileName = null;
    }

    public static void main(String[] args) {
        Main main = new Main();
        if (args.length >= 2) {
            if (args[0].trim().equals("-import")) {
                importFileName = args[1].trim();
                main.importing(importFileName);
                if (args.length == 4) {
                    exportFileName = args[3].trim();
                    firstSave = true;
                    main.exporting(exportFileName, "");
                }
            } else if (args[0].trim().equals("-export")) {
                exportFileName = args[1].trim();
                firstSave = true;
                main.exporting(exportFileName, "");
                if (args.length == 4) {
                    importFileName = args[3].trim();
                    main.importing(importFileName);
                }
            }
        }
        main.display();
    }

    private void display() {
        do {
            String actionText = "Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):";
            System.out.println(actionText);
            String action = scanner.nextLine().trim();
            logArrayList.add(actionText);
            logArrayList.add(action);

            switch (action) {
                case "add":
                    adding();
                    break;
                case "remove":
                    removing();
                    break;
                case "import":
                    importing(importFileName);
                    break;
                case "export":
                    System.out.println(fileNameText);
                    String fileName = scanner.nextLine();
                    logArrayList.add(fileNameText);
                    logArrayList.add(fileName);
                    exportFileName = fileName;
                    exporting(exportFileName, "export");
                    break;
                case "ask":
                    asking();
                    break;
                case "exit":
                    firstSave = true;
                    if (importFileName != null && exportFileName == null) {
                        exportFileName = importFileName;
                    } else if (exportFileName == null) {
                        System.out.println(fileNameText);
                        String fileName2 = scanner.nextLine();
                        logArrayList.add(fileNameText);
                        logArrayList.add(fileName2);
                        exportFileName = fileName2;
                    }
                    System.out.println("Bye bye!");
                    logArrayList.add("Bye bye!");
                    exporting(exportFileName, "exit");
                    bool = true;
                    break;
                case "log":
                    log();
                    break;
                case "hardest card":
                    getHardestCard();
                    break;
                case "reset stats":
                    resettingMistakesCards();
                    break;
                default:

            }
            System.out.println();
            logArrayList.add("\n");
        } while (!bool);
    }

    private void adding() {
        System.out.println(theCard);
        termOfTheCards = scanner.nextLine().trim();
        logArrayList.add(theCard);
        logArrayList.add(termOfTheCards);

        if (cards.containsKey(termOfTheCards)) {
            String strResult = "The card \"" + termOfTheCards + "\" already exists.";
            System.out.println(strResult);
            logArrayList.add(strResult);
            return;
        }
        String theDefinitionOfTheCard = "The definition of the card:";
        System.out.println(theDefinitionOfTheCard);
        String definitionOfTheCards = scanner.nextLine().trim();
        logArrayList.add(theDefinitionOfTheCard);
        logArrayList.add(definitionOfTheCards);

        if (cards.containsValue(definitionOfTheCards)) {
            String strResult = "The definition \"" + definitionOfTheCards + "\" already exists.";
            System.out.println(strResult);
            return;
        }
        cards.put(termOfTheCards, definitionOfTheCards);
        mistakes.put(termOfTheCards, 0);
        String strResult = "The pair (\"" + termOfTheCards + "\":\"" + definitionOfTheCards + "\") has been added.";
        System.out.println(strResult);
        logArrayList.add(strResult);
        changed = true;
    }

    private void removing() {
        System.out.println(theCard);
        termOfTheCards = scanner.nextLine().trim();
        logArrayList.add(theCard);
        logArrayList.add(termOfTheCards);
        if (cards.get(termOfTheCards) != null) {
            cards.remove(termOfTheCards);
            mistakes.remove(termOfTheCards);
            String theCardHasBeenRemoved = "The card has been removed.";
            System.out.println(theCardHasBeenRemoved);
            logArrayList.add(theCardHasBeenRemoved);
            changed = true;
        } else {
            String strResult = "Can't remove \"" + termOfTheCards + "\": there is no such card.";
            System.out.println(strResult);
            logArrayList.add(strResult);
        }
    }

    private void importing(String fileName) {
        if (fileName == null) {
            System.out.println(fileNameText);
            fileName = scanner.nextLine();
            logArrayList.add(fileNameText);
            logArrayList.add(fileName);
        }
        file = new File(fileName);
        int counter = 0;
        if (file.exists()) {
            try (Scanner reader = new Scanner(file)) {
                while (reader.hasNextLine()) {
                    String[] string = reader.nextLine().split(":");
                    cards.put(string[0], string[1]);
                    mistakes.put(string[0], Integer.parseInt(string[2]));
                    counter++;
                }
                System.out.printf("%d cards have been loaded.\n", counter);
                logArrayList.add(counter + " cards have been loaded.");
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
                logArrayList.add(ex.getMessage());
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                logArrayList.add(ex.getMessage());
            }
        } else {
            String fileNotFound = "File not found.";
            System.out.println(fileNotFound);
            logArrayList.add(fileNotFound);
        }
    }

    private void exporting(String fileName, String invoker) {
        file = new File(fileName);
        numberOfCards = 0;

        try (PrintWriter writer = new PrintWriter(file)) {
            for (String key: cards.keySet()) {
                String val = cards.get(key);
                Integer mis = mistakes.get(key) == null ? 0 : mistakes.get(key);
                writer.print(key + ":" + val + ":" + mis);
                writer.print("\r\n");
                numberOfCards++;
            }
            if (!firstSave || invoker.equals("export") || changed) {
                System.out.printf("%d cards have been saved.\n", numberOfCards);
                logArrayList.add(numberOfCards + " cards have been saved.");
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            logArrayList.add(ex.getMessage());
        }
    }

    private void asking() {
        String howManyTimeToAsk = "How many times to ask?";
        System.out.println(howManyTimeToAsk);
        logArrayList.add(howManyTimeToAsk);
        try {
            int numberOfAsk = Integer.parseInt(scanner.nextLine());
            logArrayList.add(String.valueOf(numberOfAsk));
            while (numberOfAsk > 0 && cards.size() != 0) {
                for (String key : cards.keySet()) {
                    if (numberOfAsk > 0) {
                        String strResult = "Print the definition of \"" + key + "\":";
                        System.out.println(strResult);
                        logArrayList.add(strResult);
                        String getAnswer = scanner.nextLine().trim();
                        logArrayList.add(getAnswer);
                        String val = cards.get(key);
                        if (getAnswer.equals(val)) {
                            String correctAnswer = "Correct answer.";
                            System.out.println(correctAnswer);
                            logArrayList.add(correctAnswer);
                        } else {
                            if (cards.containsValue(getAnswer)) {
                                String key2 = "";
                                for (String k : cards.keySet()) {
                                    if (cards.get(k).equals(getAnswer)) {
                                        key2 = k;
                                    }
                                }
                                String strResult2 = "Wrong answer. The correct one is \"" + val + "\", " +
                                        "you've just written the definition of \"" +
                                        key2 + "\".";
                                System.out.println(strResult2);
                                logArrayList.add(strResult2);
                            } else {
                                String strResult3 = "Wrong answer. The correct one is \"" + val + "\".";
                                System.out.println(strResult3);
                                logArrayList.add(strResult3);
                            }
                            mistakes.replace(key, mistakes.get(key),mistakes.get(key) + 1);
                        }
                    } else {
                        break;
                    }
                    numberOfAsk--;
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            logArrayList.add(ex.getMessage());
        }
    }

    private void log() {
        System.out.println(fileNameText);
        String fileName = scanner.nextLine();
        logArrayList.add(fileNameText);
        logArrayList.add(fileName);
        file = new File(fileName);

        try (PrintWriter writer = new PrintWriter(file)) {
            for (String log: logArrayList) {
                writer.print(log);
                writer.print("\r\n");
                numberOfCards++;
            }
            String strResult = "The log has been saved.";
            System.out.println(strResult);
            logArrayList.add(strResult);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            logArrayList.add(ex.getMessage());
        }
    }

    private void getHardestCard() {
        String strResult = "There are no cards with errors.";
        if (mistakes.isEmpty()) {
            System.out.println(strResult);
            logArrayList.add(strResult);
        } else {
            int[] index = new int[mistakes.size()];
            int counter = 0;
            for (String string: mistakes.keySet()) {
                Integer integer = mistakes.get(string);
                index[counter] = integer;
                counter++;
            }
            Arrays.sort(index);
            int max = index[index.length - 1];
            ArrayList<String> mis = new ArrayList<>();
            if (max == 0) {
                System.out.println(strResult);
                logArrayList.add(strResult);
            } else {
                for (String string: mistakes.keySet()) {
                    Integer integer = mistakes.get(string);
                    if (integer == max) {
                        mis.add(string);
                    }
                }
                if (mis.size() < 2) {
                    String strResult2 = "The hardest card is \"" + mis.get(0) +"\". You have " + max + " errors answering it.";
                    System.out.println(strResult2);
                    logArrayList.add(strResult2);
                } else {
                    counter = 0;
                    String str1 = "The hardest cards are ";
                    StringBuilder str2 = new StringBuilder();
                    String str3 = ". You have " + max + " errors answering them.";
                    for (String str: mis) {
                        str2.append('\"').append(str).append("\"");
                        if (counter != mis.size() - 1) {
                            str2.append(", ");
                        }
                        counter++;
                    }
                    String strResult3 = str1 + str2 + str3;
                    System.out.println(strResult3);
                    logArrayList.add(strResult3);
                }
            }
        }
    }

    private void resettingMistakesCards() {
        for (String string: mistakes.keySet()) {
            mistakes.replace(string, mistakes.get(string), 0);
        }
        String strResult = "Card statistics has been reset.";
        System.out.println(strResult);
        logArrayList.add(strResult);
    }
}
