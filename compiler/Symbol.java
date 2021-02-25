package compiler;

public class SymbolTable {
    private final String token;
    private final Object value;

    public SymbolTable(String token) {
        this.token = token;
        this.value = token;
    }

    public SymbolTable(String token, Object value) {
        this.token = token;

        this.value = value;
    }

    public String getToken() {
        return token;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}


