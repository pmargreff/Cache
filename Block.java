
import java.util.ArrayList;



public class Block {
    
	private boolean _validate;
        private boolean _dirty;
	private int _tag;
        private int _size;
        private ArrayList<Cell> _data;
        
	public Block(){
            this._validate = false;
            this._tag = 0;
            this._dirty = false;
            this._size = 32;
	}
        
        public Block(int bSize){
            this._validate = false;
            this._tag = 0;
            this._dirty = false;
            this._data = new ArrayList<>(bSize);
	}

	boolean getValidate(){
		return _validate;
	}

	void setValidate(boolean newBool){
		this._validate = newBool;
	}

	int getTag(){
		return _tag;
	}

	void setTag(int newTag){
		this._tag = newTag;
	}
        
        public int getSize(){
            return this._data.size();
        }
}
