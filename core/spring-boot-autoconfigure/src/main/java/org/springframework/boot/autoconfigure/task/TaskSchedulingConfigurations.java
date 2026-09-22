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

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import org.jspecify.annotations.Nullable;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnThreading;
import org.springframework.boot.task.SimpleAsyncTaskSchedulerBuilder;
import org.springframework.boot.task.SimpleAsyncTaskSchedulerCustomizer;
import org.springframework.boot.task.ThreadPoolTaskSchedulerBuilder;
import org.springframework.boot.task.ThreadPoolTaskSchedulerCustomizer;
import org.springframework.boot.thread.Threading;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.support.CompositeTaskDecorator;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.config.TaskManagementConfigUtils;

/**
 * {@link TaskScheduler} configurations to be imported by
 * {@link TaskSchedulingAutoConfiguration} in a specific order.
 * <p>由 {@link TaskSchedulingAutoConfiguration} 按特定顺序导入的 {@link TaskScheduler} 配置。</p>
 *
 * @author Moritz Halbritter
 */
class TaskSchedulingConfigurations {

	private static @Nullable TaskDecorator getTaskDecorator(ObjectProvider<TaskDecorator> taskDecorator) {
		List<TaskDecorator> taskDecorators = taskDecorator.orderedStream().toList();
		if (taskDecorators.size() == 1) {
			return taskDecorators.get(0);
		}
		return (!taskDecorators.isEmpty()) ? new CompositeTaskDecorator(taskDecorators) : null;
	}

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnBean(name = TaskManagementConfigUtils.SCHEDULED_ANNOTATION_PROCESSOR_BEAN_NAME)
	@ConditionalOnMissingBean({ TaskScheduler.class, ScheduledExecutorService.class })
	@Import(DefaultTaskSchedulerConfiguration.class)
	static class TaskSchedulerConfiguration {

	}

	@Configuration(proxyBeanMethods = false)
	static class ThreadPoolTaskSchedulerBuilderConfiguration {

		@Bean
		@ConditionalOnMissingBean
		ThreadPoolTaskSchedulerBuilder threadPoolTaskSchedulerBuilder(TaskSchedulingProperties properties,
				ObjectProvider<TaskDecorator> taskDecorator,
				ObjectProvider<ThreadPoolTaskSchedulerCustomizer> threadPoolTaskSchedulerCustomizers) {
			TaskSchedulingProperties.Shutdown shutdown = properties.getShutdown();
			ThreadPoolTaskSchedulerBuilder builder = new ThreadPoolTaskSchedulerBuilder();
			builder = builder.poolSize(properties.getPool().getSize());
			builder = builder.awaitTermination(shutdown.isAwaitTermination());
			builder = builder.awaitTerminationPeriod(shutdown.getAwaitTerminationPeriod());
			builder = builder.threadNamePrefix(properties.getThreadNamePrefix());
			builder = builder.taskDecorator(getTaskDecorator(taskDecorator));
			builder = builder.customizers(threadPoolTaskSchedulerCustomizers);
			return builder;
		}

	}

	@Configuration(proxyBeanMethods = false)
	static class SimpleAsyncTaskSchedulerBuilderConfiguration {

		private final TaskSchedulingProperties properties;

		private final ObjectProvider<TaskDecorator> taskDecorator;

		private final ObjectProvider<SimpleAsyncTaskSchedulerCustomizer> taskSchedulerCustomizers;

		SimpleAsyncTaskSchedulerBuilderConfiguration(TaskSchedulingProperties properties,
				ObjectProvider<TaskDecorator> taskDecorator,
				ObjectProvider<SimpleAsyncTaskSchedulerCustomizer> taskSchedulerCustomizers) {
			this.properties = properties;
			this.taskDecorator = taskDecorator;
			this.taskSchedulerCustomizers = taskSchedulerCustomizers;
		}

		@Bean
		@ConditionalOnMissingBean
		@ConditionalOnThreading(Threading.PLATFORM)
		SimpleAsyncTaskSchedulerBuilder simpleAsyncTaskSchedulerBuilder() {
			return builder();
		}

		@Bean(name = "simpleAsyncTaskSchedulerBuilder")
		@ConditionalOnMissingBean
		@ConditionalOnThreading(Threading.VIRTUAL)
		SimpleAsyncTaskSchedulerBuilder simpleAsyncTaskSchedulerBuilderVirtualThreads() {
			return builder().virtualThreads(true);
		}

		private SimpleAsyncTaskSchedulerBuilder builder() {
			SimpleAsyncTaskSchedulerBuilder builder = new SimpleAsyncTaskSchedulerBuilder();
			builder = builder.threadNamePrefix(this.properties.getThreadNamePrefix());
			builder = builder.taskDecorator(getTaskDecorator(this.taskDecorator));
			builder = builder.customizers(this.taskSchedulerCustomizers.orderedStream()::iterator);
			TaskSchedulingProperties.Simple simple = this.properties.getSimple();
			builder = builder.concurrencyLimit(simple.getConcurrencyLimit());
			TaskSchedulingProperties.Shutdown shutdown = this.properties.getShutdown();
			if (shutdown.isAwaitTermination()) {
				builder = builder.taskTerminationTimeout(shutdown.getAwaitTerminationPeriod());
			}
			return builder;
		}

	}

}