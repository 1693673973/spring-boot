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
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

/**
 * A simple object registry that is available during startup and {@link Environment}
 * post-processing up to the point that the {@link ApplicationContext} is prepared.
 * <p>一个简单的对象注册表，在启动期间和 {@link Environment} 后处理期间可用，直到 {@link ApplicationContext} 准备好为止。</p>
 *
 * <p>
 * Can be used to register instances that may be expensive to create, or need to be shared
 * before the {@link ApplicationContext} is available.
 * <p>可用于注册可能创建成本高昂或需要在 {@link ApplicationContext} 可用之前共享的实例。</p>
 *
 * <p>
 * The registry uses {@link Class} as a key, meaning that only a single instance of a
 * given type can be stored.
 * <p>注册表使用 {@link Class} 作为键，这意味着只能存储给定类型的单个实例。</p>
 *
 * <p>
 * The {@link #addCloseListener(ApplicationListener)} method can be used to add a listener
 * that can perform actions when {@link BootstrapContext} has been closed and the
 * {@link ApplicationContext} is fully prepared. For example, an instance may choose to
 * register itself as a regular Spring bean so that it is available for the application to
 * use.
 * <p>{@link #addCloseListener(ApplicationListener)} 方法可用于添加一个监听器，当 {@link BootstrapContext} 关闭且 {@link ApplicationContext} 完全准备好时，该监听器可以执行操作。例如，实例可以选择将自身注册为常规 Spring bean，以便应用程序可以使用它。</p>
 *
 * @author Phillip Webb
 * @since 4.0.0
 * @see BootstrapContext
 * @see ConfigurableBootstrapContext
 */
public interface BootstrapRegistry {

	/**
	 * Register a specific type with the registry. If the specified type has already been
	 * registered and has not been obtained as a {@link Scope#SINGLETON singleton}, it
	 * will be replaced.
	 * <p>向注册表注册特定类型。如果指定的类型已经注册且尚未作为 {@link Scope#SINGLETON 单例} 获取，则它将被替换。</p>
	 * @param <T> the instance type
	 * <p>实例类型</p>
	 * @param type the instance type
	 * <p>实例类型</p>
	 * @param instanceSupplier the instance supplier
	 * <p>实例供应器</p>
	 */
	<T> void register(Class<T> type, InstanceSupplier<T> instanceSupplier);

	/**
	 * Register a specific type with the registry if one is not already present.
	 * <p>如果尚不存在，则向注册表注册特定类型。</p>
	 * @param <T> the instance type
	 * <p>实例类型</p>
	 * @param type the instance type
	 * <p>实例类型</p>
	 * @param instanceSupplier the instance supplier
	 * <p>实例供应器</p>
	 */
	<T> void registerIfAbsent(Class<T> type, InstanceSupplier<T> instanceSupplier);

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

	/**
	 * Return any existing {@link InstanceSupplier} for the given type.
	 * <p>返回给定类型的任何现有 {@link InstanceSupplier}。</p>
	 * @param <T> the instance type
	 * <p>实例类型</p>
	 * @param type the instance type
	 * <p>实例类型</p>
	 * @return the registered {@link InstanceSupplier} or {@code null}
	 * <p>已注册的 {@link InstanceSupplier} 或 {@code null}</p>
	 */
	<T> @Nullable InstanceSupplier<T> getRegisteredInstanceSupplier(Class<T> type);

	/**
	 * Add an {@link ApplicationListener} that will be called with a
	 * {@link BootstrapContextClosedEvent} when the {@link BootstrapContext} is closed and
	 * the {@link ApplicationContext} has been prepared.
	 * <p>添加一个 {@link ApplicationListener}，当 {@link BootstrapContext} 关闭且 {@link ApplicationContext} 已准备好时，将使用 {@link BootstrapContextClosedEvent} 调用它。</p>
	 * @param listener the listener to add
	 * <p>要添加的监听器</p>
	 */
	void addCloseListener(ApplicationListener<BootstrapContextClosedEvent> listener);

