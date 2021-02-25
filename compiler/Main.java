package compiler;

import java.io.FileReader;
import java.io.IOException;

import Lexical.LexicalAnalyzer;

public class Main {
    public static void main(String[] args) throws IOException {
        LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(new FileReader("type file name here"));
        System.out.println("Compilation complete");

    }
}




