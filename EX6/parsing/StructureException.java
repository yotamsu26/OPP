package oop.ex6.parsing;

/**
 * StructureException, when there is an invalid line.
 * @author Yotam Suliman, Edan Topper
 * @see Exception
 */
public class StructureException extends Exception{
    /**
     * constructor.
     * @param message the message to print.
     */
    StructureException(String message)
    {
        super(message);
    }
}
