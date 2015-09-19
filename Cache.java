
public class Cache {

	Cel _arrayCel[][] ;
	private int _cellsPerBlock;
	private int _sizeBlock;

	public Cache(int height) {
		this._cellsPerBlock = 1;
		this._sizeBlock = height;
		this._arrayCel = new Cel[this._cellsPerBlock][this._sizeBlock];
	}

	public Cache(int height, int width) {
		this._cellsPerBlock = width;
		this._sizeBlock = height;
		this._arrayCel = new Cel[this._cellsPerBlock][this._sizeBlock];
	}
}
