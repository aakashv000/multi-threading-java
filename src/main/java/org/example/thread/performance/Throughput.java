package org.example.thread.performance;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Throughput calculation - used JMeter, plan saved (Throughput - Word Count - Multithreading - Test Plan.jmx)
 * Throughput load - search_words.csv
 */
public class Throughput {
    private static final String INPUT_FILE_PATH = "./src/main/resources/throughput/war_and_peace.txt";
    private static final int NUM_OF_THREADS = 4;
    // ^ from 1 to # logical cores, throughput strictly increases, then saturates

    public static void main(String[] args) throws IOException {
        String document = new String(Files.readAllBytes(Path.of(INPUT_FILE_PATH)));

        startServer(document);
    }

    /**
     * url example - http://localhost:8111/count?word=cow
     */
    private static void startServer (String doc) throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(8111), 0);
        // ^ backlog, is max. no. of queued incoming TCP connections = 0,
        // because, we want all the queuing to be taken care by our executor's task queue

        httpServer.createContext("/count", new WordCountHttpHandler(doc));

        // thread pool
        Executor executor = Executors.newFixedThreadPool(NUM_OF_THREADS);
        httpServer.setExecutor(executor);

        httpServer.start();
    }


    private static class WordCountHttpHandler implements HttpHandler {
        private String doc;

        WordCountHttpHandler(String doc) {
            this.doc = doc;
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String query = httpExchange.getRequestURI().getQuery();

            String key = query.split("=")[0];
            String value = query.split("=")[1];
            if (! key.equals("word")) {
                httpExchange.sendResponseHeaders(400, 0);
                return;
            }

            int count = countWord(value);
            byte [] response = Integer.toString(count).getBytes();

            httpExchange.sendResponseHeaders(200, response.length);

            OutputStream responseBodyOS = httpExchange.getResponseBody();
            responseBodyOS.write(response);
            responseBodyOS.close();
        }

        private int countWord (String word) {
            int count = 0;
            int currIdx = 0;
            while (currIdx >= 0) {
                currIdx = this.doc.indexOf(word, currIdx);
                if (currIdx >= 0) {
                    count++;
                    currIdx++;
                }
            }
            return count;
        }
    }
}
