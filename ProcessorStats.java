



/**
 *
 * @author pmargreff
 */
public class ProcessorStats {
    private int _miss;
    private int _hit;

    public ProcessorStats() {
        this._hit = 0;
        this._miss = 0;
    }
    
    public void addMiss(){
        this._miss++;
    }
    
    public void  addHit(){
        this._hit++;
    }

    public int getMiss() {
        return _miss;
    }

    
    public int getHit() {
        return _hit;
    }  
}
