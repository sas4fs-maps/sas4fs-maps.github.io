import java.util.*;
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
			Path p=Paths.get(path);
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
	public static String getPath1(String line){
		return line.split(",")[0];
	}
	public static String getPath2(String line){
		return line.substring(line.lastIndexOf("/")+1);
	}
	public static void main(String[] args){
		try{
			HttpClient client = HttpClient.newBuilder()
			.proxy(ProxySelector.of(new InetSocketAddress("127.0.0.1", 9666)))
			.build();
			Files.lines(Paths.get(args[0]))
				.distinct().forEach(path->{
					HttpResponse<byte[]> response=null;
					try{
						HttpRequest request = HttpRequest.newBuilder()
							 .uri(URI.create(path))
							 .build();
						response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
						if(response.statusCode()==200){
							createFile(getPath2(path),response.body());
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
