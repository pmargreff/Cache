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
	}

	public Cache(int height, int width) {
		this._cellsPerBlock = width;
		this._sizeBlock = height;
		this._cells = new ArrayList<>(height*width);
	}

    public Cell getCell(int index) {
        return _cells.get(index);
    }

    public void setCells(Cell cell, int index) {       
        this._cells.set(index, cell);
    }
}
