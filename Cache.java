
import java.util.ArrayList;
import java.util.Random;

public class Cache {

    private ArrayList<Set> _sets;

    private int _associative;
    private int _nSets;
    private int _bSize;

    Cache _nextLevel;

    public ProcessorStats _stats;

    public Cache(int nSets, int assoc, int bSize) {
        this._associative = assoc;
        this._nSets = nSets;
        this._sets = new ArrayList<>();
        this._bSize = bSize;
        this._stats = new ProcessorStats();
        _nextLevel = null;

        for (int i = 0; i < this._associative; i++) {
            Set s = new Set(this._nSets, this._bSize);
            _sets.add(i, s);
        }
    }

    public Cache(int nSets, int assoc, int bSize, Cache nextLevel) {
        this._associative = assoc;
        this._nSets = nSets;
        this._sets = new ArrayList<>();
        this._bSize = bSize;
        this._stats = new ProcessorStats();
        this._nextLevel = nextLevel;

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

    public int access(int address, int accessType) {
        if (this._associative >= 1 && this._nSets >= 1) {
            if (this._associative == 1) {

//                TODO: Change it to be doing on interface
//                this._bSize = 4; //if is direct mapped force the byte size to be 4
//                this._nSets = 1024; //if is direct mapped force the sets number 
                return directMapped(address, accessType);
            } else if (this._nSets == 1) {
                fullyAssociative(address, accessType);
            } else if (this._nSets > 1 && this._associative > 1) {
                setAssociative(address, accessType);
            }
        } else {
//            error message
        }
        return 0;
    }

    public int directMapped(int address, int accessType) {
        
        int tag; //Tag on that will be on cache memory
        int blockIndex; //block line that will be changed
        Block tempBlock; //new block to replacement

        blockIndex = address / this._bSize; //
        blockIndex %= this._nSets;

        tag = address / this._bSize;
        tag /= this._nSets;

        tempBlock = _sets.get(0).getBlock(blockIndex);

        if (accessType == 0) { //Read access
            if ((tempBlock.isValidate()) && (tempBlock.getTag() == tag)) { //if the hit case only add hit counter
                _stats.addHit();
            } else if ((_nextLevel != null) && (_nextLevel.access(address, 0) == address)) { //try get in the next level if exists
                _stats.addMiss(); //change
                
                //save the older data at _nextLevel if this is dirty 
                if (_sets.get(0).getBlock(blockIndex).isDirty() && _nextLevel != null) {
                int newAddress = (_sets.get(0).getBlock(blockIndex).getTag() * this._bSize * this._nSets) + blockIndex; //recontruct the address
                _nextLevel.access(newAddress, 1);
                }
                
                //int tagOld = _nextLevel.getSet(0).getBlock(blockIndex).getTag();
                boolean validateOld = _nextLevel.getSet(0).getBlock(blockIndex).isValidate();
                boolean dirtyOld = _nextLevel.getSet(0).getBlock(blockIndex).isDirty();

                getSet(0).getBlock(blockIndex).setTag(tag);
                getSet(0).getBlock(blockIndex).setValidate(validateOld);
                getSet(0).getBlock(blockIndex).setDirty(dirtyOld);
            } else {
                _stats.addMiss(); //change
                _sets.get(0).getBlock(blockIndex).setTag(tag);
                _sets.get(0).getBlock(blockIndex).setValidate(true);
                _sets.get(0).getBlock(blockIndex).setDirty(false);

            }
        } else { //write access
            _stats.addMiss(); //change
            if (_sets.get(0).getBlock(blockIndex).isDirty() && _nextLevel != null) {
                int newAddress = (_sets.get(0).getBlock(blockIndex).getTag() * this._bSize * this._nSets) + blockIndex; //recontruct the address
                _nextLevel.access(newAddress, 1);
            } else {
                _sets.get(0).getBlock(blockIndex).setTag(tag);
                _sets.get(0).getBlock(blockIndex).setValidate(true);
                _sets.get(0).getBlock(blockIndex).setDirty(true);
            }
        }
        return address;
    }

    public void setAssociative(int address, int accessType) {
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

        for (int i = 0; i < getnSets(); i++) {
            if ((_sets.get(associativeIndex).getBlocks().get(i).isValidate()) && (_sets.get(associativeIndex).getBlocks().get(i).getTag() == tag)) {
                _stats.addHit();
                hasFound = true;
                break;
            }
        }

        if (!hasFound) {
            _stats.addMiss();
            for (int i = 0; i < getnSets(); i++) {
                if (!_sets.get(associativeIndex).getBlock(i).isValidate()) {
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
            int index = random.nextInt(getnSets());
            _sets.get(associativeIndex).getBlock(index).setValidate(true);
            _sets.get(associativeIndex).getBlock(index).setTag(tag);

        }

    }

    public void fullyAssociative(int address, int accessType) {
        /**
         * Only do a div address to bSize, if was need get and set memory data
         * is only necessary do a loop and set or get cells in a Block _bSize
         * times
         */
        address /= getbSize();
        int tag;
        boolean hasFound = false;
        boolean hasOccupied = true;

//          search for adress in cache
        for (int i = 0; i < getAssociative(); i++) {
            if ((_sets.get(i).getBlock(0).isValidate()) && (_sets.get(i).getBlock(0).getTag() == address)) {
                _stats.addHit();
                hasFound = true;
                break; //if find value get out a loop
            }
        }

        //if don't find adress count hit and replace, first search by a empty 
        //place, after it set a block in a random index
        if (!hasFound) {
            _stats.addMiss();
            for (int i = 0; i < getAssociative(); i++) {
                if ((!_sets.get(i).getBlock(0).isValidate())) {
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
            int index = random.nextInt(getAssociative());
            _sets.get(index).getBlock(0).setValidate(true);
            _sets.get(index).getBlock(0).setTag(address);
        }
    }

    /**
     * @return the _associative
     */
    public int getAssociative() {
        return _associative;
    }

    /**
     * @return the _nSets
     */
    public int getnSets() {
        return _nSets;
    }

    /**
     * @return the _bSize
     */
    public int getbSize() {
        return _bSize;
    }
}
