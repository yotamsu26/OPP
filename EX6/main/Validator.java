package oop.ex6.main;

import oop.ex6.parsing.Classifier;
import oop.ex6.parsing.StructureException;
import oop.ex6.parsing.Tokenizer;
import oop.ex6.symbol_table.SymbolTableManager;
import oop.ex6.symbol_table.VarData;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Validator of the sjava file. Checks that the sjava file is written in the
 * right syntax of the sjava language.
 * @author Yotam Suliman , Edan Topper
 */
public class Validator {

    //********* Error massages *********//
    private static final String INVALID_LINE_IN_THE_GLOBAL_SCOPE_ERR = "Invalid line in the global scope";
    private static final String TRYING_TO_ASSIGN_A_VALUE_TO_UNDECLARED_VARIABLE_ERR =
            "Trying to assign a value to undeclared variable";
    private static final String TRYING_TO_ASSIGN_A_VALUE_FROM_A_NON_MATCHING_CONSTANT_ERR =
            "Trying to assign a value from a non matching Constant";
    private static final String TRYING_TO_ASSIGN_A_VALUE_FROM_UNINITIALIZED_VARIABLE_ERR =
            "Trying to assign a value from uninitialized variable";
    private static final String DECLARING_A_FINAL_VARIABLE_WITHOUT_ASSIGNING_A_VALUE_ERR =
            "Declaring a final variable without assigning a value";
    private static final String TRYING_TO_ASSIGN_A_VALUE_FROM_A_NON_TYPE_MATCHING_VARIABLE_ERR =
            "Trying to assign a value from a non type-matching variable";
    private static final String PARAMETERS_IN_THE_SAME_SCOPE_WITH_SAME_NAME_ERR =
            "2 parameters in the same scope with same name";
    private static final String FUNCTIONS_WITH_SAME_NAME_ERR = "2 Functions with same name";
    private static final String NON_MATCHING_NUMBER_OF_SCOPES_OPENED_AND_SCOPES_CLOSED_ERR =
            "Non matching number of scopes opened and scopes closed";
    private static final String FUNCTION_DECLARATION_INSIDE_FUNCTION_BODY_ERR =
            "Function declaration inside function body";
    private static final String ILLEGAL_LINE_INSIDE_SCOPE_ERR = "Illegal line inside scope";
    private static final String TRYING_TO_CALL_TO_UNKNOWN_FUNCTION_ERR = "Trying to call to unknown function";
    private static final String NUMBER_OF_ARGUMENTS_GIVEN_TO_FUNCTION_DOESNT_MATCH_ERR =
            "Number of arguments given to function doesnt match";
    private static final String ARGUMENT_SUPPLIED_TO_FUNCTION_IS_NOT_INITIALIZED_ERR =
            "Argument supplied to function is not initialized";
    private static final String MISMATCH_OF_ARGUMENT_TYPE_GIVEN_TO_FUNCTION_ERR =
            "Mismatch of argument type given to function";
    private static final String VARIABLE_IN_BOOLEAN_EXPRESSION_IS_NOT_INITIALIZES_ERR =
            "Variable in boolean expression is not initializes";
    private static final String VARIABLE_IN_BOOLEAN_EXPRESSION_CANNOT_BE_ASSIGNED_TO_BOOLEAN_ERR =
            "Variable in boolean expression cannot be assigned to boolean";
    private static final String CONST_IN_BOOLEAN_EXPRESSION_CANNOT_BE_ASSIGNED_TO_BOOLEAN_ERR =
            "Const in boolean expression cannot be assigned to boolean";

    //************* Constants ***********/
    private static final String FINAL = "final";
    private static final String SEMI_COL = ";";
    private static final String EQUAL_SIGN = "=";
    private static final String CLOSING_PARENTHESIS = ")";
    private static final String BOOLEAN = "boolean";
    private static final String COMMA = ",";

    //************* Fields ***********/
    private final BufferedReader reader2;
    private final SymbolTableManager symbolTableManager;
    private final HashMap<String, ArrayList<String>> funcMap;
    private int curScope;
    private Tokenizer tokenizer;

    /**
     * Constructs a new Validator object
     * @param reader1 reader for the first pass
     * @param reader2 reader for the second pass
     * @throws IOException in case there is a problem with the reader
     */
    public Validator(BufferedReader reader1, BufferedReader reader2) throws IOException {
        this.tokenizer = new Tokenizer(reader1);
        this.symbolTableManager = new SymbolTableManager();
        this.funcMap = new HashMap<>();
        this.curScope = 0;
        this.reader2 = reader2;
    }

    /**
     * runs the Validator and validate the file
     * @throws StructureException in case of a line structure problem
     * @throws LogicalException in case of a logical problem
     * @throws IOException in case of a problem with the file
     */
    public void run() throws StructureException, LogicalException, IOException {
        firstRun();
        secondRun();
    }

