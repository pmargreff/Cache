
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.lang.Integer;

/**
 *
 * @author pmargreff
 */
public class Processor {
    
    private ArrayList<Integer> _address; //Memory Adress
    private ArrayList<Integer> _requestType; //Define the action: 0 read memory, 1 writte on memory
    private String _path; //Path to memory file

    //Caches
    private Cache _cacheL1D; //Data cache - L1
    private Cache _cacheL1I; //Instructions cache - L1
    private Cache _cacheL2U; //Unified cache - L2

    //Caches sizes
    private int _blockSizeL1D; //Blocks size in L1D
    private int _setsNumberL1D; //Number of blocks in L1D
    private int _blockSizeL1I; //Blocks size in L1I
    private int _setsNumberL1I; //Number of blocks in L1I
    private int _splitAddress; //Numbers equal or less (<=) than _splitAdress will be save on data memory
    private int _blockSizeL2; //Blocks size in L2
    private int _setsNumberL2; //Number of blocks in L2

    //Stats of all caches
    public ProcessorStats _requestedInformationL1D; //miss or hits numbers    
    public ProcessorStats _requestedInformationL1I;
    public ProcessorStats _requestedInformationL2U;
    
    public Processor(int setsNumber, String path) {
        
        this._path = path; //get the path 
        this._address = new ArrayList<>(); //Init the Adress List
        this._requestType = new ArrayList<>(); //Init the comand list (Read or Write)
        this._setsNumberL1D = setsNumber;
        this._blockSizeL1D = 4;
        this._setsNumberL1I = setsNumber;
        this._blockSizeL1I = 4;
//        this._setsNumberL2 = setsNumber;
//        this._blockSizeL2 = 4;
        
        this._splitAddress = 512;
        
        this._cacheL1D = new Cache(this._setsNumberL1D, this._blockSizeL1D); //Init cache with split address plus 1
        this._cacheL1I = new Cache(this._setsNumberL1I, this._blockSizeL1I); //Init cache with the rest of size
        this._cacheL2U = new Cache(this._setsNumberL2, this._blockSizeL2);
        
        this._requestedInformationL1D = new ProcessorStats();
        this._requestedInformationL1I = new ProcessorStats();
        
        boolean textType = true; //change to read a binary file
        //If textType = 1 try open .txt file format, else try open binary file

        if (!textType) {
            this.ReadBinaryFile();
        } else {
            this.ReadTextFile();
        }
    }
    
    public static void main(String[] argv) {
        String path;
        path = "IO/arqTexto1_rw_10k.txt";
        int setsNumber = 128;
        
        Processor sample = new Processor(setsNumber, path);
        sample.DirectMapped();

        /*if ( argv.length == 2){
         lines = Integer.parseInt(argv[0]);
         path = argv[1];
            
         Processor sample = new Processor(lines, path);
         sample.DirectMapped();
         } else {
         System.out.println("ERROR! Argumento: Número_de_blocos<int> caminho_arquivo_entrada<String>");
         }
         */
        System.out.println("L1 Data - Hit: " + sample._requestedInformationL1D.getHit() + " Miss: " + sample._requestedInformationL1D.getMiss());
        System.out.println("L1 Instructions - Hit: " + sample._requestedInformationL1I.getHit() + " Miss: " + sample._requestedInformationL1I.getMiss());
    }

