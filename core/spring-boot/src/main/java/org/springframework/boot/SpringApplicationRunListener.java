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

package org.springframework.boot;

import java.time.Duration;

import org.jspecify.annotations.Nullable;

import org.springframework.boot.bootstrap.ConfigurableBootstrapContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.support.SpringFactoriesLoader;

/**
 * Listener for the {@link SpringApplication} {@code run} method.
 * {@link SpringApplicationRunListener}s are loaded through the
 * {@link SpringFactoriesLoader} and should declare a public constructor that accepts a
 * {@link SpringApplication} instance and a {@code String[]} of arguments. A new
 * {@link SpringApplicationRunListener} instance will be created for each run.
 * <p>用于 {@link SpringApplication} 的 {@code run} 方法的监听器。{@link SpringApplicationRunListener} 通过 {@link SpringFactoriesLoader} 加载，并且应声明一个公共构造函数，该构造函数接受一个 {@link SpringApplication} 实例和一个 {@code String[]} 参数。每次运行都会创建一个新的 {@link SpringApplicationRunListener} 实例。</p>
 *
 * @author Phillip Webb
 * @author Dave Syer
 * @author Andy Wilkinson
 * @author Chris Bono
 * @since 1.0.0
 */
public interface SpringApplicationRunListener {

	/**
	 * Called immediately when the run method has first started. Can be used for very
	 * early initialization.
	 * <p>当 run 方法首次启动时立即调用。可用于非常早期的初始化。</p>
	 * @param bootstrapContext the bootstrap context
	 * <p>引导上下文</p>
	 */
	default void starting(ConfigurableBootstrapContext bootstrapContext) {
	}

	/**
	 * Called once the environment has been prepared, but before the
	 * {@link ApplicationContext} has been created.
	 * <p>在环境准备好之后、{@link ApplicationContext} 创建之前调用。</p>
	 * @param bootstrapContext the bootstrap context
	 * <p>引导上下文</p>
	 * @param environment the environment
	 * <p>环境</p>
	 */
	default void environmentPrepared(ConfigurableBootstrapContext bootstrapContext,
			ConfigurableEnvironment environment) {
	}

	/**
	 * Called once the {@link ApplicationContext} has been created and prepared, but
	 * before sources have been loaded.
	 * <p>在 {@link ApplicationContext} 创建并准备好之后、加载源之前调用。</p>
	 * @param context the application context
	 * <p>应用程序上下文</p>
	 */
	default void contextPrepared(ConfigurableApplicationContext context) {
	}

	/**
	 * Called once the application context has been loaded but before it has been
	 * refreshed.
	 * <p>在应用程序上下文加载之后、刷新之前调用。</p>
	 * @param context the application context
	 * <p>应用程序上下文</p>
	 */
	default void contextLoaded(ConfigurableApplicationContext context) {
	}

	/**
	 * The context has been refreshed and the application has started but
	 * {@link CommandLineRunner CommandLineRunners} and {@link ApplicationRunner
	 * ApplicationRunners} have not been called.
	 * <p>上下文已刷新，应用程序已启动，但尚未调用 {@link CommandLineRunner CommandLineRunners} 和 {@link ApplicationRunner ApplicationRunners}。</p>
	 * @param context the application context.
	 * <p>应用程序上下文。</p>
	 * @param timeTaken the time taken to start the application or {@code null} if unknown
	 * <p>启动应用程序所花费的时间，如果未知则为 {@code null}</p>
	 * @since 2.6.0
	 */
	default void started(ConfigurableApplicationContext context, @Nullable Duration timeTaken) {
	}

	/**
	 * Called immediately before the run method finishes, when the application context has
	 * been refreshed and all {@link CommandLineRunner CommandLineRunners} and
	 * {@link ApplicationRunner ApplicationRunners} have been called.
	 * <p>在 run 方法完成之前立即调用，此时应用程序上下文已刷新，并且所有 {@link CommandLineRunner CommandLineRunners} 和 {@link ApplicationRunner ApplicationRunners} 都已调用。</p>
	 * @param context the application context.
	 * <p>应用程序上下文。</p>
	 * @param timeTaken the time taken for the application to be ready or {@code null} if
	 * unknown
	 * <p>应用程序准备好所花费的时间，如果未知则为 {@code null}</p>
	 * @since 2.6.0
	 */
	default void ready(ConfigurableApplicationContext context, @Nullable Duration timeTaken) {
	}

	/**
	 * Called when a failure occurs when running the application.
	 * <p>在运行应用程序时发生失败时调用。</p>
	 * @param context the application context or {@code null} if a failure occurred before
	 * the context was created
	 * <p>应用程序上下文，如果在创建上下文之前发生失败，则为 {@code null}</p>
	 * @param exception the failure
	 * <p>失败异常</p>
	 * @since 2.0.0
	 */
	default void failed(@Nullable ConfigurableApplicationContext context, Throwable exception) {
	}

}