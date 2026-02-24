import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class LogAnalyzer4 {

    static AtomicInteger total200 = new AtomicInteger(0);
    static AtomicInteger total500 = new AtomicInteger(0);

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Uso: java LogAnalyzer <arquivos_de_log>");
            System.exit(1);
        }

        ExecutorService es = Executors.newCachedThreadPool();
        List<Future<List<Integer>>> answers = new ArrayList<>();

        for (String fileName : args) {
            System.out.println("Processando arquivo: " + fileName);
            FileProcessor4 fp = new FileProcessor4(fileName);
            Future<List<Integer>> answer = es.submit(fp);
            answers.add(answer);
        }

        for (Future<List<Integer>> a: answers){
            try{
                List<Integer> l = a.get();
                total200.addAndGet(l.get(0));
                total500.addAndGet(l.get(1));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            
        }
        es.shutdown();

        System.out.println("===== RESULTADO FINAL =====");
        System.out.println("Total 200: " + total200);
        System.out.println("Total 500: " + total500);
    }

    
}

class FileProcessor4 implements Callable<List<Integer>>{

    private String fileName;

    FileProcessor4(String fileName){
        this.fileName = fileName;
    }

    public List<Integer> processFile() {
             int count200 = 0;
            int count500 = 0;

	try (BufferedReader br = new BufferedReader(new FileReader(this.fileName))) {
       
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length == 3) {
                    String code = parts[2];
                    if (code.equals("200")) {
                        count200++;
                    } else if (code.equals("500")) {
                        count500++;
                    }
                }
            }

	} catch (IOException e) {
            System.err.println("Erro ao ler arquivo: " + this.fileName);
            e.printStackTrace();
        }
        List<Integer> answer = new ArrayList<>();
        answer.add(count200);
        answer.add(count500);
        return answer;
    }
    @Override
    public List<Integer> call(){
        return this.processFile();
    }
}
