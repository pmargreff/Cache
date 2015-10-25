
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
    private ArrayList<Integer> _accessType; //Define the action: 0 read memory, 1 writte on memory
    private String _path; //Path to memory file

    //Caches
    private Cache _cacheL1D; //Data cache - L1
    private Cache _cacheL1I; //Instructions cache - L1
    private Cache _cacheL2U; //Unified cache - L2

    private int _splitAddress; //Numbers equal or less (<=) than _splitAdress will be save on data memory

    public Processor(String path) {
        //Caches sizes
        int assocL1D; //Blocks size in L1D
        int nSetsL1D; //Number of blocks in L1D
        int bSizeL1D;

        int assocL1I; //Blocks size in L1I
        int nSetsL1I; //Number of blocks in L1I
        int bSizeL1I;

        int assocL2U; //Blocks size in L1I
        int nSetsL2U; //Number of blocks in L1I
        int bSizeL2U;
        
        this._path = path; //get the path
        this._address = new ArrayList<>(); //Init the Adress List
        this._accessType = new ArrayList<>(); //Init the comand list (Read or Write)

        nSetsL1D = 4;
        assocL1D = 64;
        bSizeL1D = 1;

        nSetsL1I = 4;
        assocL1I = 64;
        bSizeL1I = 1;

        nSetsL2U = 1024;
        assocL2U = 1;
        bSizeL2U = 1;

        this._splitAddress = 500;

        this._cacheL2U = new Cache(nSetsL2U, assocL2U, bSizeL2U);

        this._cacheL1D = new Cache(nSetsL1D, assocL1D, bSizeL1D, _cacheL2U); //Init cache with split address plus 1
        this._cacheL1I = new Cache(nSetsL1I, assocL1I, bSizeL1I, _cacheL2U); //Init cache with the rest of size

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

        Processor sample = new Processor(path);
        sample.run();

        /*if ( argv.length == 2){
         lines = Integer.parseInt(argv[0]);
         path = argv[1];

         Processor sample = new Processor(lines, path);
         sample.run();
         } else {
         System.out.println("ERROR! Argumento: Número_de_blocos<int> caminho_arquivo_entrada<String>");
         }
         */
        sample._cacheL1D._stats.print();
        sample._cacheL1I._stats.print();
        sample._cacheL2U._stats.print();
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
                    _accessType.add(Integer.parseInt(line));
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
    public void run() {
        for (int i = 0; i < _address.size(); i++) {

            if (this._address.get(i) <= this._splitAddress) {
                _cacheL1D.access(_address.get(i), _accessType.get(i));
            } else {
                _cacheL1I.access(_address.get(i), _accessType.get(i));
            }
        }
    }
}
