import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class Export {

	public static boolean exportFile(int[][] data, String inputFileLocation, String inputFileName) throws IOException {
		
		int counter = 0;
		byte[] dataByte = new byte[4];
		int header = 240;
		boolean response = false;
		String inputFile = inputFileLocation + "\\" + inputFileName;
		String outputFile = inputFileLocation + "\\migrado-" + inputFileName;
		System.out.println(outputFile);
		
		try (
	            InputStream inputStream = new FileInputStream(inputFile);
	            OutputStream outputStream = new FileOutputStream(outputFile);
	        ) {
	            int byteRead;
	 
	            // Guarda los valores del enzabezado hsata el byte 3600
	            while ((byteRead = inputStream.read()) != -1) {
            		outputStream.write(byteRead);
            		counter++;
	            	if(counter == 3600){
	            		break;
	            	}
	            }	            
	            for(int i = 0; i < data.length; i++){
		            for(int j = 0; j < data[i].length+header;j++){
		            	if(j<header){
		            		
		            		// Guarda los bytes del encabezado de cada traza
		            		byteRead = inputStream.read();
		            		outputStream.write(byteRead);
		            	}else{
		            		
		            		//Guarda los datos de cada traza
		            		dataByte = byte4floatIBMR4(data[i][j-header]);
		            		for(int k = 0; k<dataByte.length;k++){
		            			byteRead = inputStream.read();
		            			outputStream.write(dataByte[k]);
		            		}
		            	}
		            }
	            }
	            outputStream.close();  
	            response = true;
	        } catch (IOException ex) {
	            ex.printStackTrace();
	        }
		return response;
	}
	
    public static byte[] byte4floatIBMR4(float from) {
        int fconv = Float.floatToRawIntBits(from);

        int fmant = (0x007fffff & fconv) | 0x00800000;
        int t = (int)((0x7f800000 & fconv) >> 23) - 126;
        while (0 != (t & 0x3)) { ++t; fmant >>= 1; }
        fconv = (int)(0x80000000 & fconv) | (((t >> 2) + 64) << 24) | fmant;
        byte[] bytes = ByteBuffer.allocate(4).putInt(fconv).array();
        return bytes; // big endian order
    }
}
