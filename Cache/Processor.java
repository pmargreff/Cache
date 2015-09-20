package Cache;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
/**
 *
 * @author pmargreff
 */
public class Processor {
    private String _path;
    
    public static void main(String[] args) {

    }
    
    public void ReadFile(String filePath){
        this._path = filePath;
    }
    
    public void OpenFile() throws IOException {
        FileReader file = new FileReader(this._path);
        BufferedReader text = new BufferedReader(file);
    }
}
