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
import java.util.concurrent.Executor;

import io.micrometer.context.ContextSnapshot;
import org.jspecify.annotations.Nullable;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnThreading;
import org.springframework.boot.task.SimpleAsyncTaskExecutorBuilder;
import org.springframework.boot.task.SimpleAsyncTaskExecutorCustomizer;
import org.springframework.boot.task.ThreadPoolTaskExecutorBuilder;
import org.springframework.boot.task.ThreadPoolTaskExecutorCustomizer;
import org.springframework.boot.thread.Threading;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.support.CompositeTaskDecorator;
import org.springframework.core.task.support.ContextPropagatingTaskDecorator;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * {@link TaskExecutor} configurations to be imported by
 * {@link TaskExecutionAutoConfiguration} in a specific order.
 * <p>由 {@link TaskExecutionAutoConfiguration} 按特定顺序导入的 {@link TaskExecutor} 配置。</p>
 *
 * @author Andy Wilkinson
 * @author Moritz Halbritter
 * @author Yanming Zhou
 */
class TaskExecutorConfigurations {

	private static @Nullable TaskDecorator getTaskDecorator(ObjectProvider<TaskDecorator> taskDecorator) {
		List<TaskDecorator> taskDecorators = taskDecorator.orderedStream().toList();
		if (taskDecorators.size() == 1) {
			return taskDecorators.get(0);
		}
		return (!taskDecorators.isEmpty()) ? new CompositeTaskDecorator(taskDecorators) : null;
	}

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnClass(ContextSnapshot.class)
	static class TaskExecutorContextPropagationConfiguration {

		@Bean
		@ConditionalOnProperty(name = "spring.task.execution.propagate-context", havingValue = "true")
		ContextPropagatingTaskDecorator contextPropagatingTaskDecorator() {
			return new ContextPropagatingTaskDecorator();
		}

	}

	@Configuration(proxyBeanMethods = false)
	@Conditional(OnExecutorCondition.class)
	@Import({ AsyncConfigurerWrapperConfiguration.class, AsyncConfigurerConfiguration.class })
	static class TaskExecutorConfiguration {

		@Bean(TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME)
		@ConditionalOnThreading(Threading.VIRTUAL)
		SimpleAsyncTaskExecutor applicationTaskExecutorVirtualThreads(SimpleAsyncTaskExecutorBuilder builder) {
			return builder.build();
		}

		@Bean(TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME)
		@Lazy
		@ConditionalOnThreading(Threading.PLATFORM)
		ThreadPoolTaskExecutor applicationTaskExecutor(ThreadPoolTaskExecutorBuilder threadPoolTaskExecutorBuilder) {
			return threadPoolTaskExecutorBuilder.build();
		}

	}

	@Configuration(proxyBeanMethods = false)
	static class ThreadPoolTaskExecutorBuilderConfiguration {

		@Bean
		@ConditionalOnMissingBean
		ThreadPoolTaskExecutorBuilder threadPoolTaskExecutorBuilder(TaskExecutionProperties properties,
				ObjectProvider<ThreadPoolTaskExecutorCustomizer> threadPoolTaskExecutorCustomizers,
				ObjectProvider<TaskDecorator> taskDecorator) {
			TaskExecutionProperties.Pool pool = properties.getPool();
			ThreadPoolTaskExecutorBuilder builder = new ThreadPoolTaskExecutorBuilder();
			builder = builder.queueCapacity(pool.getQueueCapacity());
			builder = builder.corePoolSize(pool.getCoreSize());
			builder = builder.maxPoolSize(pool.getMaxSize());
			builder = builder.allowCoreThreadTimeOut(pool.isAllowCoreThreadTimeout());
			builder = builder.keepAlive(pool.getKeepAlive());
			builder = builder.acceptTasksAfterContextClose(pool.getShutdown().isAcceptTasksAfterContextClose());
			TaskExecutionProperties.Shutdown shutdown = properties.getShutdown();
			builder = builder.awaitTermination(shutdown.isAwaitTermination());
			builder = builder.awaitTerminationPeriod(shutdown.getAwaitTerminationPeriod());
			builder = builder.threadNamePrefix(properties.getThreadNamePrefix());
			builder = builder.customizers(threadPoolTaskExecutorCustomizers.orderedStream()::iterator);
			builder = builder.taskDecorator(getTaskDecorator(taskDecorator));
			return builder;
		}

	}

	@Configuration(proxyBeanMethods = false)
	static class SimpleAsyncTaskExecutorBuilderConfiguration {

		private final TaskExecutionProperties properties;

		private final ObjectProvider<SimpleAsyncTaskExecutorCustomizer> taskExecutorCustomizers;

		private final ObjectProvider<TaskDecorator> taskDecorator;

		SimpleAsyncTaskExecutorBuilderConfiguration(TaskExecutionProperties properties,
				ObjectProvider<SimpleAsyncTaskExecutorCustomizer> taskExecutorCustomizers,
				ObjectProvider<TaskDecorator> taskDecorator) {
			this.properties = properties;
			this.taskExecutorCustomizers = taskExecutorCustomizers;
			this.taskDecorator = taskDecorator;
		}

		@Bean
		@ConditionalOnMissingBean
		@ConditionalOnThreading(Threading.PLATFORM)
		SimpleAsyncTaskExecutorBuilder simpleAsyncTaskExecutorBuilder() {
			return builder();
		}

