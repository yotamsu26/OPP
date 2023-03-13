package oop.ex6.symbol_table;

/**
 * data of each identifier.
 * @authoe Yotam Suliman and Edan Topper.
 */
public class VarData {
    private final String type;
    private final boolean isFinal;
    private boolean isInitialized;

    /**
     *
     * @param type the relevant type.
     * @param isFinal checks if it is final.
     * @param isInitialized checks if initialized.
     */
    public VarData(String type, boolean isFinal, boolean isInitialized) {
        this.type = type;
        this.isFinal = isFinal;
        this.isInitialized = isInitialized;
    }

    /**
     *
     * @return the type of the data.
     */
    public String type(){
        return type;
    }

    /**
     *
     * @return true if the var is initialized.
     */
    public boolean initialized() {
        return isInitialized;
    }

    public void setInitialized(boolean b) {
        this.isInitialized = b;
    }

    /**
     *
     * @return true if the var is final
     */
    public boolean isFinal() {
        return isFinal;
    }
}