	/**
	 * Supplier used to provide the actual instance when needed.
	 * <p>用于在需要时提供实际实例的供应器。</p>
	 *
	 * @param <T> the instance type
	 * <p>实例类型</p>
	 * @see Scope
	 */
	@FunctionalInterface
	interface InstanceSupplier<T> {

		/**
		 * Factory method used to create the instance when needed.
		 * <p>用于在需要时创建实例的工厂方法。</p>
		 * @param context the {@link BootstrapContext} which may be used to obtain other
		 * bootstrap instances.
		 * <p>可用于获取其他引导实例的 {@link BootstrapContext}。</p>
		 * @return the instance or {@code null}
		 * <p>实例或 {@code null}</p>
		 */
		@Nullable T get(BootstrapContext context);

		/**
		 * Return the scope of the supplied instance.
		 * <p>返回所提供实例的作用域。</p>
		 * @return the scope
		 * <p>作用域</p>
		 */
		default Scope getScope() {
			return Scope.SINGLETON;
		}

		/**
		 * Return a new {@link InstanceSupplier} with an updated {@link Scope}.
		 * <p>返回一个带有更新后的 {@link Scope} 的新 {@link InstanceSupplier}。</p>
		 * @param scope the new scope
		 * <p>新作用域</p>
		 * @return a new {@link InstanceSupplier} instance with the new scope
		 * <p>带有新作用域的新 {@link InstanceSupplier} 实例</p>
		 */
		default InstanceSupplier<T> withScope(Scope scope) {
			Assert.notNull(scope, "'scope' must not be null");
			InstanceSupplier<T> parent = this;
			return new InstanceSupplier<>() {

				@Override
				public @Nullable T get(BootstrapContext context) {
					return parent.get(context);
				}

				@Override
				public Scope getScope() {
					return scope;
				}

			};
		}

		/**
		 * Factory method that can be used to create an {@link InstanceSupplier} for a
		 * given instance.
		 * <p>可用于为给定实例创建 {@link InstanceSupplier} 的工厂方法。</p>
		 * @param <T> the instance type
		 * <p>实例类型</p>
		 * @param instance the instance
		 * <p>实例</p>
		 * @return a new {@link InstanceSupplier}
		 * <p>新的 {@link InstanceSupplier}</p>
		 */
		static <T> InstanceSupplier<T> of(@Nullable T instance) {
			return (registry) -> instance;
		}

		/**
		 * Factory method that can be used to create an {@link InstanceSupplier} from a
		 * {@link Supplier}.
		 * <p>可用于从 {@link Supplier} 创建 {@link InstanceSupplier} 的工厂方法。</p>
		 * @param <T> the instance type
		 * <p>实例类型</p>
		 * @param supplier the supplier that will provide the instance
		 * <p>将提供实例的供应器</p>
		 * @return a new {@link InstanceSupplier}
		 * <p>新的 {@link InstanceSupplier}</p>
		 */
		static <T> InstanceSupplier<T> from(@Nullable Supplier<T> supplier) {
			return (registry) -> (supplier != null) ? supplier.get() : null;
		}

	}

	/**
	 * The scope of an instance.
	 * <p>实例的作用域。</p>
	 */
	enum Scope {

		/**
		 * A singleton instance. The {@link InstanceSupplier} will be called only once and
		 * the same instance will be returned each time.
		 * <p>单例实例。{@link InstanceSupplier} 将仅被调用一次，并且每次都将返回同一个实例。</p>
		 */
		SINGLETON,

		/**
		 * A prototype instance. The {@link InstanceSupplier} will be called whenever an
		 * instance is needed.
		 * <p>原型实例。每当需要实例时，都会调用 {@link InstanceSupplier}。</p>
		 */
		PROTOTYPE

	}

}