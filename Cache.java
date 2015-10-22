
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

    /**
     *
     * @param address
     * @param accessType
     * @return 0 - Haven't , 1 - Have and isn't dirty, 2 - Have and is dirty
     */
    public int access(int address, int accessType) {
        if (this._associative >= 1 && this._nSets >= 1) {
            if (this._associative == 1) {

//                TODO: Change it to be doing on interface
//                this._bSize = 4; //if is direct mapped force the byte size to be 4
//                this._nSets = 1024; //if is direct mapped force the sets number 
                return directMapped(address, accessType);
            } else if (this._nSets == 1) {
//                fullyAssociative(address, accessType);
            } else if (this._nSets > 1 && this._associative > 1) {
//                setAssociative(address, accessType);
            }
        } else {
//            error message
        }
        return 0;
    }

    private int directMapped(int address, int accessType) {
        int tag; //Tag on that will be on cache memory
        int blockIndex; //block line that will be changed
        Block tempBlock; //new block to replacement

        blockIndex = address / this._bSize; //
        blockIndex %= this._nSets;

        tag = address / this._bSize;
        tag /= this._nSets;

        tempBlock = _sets.get(0).getBlock(blockIndex);

        if (accessType == 0) { //Read access
            if ((tempBlock.isValidate()) && (tempBlock.getTag() == tag)) { //case find and isn't dirty
                _stats.addHit();
                return 1;
            } else if ((tempBlock.isValidate()) && (tempBlock.getTag() == tag) && (tempBlock.isDirty())) { //case find and is dirty
                _stats.addHit();
                return 2;
            } else if (_nextLevel != null) { //case find in next level
                int returnNextLevel = _nextLevel.access(address, 0);

                if (getSet(0).getBlock(blockIndex).isDirty()) {
                    int newAddress = (_sets.get(0).getBlock(blockIndex).getTag() * this._bSize * this._nSets) + blockIndex; //recontruct the address
                    _nextLevel.access(newAddress, 1);
                }

                if (returnNextLevel == 0) { // if haven't data
                    _stats.addMiss();
                    getSet(0).getBlock(blockIndex).setTag(tag);
                    getSet(0).getBlock(blockIndex).setValidate(true);
                    getSet(0).getBlock(blockIndex).setDirty(false);
                    return 0;
                } else if (returnNextLevel == 1) { //if have and isn't dirty 
                    _stats.addMiss();
                    getSet(0).getBlock(blockIndex).setTag(tag);
                    getSet(0).getBlock(blockIndex).setValidate(true);
                    getSet(0).getBlock(blockIndex).setDirty(false);
                    return 0;
                } else if (returnNextLevel == 2) { //if have and is dirty
                    _stats.addMiss();
                    getSet(0).getBlock(blockIndex).setTag(tag);
                    getSet(0).getBlock(blockIndex).setValidate(true);
                    getSet(0).getBlock(blockIndex).setDirty(true);
                    return 0;
                }
            } else { //if is in the last level or don't find
                _stats.addMiss();
                _sets.get(0).getBlock(blockIndex).setTag(tag);
                _sets.get(0).getBlock(blockIndex).setValidate(true);
                _sets.get(0).getBlock(blockIndex).setDirty(false);
                return 0;
            }
        } else { //write access
            _stats.addMiss(); //SEE IF WRITTING DOING MISS
            if (_sets.get(0).getBlock(blockIndex).isDirty() && _nextLevel != null) {
                int newAddress = (_sets.get(0).getBlock(blockIndex).getTag() * this._bSize * this._nSets) + blockIndex; //recontruct the address
                _nextLevel.access(newAddress, 1);
            }
            
            _sets.get(0).getBlock(blockIndex).setTag(tag);
            _sets.get(0).getBlock(blockIndex).setValidate(true);
            _sets.get(0).getBlock(blockIndex).setDirty(true);

        }

        return 0;
    }

    public void setAssociative(int address, int accessType) {
        /**
         * Only do a div address to bSize, if was need get and set memory data
         * is only necessary do a loop and set or get cells in a Block _bSize
         * times
         */
        //System.out.println("CONJUNTO ASSOCIATIVO ");
        boolean find = false;
        int index;
        int tag;
        tag = (address / _bSize) / _nSets;
        int set;
        if (accessType == 0) {
            set = (address / _bSize) % _nSets;
            for (index = 0; index < _associative && !find; index++) {
                if (_sets.get(set).getBlocks().get(index).isValidate() && _sets.get(set).getBlocks().get(index).getTag() == tag) {
                    find = true;
                    _stats.addHit();
                }
            }
            if (!find) {
                if (_nextLevel != null) {
                    _nextLevel.access(address, accessType);
                }
                for (index = 0; index < _associative && !find; index++) {
                    if (!_sets.get(set).getBlocks().get(index).isValidate()) {
                        find = true;
                    }
                }
                if (find) {
                    index--; // a don't know why need this. But work!
                    _sets.get(set).getBlocks().get(index).setValidate(true);
                    _sets.get(set).getBlocks().get(index).setTag(tag);
                    _sets.get(set).getBlocks().get(index).setDirty(false);
                    _stats.addMiss();
                } else {
                    Random random = new Random();
                    index = random.nextInt(_associative);
                    if (_sets.get(set).getBlocks().get(index).isDirty() && _nextLevel != null) {
                        _nextLevel.access(address, 0);
                    }
                    _sets.get(set).getBlocks().get(index).setValidate(true);
                    _sets.get(set).getBlocks().get(index).setTag(tag);
                    _sets.get(set).getBlocks().get(index).setDirty(false);
                    _stats.addMiss();

                }
            }
        } else {
            set = (address / _bSize) % _nSets;
            for (index = 0; index < _associative && !find; index++) {
                if (_sets.get(set).getBlocks().get(index).isValidate() && _sets.get(set).getBlocks().get(index).getTag() == tag) {
                    find = true;
                    _sets.get(set).getBlocks().get(index).setDirty(true);
                    _stats.addHit();
                }
            }
            if (!find) {
                for (index = 0; !find && index < _associative; index++) {
                    if (!_sets.get(set).getBlocks().get(index).isValidate()) {
                        find = true;
                    }
                }
                if (find) {
                    index--; // a don't know why need this. But work!
                    _sets.get(set).getBlocks().get(index).setValidate(true);
                    _sets.get(set).getBlocks().get(index).setTag(tag);
                    _sets.get(set).getBlocks().get(index).setDirty(true);
                    _stats.addMiss();
                } else {
                    Random random = new Random();
                    index = random.nextInt(_associative);
                    if (_sets.get(set).getBlocks().get(index).isDirty() && _nextLevel != null) {
                        _nextLevel.access(address, 0);
                    }
                    _sets.get(set).getBlocks().get(index).setValidate(true);
                    _sets.get(set).getBlocks().get(index).setTag(tag);
                    _sets.get(set).getBlocks().get(index).setDirty(true);
                    _stats.addMiss();

                }
            }

        }
    }

    /*
     int addressTemp;
     addressTemp = address / _bSize;

     int associativeIndex = addressTemp % _associative;
     int tag = (int) addressTemp / _associative;
     boolean hasFound = false;
     boolean hasOccupied = true;

     for (int i = 0; i < getnSets(); i++) {  //WTF?? Why this for???
     if ((_sets.get(associativeIndex).getBlocks().get(i).isValidate()) && (_sets.get(associativeIndex).getBlocks().get(i).getTag() == tag)) {
     _stats.addHit();
     hasFound = true;
               
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
     */
    // 0 is read and 1 is write
    public void fullyAssociative(int address, int accessType) {
        /**
         * Only do a div address to bSize, if was need get and set memory data
         * is only necessary do a loop and set or get cells in a Block _bSize
         * times
         */
        //address /= getbSize();
        // Don't lost original address, using tag to work
        int tag = address / getbSize();
        boolean hasFound = false;
        boolean hasOccupied = true;

        if (accessType == 0) {
            //          search for address in cache
            for (int i = 0; i < getAssociative(); i++) {
                if ((_sets.get(0).getBlock(i).isValidate()) && (_sets.get(0).getBlock(i).getTag() == tag)) {
                    _stats.addHit();
                    hasFound = true;
                    break; //if find value get out a loop
                }
            }

            //if don't find adress count hit and replace, first search by a empty 
            //place, after it set a block in a random index
            if (!hasFound) {
                _stats.addMiss();
                if (_nextLevel != null) {
                    _nextLevel.access(address, 0);
                }
                for (int i = 0; i < getAssociative(); i++) {
                    if ((!_sets.get(0).getBlock(i).isValidate())) {
                        _sets.get(0).getBlock(i).setValidate(true);
                        _sets.get(0).getBlock(i).setTag(tag);
                        hasOccupied = true;
                        break; //if find value get out a loop
                    }
                    hasOccupied = false;
                }
            }
            if (!hasOccupied) {
                Random random = new Random();
                int index = random.nextInt(getAssociative());
                if (_sets.get(0).getBlock(index).isDirty() && _nextLevel != null) {
                    _nextLevel.access(address, 1);
                }
                _sets.get(0).getBlock(index).setValidate(true);
                _sets.get(0).getBlock(index).setTag(tag);
            }
        } else {
            for (int i = 0; i < getAssociative(); i++) {
                if ((_sets.get(0).getBlock(i).isValidate()) && (_sets.get(0).getBlock(i).getTag() == tag)) {
                    //_stats.addHit();
                    _sets.get(0).getBlock(i).setDirty(true);
                    hasFound = true;
                    break; //if find value get out a loop
                }
            }
            if (!hasFound) {

                for (int i = 0; i < getAssociative(); i++) {
                    if ((!_sets.get(0).getBlock(i).isValidate())) {
                        _sets.get(0).getBlock(i).setValidate(true);
                        _sets.get(0).getBlock(i).setTag(tag);
                        _sets.get(0).getBlock(i).setDirty(true);
                        hasOccupied = true;
                        break; //if find value get out a loop
                    }
                    hasOccupied = false;
                }
            }
            if (!hasOccupied) {
                Random random = new Random();
                int index = random.nextInt(getAssociative());
                if (_sets.get(0).getBlock(index).isDirty() && _nextLevel != null) {
                    _nextLevel.access(address, 1);
                }
                _sets.get(0).getBlock(index).setValidate(true);
                _sets.get(0).getBlock(index).setTag(tag);
                _sets.get(0).getBlock(index).setDirty(true);
            }
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
