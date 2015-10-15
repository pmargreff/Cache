import java.util.ArrayList;
/**
 *
 * @author pmargreff
 */
public class Set {
    public ArrayList<Block> _blocks;
    
    public Set(int size, int bSize) {
        this._blocks = new ArrayList<>(size);
        
        for (int i = 0; i < size; i++) {
            Block b = new Block(bSize);
            _blocks.add(i, b);
        }
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
