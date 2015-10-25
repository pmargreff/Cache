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
    public int _written;
    
    public ProcessorStats() {
        this._hit = 0;
        this._miss = 0;
        this._capacity = 0;
        this._access = 0;
        this._compulsory = 0;
        this._conflict = 0;
        this._written = 0;
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
    
    public void  addWritten(){
        this._written++;
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


    public int getCompulsory() {
        return _compulsory;
    }

    public int getConflict() {
        return _conflict;
    }
    

    public int getAccess() {
        return _access;
    }

    public int getWritten() {
        return _written;
    }
    
    private float getMissRatio(){
        float ratio;
        int access;
        access = _access - _written;
        ratio = (float) _miss / access;
        ratio *= 100;
        return ratio;
    }
    
    private float getHitRatio(){
        float ratio;
        int access;
        access = _access - _written;
        ratio = (float) _hit / access;
        ratio *= 100;
        return ratio;
    }
    
    public void print(){
        System.out.println("Access number: " + getAccess());
        System.out.println("    Hits: " + getHit());
        System.out.println("    Written: " + getWritten());
        System.out.println("    Misses: " + getMiss());
        System.out.println("        Compulsory: " + getCompulsory());
        System.out.println("        Conflict: " + getConflict());
        System.out.println();
        System.out.printf("Hit Ratio: %.2f" , getHitRatio());
        System.out.print(" %");
        System.out.println();
        System.out.printf("Miss Ratio: %.2f", getMissRatio());
        System.out.print(" %");
        System.out.println();
    }

   
}
