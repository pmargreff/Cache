
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
        this._sets = new ArrayList<>();
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

    public void addressSearch(int address, int assoc, int nSets) {
        if (assoc >= 1 && nSets >= 1) {
            if (assoc == 1) {

//                TODO: Change it to be doing on interface
//                this._bSize = 4; //if is direct mapped force the byte size to be 4
//                this._nSets = 1024; //if is direct mapped force the sets number 
                directMapped(address);
            } else if (nSets == 1) {
                fullyAssociative(address);
            } else if (nSets > 1 && assoc > 1) {
                setAssociative(address);
            }
        } else {
//            error message
        }
    }

    public void directMapped(int address) {
        int tag; //Tag on that will be on cache memory
        int blockIndex; //block line that will be changed
        Block tempBlock = new Block(this._bSize); //new block to replacement

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
        /**
         * Only do a div address to bSize, if was need get and set memory data
         * is only necessary do a loop and set or get cells in a Block _bSize
         * times
         */
        address /= _bSize;

        int associativeIndex = address % _associative;
        int tag = (int) address / _associative;
        boolean hasFound = false;
        boolean hasOccupied = true;

        for (int i = 0; i < _nSets; i++) {
            if ((_sets.get(associativeIndex).getBlocks().get(i).getValidate()) && (_sets.get(associativeIndex).getBlocks().get(i).getTag() == tag)) {
                _stats.addHit();
                hasFound = true;
                break;
            }
        }

        if (!hasFound) {
            _stats.addMiss();
            for (int i = 0; i < _nSets; i++) {
                if (!_sets.get(associativeIndex).getBlock(i).getValidate()) {
                    _sets.get(associativeIndex).getBlock(i).setValidate(true);
                    _sets.get(associativeIndex).getBlock(i).setTag(tag);
                    hasOccupied = true;
                    break; //if find value get out a loop           
                }
                hasOccupied = false;
            }
        }

        if (!hasOccupied) {

            Random random = new Random();
            int index = random.nextInt(_nSets);
            _sets.get(associativeIndex).getBlock(index).setValidate(true);
            _sets.get(associativeIndex).getBlock(index).setTag(tag);

        }

    }

    public void fullyAssociative(int address) {
        /**
         * Only do a div address to bSize, if was need get and set memory data
         * is only necessary do a loop and set or get cells in a Block _bSize
         * times
         */
        address /= _bSize;
        int tag;
        boolean hasFound = false;
        boolean hasOccupied = true;

//          search for adress in cache
        for (int i = 0; i < _associative; i++) {
            if ((_sets.get(i).getBlock(0).getValidate()) && (_sets.get(i).getBlock(0).getTag() == address)) {
                _stats.addHit();
                hasFound = true;
                break; //if find value get out a loop
            }
        }
        
        //if don't find adress count hit and replace, first search by a empty 
        //place, after it set a block in a random index
        if (!hasFound) {
            _stats.addMiss();
            for (int i = 0; i < _associative; i++) {
                if ((!_sets.get(i).getBlock(0).getValidate())) {
                    _sets.get(i).getBlock(0).setValidate(true);
                    _sets.get(i).getBlock(0).setTag(address);
                    hasOccupied = true;
                    break; //if find value get out a loop
                }
                hasOccupied = false; 
            }
        }
        if (!hasOccupied) {
            Random random = new Random();
            int index = random.nextInt(_associative);
            _sets.get(index).getBlock(0).setValidate(true);
            _sets.get(index).getBlock(0).setTag(address);
        }
    }
}
