package Cache;

import java.util.ArrayList;


public class Cache {

	private ArrayList<Cell> _cells;
	private int _cellsPerBlock;
	private int _sizeBlock;

	public Cache(int height){
		this._cellsPerBlock = 1;    
		this._sizeBlock = height;
		this._cells = new ArrayList<>(height);
                
                for(int i = 0; i < height;i++){
                    Cell c = new Cell();
                    _cells.add(i, c);
                }
	}

	public Cache(int height, int width) {
		this._cellsPerBlock = width;
		this._sizeBlock = height;
		this._cells = new ArrayList<>(height*width);
                
                for(int i = 0; i < height * width;i++){
                    Cell c = new Cell();
                    _cells.add(i, c);
                }
	}

    public Cell getCell(int index) {
        return _cells.get(index);
    }
    
    public int getSize(){
        return this._cells.size();
    }
}
