
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class ReadIBM {

	String processName = "readFile";
	
	public int[][] readSegY(String fileLocation, String fileName) throws IOException {
		System.out.println("Leyendo...");
		File fileRead = new File(fileLocation+"\\"+fileName);
        Long startTime = System.nanoTime();
        InputStream is = new FileInputStream(fileRead);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int counter = 0;
        int line;
        int position;
        int bigger = 0;
        int smaller = 0;
        int dataForTraces = 0; // numero de datos mas header de cada traza "240/4"
        int totalTraces = 0;
        int[][] arrayTotalTraces = null;
        
        // Lee los primeros 3200 bytes del encabezado de texto 
        while ((line = is.read()) != -1) {
            baos.write((char) line);
            counter++;
            if (counter % 3200 == 0) {
                baos = new ByteArrayOutputStream();
                break;
            }
        }
        
        //Lee en encabezado de contenido hasta el byte 3600
        while (counter <= 3599) {
        	if(counter == 3220){
        		dataForTraces = returnInt(2, is);
                totalTraces = (int) (fileRead.length()-3600)/4/(dataForTraces+60);
                System.out.println("Numero de trazas:" + totalTraces);
                System.out.println("Datos por traza:" + dataForTraces);
                arrayTotalTraces = new int[totalTraces][dataForTraces];
                counter+=2;
        	}
            returnInt(2, is);
            counter+=2;
        }
        
        
        // Inicia con la conversion de los datos de cada traza
        for (int i = 0; i < totalTraces; i++) {
        	for (int k = 0; k < dataForTraces+60; k++) {
            	position = k-60;
            	
            	// Se salta los primeros 240 bytes del encabezado de cada traza
            	if(position<0){
            		returnInt(2, is);
            		returnInt(2, is);
            		
            	}else{
            		
            		//Realiza la conversion de los 4 bytes a entero
	                int value = (int) float4byteIBMR4(is);
	                
	                // Omite los valores mayores a 100.000 y menores a -100.000 con el fin de evitar saltos
	                if(value > 100000 || value < -100000){
	                	value = 0;
	                }
	                
	                // Almacena el valor convertido
	                arrayTotalTraces[i][position] = value;
	                
	                ///verificar mas alto y mas bajo para escalar la grafica
	                if (value > bigger){
	                	bigger = value;
	                }
	                if (smaller > value){
	                	smaller = value;
	                }	                
            	}
                counter += 4;
            }
        }
        
        // Guarda los valores de datos por traza y numero de trazas en el archivo de propiedades 
        saveProperties(totalTraces, dataForTraces);
        
        // Realiza la grafica
        new NativeGraphic(arrayTotalTraces, processName, fileLocation, fileName, bigger, smaller);
        
        System.out.println("mayor: " + bigger);
        System.out.println("menor: " + smaller);
        System.out.println("Tiempo total: " + (System.nanoTime() - startTime)/1000000000 + " segundos");
        
        return (arrayTotalTraces);
    }
    
    private static int returnInt(int numBytes, InputStream is) throws IOException {
        int result = 0;
        for (int i = 0; i < numBytes; i++) {
            result += is.read()*Math.pow(256, numBytes-i-1);
        }
        return result;
    }

    private static float float4byteIBMR4(InputStream is) throws IOException {
        byte [] bytes = new byte[4];
    	
    	for (int i = 0; i < 4; i++) {
    		bytes[i] = (byte)is.read();
    	}
        return float4byteIBMR4(bytes);
    }
    
    public static float float4byteIBMR4(byte [] bytes) throws IOException {
    	int sgn, mant, exp;
    	
    	mant = ( bytes[1] &0xFF) << 16 | (bytes[2] & 0xFF ) << 8 | ( bytes[3] & 0xFF);
        if (mant == 0) return 0.0f;

        sgn = -(((bytes[0] & 128) >> 6) - 1);
        exp = (bytes[0] & 127) - 64;

        return (float) (sgn * Math.pow(16.0, exp - 6) * mant);
    }
 
    public void saveProperties(int nt, int nx){
	    PrintWriter writer;
		try {
			
			// Escribe los valores predeterminados
			writer = new PrintWriter("properties.conf", "UTF-8");
		    writer.println("0.1");
		    writer.println("1.0");
		    writer.println(".04");
		    writer.println(".04");
		    writer.println(""+nt);
		    writer.println(""+nx);
		    writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
