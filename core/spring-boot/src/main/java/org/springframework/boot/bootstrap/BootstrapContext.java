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

package org.springframework.boot.bootstrap;

import java.util.function.Supplier;

import org.jspecify.annotations.Nullable;

import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

/**
 * A simple bootstrap context that is available during startup and {@link Environment}
 * post-processing up to the point that the {@link ApplicationContext} is prepared.
 * <p>一个简单的引导上下文，在启动期间和 {@link Environment} 后处理期间可用，直到 {@link ApplicationContext} 准备好为止。</p>
 *
 * <p>
 * Provides lazy access to singletons that may be expensive to create, or need to be
 * shared before the {@link ApplicationContext} is available.
 * <p>提供对单例的延迟访问，这些单例可能创建成本高昂，或者需要在 {@link ApplicationContext} 可用之前共享。</p>
 *
 * <p>
 * Instances are registered by type. The contact may return {@code null} values when a
 * type has been registered but no value is actually supplied.
 * <p>实例按类型注册。当类型已注册但实际未提供值时，上下文可能返回 {@code null} 值。</p>
 *
 * @author Phillip Webb
 * @since 4.0.0
 * @since 2.4.0
 * @see BootstrapRegistry
 */
public interface BootstrapContext {

	/**
	 * Return an instance from the context if the type has been registered. The instance
	 * will be created if it hasn't been accessed previously.
	 * <p>如果类型已注册，则从上下文返回实例。如果之前未访问过，则将创建该实例。</p>
	 * @param <T> the instance type
	 * <p>实例类型</p>
	 * @param type the instance type
	 * <p>实例类型</p>
	 * @return the instance managed by the context, which may be {@code null}
	 * <p>由上下文管理的实例，可能为 {@code null}</p>
	 * @throws IllegalStateException if the type has not been registered
	 * <p>如果该类型尚未注册，则抛出 IllegalStateException</p>
	 */
	<T> @Nullable T get(Class<T> type) throws IllegalStateException;

	/**
	 * Return an instance from the context if the type has been registered. The instance
	 * will be created if it hasn't been accessed previously.
	 * <p>如果类型已注册，则从上下文返回实例。如果之前未访问过，则将创建该实例。</p>
	 * @param <T> the instance type
	 * <p>实例类型</p>
	 * @param type the instance type
	 * <p>实例类型</p>
	 * @param other the instance to use if the type has not been registered
	 * <p>如果类型尚未注册，则使用的实例</p>
	 * @return the instance, which may be {@code null}
	 * <p>实例，可能为 {@code null}</p>
	 */
	<T> @Nullable T getOrElse(Class<T> type, @Nullable T other);

	/**
	 * Return an instance from the context if the type has been registered. The instance
	 * will be created if it hasn't been accessed previously.
	 * <p>如果类型已注册，则从上下文返回实例。如果之前未访问过，则将创建该实例。</p>
	 * @param <T> the instance type
	 * <p>实例类型</p>
	 * @param type the instance type
	 * <p>实例类型</p>
	 * @param other a supplier for the instance to use if the type has not been registered
	 * <p>如果类型尚未注册，则用于提供要使用的实例的供应器</p>
	 * @return the instance, which may be {@code null}
	 * <p>实例，可能为 {@code null}</p>
	 */
	<T> @Nullable T getOrElseSupply(Class<T> type, Supplier<@Nullable T> other);

	/**
	 * Return an instance from the context if the type has been registered. The instance
	 * will be created if it hasn't been accessed previously.
	 * <p>如果类型已注册，则从上下文返回实例。如果之前未访问过，则将创建该实例。</p>
	 * @param <T> the instance type
	 * <p>实例类型</p>
	 * @param <X> the exception to throw if the type is not registered
	 * <p>如果类型未注册则抛出的异常</p>
	 * @param type the instance type
	 * <p>实例类型</p>
	 * @param exceptionSupplier the supplier which will return the exception to be thrown
	 * <p>将返回要抛出的异常的供应器</p>
	 * @return the instance managed by the context, which may be {@code null}
	 * <p>由上下文管理的实例，可能为 {@code null}</p>
	 * @throws X if the type has not been registered
	 * <p>如果该类型尚未注册，则抛出 X</p>
	 */
	<T, X extends Throwable> @Nullable T getOrElseThrow(Class<T> type, Supplier<? extends X> exceptionSupplier)
			throws X;

	/**
	 * Return if a registration exists for the given type.
	 * <p>返回给定类型是否存在注册。</p>
	 * @param <T> the instance type
	 * <p>实例类型</p>
	 * @param type the instance type
	 * <p>实例类型</p>
	 * @return {@code true} if the type has already been registered
	 * <p>如果该类型已注册，则为 {@code true}</p>
	 */
	<T> boolean isRegistered(Class<T> type);

}