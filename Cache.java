
import java.util.ArrayList;

public class Cache {

    private ArrayList<Block> _blocks;
    private int _cellsPerBlock;
    private int _sizeBlock;
    private int _associative;
    public ProcessorStats _stats;

    public Cache(int height, int assoc) {
        this._cellsPerBlock = 1;
        this._sizeBlock = height;
        this._blocks = new ArrayList<>(height);
        this._stats = new ProcessorStats();
        this._associative = assoc;

        for (int i = 0; i < height; i++) {
            Block b = new Block(i);
            _blocks.add(i, b);
        }
    }

    public Cache(int height, int width, int assoc) {
        this._cellsPerBlock = width;
        this._sizeBlock = height;
        this._blocks = new ArrayList<>(height * width);
        this._stats = new ProcessorStats();
        this._associative = assoc;

        for (int i = 0; i < height * width; i++) {
            Block b = new Block(i);
            _blocks.add(i, b);
        }
    }

    public Block getBlock(int index) {
        return _blocks.get(index);
    }

    public int getSize() {
        return this._blocks.size();
    }

    public void addressSearch(int address) {
        if (_associative == 1) {
            directMapped(address);
        } else if (_associative == 2) {

        } else if (_associative == 3) {

        } else {
            System.out.println("Mapping ERROR!!!");
        }
    }

    //só vou mijar
    public void directMapped(int address) {
        int tag; //Tag on that will be on cache memory
        int rest;
        int blockLine; //block line that will be changed
        Block tempBlock;

        rest = (int) address % _cellsPerBlock; //absolute address modulo size blocks 
        tag = blockLine = (int) (address / _cellsPerBlock);
        blockLine %= _sizeBlock; //
        tempBlock = _blocks.get(blockLine); //

        if ((tempBlock.getValidate()) && (tempBlock.getTag() == tag)) { //if the hit case only add hit counter
            _stats.addHit();
        } else {
            _blocks.get(blockLine).setTag(tag);
            _blocks.get(blockLine).setValidate(true);
            _stats.addMiss();
        }
    }

    public void setAssociative() {

    }

    public void FullyAssociative() {

    }
}
