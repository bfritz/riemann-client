package org.robotninjas.riemann.sample;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.yammer.metrics.core.MetricsRegistry;
import com.yammer.metrics.reporting.ConsoleReporter;
import org.apache.commons.cli.*;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.robobninjas.riemann.guice.RiemannClientModule;

import java.util.concurrent.TimeUnit;

public class ClientLoadTest {

  private static final int WORKER_COUNT = 1;
  private static final int ACTIVE_CONNECTIONS = 1;
  private static final int NETTY_WORKER_COUNT = 1;

  public static void main(String[] args) {

    try {
      final Options options = new Options();
      options.addOption("w", "workers", true, "number of concurrent workers");
      options.addOption("c", "connections", true, "number of concurrent connections");
      options.addOption("n", "netty-workers", true, "number of netty worker threads");

      final CommandLineParser parser = new PosixParser();
      final CommandLine line = parser.parse(options, args);

      final int workers = line.hasOption("W") ? ((Number) line.getParsedOptionValue("w")).intValue() : WORKER_COUNT;
      final int connections = line.hasOption("c") ? ((Number) line.getParsedOptionValue("c")).intValue() : ACTIVE_CONNECTIONS;
      final int nettyWorkers = line.hasOption("n") ? ((Number) line.getParsedOptionValue("n")).intValue() : NETTY_WORKER_COUNT;

      final GenericObjectPool.Config poolConfig = new GenericObjectPool.Config();
      poolConfig.maxActive = ACTIVE_CONNECTIONS;

      final Injector injector = Guice.createInjector(
        new RiemannClientModule("localhost", 5555, NETTY_WORKER_COUNT, poolConfig),
        new LoadTestModule(WORKER_COUNT));

      final MetricsRegistry registry = injector.getInstance(MetricsRegistry.class);
      ConsoleReporter.enable(registry, 1, TimeUnit.SECONDS);

      final ClientLoadTestService loadTestService = injector.getInstance(ClientLoadTestService.class);
      Runtime.getRuntime().addShutdownHook(new Thread() {
        @Override
        public void run() {
          loadTestService.stopAndWait();
        }
      });
      loadTestService.startAndWait();
    } catch (ParseException e) {
      e.printStackTrace();
    }

  }

}
