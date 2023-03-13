package oop.ex6.parsing;

/**
 * Class which in charge of receive a line and classify it to the relevant line type.
 * @author Yotam Suliman and Edan Topper.
 */
public class Classifier {
    // regex constants.
    private static final String VOID = "\\s*void\\s+";
    private static final String VAR_NAME = "\\s*([A-Za-z]|_\\w+)\\w*\\s*";
    private static final String FUNC_NAME = "\\s*[A-Za-z]\\w*\\s*";
    private static final String OPEN_BRACKETS = "\\s*\\(\\s*";
    private static final String SEMI_COLON = "\\s*;\\s*";
    private static final String OPEN_PARENTHESIS = "\\s*\\{\\s*";
    private static final String CLOSE_BRACKETS = "\\s*\\)\\s*";
    private static final String TYPE = "(double|int|char|boolean|String)";
    private static final String FINAL = "\\s*(final\\s+)?\\s*";
    private static final String PARAM =  "(" + FINAL + TYPE + VAR_NAME + ")";
    private static final String PARAM_WITH_COMA = "(" + PARAM + "\\s*,\\s*" + ")*";
    private static final String FUNC_DEC_START = "\\s*void.*";
    private static final String VAR_DEC_START = "\\s*(char|boolean|String|int|double).*";
    private static final String INT_EXP = "(\\+|-)?[0-9]+";
    private static final String DOUBLE_EXP = "(\\+|-)?([0-9]+\\.[0-9]*|[0-9]*\\.[0-9]+|[0-9]+)";
    private static final String CHAR_EXP = "'[^']'";
    private static final String STRING_EXP = "\"[^\"]*\"";
    private static final String TRUE_OR_FALSE = "true|false";
    private static final String BOOLEAN_EXP ="(" + TRUE_OR_FALSE + "|" + DOUBLE_EXP + "|" + INT_EXP + ")";
    private static final String CHAR_VAR = FINAL + "\\s*char" + "(" + VAR_NAME + "(=\\s*" + "(" +
            VAR_NAME + "|" + CHAR_EXP + ")" + "\\s*)?\\s*,\\s*)*" +
            VAR_NAME + "(=\\s*" + "(" + VAR_NAME + "|" + CHAR_EXP + ")" +"\\s*)?\\s*;\\s*";
    private static final String STRING_VAR = FINAL + "\\s*String" + "(" + VAR_NAME +
            "(=\\s*" + "(" + VAR_NAME +"|" + STRING_EXP + ")" + "\\s*)?\\s*,\\s*)*"
            + VAR_NAME + "(=\\s*" + "(" + VAR_NAME+ "|" + STRING_EXP + ")" + "\\s*)?\\s*;\\s*";
    private static final String INT_VAR = FINAL + "\\s*int" + "(" + VAR_NAME +
            "(=\\s*" + "(" + VAR_NAME + "|" + INT_EXP + ")" +
            "\\s*)?\\s*,\\s*)*" + VAR_NAME + "(=\\s*" + "(" + VAR_NAME +"|"+ INT_EXP + ")"+ "\\s*)?\\s*;\\s*";
    private static final String DOUBLE_VAR = FINAL + "\\s*double" + "(" + VAR_NAME +
            "(=\\s*" + "(" + VAR_NAME + "|" + DOUBLE_EXP + ")"+
            "\\s*)?\\s*,\\s*)*" + VAR_NAME + "(=\\s*" +"(" + VAR_NAME+"|"+DOUBLE_EXP+")" + "\\s*)?\\s*;\\s*";
    private static final String BOOLEAN_VAR = FINAL + "\\s*boolean" + "(" + VAR_NAME +
            "(=\\s*" + "(" +VAR_NAME+"|"+BOOLEAN_EXP+")" + "\\s*)?\\s*,\\s*)*" +
            VAR_NAME + "(=\\s*" +"("+ VAR_NAME +"|"+BOOLEAN_EXP+")"+ "\\s*)?\\s*;\\s*";
    private static final String CONDITION = "\\s*(" + BOOLEAN_EXP + "|" + VAR_NAME + "|" + ")\\s*" +
            "((\\|\\||&&)\\s*(" + BOOLEAN_EXP + "|" + VAR_NAME + "|" + "))*\\s*";
    private static final String IF_OR_WHILE = "\\s*(if|while)\\s*" +OPEN_BRACKETS + CONDITION +
            CLOSE_BRACKETS + OPEN_PARENTHESIS;
    private static final String RETURN_REGEX = "\\s*return\\s*;";
    private static final String FUNC_PARAM = "\\s*((" + BOOLEAN_EXP + "|" + VAR_NAME + "|" + STRING_EXP +
            "|" + CHAR_EXP + ")\\s*,)*\\s*(" + BOOLEAN_EXP + "|" + VAR_NAME +
            "|" + STRING_EXP + "|" + CHAR_EXP + ")?";
    private static final String ASSIGNMENT_REGEX = "\\s*(" + VAR_NAME + "\\s*=\\s*" + "(" +BOOLEAN_EXP + "|" +
            VAR_NAME + "|" + STRING_EXP +"|" + CHAR_EXP + ")" + "\\s*,\\s*)*\\s*" +
            VAR_NAME + "\\s*=\\s*" + "(" + BOOLEAN_EXP + "|" + VAR_NAME + "|" +
            STRING_EXP +"|" + CHAR_EXP +  ")" + SEMI_COLON;
    private static final String FUNC_CALL_REGEX = FUNC_NAME+OPEN_BRACKETS + FUNC_PARAM +
            CLOSE_BRACKETS+SEMI_COLON;
    private static final String FUNC_DEC = VOID+FUNC_NAME+OPEN_BRACKETS+PARAM_WITH_COMA+
            PARAM+"?"+CLOSE_BRACKETS+OPEN_PARENTHESIS;

