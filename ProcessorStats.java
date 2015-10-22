/**
 *
 * @author pmargreff
 */
public class ProcessorStats {
    private int _miss;
    private int _hit;
    private int _capacity;
    private int _compulsory;
    private int _conflict;
    private int _access;
    
    public ProcessorStats() {
        this._hit = 0;
        this._miss = 0;
    }

    public void addConflictMiss(){
        this._conflict++;
        addMiss();
    }
    
    public void addCompulsoryMiss(){
        this._compulsory++;
        addMiss();
    }
    
    public void addCapacityMiss(){
        this._capacity++;
        addMiss();
    }
    
    private void addMiss(){
        this._miss++;
        this._access++;
    }

    public void  addHit(){
        this._hit++;
        this._access++;
    }

    public int getMiss() {
        return _miss;
    }


    public int getHit() {
        return _hit;
    }

    public int getCapacity() {
        return _capacity;
    }

    public void setCapacity(int _capacity) {
        this._capacity = _capacity;
    }

    public int getCompulsory() {
        return _compulsory;
    }

    public void setCompulsory(int _compulsory) {
        this._compulsory = _compulsory;
    }

    public int getConflict() {
        return _conflict;
    }

    public void setConflict(int _conflict) {
        this._conflict = _conflict;
    }
}
