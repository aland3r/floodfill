package service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Grava PNGs em paralelo (várias codificações ao mesmo tempo) para não travar o preenchimento.
 * Cada frame é uma cópia da imagem no momento do disparo — seguro com multithreading.
 */
public final class GravadorFramesParalelo implements AutoCloseable {

    private static final int MAX_THREADS = Math.min(4, Math.max(2, Runtime.getRuntime().availableProcessors()));

    private final ImageIOService io;
    private final ExecutorService exec;
    private final List<Future<?>> futures = new ArrayList<>();

    public GravadorFramesParalelo(ImageIOService io) {
        this.io = io;
        this.exec = Executors.newFixedThreadPool(MAX_THREADS);
    }

    /** Copia a imagem atual e agenda gravação em disco (outra thread). */
    public void submitFrame(BufferedImage imagemAtual, String caminho) {
        BufferedImage copia = io.copiar(imagemAtual);
        Future<?> f = exec.submit(() -> {
            io.salvar(copia, caminho);
            return null;
        });
        futures.add(f);
    }

    @Override
    public void close() {
        exec.shutdown();
        try {
            if (!exec.awaitTermination(2, TimeUnit.HOURS)) {
                exec.shutdownNow();
            }
            for (Future<?> f : futures) {
                f.get();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new UncheckedIOException(new IOException("interrompido ao gravar frames", e));
        } catch (ExecutionException e) {
            Throwable c = e.getCause();
            if (c instanceof RuntimeException) {
                throw (RuntimeException) c;
            }
            throw new RuntimeException(c);
        }
    }
}
