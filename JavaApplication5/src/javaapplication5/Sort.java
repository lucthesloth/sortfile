package javaapplication5;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/** @author Lucas */
public class Sort {
    public static void main(String[] args) throws IOException {
        File tmp = new File("tmp");
        System.out.println(tmp.getAbsolutePath());
        RandomAccessFile f = new RandomAccessFile("cep.dat", "r");
        List<File> files = splitIntoFiles(f, 8, false);
        files.stream().forEach(k -> {
            try {
                sortFile(k);
            } catch (IOException ex) {
                Logger.getLogger(Sort.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        mergeAll(files);
    }
    public static File mergeAll(List<File> files) throws IOException{
        List<File> tmp = new ArrayList<>();
        for (int i = 0; i < files.size(); i+=2){
            File t1 = files.get(i);
            File t2 = files.get(i+1);
            tmp.add(merge(t1, t2));
            t1.delete();
            t2.delete();
        }
        return tmp.size() == 1 ? tmp.get(0) : mergeAll(tmp);
    }
    public static File merge(File a, File b) throws FileNotFoundException, IOException{
        RandomAccessFile raf_a = new RandomAccessFile(a, "rw");
        RandomAccessFile raf_b = new RandomAccessFile(b, "rw");
        File tmpF = new File("T" + System.currentTimeMillis());
        RandomAccessFile n = new RandomAccessFile(tmpF, "rw");
        System.out.println("Merging " + a.getName() + " " + b.getName());
        Endereco e1 = new Endereco();
        Endereco e2 = new Endereco();
        
        e1.leEndereco(raf_a);
        e2.leEndereco(raf_b);
        while (raf_a.getFilePointer() <= raf_a.length() && raf_b.getFilePointer() <= raf_b.length()
                && e1 != null && e2 != null){
            int i = new compareC().compare(e1, e2);
            if (i < 0){
                e1.escreveEndereco(n);
                if (raf_a.getFilePointer() == raf_a.length()) e1 = null;
                else e1.leEndereco(raf_a);
            } else {
                e2.escreveEndereco(n);
                if (raf_b.getFilePointer() == raf_b.length()) e2 = null;
                else e2.leEndereco(raf_b);
            }
        }
        while (raf_a.getFilePointer() <= raf_a.length() && e1 != null){
            e1.escreveEndereco(n);
            if (raf_a.getFilePointer() == raf_a.length()) e1 = null;
            else e1.leEndereco(raf_a);
        }
        while (raf_b.getFilePointer() <= raf_b.length() && e2 != null){
            e2.escreveEndereco(n);
            if (raf_b.getFilePointer() == raf_b.length()) e2 = null;
            else e2.leEndereco(raf_b);
        }
        return tmpF;
    }
    public static File sortFile(File f) throws FileNotFoundException, IOException{
        RandomAccessFile tmpF = new RandomAccessFile(f, "rw");
        List<Endereco> tmpL = new ArrayList<>();
        System.out.println("Sorting " + f.getName());
        while (tmpF.length() > tmpF.getFilePointer()){
            Endereco e = new Endereco();
            e.leEndereco(tmpF);
            tmpL.add(e);
        }
        tmpF.setLength(0);
        tmpF.seek(0);
        Collections.sort(tmpL, new compareC());
        tmpL.stream().forEach(k -> {
            try {
                k.escreveEndereco(tmpF);
            } catch (IOException ex) {
                Logger.getLogger(Sort.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        return f;
    }
    public static List<File> splitIntoFiles(RandomAccessFile f, int splt, boolean dump) throws IOException{
        List<File> tmp = new ArrayList<>();
        long lines = f.length()/300;
        long sect = lines/splt;
        int i = 1;
        for (; i <= splt; i++){
            File tmpF = new File("tmp_file_" + i + ".dat");
            System.out.println("Making file " + tmpF.getName());
            //PrintWriter pw = new PrintWriter(new FileWriter(tmpF, false));
            FileOutputStream oS = new FileOutputStream(tmpF);
            for (int j = 1; j <= sect && f.getFilePointer() < f.length(); j++){
                byte b[] = new byte[300];
                f.read(b);
                oS.write(b);
            }
            tmp.add(tmpF);
        }
        if (f.getFilePointer() < f.length()){
            if (!dump) i--;
            File tmpF = new File("tmp_file_" + i + ".dat");
            FileOutputStream oS = new FileOutputStream(tmpF, true);
            while (f.getFilePointer() < f.length()){
                byte b[] = new byte[300];
                f.read(b);
                oS.write(b);
            }
            if (dump) tmp.add(tmpF);
        }
            
        return tmp;
    }
}
class compareC implements Comparator<Endereco>{

    public compareC() {
    }
    
    @Override
    public int compare(Endereco o1, Endereco o2) {
        return o1.getCep().compareTo(o2.getCep());
    }
    
}