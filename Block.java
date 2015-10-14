import java.util.ArrayList;
/**
 *
 * @author pmargreff
 */
public class Block {
    public boolean _validate;
    public int _tag;
    public ArrayList<Cell> _cells;
    
    public Block(int size) {
        this._validate = false;
        this._tag = 0;
        this._cells = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            Cell c = new Cell();
            _cells.add(i, c);
        }
    }

    public boolean getValidate() {
        return _validate;
    }

    public void setValidate(boolean _validate) {
        this._validate = _validate;
    }
 
    public int getTag() {
        return _tag;
    }

    public void setTag(int _tag) {
        this._tag = _tag;
    }
    
    public ArrayList<Cell> getCells() {
        return _cells;
    }
   
    public void setCells(ArrayList<Cell> _cells) {
        this._cells = _cells;
    }
    
    public Cell getCell (int n) {
        return _cells.get(n);
    }
    
    public void setCell (Cell newCell, int index) {
        _cells.set(index, newCell);
    }
    
}
