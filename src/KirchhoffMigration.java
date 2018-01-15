import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class KirchhoffMigration {
	int bigger = 0;
	int smaller = 0;
	double velhalf; 
	double t0; 
	double dx; 
	double dt;
	int nt;
	int nx;
	int[][] data;
	int[][] oldData;
	String fileLocation, fileName;
	String processName = "kirchhoffMigration";
	
	
	//Lee el archivo de propiedades y carga sus variables
	public int[][] readProperties(int[][] data, String fileLocation, String fileName) throws IOException {
		try(BufferedReader br = new BufferedReader(new FileReader("properties.conf"))) {
			this.fileLocation = fileLocation;
			this.fileName = fileName;
			this.oldData = data;
		    StringBuilder sb = new StringBuilder();
	        sb.append(System.lineSeparator());
	        velhalf = (double) Double.parseDouble(br.readLine());
	        t0 = (double) Double.parseDouble(br.readLine());
	        dx = (double) Double.parseDouble(br.readLine());
	        dt = (double) Double.parseDouble(br.readLine());
	        nt = (int) Integer.parseInt(br.readLine());
	        nx = (int) Integer.parseInt(br.readLine());

	        // ejecuta la migracion
	        kirchhoffMigration();
	        
		}catch (FileNotFoundException  e) {
			//Cancelar migracion por error en la lectura de las propiedades
			
		}
		return this.data;
	}
	
	public void kirchhoffMigration () {
		Long startTime = System.nanoTime();
		System.out.println("Migrando...");
		double x0=0.0, y0=0.0;
		double dy=dx, z0=t0, dz=dt; 
		int nz=nt;		 
		double x, y, z, hs, t; 
		int it;
		data = new int[nt][nx];
		
		 for (int ix=0; ix < nx; ix++) {
			 x = x0 + dx * ix;
			 for (int iy=0; iy < nx; iy++) {
				 y = y0 + dy * iy;
				 for (int iz=0; iz < nz; iz++) {
					 z = z0 + dz * iz;
					 hs = (x-y) / velhalf;
					 t = Math.sqrt(z*z + hs*hs);
					 it = (int)((0.5+(t-t0))/ dt);
					 if (it < nt) {
						 if (oldData[it][iy]!=0){
							 data[it][iy] = (int) (data[it][iy] + oldData[iz][ix]);
							 if(data[it][iy]>bigger){
								 bigger = data[it][iy];
							 }
							 if(data[it][iy]<smaller){
								 smaller = data[it][iy];
							 }
						 }
					 }
				 }
			 }
		 } 
		System.out.println("mayor: " + bigger);
	    System.out.println("menor: " + smaller);
	    
	    // Llama el proceso de grafica
        new NativeGraphic(data, processName, fileLocation, fileName, bigger, smaller);
        System.out.println("Tiempo total: " + (System.nanoTime() - startTime)/1000000000 + " segundos");
	}
}