    // string constants.
    public static final String METHOD_DEC = "method";
    public static final String VAR_DEC = "var_dec";
    public static final String IF_WHILE = "if_or_while";
    public static final String FUNC_CALL = "func_call";
    public static final String ASSIGNMENT = "assignment";
    public static final String RETURN = "return";
    public static final String DOUBLE = "double";
    public static final String INT = "int";
    private static final String BOOLEAN = "boolean";
    private static final String STRING = "String";
    private static final String CHAR = "char";
    // messages constants
    private static final String GLOBAL_VAR_EXCEPTION = "Global var declaration is invalid.";
    private static final String GLOBAL_FUNC_EXCEPTION = "Global func declaration is invalid.";
    private static final String INVALID_LINE_EXCEPTION = "There was no match to a valid line.";

    /**
     *
     * @param curCommand the current line.
     * @return classify the type of the line.
     * @throws StructureException invalid line.
     */
    public static String classify(String curCommand) throws StructureException {
        if(methodMatch(curCommand))
            return METHOD_DEC;
        else if(varMatch(curCommand))
            return VAR_DEC;
        else if(ifWhileMatch(curCommand))
            return IF_WHILE;
        else if(funcCall(curCommand))
            return FUNC_CALL;
        else if(assignment(curCommand))
            return ASSIGNMENT;
        else if(curCommand.matches(RETURN_REGEX))
            return RETURN;
        throw new StructureException(INVALID_LINE_EXCEPTION);
    }

    /*
    checks if the line is assignment line.
     */
    private static boolean assignment(String curCommand) {
        return curCommand.matches(ASSIGNMENT_REGEX);
    }

    /*
    checks if the line is function call.
     */
    private static boolean funcCall(String curCommand) {
        return curCommand.matches(FUNC_CALL_REGEX);
    }

    /*
    checks if the line is if or while line.
     */
    private static boolean ifWhileMatch(String curCommand) {
        return curCommand.matches(IF_OR_WHILE);
    }

    /*
    checks if the line is a var declaration line.
     */
    private static boolean varMatch(String curCommand) throws StructureException {
        if (curCommand.matches(BOOLEAN_VAR) || curCommand.matches(INT_VAR) || curCommand.matches(STRING_VAR)
                || curCommand.matches(DOUBLE_VAR) || curCommand.matches(CHAR_VAR))
            return true;
        else if (curCommand.matches(VAR_DEC_START))
            throw new StructureException(GLOBAL_VAR_EXCEPTION);
        return false;
    }

    /*
    check if the line is method declaration line.
     */
    private static boolean methodMatch(String curCommand) throws StructureException {
        if(curCommand.matches(FUNC_DEC))
            return true;
        else if(curCommand.matches(FUNC_DEC_START))
            throw new StructureException(GLOBAL_FUNC_EXCEPTION);
        return curCommand.matches(FUNC_DEC);
    }

    /**
     *
     * @param curCommand the current line.
     * @return true if the line is open scope line.
     */
    public static boolean isOpenScopeLine(String curCommand) {
        return curCommand.contains("{");

    }

    /**
     *
     * @param curCommand the current line.
     * @return true if the line is closed scope line.
     */
    public static boolean isCloseScopeLine(String curCommand) {
        return curCommand.contains("}");
    }

    /**
     *
     * @param type1 type of parameter number 1.
     * @param type2 type of parameter number 2.
     * @return true if it can assign parameter one to parameter two.
     */
    public static boolean matchTypes(String type1, String type2)
    {
        if(type1 == null || type2 == null)
            return false;
        if (type1.equals(type2))
            return true;
        if(type1.equals(INT) && type2.equals(DOUBLE))
            return true;
        if(type1.equals(DOUBLE) && type2.equals(BOOLEAN))
            return true;
        return type1.equals(INT) && type2.equals(BOOLEAN);
    }

    /**
     *
     * @param token the current word.
     * @return which type is the token.
     */
    public static String type(String token)
    {
      if(token.matches(INT_EXP))
          return INT;
      else if (token.matches(DOUBLE_EXP))
            return DOUBLE;
      else if(token.matches(TRUE_OR_FALSE))
          return BOOLEAN;
      else if (token.matches(STRING_EXP))
          return STRING;
      else if (token.matches(CHAR_EXP))
          return CHAR;
      else
          return null;
    }
}


