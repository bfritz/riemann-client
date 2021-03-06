/*******************************************************************************
 * Copyright (c) 2013 GigaSpaces Technologies Ltd. All rights reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package org.robobninjas.riemann.spring.internal;

import org.jboss.netty.channel.socket.nio.NioClientBossPool;
import org.jboss.netty.channel.socket.nio.NioWorkerPool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public class RiemannNioConfiguration {

    public static final int DEFAULT_NUM_WORKERS = 1;
    @Value("${riemann-client.nio.num-workers:" + DEFAULT_NUM_WORKERS + "}")
    private Integer numWorkers;

    private final Executor executor = Executors.newCachedThreadPool();


    @Bean(destroyMethod = "shutdown")
    public NioClientBossPool bossPool() {
        return new NioClientBossPool(executor, 1);
    }

    @Bean(destroyMethod = "shutdown")
    public NioWorkerPool getWorkerPool() {
        return new NioWorkerPool(executor, numWorkers);
    }

}
