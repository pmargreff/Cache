

import java.util.ArrayList;


public class Cache {

	private ArrayList<Block> _blocks;
	private int _cellsPerBlock;
	private int _sizeBlock;

	public Cache(int height){
		this._cellsPerBlock = 1;    
		this._sizeBlock = height;
		this._blocks = new ArrayList<>(height);
                
                for(int i = 0; i < height;i++){
                    Block temp = new Block(_cellsPerBlock);
                    _blocks.add(i, temp);
                }
	}

	public Cache(int height, int width) {
                this._cellsPerBlock = width;    
		this._sizeBlock = height;
		this._blocks = new ArrayList<>(height);
                
                for(int i = 0; i < height;i++){
                    Block temp = new Block(_cellsPerBlock);
                    _blocks.add(i, temp);
                }
	}

    public Block getBlock(int index) {
        return _blocks.get(index);
    }
    
    public int getSize(){
        return this._blocks.size();
    }
}
