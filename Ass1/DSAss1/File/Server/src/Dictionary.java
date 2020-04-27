import java.io.*;

import org.json.JSONObject;

public class Dictionary {
	
	public static String dictionary (JSONObject json, String filePath) {
        	try{
        		String request = json.getString("request");
		        switch(request){
		        	case "SEARCH":
		        		String s1 = readJsonFile.readJsonFile(filePath);
		                JSONObject json1 = new JSONObject(s1);
		        		search(json1, json.getString("word"));
		        		break;
		        	case "ADD":
		        		String s2 = readJsonFile.readJsonFile(filePath);
		                JSONObject json2 = new JSONObject(s2);
		        		add(json2, json.getString("word"), json.getString("meaning"), filePath);
		        		break;
		        	case "DELETE":
		        		String s3 = readJsonFile.readJsonFile(filePath);
		                JSONObject json3 = new JSONObject(s3);
		        		delete(json3, json.getString("word"), filePath);
		        		break;
		        	default:
		        		throw new Exception("invalid input!");
		        }
	        }catch(Exception e){
				return e.getMessage();
			}
        	return "sth is wrong";
		}
	
	public synchronized static void write(JSONObject json, String filePath){
		File file = new File(filePath);
		FileWriter filewrite;
		try{
			if(!file.exists()){
				file.createNewFile();
			}
			filewrite = new FileWriter(file);
			filewrite.write(json.toString());
			filewrite.flush();
			filewrite.close();
		}catch (IOException e) {
			System.out.println("File update error!");
		}
		
	}
	
	public synchronized static void search(JSONObject json, String word) throws Exception{
		if(!isExist(json, word)){
			throw new Exception("Word not found in the dictionary!");
		}else{
			throw new Exception("" + json.get(word));
		}
	}
	
	public synchronized static void add(JSONObject json, String word, String meaning, String filePath) throws Exception{
		if(isExist(json, word)){
			throw new Exception("Word already exists!");
		}else if(meaning.isEmpty()){
			throw new Exception("Meaning cannot be empty!");
		}else{
			json.append(word, meaning);
			write(json, filePath);
			throw new Exception("Success add word!");
		}
	}
	
	public synchronized static void delete(JSONObject json, String word, String filePath) throws Exception{
		if(!isExist(json, word)){
			throw new Exception("Word not found in the dictionary!");
		}else{
			json.remove(word);
			write(json, filePath);
			throw new Exception("Word has been delete!");
		}
	}
	
	public static boolean isExist(JSONObject json, String word){
		return json.has(word);
	}
	
}
	

