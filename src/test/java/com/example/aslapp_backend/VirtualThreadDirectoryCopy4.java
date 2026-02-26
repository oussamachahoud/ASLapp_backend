//// Source - https://stackoverflow.com/q
//// Posted by Ajay Kumar, modified by community. See post 'Timeline' for change history
//// Retrieved 2026-01-10, License - CC BY-SA 4.0
//package com.example.aslapp_backend;
//import java.io.IOException;
//import java.io.UncheckedIOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.StandardCopyOption;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.Semaphore;
//
//public class VirtualThreadDirectoryCopy4 {
//
//    public static void main(String[] args) throws Exception {
//
//        Path source = Path.of("C:\\source");
//        Path target = Path.of("C:\\source2");
//
//        int maxVirtualThreads = 2; // set your VT limit here
//
//        copyDirectory(source, target, maxVirtualThreads);
//    }
//
//    public static void copyDirectory(Path source, Path target, int maxThreads) throws Exception {
//
//        // Create a fixed-size virtual-thread executor
//        ExecutorService executor = Executors.newThreadPerTaskExecutor(
//                Thread.ofVirtual().factory()
//        );
//
//        // Use a semaphore to limit concurrency
//        final Semaphore semaphore = new Semaphore(maxThreads);
//
//        try (executor) {
//            Files.walk(source).forEach(path -> {
//                Path relative = source.relativize(path);
//                Path dest = target.resolve(relative);
//
//                if (Files.isDirectory(path)) {
//                    try {
//                        Files.createDirectories(dest);
//                    } catch (IOException e) {
//                        throw new UncheckedIOException(e);
//                    }
//                } else {
//                    executor.submit(() -> {
//                        try {
//                            semaphore.acquire();
//                            copyFileWithThreadInfo(path, dest);
//                        } catch (InterruptedException e) {
//                            Thread.currentThread().interrupt();
//                        } finally {
//                            semaphore.release();
//                        }
//                    });
//                }
//            });
//        }
//    }
//
//    private static void copyFileWithThreadInfo(Path src, Path dest) {
//        long start = System.nanoTime();
//
//        Thread t = Thread.currentThread();
//        String threadName = t.getName();
//        long threadId = t.threadId();
//
//        try {
//            Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
//            long end = System.nanoTime();
//
//            long millis = (end - start) / 1_000_000;
//
//            System.out.printf(
//                    "Thread %s (ID %d) copied %s → %s in %d ms%n",
//                    threadName, threadId, src, dest, millis
//            );
//
//        } catch (IOException e) {
//            System.err.printf(
//                    "Thread %s (ID %d) failed to copy %s: %s%n",
//                    threadName, threadId, src, e.getMessage()
//            );
//        }
//    }
//}
