

public class Cell {
	private boolean _validate;
	private int _tag;

	Cell(){
            _validate = false;
            _tag = 0;
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
}
