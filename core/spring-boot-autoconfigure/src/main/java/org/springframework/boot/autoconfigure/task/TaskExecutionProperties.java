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

import java.time.Duration;

import org.jspecify.annotations.Nullable;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for task execution.
 * <p>任务执行的配置属性。</p>
 *
 * @author Stephane Nicoll
 * @author Filip Hrisafov
 * @author Yanming Zhou
 * @since 2.1.0
 */
@ConfigurationProperties("spring.task.execution")
public class TaskExecutionProperties {

	private final Pool pool = new Pool();

	private final Simple simple = new Simple();

	private final Shutdown shutdown = new Shutdown();

	/**
	 * Determine when the task executor is to be created.
	 * <p>确定何时创建任务执行器。</p>
	 */
	private Mode mode = Mode.AUTO;

	/**
	 * Whether to propagate the current context to task executions.
	 * <p>是否将当前上下文传播到任务执行。</p>
	 */
	private boolean propagateContext;

	/**
	 * Prefix to use for the names of newly created threads.
	 * <p>用于新建线程名称的前缀。</p>
	 */
	private String threadNamePrefix = "task-";

	public Simple getSimple() {
		return this.simple;
	}

	public Pool getPool() {
		return this.pool;
	}

	public Shutdown getShutdown() {
		return this.shutdown;
	}

