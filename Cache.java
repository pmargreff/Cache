
import java.util.ArrayList;
import java.util.Random;

public class Cache {

    private ArrayList<Block> _blocks;
    private ArrayList<Cell> _cells;
    private int _cellsPerBlock;
    private int _numberBlocks;
    private int _associative;
    public ProcessorStats _stats;
    private Cache L2;
    

    public Cache(int width, int assoc) {
        this._cellsPerBlock = width;
        this._numberBlocks = 1;
        this._blocks = new ArrayList<>(width);
        this._stats = new ProcessorStats();
        this._associative = assoc;

        for (int i = 0; i < _numberBlocks; i++) {
            Block b = new Block(i);
            _blocks.add(i, b);
        }
    }

    public Cache(int height, int width, int assoc) {
        this._cellsPerBlock = width;
        this._numberBlocks = height;
        this._blocks = new ArrayList<>(_numberBlocks);
        this._stats = new ProcessorStats();
        this._associative = assoc;

        for (int i = 0; i < _numberBlocks; i++) {
            Block b = new Block(_cellsPerBlock);
            _blocks.add(i, b);
        }
    }
    
    public void setL2 (Cache L2){
        this.L2 = L2;
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
            setAssociative(address);
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
        blockLine %= _numberBlocks; //
        tempBlock = _blocks.get(blockLine); //

        if ((tempBlock.getValidate()) && (tempBlock.getTag() == tag)) { //if the hit case only add hit counter
            _stats.addHit();
        } else {
            _blocks.get(blockLine).setTag(tag);
            _blocks.get(blockLine).setValidate(true);
            _stats.addMiss();
            //L2.addressSearch(address);
            
        }
    }

    public void setAssociative(int address) {
        int set;
        int i;
        set = address % _numberBlocks;
        //for(i=0;i<_cellsPerBlock && ( _blocks.get(set).getCells().get(i).getTag()!=((int)address/_numberBlocks) && _blocks.get(set).getCells().get(i).getValidate()==true); i++);
        i=0;
        while(i<_cellsPerBlock && ( _blocks.get(set).getCells().get(i).getTag()!=((int)address/_numberBlocks))) {
            i++;
        }
                
        if(i>=_cellsPerBlock) {
            _stats.addMiss();
            //L2.addressSearch(address);
            for(i=0;i<_cellsPerBlock && _blocks.get(set).getCells().get(i).getValidate()==true;i++);
            if(i>=_cellsPerBlock){
                Random place = new Random();
                int index = place.nextInt(_cellsPerBlock);
                _blocks.get(set).getCells().get(index).setValidate(true);
                _blocks.get(set).getCells().get(index).setTag((int)address/_numberBlocks);
            }
            else {
                _blocks.get(set).getCells().get(i).setValidate(true);
                _blocks.get(set).getCells().get(i).setTag((int)address/_numberBlocks);
            }
        }
        else {
            _stats.addHit();
            
        }
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
                //L2.addressSearch(address);
            }
            else { // if don't find a empty cell, select a random Cell to set
                Random place = new Random();
                int index = place.nextInt(_cellsPerBlock);
                _blocks.get(0).getCells().get(index).setTag(address);
                _blocks.get(0).getCells().get(index).setValidate(true);
                _stats.addMiss();
                //L2.addressSearch(address);
            }
        }
    }
}