    /**
     * Get the file input in txt format
     */
    private void ReadTextFile() {
        
        String line = null;
        boolean arrayFlag = true; //If true, put number in adress array, else put on readOrWrite
        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = new FileReader(_path);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            
            while ((line = bufferedReader.readLine()) != null) {
                if (arrayFlag) {
                    _address.add(Integer.parseInt(line));
                    arrayFlag = false;
                } else {
                    _requestType.add(Integer.parseInt(line));
                    arrayFlag = true;
                }
            }
            //IMPORTANT: close files.
            bufferedReader.close();
            
        } catch (FileNotFoundException ex) {
            System.out.println("Unable to open file '" + _path + "'");
        } catch (IOException ex) {
            System.out.println("Error reading file '" + _path + "'");
        }
    }

    /**
     * Get the file input in binary format
     */
    private void ReadBinaryFile() {
        try {
            // Use this for reading the data.
            byte[] buffer = new byte[1000];
            
            FileInputStream inputStream
                    = new FileInputStream(_path);

            // read fills buffer with data and returns
            // the number of bytes read (which of course
            // may be less than the buffer size, but
            // it will never be more).
            int total = 0;
            int nRead = 0;
            while ((nRead = inputStream.read(buffer)) != -1) {
                // Convert to String so we can display it.
                // Of course you wouldn't want to do this with
                // a 'real' binary file.
                System.out.println(new String(buffer));
                total += nRead;
            }
            
            for (int i = 0; i < total; i++) {
                System.out.println(buffer[i]);
            }
            // Always close files.
            inputStream.close();
            
            System.out.println("Read " + total + " bytes");
        } catch (FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '"
                    + _path + "'");
        } catch (IOException ex) {
            System.out.println(
                    "Error reading file '"
                    + _path + "'");
        }
    }

    /**
     * Get the results for a cache with a Direct Mapped
     */
    public void DirectMapped() {
        ProcessorStats statsL1D = new ProcessorStats(); //init a result instance
        ProcessorStats statsL1I = new ProcessorStats();        
        ProcessorStats statsL2U = new ProcessorStats();        
        
        int tag; //Tag on that will be on cache memory
        int rest;        
        int blockLine; //block line that will be changed
        Cell tempCell;

        //run for all cache's cells
        for (int i = 0; i < _address.size(); i++) {
            
            if (this._address.get(i) <= this._splitAddress) {
                
                rest = (int) _address.get(i) % _blockSizeL1D; //absolute address modulo size blocks 
                tag = blockLine = (int) (_address.get(i) / _blockSizeL1D);                
                blockLine %= _setsNumberL1D; //
                tempCell = _cacheL1D.getCell((blockLine * _blockSizeL1D) + rest); //

                if ((tempCell.getValidate()) && (tempCell.getTag() == tag)) { //if the hit case only add hit counter
                    statsL1D.addHit();
                } else {
                    for (int j = 0; j < this._blockSizeL1D; j++) { //else, set the memmory address on cache
                        _cacheL1D.getCell((blockLine * _blockSizeL1D) + j).setTag(tag);
                        _cacheL1D.getCell((blockLine * _blockSizeL1D) + j).setValidate(true);
                    }
                    statsL1D.addMiss();
                }
            } else {
                
                rest = (int) _address.get(i) % _blockSizeL1I; //absolute address modulo size blocks 
                tag = blockLine = (int) (_address.get(i) / _blockSizeL1I);                
                blockLine %= _setsNumberL1I; //
                tempCell = _cacheL1I.getCell((blockLine * _blockSizeL1I) + rest); //

                if ((tempCell.getValidate()) && (tempCell.getTag() == tag)) { //if the hit case only add hit counter
                    statsL1I.addHit();
                } else {
                    for (int j = 0; j < this._blockSizeL1I; j++) { //else, set the memmory address on cache
                        _cacheL1I.getCell((blockLine * _blockSizeL1I) + j).setTag(tag);
                        _cacheL1I.getCell((blockLine * _blockSizeL1I) + j).setValidate(true);
                    }
                    statsL1I.addMiss();
                }
            }
            
        }
        setRequestedInformationL1D(statsL1D);
        setRequestedInformationL1I(statsL1I);
    }

    //Pablo

    public void SetAssociative() {
        
    }

    //Kellerson
    public void FullyAssociative() {
        
    }
    
    public ProcessorStats getRequestedInformationL1D() {
        return _requestedInformationL1D;
    }
    
    private void setRequestedInformationL1D(ProcessorStats _requestedInformationL1D) {
        this._requestedInformationL1D = _requestedInformationL1D;
    }

    /**
     * @return the _requestedInformationL1I
     */
    public ProcessorStats getRequestedInformationL1I() {
        return _requestedInformationL1I;
    }

    /**
     * @param _requestedInformationL1I the _requestedInformationL1I to set
     */
    public void setRequestedInformationL1I(ProcessorStats _requestedInformationL1I) {
        this._requestedInformationL1I = _requestedInformationL1I;
    }

    /**
     * @return the _requestedInformationL2U
     */
    public ProcessorStats getRequestedInformationL2U() {
        return _requestedInformationL2U;
    }

    /**
     * @param _requestedInformationL2U the _requestedInformationL2U to set
     */
    public void setRequestedInformationL2U(ProcessorStats _requestedInformationL2U) {
        this._requestedInformationL2U = _requestedInformationL2U;
    }
}
