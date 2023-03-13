package oop.ex6.symbol_table;

import java.util.HashMap;

/**
 * associated each identifier (such as variables and ect) to information. (final, initialized, ect)
 * @author Yotam Suliman and Edan Topper.
 */
public class SymbolTable {

    private final HashMap<String, VarData> map;


    /**
     * constructor.
     */
    public SymbolTable(){
        this.map = new HashMap<>();
    }

    /**
     * adding an identifier to the symbol table.
     * @param varName the name of the identifier.
     * @param type the type information.
     * @param isFinal final information.
     * @param isInitialized initialized information.
     */
    public void add(String varName, String type, boolean isFinal, boolean isInitialized){
        VarData varData = new VarData(type, isFinal, isInitialized);
        map.put(varName, varData);
    }

    /**
     *
     * @param varName the name of the identifier.
     * @return the data about this identifier.
     */
    public VarData getVarData(String varName) {
        if (map.containsKey(varName))
            return map.get(varName);

        return null;
    }
}
