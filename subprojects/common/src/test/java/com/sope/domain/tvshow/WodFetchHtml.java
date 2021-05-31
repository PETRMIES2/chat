package com.sope.domain.tvshow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;

public class WodFetchHtml {
    public static void fetchWods() throws IOException, InterruptedException {

        PrintWriter writer = new PrintWriter("f:\\temp\\2001-2017-allWods.txt", "UTF-8");

        String urlTemplate = "https://crossfit.com/workout/%s?page=%s";
        int startFrom = 2001;
        int endTo = 2017;
        for (; startFrom <= endTo; ++startFrom) {
            for (int page = 1; page <= 12; ++page) {
                
                URL url = new URL(String.format(urlTemplate, startFrom, page));
                System.out.println(url);
                InputStream is = url.openStream();
//                Thread.sleep(400);
                
                try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                    String line;
                    boolean print = false;
                    
                    while ((line = br.readLine()) != null) {
                        
                        if (line.contains("id=\"archives\"")) {
                            print = true;
                        }
                        if (line.contains("id=\"footer\"")) {
                            print = false;
                        }
                        if (print) {
                            writer.println(line);
                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    throw new MalformedURLException("URL is malformed!!");
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new IOException();
                }
                
            }
        }
        writer.close();

    }

    public static void main(String[] args) throws IOException, InterruptedException {
        fetchWods();
    }
    

}