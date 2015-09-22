package Cache;

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
    private ProcessorStats _requestedInformation; //miss or hits number    
    
    public Processor(String path, boolean textType) {
        this._path = path; //get the path 
        _address = new ArrayList<>(); //Init the Adress List
        _readOrWrite = new ArrayList<>(); //Init the comand list (Read or Write)
        _cacheMemory = new Cache(2,4); //Init cache with 8 adress size
        _requestedInformation = new ProcessorStats();
        
        //If textType = 1 try open .txt file format, else try open binary file
        if (textType) {
            this.ReadBinaryFile();
        } else {
            this.ReadTextFile();
        }
    }

    public static void main(String[] args) {

        Processor sample = new Processor("/home/pmargreff/Dropbox/faculdade/AOC_II/Cache/IO/arqTexto1_rw_10.txt", false);
        
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
    
    public ProcessorStats MissHitAverage(){
        for (Integer address : _address) {
            
            if(TestHere){
                
                
            } else {
                
            }
        }
    }
}