    // ***************************** //
    // *******FirstRunFunctions***** //
    // ***************************** //

    /*
     * Initialize all the things needed in the global scope.
     * catches some errors of the structure
     */
    private void firstRun() throws StructureException, LogicalException, IOException {
        symbolTableManager.openScope(); // init the globalScope
        while (tokenizer.curCommand() != null){
            String lineType = Classifier.classify(tokenizer.curCommand());
            if (lineType.equals(Classifier.METHOD_DEC)){
                validateMethodDec();
            } else if (lineType.equals(Classifier.VAR_DEC)) {
                validateVarDec();
            } else if (lineType.equals(Classifier.ASSIGNMENT)) {
                validateAssignment();
            } else {
                    throw new LogicalException(INVALID_LINE_IN_THE_GLOBAL_SCOPE_ERR);
            }
        }
    }

    /*
     * Validate an assignment line
     */
    private void validateAssignment() throws IOException, LogicalException {
        // assumes structure is legal
        validateSingleAssignment();
        while(tokenizer.curToken().equals(COMMA)){
            tokenizer.advance(); // go after ","
            validateSingleAssignment();
        }
        tokenizer.advance(); // go after ";"
    }

    /*
     * Validate a single assignment <var> = <value>
     */
    private void validateSingleAssignment() throws LogicalException, IOException {
        String assignedVar = tokenizer.curToken();
        VarData varData = symbolTableManager.getVarData(assignedVar);
        if (varData == null || varData.isFinal()) // if final should be initialized
            throw new LogicalException(TRYING_TO_ASSIGN_A_VALUE_TO_UNDECLARED_VARIABLE_ERR);
        if (!varData.initialized() && !symbolTableManager.isOnCurrentScope(assignedVar)){
            //copy to cur symbol table
            symbolTableManager.add(assignedVar, varData.type(), varData.isFinal(), true);
            varData = symbolTableManager.getVarData(assignedVar);
        }
        tokenizer.advance(); // go after assignedVar
        tokenizer.advance(); // go after "="
        String value = tokenizer.curToken();
        VarData valueData = symbolTableManager.getVarData(value);
        if (valueData == null){
            if (!Classifier.matchTypes(Classifier.type(value), varData.type()))
                throw new LogicalException(TRYING_TO_ASSIGN_A_VALUE_FROM_A_NON_MATCHING_CONSTANT_ERR);
        }
        else if (!valueData.initialized()){
            throw new LogicalException(TRYING_TO_ASSIGN_A_VALUE_FROM_UNINITIALIZED_VARIABLE_ERR);
        } else if (!Classifier.matchTypes(valueData.type(), varData.type())){
            throw new LogicalException(TRYING_TO_ASSIGN_A_VALUE_FROM_A_NON_TYPE_MATCHING_VARIABLE_ERR);
        }
        varData.setInitialized(true);
        tokenizer.advance(); // go after value
    }

    /*
     * Validate a Var Declaration line
     */
    private void validateVarDec() throws IOException, LogicalException {
        boolean isFinal = (tokenizer.curToken().equals(FINAL));
        if (isFinal){
            tokenizer.advance(); // go after "final"
        }
        String type = tokenizer.curToken();
        tokenizer.advance(); // go after the type
        while(!tokenizer.curToken().equals(SEMI_COL)){
            boolean initialized = false;
            if (tokenizer.curToken().equals(COMMA))
                tokenizer.advance(); // go after the ","
            String varName = tokenizer.curToken();
            tokenizer.advance(); // go after the varName
            if (isFinal && !tokenizer.curToken().equals(EQUAL_SIGN))
                throw new LogicalException(DECLARING_A_FINAL_VARIABLE_WITHOUT_ASSIGNING_A_VALUE_ERR);
            if (tokenizer.curToken().equals(EQUAL_SIGN)){
                tokenizer.advance(); // go after the "="
                String value = tokenizer.curToken();
                VarData valueData = symbolTableManager.getVarData(value);
                if (valueData == null){
                    if (!Classifier.matchTypes(Classifier.type(value), type))
                        throw new LogicalException(TRYING_TO_ASSIGN_A_VALUE_FROM_A_NON_MATCHING_CONSTANT_ERR);
                }
                else if (!valueData.initialized()){
                    throw new LogicalException(TRYING_TO_ASSIGN_A_VALUE_FROM_UNINITIALIZED_VARIABLE_ERR);
                } else if (!Classifier.matchTypes(valueData.type(), type)){
                    throw new LogicalException(
                            Validator.TRYING_TO_ASSIGN_A_VALUE_FROM_A_NON_TYPE_MATCHING_VARIABLE_ERR);
                }
                initialized = true;
                tokenizer.advance(); // go after value
            }
            if (symbolTableManager.isOnCurrentScope(varName))
                throw new LogicalException(PARAMETERS_IN_THE_SAME_SCOPE_WITH_SAME_NAME_ERR);
            symbolTableManager.add(varName, type, isFinal, initialized);
        }
        tokenizer.advance(); // go after ";"
    }

