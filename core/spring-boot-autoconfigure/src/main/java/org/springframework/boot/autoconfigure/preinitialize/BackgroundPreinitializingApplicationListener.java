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

package org.springframework.boot.autoconfigure.preinitialize;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.SpringApplicationEvent;
import org.springframework.boot.context.logging.LoggingApplicationListener;
import org.springframework.context.ApplicationListener;
import org.springframework.core.NativeDetector;
import org.springframework.core.Ordered;
import org.springframework.core.io.support.SpringFactoriesLoader;

/**
 * {@link ApplicationListener} to trigger early initialization in a background thread of
 * time-consuming tasks.
 * <p>{@link ApplicationListener}，用于在后台线程中触发耗时任务的早期初始化。</p>
 *
 * <p>
 * Set the {@link #IGNORE_BACKGROUNDPREINITIALIZER_PROPERTY_NAME} system property to
 * {@code true} to disable this mechanism.
 * <p>将 {@link #IGNORE_BACKGROUNDPREINITIALIZER_PROPERTY_NAME} 系统属性设置为 {@code true} 以禁用此机制。</p>
 *
 * @author Phillip Webb
 * @author Andy Wilkinson
 * @author Artsiom Yudovin
 * @author Sebastien Deleuze
 * @see BackgroundPreinitializer
 */
class BackgroundPreinitializingApplicationListener implements ApplicationListener<SpringApplicationEvent>, Ordered {

	/**
	 * System property that instructs Spring Boot how to run pre initialization. When the
	 * property is set to {@code true}, no pre-initialization happens and each item is
	 * initialized in the foreground as it needs to. When the property is {@code false}
	 * (default), pre initialization runs in a separate thread in the background.
	 * <p>指示 Spring Boot 如何运行预初始化的系统属性。当该属性设置为 {@code true} 时，不会发生预初始化，
	 * 每个项在需要时在前台初始化。当该属性为 {@code false}（默认值）时，预初始化在后台的单独线程中运行。</p>
	 */
	public static final String IGNORE_BACKGROUNDPREINITIALIZER_PROPERTY_NAME = "spring.backgroundpreinitializer.ignore";

	private static final AtomicBoolean started = new AtomicBoolean();

	private static final CountDownLatch complete = new CountDownLatch(1);

	private final SpringFactoriesLoader factoriesLoader;

	private final boolean enabled;

	BackgroundPreinitializingApplicationListener() {
		this(SpringFactoriesLoader.forDefaultResourceLocation());
	}

	BackgroundPreinitializingApplicationListener(SpringFactoriesLoader factoriesLoader) {
		this.factoriesLoader = factoriesLoader;
		this.enabled = !NativeDetector.inNativeImage()
				&& !Boolean.getBoolean(IGNORE_BACKGROUNDPREINITIALIZER_PROPERTY_NAME)
				&& Runtime.getRuntime().availableProcessors() > 1;
	}

	@Override
	public int getOrder() {
		return LoggingApplicationListener.DEFAULT_ORDER + 1;
	}

	@Override
	public void onApplicationEvent(SpringApplicationEvent event) {
		if (!this.enabled) {
			return;
		}
		if (event instanceof ApplicationEnvironmentPreparedEvent && started.compareAndSet(false, true)) {
			preinitialize();
		}
		if ((event instanceof ApplicationReadyEvent || event instanceof ApplicationFailedEvent) && started.get()) {
			try {
				complete.await();
			}
			catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}
	}

	private void preinitialize() {
		Runner runner = new Runner(this.factoriesLoader.load(BackgroundPreinitializer.class));
		try {
			Thread thread = new Thread(runner, "background-preinit");
			thread.start();
		}
		catch (Exception ex) {
			// This will fail on Google App Engine where creating threads is
			// prohibited. We can safely continue but startup will be slightly slower
			// as the initialization will now happen on the main thread.
			// 在 Google App Engine 上，由于禁止创建线程，此操作将失败。
			// 我们可以安全地继续，但启动会稍慢一些，
			// 因为初始化现在将在主线程上进行。
			complete.countDown();
		}
	}

	/**
	 * Runner thread to call the {@link BackgroundPreinitializer} instances.
	 * <p>用于调用 {@link BackgroundPreinitializer} 实例的运行器线程。</p>
	 *
	 * @param preinitializers the preinitializers
	 *                        <p>预初始化器</p>
	 */
	record Runner(List<BackgroundPreinitializer> preinitializers) implements Runnable {

		@Override
		public void run() {
			for (BackgroundPreinitializer preinitializer : this.preinitializers) {
				try {
					preinitializer.preinitialize();
				}
				catch (Throwable ex) {
				}
			}
			complete.countDown();
		}

	}

}