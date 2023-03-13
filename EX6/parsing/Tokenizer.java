package oop.ex6.parsing;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tokenizer class is in charge on reading the file and split by the needs.
 * @author Yotam Suliman and Edan Topper.
 */
public class Tokenizer {
    // regex constants.
    private static final String TOKEN_REGEX = "'.*'|\".*\"|[+-]?\\d*\\.*\\d+|\\w+|[|]{2}|&{2}|\\S";
    private final Pattern emptyLine = Pattern.compile("\\s*");

    // class fields.
    private final BufferedReader reader;
    private ArrayList<String> tokens;
    private int curIndex;
    private String curLine;
    private final Pattern tokenPattern;

    /**
     *
     * @param reader the buffered reader to read from.
     * @throws IOException if the file is invalid or of there is a problem with reading it.
     */
    public Tokenizer(BufferedReader reader) throws IOException {
        this.reader = reader;
        this.tokenPattern = Pattern.compile(TOKEN_REGEX);
        advanceLine();
    }

    /**
     * advance the current index in 1.
     * @throws IOException throws exception if the advance is illegal.
     */
    public void advance() throws IOException {
        if (curIndex < tokens.size() - 1)
            curIndex++;

        else
            advanceLine();
    }

    /**
     * advances the token in one line.
     * @throws IOException throws an exception if the advance is invalid.
     */
    public void advanceLine() throws IOException {
        curLine = reader.readLine();
        while (curLine != null &&
                (curLine.startsWith("//") || checkEmptyLine()))
            curLine = reader.readLine();

        if (curLine != null) {
            this.curIndex = 0;
            Matcher m = tokenPattern.matcher(this.curLine);
            tokens = new ArrayList<>();
            while (m.find())
                tokens.add(curLine.substring(m.start(), m.end()));
        }
    }

    /*
     * return true if the line is empty.
     */
    private boolean checkEmptyLine() {
        Matcher m = emptyLine.matcher(curLine);
        return m.matches();
    }

    /**
     *
     * @return the current line.
     */
    public String curCommand() {
        return this.curLine;
    }

    /**
     *
     * @return the current index.
     */
    public String curToken() {
        return tokens.get(curIndex);
    }
}
