
import java.util.ArrayList;
import java.util.Random;

public class Cache {

    private ArrayList<Block> _blocks;
    private ArrayList<Cell> _cells;
    private int _cellsPerBlock;
    private int _sizeBlock;
    private int _associative;
    public ProcessorStats _stats;

    public Cache(int width, int assoc) {
        this._cellsPerBlock = width;
        this._sizeBlock = 1;
        this._blocks = new ArrayList<>(width);
        this._stats = new ProcessorStats();
        this._associative = assoc;

        for (int i = 0; i < _sizeBlock; i++) {
            Block b = new Block(i);
            _blocks.add(i, b);
        }
    }

    public Cache(int height, int width, int assoc) {
        this._cellsPerBlock = width;
        this._sizeBlock = height;
        this._blocks = new ArrayList<>(_sizeBlock);
        this._stats = new ProcessorStats();
        this._associative = assoc;

        for (int i = 0; i < _sizeBlock; i++) {
            Block b = new Block(_cellsPerBlock);
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
            fullyAssociative(address);
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

    public void fullyAssociative(int address) {
        int i;
        i = 0;
        boolean find = false;
        while (!find && i < _cellsPerBlock) { //check if address are in Cache
  
            if (_blocks.get(0).getCells().get(i).getValidate() == true && _blocks.get(0).getCells().get(i).getTag() == address) {
                find = true;
                _stats.addHit();
            }
            i++;
        }
        if (i >= _cellsPerBlock) { //if don't find the address in cache, search empty cell
            i=0;
            while (i < _cellsPerBlock && _blocks.get(0).getCells().get(i).getValidate()) { 
                i++;
            }
            if(i<_cellsPerBlock){  //if find a empty cell, set in this cell 
                _blocks.get(0).getCells().get(i).setTag(address);
                _blocks.get(0).getCells().get(i).setValidate(true);
                _stats.addMiss();
            }
            else { // if don't find a empty cell, select a random Cell to set
                Random place = new Random();
                int index = place.nextInt(_cellsPerBlock);
                _blocks.get(0).getCells().get(index).setTag(address);
                _blocks.get(0).getCells().get(index).setValidate(true);
                _stats.addMiss();
            }
        }
    }
}
