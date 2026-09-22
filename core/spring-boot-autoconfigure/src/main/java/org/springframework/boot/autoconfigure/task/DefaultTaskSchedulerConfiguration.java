/*
 * Copyright 2012-present the original author or authors.
 * 版权所有 2012-至今 原始作者
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * 根据 Apache 许可证 2.0 版本（"许可证"）授权；
 * you may not use this file except in compliance with the License.
 * 您仅在遵守许可证的情况下才可使用本文件。
 * You may obtain a copy of the License at
 * 您可以从以下地址获取许可证副本：
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * 除非适用法律要求或书面同意，按许可证分发的软件是基于"按原样"基础分发的，
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 不附带任何明示或暗示的保证或条件。
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 请查看许可证以了解管辖权限和限制的具体语言。
 */

package org.springframework.boot.autoconfigure.task;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnThreading;
import org.springframework.boot.task.SimpleAsyncTaskSchedulerBuilder;
import org.springframework.boot.task.ThreadPoolTaskSchedulerBuilder;
import org.springframework.boot.thread.Threading;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.TaskSchedulerRouter;

/**
 * Configuration that can be imported to expose a standard {@link TaskScheduler} if the
 * user has not enabled task scheduling explicitly. A {@link SimpleAsyncTaskScheduler} is
 * exposed if the user enables virtual threads via
 * {@code spring.threads.virtual.enabled=true}, otherwise {@link ThreadPoolTaskScheduler}.
 * <p>配置，如果用户未显式启用任务调度，则可以导入以暴露标准的 {@link TaskScheduler}。
 * 如果用户通过 {@code spring.threads.virtual.enabled=true} 启用虚拟线程，
 * 则暴露 {@link SimpleAsyncTaskScheduler}，否则暴露 {@link ThreadPoolTaskScheduler}。</p>
 *
 * <p>
 * Configurations importing this one should be ordered after
 * {@link TaskSchedulingAutoConfiguration}.
 * <p>导入此配置的配置应排在 {@link TaskSchedulingAutoConfiguration} 之后。</p>
 *
 * @author Phillip Webb
 * @since 4.1.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnMissingBean(name = DefaultTaskSchedulerConfiguration.DEFAULT_TASK_SCHEDULER_BEAN_NAME)
public class DefaultTaskSchedulerConfiguration {

	/**
	 * The bean name of the default task scheduler.
	 * <p>默认任务调度器的 bean 名称。
	 */
	public static final String DEFAULT_TASK_SCHEDULER_BEAN_NAME = TaskSchedulerRouter.DEFAULT_TASK_SCHEDULER_BEAN_NAME;

	@Bean(name = DEFAULT_TASK_SCHEDULER_BEAN_NAME)
	@ConditionalOnBean(ThreadPoolTaskSchedulerBuilder.class)
	@ConditionalOnThreading(Threading.PLATFORM)
	ThreadPoolTaskScheduler taskScheduler(ThreadPoolTaskSchedulerBuilder threadPoolTaskSchedulerBuilder) {
		return threadPoolTaskSchedulerBuilder.build();
	}

	@Bean(name = DEFAULT_TASK_SCHEDULER_BEAN_NAME)
	@ConditionalOnBean(SimpleAsyncTaskSchedulerBuilder.class)
	@ConditionalOnThreading(Threading.VIRTUAL)
	SimpleAsyncTaskScheduler taskSchedulerVirtualThreads(
			SimpleAsyncTaskSchedulerBuilder simpleAsyncTaskSchedulerBuilder) {
		return simpleAsyncTaskSchedulerBuilder.build();
	}

}