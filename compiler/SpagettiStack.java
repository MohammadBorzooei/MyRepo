package Model;

public class SpagettiStack {
    public static SymbolTable root = new SymbolTable(null);
    public static SymbolTable curr = root;
    public static int size  = 0;
    public  static void add(){
        curr = new SymbolTable(curr);
        size++;
    }
    public static void goup(){
        curr = curr.parent;
        size--;
    }
    public  static void add(boolean isClone){
        curr = new SymbolTable(curr,isClone);
        size++;
    }

}
