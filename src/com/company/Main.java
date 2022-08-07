package com.company;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {

    static String pathToTable;

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Введите номер ИУК'a: ");
        int IYK_num = scanner.nextInt(); //scanner.nextInt();
        System.out.print("Введите кол-во баллов в сумме: ");
        int myResult = scanner.nextInt(); //scanner.nextInt();

        URL myUrl = null;
        switch (IYK_num) {
            case 6:
                myUrl = UrlIYK6();
                break;
            case 4:
                myUrl = UrlIYK4();
                break;
            case 5:
            case 2:
                myUrl = UrlIYK5and2();
                break;
            default:
                System.out.println("Такого ИУК'а нету в вашем списке!");
                return;
        }
        System.out.println(myUrl.getFile());

        pathToTable = "/tables.pdf";
        BufferedInputStream inputStream = new BufferedInputStream(myUrl.openStream());
        FileOutputStream fileOS = new FileOutputStream(pathToTable);
        byte[] data = new byte[1024];
        int byteContent;
        while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
            fileOS.write(data, 0, byteContent);
        }

        checkPositionInTable(myResult);
    }

    static URL UrlIYK6() throws MalformedURLException {
        return new URL("https://priem.bmstu.ru/lists/upload/enrollees/first/kaluga-1/10.05.03.pdf");
    }

    static URL UrlIYK4() throws MalformedURLException {
        return new URL("https://priem.bmstu.ru/lists/upload/enrollees/first/kaluga-1/09.03.04.pdf");
    }

    static URL UrlIYK5and2() throws MalformedURLException {
        return new URL("https://priem.bmstu.ru/lists/upload/enrollees/first/kaluga-1/09.03.01.pdf");
    }

    static void checkPositionInTable(int myResult) throws IOException {
        File file = new File(pathToTable);
        PDDocument myPDF = PDDocument.load(file);

        PDFTextStripper pdfTextStripper = new PDFTextStripper();
        String text = pdfTextStripper.getText(myPDF);

        String split_text = "5. Поступающие на места в рамках КЦП по общему конкурсу ";

        String[] words = text.split(split_text);
        System.out.println("===================");
        words = words[0].split("\n");
        List<String> arrayWords = new ArrayList<String>(Arrays.asList(words));

        for (int i = 0; i < 18; i++) {
            arrayWords.remove(0);
        }
        ArrayList<String> resultsOfPeoples = new ArrayList<String>();
        for (int i = 0; i < arrayWords.size(); i++) {
            if (arrayWords.get(i).split(" ").length > 10) {
                resultsOfPeoples.add(arrayWords.get(i));
            }
        }
        resultsOfPeoples.remove(resultsOfPeoples.size() - 1);
        resultsOfPeoples.remove(resultsOfPeoples.size() - 1);

        System.out.println(posInTable(resultsOfPeoples, myResult) + " место в списке");
        myPDF.close();
    }

    static int posInTable(ArrayList<String> resultsOfPeoples, int myResult) {
        int num = 0;
        boolean flag = false;
        for (String oneResult : resultsOfPeoples) {
            String text = oneResult.toLowerCase();
            List<String> item = new ArrayList<>(Arrays.asList(text.split(" ")));
            if (flag) {
                break;
            }
            switch (item.get(item.size() - 1)) {
                case "нет":
                    break;
                case "да":
                    switch (item.get(item.size() - 2)) {
                        case "нет":
                            break;
                        case "да":
                            num++;
                            if (Integer.parseInt(item.get(4)) <= myResult) {
                                flag = true;
                                break;
                            }
                            break;
                    }
                    break;
                default:
                    switch (item.get(item.size() - 2)) {
                        case "нет":
                            break;
                        case "да":
                            switch (item.get(item.size() - 3)) {
                                case "нет":
                                    break;
                                case "да":
                                    num++;
                                    if (Integer.parseInt(item.get(4)) <= myResult) {
                                        flag = true;
                                        break;
                                    }
                                    break;
                            }
                            break;
                        default:
                            switch (item.get(item.size() - 3)) {
                                case "нет":
                                    break;
                                case "да":
                                    switch (item.get(item.size() - 4)) {
                                        case "нет":
                                            break;
                                        case "да":
                                            num++;
                                            if (Integer.parseInt(item.get(4)) <= myResult) {
                                                flag = true;
                                                break;
                                            }
                                            break;
                                    }
                                    break;
                            }
                            break;
                    }
                    break;
            }
        }
        return num;
    }
}
