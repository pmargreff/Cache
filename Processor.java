

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
    private ArrayList<Integer> _readOrWrite; //Define the action: 0 read memory, 1 writte on memory
    private String _path; //Path to memory file
    private Cache _cacheMemory; 
    public ProcessorStats _requestedInformation; //miss or hits number    
    private int _colunms; //width size
    private int _lines;
    
    public Processor(int lines, String path) {
        this._path = path; //get the path 
        this._address = new ArrayList<>(); //Init the Adress List
        this._readOrWrite = new ArrayList<>(); //Init the comand list (Read or Write)
        this._lines = lines;
        this._colunms = 4;
        this._cacheMemory = new Cache(_lines,_colunms); //Init cache with 8 adress size
        this._requestedInformation = new ProcessorStats();
        boolean textType = true;
        //If textType = 1 try open .txt file format, else try open binary file
        if (!textType) {
            this.ReadBinaryFile();
        } else {
            this.ReadTextFile();
        }
    }

    public static void main(String[] argv) {
        int lines;
        String path;
        
        if ( argv.length == 2){
            lines = Integer.parseInt(argv[0]);
            path = argv[1];
            Processor sample = new Processor(lines, path);
            sample.MissHitAverage();
        } else {
            System.out.println("ERROR! Argumento: Número_de_blocos<int> caminho_arquivo_entrada<String>");
        }
//        Processor sample = new Processor("/home/pmargreff/Dropbox/faculdade/AOC_II/Cache/IO/arqTexto1_rw_10k.txt", true);
//        
    }

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
                    _readOrWrite.add(Integer.parseInt(line));
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

//    TODO: Convert type to little endian
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
            // Or we could just do this: 
            // ex.printStackTrace();
        }
    }
    
    public void MissHitAverage(){
        ProcessorStats stats = new ProcessorStats();
        int rest;
        int total;
        int tag;
        Cell tempCache;
//        System.out.println(_address.size());
        for (int i = 0; i < _address.size(); i++) {
//            System.out.println(_address.get(i));
           
           rest =(int) _address.get(i) % _colunms;
           tag = total = (int) (_address.get(i) / _colunms);
           total %= _lines;
           tempCache = _cacheMemory.getCell((total * _colunms) + rest);
//           System.out.println(tempCache.getTag() + "TAG na Cache");
           if ((tempCache.getValidate()) && (tempCache.getTag() == tag)){
               stats.addHit();
            } else {
//               System.out.println( "BLOCO ");
               for (int j = 0; j < 4; j++){
                   _cacheMemory.getCell((total * _colunms) + j).setTag(tag); 
//                   System.out.print( " " + (_address.get(i) - rest + j));
                   _cacheMemory.getCell((total * _colunms) + j).setValidate(true);
                   
               }   
               stats.addMiss();
            } 
//           System.out.println("Hit: " + stats.getHit() + " Miss: " + stats.getMiss());
           
        }
//        System.out.println(_cacheMemory.getSize());
        System.out.println("Hit: " + stats.getHit() + " Miss: " + stats.getMiss());
        setRequestedInformation(stats);
    }

    public ProcessorStats getRequestedInformation() {
        return _requestedInformation;
    }

    private void setRequestedInformation(ProcessorStats _requestedInformation) {
        this._requestedInformation = _requestedInformation;
    }
}
