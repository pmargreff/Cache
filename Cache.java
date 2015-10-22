
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
     * @return 0 - Haven't the block 1 - Have and isn't dirty 2 - Have and is
     * dirty
     */
    public int access(int address, int accessType) {
        if (this._associative >= 1 && this._nSets >= 1) {
            if (this._associative == 1) {

//                TODO: Change it to be doing on interface
//                this._bSize = 4; //if is direct mapped force the byte size to be 4
//                this._nSets = 1024; //if is direct mapped force the sets number 
                return directMapped(address, accessType);
            } else if (this._nSets == 1) {
                return fullyAssociative(address, accessType);
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

                if (getSet(0).getBlock(blockIndex).isDirty()) { //if the block is dirty
                    int newAddress = (_sets.get(0).getBlock(blockIndex).getTag() * this._bSize * this._nSets) + blockIndex; //recontruct the address
                    _nextLevel.access(newAddress, 1);
                }

                if (returnNextLevel == 0) { // if haven't data

                    if (_sets.get(0).getBlock(blockIndex).isValidate()) {
                        _stats.addConflictMiss();
                    } else {
                        _stats.addCompulsoryMiss();
                    }

                    getSet(0).getBlock(blockIndex).setTag(tag);
                    getSet(0).getBlock(blockIndex).setValidate(true);
                    getSet(0).getBlock(blockIndex).setDirty(false);
                    return 0;
                } else if (returnNextLevel == 1) { //if have and isn't dirty 

                    if (_sets.get(0).getBlock(blockIndex).isValidate()) {
                        _stats.addConflictMiss();
                    } else {
                        _stats.addCompulsoryMiss();
                    }

                    getSet(0).getBlock(blockIndex).setTag(tag);
                    getSet(0).getBlock(blockIndex).setValidate(true);
                    getSet(0).getBlock(blockIndex).setDirty(false);
                    return 0;
                } else if (returnNextLevel == 2) { //if have and is dirty

                    if (_sets.get(0).getBlock(blockIndex).isValidate()) {
                        _stats.addConflictMiss();
                    } else {
                        _stats.addCompulsoryMiss();
                    }

                    getSet(0).getBlock(blockIndex).setTag(tag);
                    getSet(0).getBlock(blockIndex).setValidate(true);
                    getSet(0).getBlock(blockIndex).setDirty(true);
                    return 0;
                }
            } else { //if is in the last level or don't find

                if (_sets.get(0).getBlock(blockIndex).isValidate()) {
                    _stats.addConflictMiss();
                } else {
                    _stats.addCompulsoryMiss();
                }

                _sets.get(0).getBlock(blockIndex).setTag(tag);
                _sets.get(0).getBlock(blockIndex).setValidate(true);
                _sets.get(0).getBlock(blockIndex).setDirty(false);
                return 0;
            }
        } else { //write access
            
            _stats.addWritten();
            
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
        int tag; //Tag on that will be on cache memory
        int blockIndex; //block line that will be changed
        int setIndex; //block collumn that will be changed
        Block tempBlock; //new block to replacement

        blockIndex = address / this._bSize; //
        blockIndex %= this._nSets;

        tag = address / this._bSize;
        tag /= this._nSets;

//        tempBlock = _sets.get(setInddex).getBlock(blockIndex);
    }

    private int fullyAssociative(int address, int accessType) {
        /**
         * Only do a div address to bSize, if was need get and set memory data
         * is only necessary do a loop and set or get cells in a Block _bSize
         * times
         */
        int tag = address / getbSize();

        int returnNextLevel = 0;
        if (accessType == 0) { //read cache

            for (int i = 0; i < getAssociative(); i++) {
                if ((_sets.get(i).getBlock(0).isValidate()) && (_sets.get(i).getBlock(0).getTag() == tag)) { //if have
                    _stats.addHit();
                    if (!_sets.get(i).getBlock(0).isDirty()) { //if isn't dirty
                        return 1;
                    } else { //if is
                        return 2;
                    }
                }
            }

            if (_nextLevel != null) { //if have next cache 

                Random random = new Random();
                int index = random.nextInt(getAssociative());

                returnNextLevel = _nextLevel.access(address, 0);

                if (returnNextLevel == 0) { //if haven't find in next level

                    for (int i = 0; i < getAssociative(); i++) {
                        if ((!_sets.get(i).getBlock(0).isValidate())) {
                            _stats.addCompulsoryMiss();
                            _sets.get(i).getBlock(0).setValidate(true);
                            _sets.get(i).getBlock(0).setDirty(false);
                            _sets.get(i).getBlock(0).setTag(tag);
                            return 0;
                        }
                    }

                    if (_sets.get(index).getBlock(0).isDirty()) { //if is dirty
                        _nextLevel.access(address, 1);
                        _sets.get(index).getBlock(0).setDirty(true);
                    } else { //if isn't
                        _sets.get(index).getBlock(0).setDirty(false);
                    }

                    if (_sets.get(index).getBlock(0).isValidate()) {
                        _stats.addConflictMiss();
                    } else {
                        _stats.addCompulsoryMiss();
                    }

                    _sets.get(index).getBlock(0).setValidate(true);
                    _sets.get(index).getBlock(0).setTag(tag);
                    return 0;

                } else if (returnNextLevel == 1) { //if find in next level and isn't dirty

                    if (_sets.get(index).getBlock(0).isValidate()) {
                        _stats.addConflictMiss();
                    } else {
                        _stats.addCompulsoryMiss();
                    }
                    
                    _sets.get(index).getBlock(0).setValidate(true);
                    _sets.get(index).getBlock(0).setDirty(false);
                    _sets.get(index).getBlock(0).setTag(tag);
                    return 0;
                } else if (returnNextLevel == 2) { //if find in next level and is    

                    if (_sets.get(index).getBlock(0).isValidate()) {
                        _stats.addConflictMiss();
                    } else {
                        _stats.addCompulsoryMiss();
                    }

                    _sets.get(index).getBlock(0).setValidate(true);
                    _sets.get(index).getBlock(0).setDirty(true);
                    _sets.get(index).getBlock(0).setTag(tag);
                    return 0;
                }
            } else { //if haven't next level
                Random random = new Random();
                int index = random.nextInt(getAssociative());

                if (_sets.get(index).getBlock(0).isValidate()) {
                    _stats.addConflictMiss();
                } else {
                    _stats.addCompulsoryMiss();
                }

                _sets.get(index).getBlock(0).setDirty(false);
                _sets.get(index).getBlock(0).setValidate(true);
                _sets.get(index).getBlock(0).setTag(tag);
                return 0;
            }

        } else { //writting
            _stats.addWritten();
            for (int i = 0; i < getAssociative(); i++) { //if is in cache only tag dirrty bit
                if ((_sets.get(i).getBlock(0).isValidate()) && (_sets.get(i).getBlock(0).getTag() == tag)) {
                    _sets.get(i).getBlock(0).setDirty(true);
                    return 0;
                }
            }
            //if haven't in cache
            for (int i = 0; i < getAssociative(); i++) { //search empty set
                if ((!_sets.get(i).getBlock(0).isValidate())) {
                    _sets.get(i).getBlock(0).setValidate(true);
                    _sets.get(i).getBlock(0).setDirty(true);
                    _sets.get(i).getBlock(0).setTag(tag);
                    return 0;
                }
            }

            //set on random assoc
            Random random = new Random();
            int index = random.nextInt(getAssociative());
            if (_sets.get(index).getBlock(0).isDirty() && _nextLevel != null) {
                _nextLevel.access(address, 1);
            }
            _sets.get(index).getBlock(0).setValidate(true);
            _sets.get(index).getBlock(0).setTag(tag);
            _sets.get(index).getBlock(0).setDirty(true);

            return 0;
        }
        return -1;
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
