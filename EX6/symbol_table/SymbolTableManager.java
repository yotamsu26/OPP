package oop.ex6.symbol_table;

import java.util.LinkedList;

/**
 * manging the scopes by holding linked list of symbol tables.
 * @author Yotam Suliman and Edan Topper.
 */
public class SymbolTableManager {
    private final LinkedList<SymbolTable> symbolTables;

    /**
     * constructor.
     */
    public SymbolTableManager(){
        this.symbolTables = new LinkedList<>();
    }

    /**
     * open a new scope symbol table and adds it to the list.
     */
    public void openScope(){
        SymbolTable scopeSymbolTable = new SymbolTable();
        symbolTables.addFirst(scopeSymbolTable);
    }

    /**
     * closed a scope symbol table and adds it to the list.
     */
    public void closeScope(){
        symbolTables.removeFirst();
    }

    /**
     * adds an identifier to symbol table.
     * @param varName the identifier name.
     * @param type the relevant type.
     * @param isFinal final information.
     * @param initialized initialized information.
     */
    public void add(String varName, String type, boolean isFinal, boolean initialized) {
        symbolTables.getFirst().add(varName, type, isFinal, initialized);
    }

    /**
     *
     * @param varName the name of the identifier.
     * @return the data of the identifier.
     */
    public VarData getVarData(String varName){
        for (SymbolTable symbolTable: symbolTables){
            VarData varData = symbolTable.getVarData(varName);
            if (varData != null){
                return varData;
            }
        }
        return null;
    }

    /**
     * checks if the relevant identifier is in the current scope.
     * @param varName the identifier name.
     * @return true if it is in the current scope.
     */
    public boolean isOnCurrentScope(String varName){
        return symbolTables.getFirst().getVarData(varName) != null;
    }
}
