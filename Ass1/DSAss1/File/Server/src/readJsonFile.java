import java.io.*;

public class readJsonFile {

	public static String readJsonFile(String filename){
		String mess = null;
		try{
			File file = new File(filename);
			FileReader fileReader = new FileReader(file);
			Reader reader = new InputStreamReader(new FileInputStream(file), "UTF-8");
			int i = 0;
			StringBuffer stringBuffer = new StringBuffer();
			while ((i = reader.read()) != -1) {
				stringBuffer.append((char) i);
            }
            fileReader.close();
            reader.close();
            mess = stringBuffer.toString();
            return mess;
		}catch(IOException e){
			e.printStackTrace();
		}
		return null;
	}
}
