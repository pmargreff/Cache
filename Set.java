import java.util.ArrayList;
/**
 *
 * @author pmargreff
 */
public class Set {
    public boolean _validate;
    public int _tag;
    public ArrayList<Block> _blocks;
    
    public Set(int size, int bSize) {
        this._validate = false;
        this._tag = 0;
        this._blocks = new ArrayList<>(size);
        
        for (int i = 0; i < size; i++) {
            Block b = new Block(bSize);
            _blocks.add(i, b);
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
    
    public ArrayList<Block> getBlocks() {
        return _blocks;
    }
   
    public void setBlocks(ArrayList<Block> _cells) {
        this._blocks = _cells;
    }
    
    public Block getBlock (int n) {
        return _blocks.get(n);
    }
    
    public void setBlock (Block newCell, int index) {
        _blocks.set(index, newCell);
    }
    
}
