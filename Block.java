import java.util.ArrayList;
/**
 *
 * @author pmargreff
 */
public class Block {
    public boolean _validate;
    public int _tag;
    public ArrayList<Cell> _info;
    
    public Block(int size) {
        this._validate = false;
        this._tag = 0;
        this._info = new ArrayList<>(size);
    }

    public boolean isValidate() {
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
    
    public ArrayList<Cell> getInfo() {
        return _info;
    }
   
    public void setInfo(ArrayList<Cell> _info) {
        this._info = _info;
    }
    
}
