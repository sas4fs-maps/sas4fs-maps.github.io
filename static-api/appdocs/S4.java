import java.util.*;
import java.util.stream.*;
import java.util.zip.*;
import java.io.*;
import java.nio.file.*;
import java.net.http.*;
import java.net.*;
public class S4{
	public static void createFile(String path, byte[] data){
		try{
			if(path.indexOf("?")>0){
				path=path.substring(0,path.indexOf("?"));
			}
			Path p=Paths.get("./mega/"+path);
			File f=p.toFile();
			f.mkdirs();
			Files.deleteIfExists(p);
			var o=Files.newOutputStream(p);
			o.write(data);
			o.close();
			System.out.println("Wrote "+path+" successfully");
			Thread.sleep(100);
		}catch(IOException e){
			System.out.println("File "+path+" failed");
		}catch(InterruptedException ee){
			Thread.currentThread().interrupt();
		}
	}
	public static void main(String[] args){
		String dir="https://avatars.ninjakiwi.com/mega/";
		try{
			var str=Files.walk(Path.of("./large")).map(Path::getFileName).map(x->dir+x).toList();
			HttpClient client = HttpClient.newHttpClient();
			str.stream().forEach(path->{
				HttpResponse<byte[]> response=null;
				try{
					HttpRequest request = HttpRequest.newBuilder()
						 .uri(URI.create(path))
						 .build();
					response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
					if(response.statusCode()==200){
						createFile(path.substring(path.lastIndexOf("/")+1),response.body());
					}else throw new IOException();
				}catch(InterruptedException ee){
					Thread.currentThread().interrupt();
				}catch(Exception e2){
					System.out.println("File "+path+" failed ("+(response==null?-1:response.statusCode())+")");
				}
			});
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
