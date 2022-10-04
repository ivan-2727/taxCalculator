import java.util.*;
import java.io.FileReader; 
import java.io.*; 
import com.sun.net.httpserver.*; 
import java.nio.charset.*;
import java.net.*; 

class Handler implements HttpHandler {
	
	CongestionTaxCalculator calc;
	
	Handler() throws Exception {
		calc = new CongestionTaxCalculator();
		calc.ReadLocalTaxRules("localTaxRules.txt");
		calc.PrintTaxRules();
	}
	
	public String getTaxes(BufferedReader br) throws IOException {
		String res = "";
		while(true) {
			String id = br.readLine(); 
			if (id == null) break;
			if (id.length() == 0) break;
			String vehicle = br.readLine();
			int numberOfTimePoints = Integer.parseInt(br.readLine());
			TimePoint[] times = new TimePoint[numberOfTimePoints]; 
			for (int i = 0; i < numberOfTimePoints; i++) {
				times[i] = new TimePoint(br.readLine().split("\\s+|-|:"));
			}
			br.readLine(); 
			res += id + " ... " + Integer.toString(calc.getTax(vehicle, times)) + "\n";
		}
		if (res.length() > 0) res = res.substring(0, res.length() - 1);
		return res; 
	}
	
	@Override
	public void handle(HttpExchange exc) throws IOException {
		InputStreamReader isr = new InputStreamReader(exc.getRequestBody(), "utf-8");
		String res = getTaxes(new BufferedReader(isr)); 
		
		exc.getResponseHeaders().put("Content-Type", Collections.singletonList("text/html")); 
		exc.sendResponseHeaders(200, res.length());
		
		byte[] bytes = res.getBytes(); 
		OutputStream os = exc.getResponseBody();
		os.write(bytes, 0, bytes.length);
		os.close();
	}
}

public class App {

	public static void main(String[] arg) throws Exception {
		HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
		System.out.println("server started");
		server.createContext("/", new Handler());
		server.setExecutor(null);
		server.start();
    }
	
}