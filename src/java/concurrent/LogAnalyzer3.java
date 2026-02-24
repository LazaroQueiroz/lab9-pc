import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class LogAnalyzer3 {

    static AtomicInteger total200 = new AtomicInteger(0);
    static AtomicInteger total500 = new AtomicInteger(0);

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Uso: java LogAnalyzer <arquivos_de_log>");
            System.exit(1);
        }

        ExecutorService es = Executors.newCachedThreadPool();

        for (String fileName : args) {
            System.out.println("Processando arquivo: " + fileName);
            FileProcessor3 fp = new FileProcessor3(fileName, total200, total500);
            es.execute(fp);
        }

        es.shutdown();
        try{
            es.awaitTermination(2, TimeUnit.SECONDS);
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        System.out.println("===== RESULTADO FINAL =====");
        System.out.println("Total 200: " + total200);
        System.out.println("Total 500: " + total500);
    }

    
}

class FileProcessor3 implements Runnable{

    private String fileName;
    private AtomicInteger total200;
    private AtomicInteger total500;

    FileProcessor3(String fileName, AtomicInteger total200, AtomicInteger total500){
        this.fileName = fileName;
        this.total200 = total200;
        this.total500 = total500;
    }
    public void processFile() {
	try (BufferedReader br = new BufferedReader(new FileReader(this.fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length == 3) {
                    String code = parts[2];
                    if (code.equals("200")) {
                        this.total200.getAndIncrement();
                    } else if (code.equals("500")) {
                        this.total500.getAndIncrement();
                    }
                }
            }
	} catch (IOException e) {
            System.err.println("Erro ao ler arquivo: " + this.fileName);
            e.printStackTrace();
        }
    }
    @Override
    public void run(){
        this.processFile();
    }
}