		@Bean(name = "simpleAsyncTaskExecutorBuilder")
		@ConditionalOnMissingBean
		@ConditionalOnThreading(Threading.VIRTUAL)
		SimpleAsyncTaskExecutorBuilder simpleAsyncTaskExecutorBuilderVirtualThreads() {
			return builder().virtualThreads(true);
		}

		private SimpleAsyncTaskExecutorBuilder builder() {
			SimpleAsyncTaskExecutorBuilder builder = new SimpleAsyncTaskExecutorBuilder();
			builder = builder.threadNamePrefix(this.properties.getThreadNamePrefix());
			builder = builder.customizers(this.taskExecutorCustomizers.orderedStream()::iterator);
			builder = builder.taskDecorator(getTaskDecorator(this.taskDecorator));
			TaskExecutionProperties.Simple simple = this.properties.getSimple();
			builder = builder.cancelRemainingTasksOnClose(simple.isCancelRemainingTasksOnClose());
			builder = builder.rejectTasksWhenLimitReached(simple.isRejectTasksWhenLimitReached());
			builder = builder.concurrencyLimit(simple.getConcurrencyLimit());
			TaskExecutionProperties.Shutdown shutdown = this.properties.getShutdown();
			if (shutdown.isAwaitTermination()) {
				builder = builder.taskTerminationTimeout(shutdown.getAwaitTerminationPeriod());
			}
			return builder;
		}

	}

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnBean(AsyncConfigurer.class)
	static class AsyncConfigurerWrapperConfiguration {

		@Bean
		static BeanPostProcessor applicationTaskExecutorAsyncConfigurerBeanPostProcessor(
				ObjectProvider<BeanFactory> beanFactory) {
			return new BeanPostProcessor() {
				@Override
				public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
					if (bean instanceof AsyncConfigurer asyncConfigurer
							&& !(bean instanceof ApplicationTaskExecutorAsyncConfigurer)) {
						return new ApplicationTaskExecutorAsyncConfigurer(beanFactory.getObject(), asyncConfigurer);
					}
					return bean;
				}
			};
		}

	}

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnMissingBean(AsyncConfigurer.class)
	static class AsyncConfigurerConfiguration {

		@Bean
		ApplicationTaskExecutorAsyncConfigurer applicationTaskExecutorAsyncConfigurer(BeanFactory beanFactory) {
			return new ApplicationTaskExecutorAsyncConfigurer(beanFactory, null);
		}

	}

	@Configuration(proxyBeanMethods = false)
	static class BootstrapExecutorConfiguration {

		@Bean
		static BeanFactoryPostProcessor bootstrapExecutorAliasPostProcessor() {
			return (beanFactory) -> {
				boolean hasBootstrapExecutor = beanFactory
						.containsBean(ConfigurableApplicationContext.BOOTSTRAP_EXECUTOR_BEAN_NAME);
				boolean hasApplicationTaskExecutor = beanFactory
						.containsBean(TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME);
				if (!hasBootstrapExecutor && hasApplicationTaskExecutor) {
					beanFactory.registerAlias(TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME,
							ConfigurableApplicationContext.BOOTSTRAP_EXECUTOR_BEAN_NAME);
				}
			};
		}

	}

	static class OnExecutorCondition extends AnyNestedCondition {

		OnExecutorCondition() {
			super(ConfigurationPhase.REGISTER_BEAN);
		}

		@ConditionalOnMissingBean(Executor.class)
		private static final class ExecutorBeanCondition {

		}

		@ConditionalOnProperty(value = "spring.task.execution.mode", havingValue = "force")
		private static final class ModelCondition {

		}

	}

	/**
	 * {@link AsyncConfigurer} implementation that delegates to the user-defined
	 * {@link AsyncConfigurer} instance, if any. Consistently use the executor named
	 * {@value TaskExecutionAutoConfiguration#APPLICATION_TASK_EXECUTOR_BEAN_NAME} in the
	 * absence of a custom executor.
	 *
	 * <p>{@link AsyncConfigurer} 实现，委托给用户定义的 {@link AsyncConfigurer} 实例（如果有）。
	 * 在没有自定义执行器的情况下，一致地使用名为
	 * {@value TaskExecutionAutoConfiguration#APPLICATION_TASK_EXECUTOR_BEAN_NAME} 的执行器。</p>
	 */
	static class ApplicationTaskExecutorAsyncConfigurer implements AsyncConfigurer {

		private final BeanFactory beanFactory;

		private final @Nullable AsyncConfigurer delegate;

		ApplicationTaskExecutorAsyncConfigurer(BeanFactory beanFactory, @Nullable AsyncConfigurer delegate) {
			this.beanFactory = beanFactory;
			this.delegate = delegate;
		}

		@Override
		public Executor getAsyncExecutor() {
			Executor executor = (this.delegate != null) ? this.delegate.getAsyncExecutor() : null;
			return (executor != null) ? executor : getApplicationTaskExecutor();
		}

		@Override
		public @Nullable AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
			return (this.delegate != null) ? this.delegate.getAsyncUncaughtExceptionHandler() : null;
		}

		private Executor getApplicationTaskExecutor() {
			return this.beanFactory.getBean(TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME,
					Executor.class);
		}

	}

}