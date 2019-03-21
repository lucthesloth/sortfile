/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication5;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 *
 * @author Lucas
 */
public class Bsearch {
    static int count = 0;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        RandomAccessFile f = new RandomAccessFile("cep_ordenado.dat", "r");
        Endereco e = new Endereco();
         
//        f.seek(390560L*300);
//        while( f.getFilePointer() < f.length() ) // para Detectar EOF
//        {
//            System.out.println(f.getFilePointer()); 
//            e.leEndereco(f);
//            System.out.println(e.getLogradouro());
//            System.out.println(e.getBairro());
//            System.out.println(e.getCidade());
//            System.out.println(e.getEstado());
//            System.out.println(e.getSigla());
//            System.out.println(e.getCep());
//            break;
//        }
        System.out.println(f.length()/300);
        e = binarySearch(f, 0, (int) (f.length()/300), "11230330");
        if (e != null)
        System.out.println(e.getLogradouro());
        else System.out.println("Not found");
        System.out.println(count);
        f.close();
    }
    public static Endereco binarySearch(RandomAccessFile f, int s, int e, String cep) throws IOException{
        Endereco _e = new Endereco();
        int middle = (e+s)/2;
        System.out.println(middle);
        f.seek(middle*300);
        count++;
        _e.leEndereco(f);
        long t = Long.parseLong(_e.getCep());
        long t2 = Long.parseLong(cep);
        System.out.println("fcep = " + t + " ucep = " + t2);
        
        if (s != e && t > t2) return binarySearch(f, s, middle-1, cep);
        else if (s != e && t < t2) return binarySearch(f, middle+1, e, cep);
        else if (s != e && t == t2) return _e;
        else return null;
    }
}