    /*
     * validate the method Dec - adds it to funcMap and skips all the lines inside the body
     */
    private void validateMethodDec() throws IOException, LogicalException {
        tokenizer.advance(); // go after "void"
        String funcName = tokenizer.curToken();
        if (funcMap.containsKey(funcName))
            throw new LogicalException(FUNCTIONS_WITH_SAME_NAME_ERR);
        tokenizer.advance(); // go after funcName
        tokenizer.advance(); // go after "("
        ArrayList<String> varTypes = getParams();
        tokenizer.advance(); // go after ")"
        funcMap.put(funcName, varTypes);
        tokenizer.advance(); // go after "{"
        curScope++;
        while(curScope != 0){
            if (tokenizer.curCommand() == null)
                throw new LogicalException(NON_MATCHING_NUMBER_OF_SCOPES_OPENED_AND_SCOPES_CLOSED_ERR);
            String curCommand = tokenizer.curCommand();
            if (Classifier.isOpenScopeLine(curCommand))
                curScope++;
            else if(Classifier.isCloseScopeLine(curCommand))
                curScope--;
            tokenizer.advanceLine();
        }
    }

    /*
     * returns the params inside the bracket
     */
    private ArrayList<String> getParams() throws IOException {
        ArrayList<String> params = new ArrayList<>();
        while(!tokenizer.curToken().equals(CLOSING_PARENTHESIS)){
            if (tokenizer.curToken().equals(COMMA)){
                tokenizer.advance(); // go after ","
            }
            if (tokenizer.curToken().equals(FINAL)){
                tokenizer.advance(); // go after "final"
            }
            String type = tokenizer.curToken();
            tokenizer.advance(); // go after type
            tokenizer.advance(); // go after varName
            params.add(type);
        }
        return params;
    }


    // ***************************** //
    // *******SecondRunFunctions**** //
    // ***************************** //

    /*
     * runs over the whole program and validate all
     */
    private void secondRun() throws IOException, StructureException, LogicalException {
        tokenizer = new Tokenizer(reader2);
        while (tokenizer.curCommand() != null){
            verifyStatements();
        }
    }

    /*
     * Verify all kind of statements - classify the line and pass to the right functions
     */
    private void verifyStatements() throws StructureException, IOException, LogicalException {
        String line = tokenizer.curCommand();
        String classification = Classifier.classify(line);
        if(classification.equals(Classifier.VAR_DEC)){
            if (curScope == 0)
                tokenizer.advanceLine();
            else
                validateVarDec();
        } else if (classification.equals(Classifier.IF_WHILE)){
            validateIfWhile();
        } else if(classification.equals(Classifier.METHOD_DEC)){
            if (curScope == 0)
                validateAllMethod();
            else
                throw new LogicalException(FUNCTION_DECLARATION_INSIDE_FUNCTION_BODY_ERR);
        } else if (classification.equals(Classifier.FUNC_CALL)) {
            validateFuncCall();
        } else if (classification.equals(Classifier.ASSIGNMENT)) {
            validateAssignment();
        } else if (classification.equals(Classifier.RETURN)){
            tokenizer.advance(); // go after return
            tokenizer.advance(); // go after ";"
        }
        else
            throw new LogicalException(ILLEGAL_LINE_INSIDE_SCOPE_ERR);
    }

    /*
     * Runs throw the whole method and validate all
     */
    private void validateAllMethod() throws IOException, LogicalException, StructureException {
        symbolTableManager.openScope();
        curScope++;
        tokenizer.advance(); // go after void
        tokenizer.advance(); // go after funcName
        tokenizer.advance(); // go after "("
        while(!tokenizer.curToken().equals(CLOSING_PARENTHESIS)){
            boolean isFinal = false;
            String type;
            String varName;
            if (tokenizer.curToken().equals(COMMA))
                tokenizer.advance(); // go after ","
            if (tokenizer.curToken().equals(FINAL)){
                isFinal = true;
                tokenizer.advance(); // go after "final"
            }
            type = tokenizer.curToken();
            tokenizer.advance(); // go after type
            varName = tokenizer.curToken();
            tokenizer.advance(); // go after varName
            if(symbolTableManager.isOnCurrentScope(varName)){
                throw new LogicalException(PARAMETERS_IN_THE_SAME_SCOPE_WITH_SAME_NAME_ERR);
            }
            symbolTableManager.add(varName, type, isFinal, true);
        }
        tokenizer.advance(); // go after ")"
        tokenizer.advance(); // go after "{"
        while(!isEndOfFunc())
            verifyStatements();
        tokenizer.advance(); // go after }
        curScope--;
        symbolTableManager.closeScope();
    }

