package oop.ex6.main;

import oop.ex6.parsing.StructureException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Sjavac is in charge on reading Sjavac files.
 * @author Yotam Suliman and Edan Topper.
 */
public class Sjavac {

    private static final int FILE_ERR_OUTPUT = 2;
    private static final int CONTENT_ERR_OUTPUT = 1;
    private static final int FILE_PASSED_OUTPUT = 0;
    private static final String ERROR_WRONG_USAGE =
            "ERROR: Wrong usage. Should receive only one argument which is the file path.";

    /**
     * this method is in charge on getting args from the cmd, read it to a file and check if the file is valid.
     * @param args cmd params.
     */
    public static void main(String[] args) {
        if(!argsValidation(args)){
            System.out.println(FILE_ERR_OUTPUT);
            System.err.println(ERROR_WRONG_USAGE);
        }
        String filePath = args[0];
        try (FileReader fileReader1 = new FileReader(filePath);
             BufferedReader reader1 = new BufferedReader(fileReader1);
             FileReader fileReader2 = new FileReader(filePath);
             BufferedReader reader2 = new BufferedReader(fileReader2)) {
            Validator validator = new Validator(reader1, reader2);
            validator.run();
        }
        // Some File Exception
        catch (IOException e){
            System.out.println(FILE_ERR_OUTPUT);
            System.err.println(e.getMessage());
            return;
        }
        // Some Validation Exception
        catch (StructureException | LogicalException e){
            System.out.println(CONTENT_ERR_OUTPUT);
            System.err.println(e.getMessage());
            return;
        }
        // Code is OK
        System.out.println(FILE_PASSED_OUTPUT);
    }

    /**
     *
     * @param args the received args.
     * @return true if there are many args as needed.
     */
    private static boolean argsValidation(String[] args) {
        return args.length == CONTENT_ERR_OUTPUT;
    }
}