	public Mode getMode() {
		return this.mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public boolean getPropagateContext() {
		return this.propagateContext;
	}

	public void setPropagateContext(boolean propagateContext) {
		this.propagateContext = propagateContext;
	}

	public String getThreadNamePrefix() {
		return this.threadNamePrefix;
	}

	public void setThreadNamePrefix(String threadNamePrefix) {
		this.threadNamePrefix = threadNamePrefix;
	}

	public static class Simple {

		/**
		 * Whether to cancel remaining tasks on close. Only recommended if threads are
		 * commonly expected to be stuck.
		 * <p>关闭时是否取消剩余任务。仅当线程通常预期会卡住时才推荐使用。</p>
		 */
		private boolean cancelRemainingTasksOnClose;

		/**
		 * Whether to reject tasks when the concurrency limit has been reached.
		 * <p>当达到并发限制时是否拒绝任务。</p>
		 */
		private boolean rejectTasksWhenLimitReached;

		/**
		 * Set the maximum number of parallel accesses allowed. -1 indicates no
		 * concurrency limit at all.
		 * <p>设置允许的最大并行访问数。-1 表示完全没有并发限制。</p>
		 */
		private @Nullable Integer concurrencyLimit;

		public boolean isCancelRemainingTasksOnClose() {
			return this.cancelRemainingTasksOnClose;
		}

		public void setCancelRemainingTasksOnClose(boolean cancelRemainingTasksOnClose) {
			this.cancelRemainingTasksOnClose = cancelRemainingTasksOnClose;
		}

		public boolean isRejectTasksWhenLimitReached() {
			return this.rejectTasksWhenLimitReached;
		}

		public void setRejectTasksWhenLimitReached(boolean rejectTasksWhenLimitReached) {
			this.rejectTasksWhenLimitReached = rejectTasksWhenLimitReached;
		}

		public @Nullable Integer getConcurrencyLimit() {
			return this.concurrencyLimit;
		}

		public void setConcurrencyLimit(@Nullable Integer concurrencyLimit) {
			this.concurrencyLimit = concurrencyLimit;
		}

	}

	public static class Pool {

		/**
		 * Queue capacity. An unbounded capacity does not increase the pool and therefore
		 * ignores the "max-size" property. Doesn't have an effect if virtual threads are
		 * enabled.
		 * <p>队列容量。无界容量不会增加池，因此忽略 "max-size" 属性。如果启用了虚拟线程，则不起作用。</p>
		 */
		private int queueCapacity = Integer.MAX_VALUE;

		/**
		 * Core number of threads. Doesn't have an effect if virtual threads are enabled.
		 * <p>核心线程数。如果启用了虚拟线程，则不起作用。</p>
		 */
		private int coreSize = 8;

		/**
		 * Maximum allowed number of threads. If tasks are filling up the queue, the pool
		 * can expand up to that size to accommodate the load. Ignored if the queue is
		 * unbounded. Doesn't have an effect if virtual threads are enabled.
		 * <p>允许的最大线程数。如果任务填满了队列，池可以扩展到该大小以容纳负载。如果队列是无界的，则忽略。
		 * 如果启用了虚拟线程，则不起作用。</p>
		 */
		private int maxSize = Integer.MAX_VALUE;

		/**
		 * Whether core threads are allowed to time out. This enables dynamic growing and
		 * shrinking of the pool. Doesn't have an effect if virtual threads are enabled.
		 * <p>是否允许核心线程超时。这可以实现池的动态增长和收缩。如果启用了虚拟线程，则不起作用。</p>
		 */
		private boolean allowCoreThreadTimeout = true;

		/**
		 * Time limit for which threads may remain idle before being terminated. Doesn't
		 * have an effect if virtual threads are enabled.
		 * <p>线程在被终止之前可以保持空闲的时间限制。如果启用了虚拟线程，则不起作用。</p>
		 */
		private Duration keepAlive = Duration.ofSeconds(60);

		private final Shutdown shutdown = new Shutdown();

		public int getQueueCapacity() {
			return this.queueCapacity;
		}

		public void setQueueCapacity(int queueCapacity) {
			this.queueCapacity = queueCapacity;
		}

		public int getCoreSize() {
			return this.coreSize;
		}

		public void setCoreSize(int coreSize) {
			this.coreSize = coreSize;
		}

		public int getMaxSize() {
			return this.maxSize;
		}

		public void setMaxSize(int maxSize) {
			this.maxSize = maxSize;
		}

		public boolean isAllowCoreThreadTimeout() {
			return this.allowCoreThreadTimeout;
		}

		public void setAllowCoreThreadTimeout(boolean allowCoreThreadTimeout) {
			this.allowCoreThreadTimeout = allowCoreThreadTimeout;
		}

		public Duration getKeepAlive() {
			return this.keepAlive;
		}

		public void setKeepAlive(Duration keepAlive) {
			this.keepAlive = keepAlive;
		}

		public Shutdown getShutdown() {
			return this.shutdown;
		}

		public static class Shutdown {

			/**
			 * Whether to accept further tasks after the application context close phase
			 * has begun.
			 * <p>在应用程序上下文关闭阶段开始后是否接受更多任务。</p>
			 */
			private boolean acceptTasksAfterContextClose;

			public boolean isAcceptTasksAfterContextClose() {
				return this.acceptTasksAfterContextClose;
			}

			public void setAcceptTasksAfterContextClose(boolean acceptTasksAfterContextClose) {
				this.acceptTasksAfterContextClose = acceptTasksAfterContextClose;
			}

		}

	}

	public static class Shutdown {

		/**
		 * Whether the executor should wait for scheduled tasks to complete on shutdown.
		 * <p>执行器在关闭时是否应等待计划任务完成。</p>
		 */
		private boolean awaitTermination;

		/**
		 * Maximum time the executor should wait for remaining tasks to complete.
		 * <p>执行器等待剩余任务完成的最长时间。</p>
		 */
		private @Nullable Duration awaitTerminationPeriod;

		public boolean isAwaitTermination() {
			return this.awaitTermination;
		}

		public void setAwaitTermination(boolean awaitTermination) {
			this.awaitTermination = awaitTermination;
		}

		public @Nullable Duration getAwaitTerminationPeriod() {
			return this.awaitTerminationPeriod;
		}

		public void setAwaitTerminationPeriod(@Nullable Duration awaitTerminationPeriod) {
			this.awaitTerminationPeriod = awaitTerminationPeriod;
		}

	}

	/**
	 * Determine when the task executor is to be created.
	 * <p>确定何时创建任务执行器。</p>
	 *
	 * @since 3.5.0
	 */
	public enum Mode {

		/**
		 * Create the task executor if no user-defined executor is present.
		 * <p>如果不存在用户定义的执行器，则创建任务执行器。</p>
		 */
		AUTO,

		/**
		 * Create the task executor even if a user-defined executor is present.
		 * <p>即使存在用户定义的执行器，也创建任务执行器。</p>
		 */
		FORCE

	}

}