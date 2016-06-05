package net.hoyoung.wfp.searcher.test;

import java.io.*;

/**
 * Created by hoyoung on 2015/10/24.
 * 导入sql
 */
public class App {
    public static void main(String[] args) {
        try {
            FileInputStream inputStream = new FileInputStream("/home/hoyoung/vmshare/Dump20160521.sql");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));


            String[] databases = new String[]{"weihao","nutch","weiphp","hive","sql_cook","wfp","wfp2","xdpaper"};

            FileOutputStream outputStream = null;
            BufferedWriter writer = null;

            String line = null;
            int count = 0;
            while ((line = reader.readLine())!=null){

                if(line.matches(".*-- Host: localhost    Database:.*")){
                    if(writer!=null){
                        writer.flush();
                        writer.close();
                    }
                    if(outputStream!=null){
                        outputStream.close();
                    }
//                    if(count>0) break;
                    outputStream = new FileOutputStream("/home/hoyoung/vmshare/sql/"+databases[count]+".sql");
                    writer = new BufferedWriter(new OutputStreamWriter(outputStream));
                    System.out.println(line);

                    count++;

                }
                if(writer!=null){
                    writer.write(line+"\n");
                }
//15510720835
                //18511825518
                //15201610418

            }
            if(writer!=null){
                writer.flush();
                writer.close();
            }
            if(outputStream!=null){
                outputStream.close();
            }

            reader.close();
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