    /*
     * decide if cur line is a function end (return;\n})
     */
    private boolean isEndOfFunc() throws StructureException, IOException {
        if (Classifier.classify(tokenizer.curCommand()).equals(Classifier.RETURN) && curScope == 1){
            tokenizer.advanceLine();
            return Classifier.isCloseScopeLine(tokenizer.curCommand());
        }
        return false;
    }

    /*
     * Validate a function call line
     */
    private void validateFuncCall() throws LogicalException, IOException {
        String funcName = tokenizer.curToken();
        if (!funcMap.containsKey(funcName)){
            throw new LogicalException(TRYING_TO_CALL_TO_UNKNOWN_FUNCTION_ERR);
        }
        ArrayList<String> argsNeededTypes = funcMap.get(funcName);
        ArrayList<String> argsSupplied = new ArrayList<>();
        tokenizer.advance(); // go after the func name
        tokenizer.advance(); // go after "("
        while (!tokenizer.curToken().equals(CLOSING_PARENTHESIS)) {
            if (tokenizer.curToken().equals(COMMA)) {
                tokenizer.advance(); // go after ","
            }
            String varName = tokenizer.curToken();
            argsSupplied.add(varName);
            tokenizer.advance(); // go after varGiven
        }
        checkCallMatch(argsNeededTypes, argsSupplied);
        tokenizer.advance(); // go after ")"
        tokenizer.advance(); // go after ";"
    }

    /*
     * Check the match between the args supplied to a func and the args that the func takes
     */
    private void checkCallMatch(ArrayList<String> argsNeededTypes,
                                ArrayList<String> argsSupplied) throws LogicalException {
        if (argsSupplied.size() != argsNeededTypes.size()){
            throw new LogicalException(NUMBER_OF_ARGUMENTS_GIVEN_TO_FUNCTION_DOESNT_MATCH_ERR);
        }
        VarData varData;
        int counter = 0;
        for(String argGiven: argsSupplied){
            if ((varData = symbolTableManager.getVarData(argGiven))!=null){
                if (!varData.initialized()){
                    throw new LogicalException(ARGUMENT_SUPPLIED_TO_FUNCTION_IS_NOT_INITIALIZED_ERR);
                }
                if(!Classifier.matchTypes(varData.type(), argsNeededTypes.get(counter))){
                    throw new LogicalException(MISMATCH_OF_ARGUMENT_TYPE_GIVEN_TO_FUNCTION_ERR);
                }
            }
            else {
                if(!Classifier.matchTypes(Classifier.type(argGiven), argsNeededTypes.get(counter)))
                    throw new LogicalException(MISMATCH_OF_ARGUMENT_TYPE_GIVEN_TO_FUNCTION_ERR);
            }
            counter++;
        }
    }

    /*
     * Validate an if or while line
     */
    private void validateIfWhile() throws IOException, StructureException, LogicalException {
        tokenizer.advance(); // go after the if or while
        tokenizer.advance(); // go after "("
        validateIsBoolExpression();
        tokenizer.advance(); // go after ")"
        tokenizer.advance(); // go after "{"
        curScope++;
        symbolTableManager.openScope();
        while(!Classifier.isCloseScopeLine(tokenizer.curCommand())){
            verifyStatements();
        }
        tokenizer.advance(); // go after "}"
        curScope--;
        symbolTableManager.closeScope();
    }

    /*
     * Validate a boolean expression
     */
    private void validateIsBoolExpression() throws IOException, LogicalException {
        validateIsBoolSingle();
        while(!tokenizer.curToken().equals(CLOSING_PARENTHESIS)){
            // Assumes structure is ok by Classifier
            // so if its not ) then must be || or &&
            tokenizer.advance(); // go after || ot &&
            validateIsBoolSingle();
        }
    }

    /*
     * Validate a single boolean
     */
    private void validateIsBoolSingle() throws LogicalException, IOException {
        String varName = tokenizer.curToken();
        VarData varData = symbolTableManager.getVarData(varName);
        if (varData!=null){
            if (!varData.initialized()){
                throw new LogicalException(VARIABLE_IN_BOOLEAN_EXPRESSION_IS_NOT_INITIALIZES_ERR);
            }
            if(!Classifier.matchTypes(varData.type(), BOOLEAN)){
                throw new LogicalException(VARIABLE_IN_BOOLEAN_EXPRESSION_CANNOT_BE_ASSIGNED_TO_BOOLEAN_ERR);
            }
        }
        else {
            if(!Classifier.matchTypes(Classifier.type(varName), BOOLEAN))
                throw new LogicalException(CONST_IN_BOOLEAN_EXPRESSION_CANNOT_BE_ASSIGNED_TO_BOOLEAN_ERR);
        }
        tokenizer.advance();
    }
}
