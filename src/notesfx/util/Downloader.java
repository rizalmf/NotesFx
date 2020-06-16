/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notesfx.util;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import static notesfx.util.ApplicationProperties.SERVER_URL;

/**
 *
 * @author RIZAL
 */
public class Downloader {
    private URL url;
    private URLConnection con;
    private DataInputStream dis; 
    private FileOutputStream fos; 
    private byte[] fileData;  
    
    public File downloadApp(String targetDir, String name_file){
        try {
                String target_url = SERVER_URL+name_file;
                url = new URL(target_url); //File Location goes here
                con = url.openConnection(); // open the url connection.
                dis = new DataInputStream(con.getInputStream());
                fileData = new byte[con.getContentLength()]; 
                for (int q = 0; q < fileData.length; q++) { 
                    fileData[q] = dis.readByte();
                }
                dis.close(); // close the data input stream
                String location = targetDir+"/"+name_file;
                File f = new File(location);
                f.createNewFile();
                fos = new FileOutputStream(f); //FILE Save Location goes here /Users/kfang/Documents/Download/file.pdf
                fos.write(fileData);  // write out the file we want to save.
                fos.close(); // close the output stream writer
                return f;
        }
        catch(Exception m) {
            Logger.getLogger(Downloader.class.getName()).log(Level.SEVERE, null, m);
        }
        return null;
    }

}
