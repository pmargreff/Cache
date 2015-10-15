
import java.util.ArrayList;
import java.util.Random;

public class Cache {

    private ArrayList<Set> _sets;
    private int _associative;
    private int _nSets;
    private int _bSize;
    public ProcessorStats _stats;

    public Cache(int nSets, int assoc, int bSize) {
        this._associative = assoc;
        this._nSets = nSets;
        this._sets = new ArrayList<>(_nSets);
        this._bSize = bSize;
        this._stats = new ProcessorStats();

        for (int i = 0; i < this._associative; i++) {
            Set s = new Set(this._nSets, this._bSize);
            _sets.add(i, s);
        }
    }

    public Set getSet(int index) {
        return _sets.get(index);
    }

    public int getSize() {
        return this._sets.size();
    }

    public void addressSearch(int address, int assoc,int nSets) {
        if (assoc >= 1 && nSets >= 1) {
            if (assoc == 1) {
                directMapped(address);
            } else if (nSets == 1) {
                fullyAssociative(address);
            } else {
                setAssociative(address);
            }
        } else {
//            error message
        }
    }

    //só vou mijar
    public void directMapped(int address) {
        int tag; //Tag on that will be on cache memory
        int blockIndex; //block line that will be changed
        Block tempBlock = new Block(this._bSize);
        int rest;

        blockIndex = address / _bSize; //
        blockIndex %= _nSets;
        
        tag = address / _bSize;
        tag /= _nSets;
//        tag = (int) (address / _nSets);
        tempBlock = _sets.get(0).getBlock(blockIndex); //

        if ((tempBlock.getValidate()) && (tempBlock.getTag() == tag)) { //if the hit case only add hit counter
            _stats.addHit();
        } else {
            _sets.get(0).getBlock(blockIndex).setTag(tag);
            _sets.get(0).getBlock(blockIndex).setValidate(true);
            _stats.addMiss();
  
        }
    }

    public void setAssociative(int address) {
        int set;
        int i;
        set = address % _nSets;
        //for(i=0;i<_cellsPerBlock && ( _sets.get(set).getBlocks().get(i).getTag()!=((int)address/_nSets) && _sets.get(set).getBlocks().get(i).getValidate()==true); i++);
        i = 0;
        while (i < _associative && (_sets.get(set).getBlocks().get(i).getTag() != ((int) address / _nSets))) {
            i++;
        }

        if (i >= _associative) {
            _stats.addMiss();
            //L2.addressSearch(address);
            for (i = 0; i < _associative && _sets.get(set).getBlocks().get(i).getValidate() == true; i++);
            if (i >= _associative) {
                Random place = new Random();
                int index = place.nextInt(_associative);
                _sets.get(set).getBlocks().get(index).setValidate(true);
                _sets.get(set).getBlocks().get(index).setTag((int) address / _nSets);
            } else {
                _sets.get(set).getBlocks().get(i).setValidate(true);
                _sets.get(set).getBlocks().get(i).setTag((int) address / _nSets);
            }
        } else {
            _stats.addHit();
        }
    }

    public void fullyAssociative(int address) {
        int i;
        i = 0;
        boolean find = false;
        while (!find && i < _associative) { //check if address are in Cache

            if (_sets.get(0).getBlocks().get(i).getValidate() == true && _sets.get(0).getBlocks().get(i).getTag() == address) {
                find = true;
                _stats.addHit();
            }
            i++;
        }
        if (i >= _associative) { //if don't find the address in cache, search empty cell
            i = 0;
            while (i < _associative && _sets.get(0).getBlocks().get(i).getValidate()) {
                i++;
            }
            if (i < _associative) {  //if find a empty cell, set in this cell
                _sets.get(0).getBlocks().get(i).setTag(address);
                _sets.get(0).getBlocks().get(i).setValidate(true);
                _stats.addMiss();
                //L2.addressSearch(address);
            } else { // if don't find a empty cell, select a random Block to set
                Random place = new Random();
                int index = place.nextInt(_associative);
                _sets.get(0).getBlocks().get(index).setTag(address);
                _sets.get(0).getBlocks().get(index).setValidate(true);
                _stats.addMiss();
                //L2.addressSearch(address);
            }
        }
    }
}
