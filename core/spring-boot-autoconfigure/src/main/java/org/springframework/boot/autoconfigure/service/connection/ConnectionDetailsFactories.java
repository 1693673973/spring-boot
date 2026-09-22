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

package org.springframework.boot.autoconfigure.service.connection;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jspecify.annotations.Nullable;

import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.core.io.support.SpringFactoriesLoader.FailureHandler;
import org.springframework.util.Assert;

/**
 * A registry of {@link ConnectionDetailsFactory} instances.
 * <p>一个 {@link ConnectionDetailsFactory} 实例的注册表。</p>
 *
 * @author Moritz Halbritter
 * @author Andy Wilkinson
 * @author Phillip Webb
 * @author Pedro Xavier Leite Cavadas
 * @since 3.1.0
 */
public class ConnectionDetailsFactories {

	private static final Log logger = LogFactory.getLog(ConnectionDetailsFactories.class);

	private final List<Registration<?, ?>> registrations = new ArrayList<>();

	/**
	 * Create a new {@link ConnectionDetailsFactories} instance.
	 * <p>创建一个新的 {@link ConnectionDetailsFactories} 实例。</p>
	 *
	 * @param classLoader the class loader used to load factories
	 *                    <p>用于加载工厂的类加载器</p>
	 * @since 3.5.0
	 */
	public ConnectionDetailsFactories(@Nullable ClassLoader classLoader) {
		this(SpringFactoriesLoader.forDefaultResourceLocation(classLoader));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	ConnectionDetailsFactories(SpringFactoriesLoader loader) {
		List<ConnectionDetailsFactory> factories = loader.load(ConnectionDetailsFactory.class,
				FailureHandler.logging(logger));
		Stream<Registration<?, ?>> registrations = factories.stream().map(Registration::get);
		registrations.filter(Objects::nonNull).forEach(this.registrations::add);
	}

	/**
	 * Return a {@link Map} of {@link ConnectionDetails} interface type to
	 * {@link ConnectionDetails} instance created from the factories associated with the
	 * given source.
	 * <p>返回一个 {@link Map}，将 {@link ConnectionDetails} 接口类型映射到从与给定源关联的工厂创建的
	 * {@link ConnectionDetails} 实例。</p>
	 *
	 * @param <S> the source type
	 *            <p>源类型</p>
	 * @param source the source
	 *               <p>源</p>
	 * @param required if a connection details result is required
	 *                 <p>是否需要连接详细信息结果</p>
	 * @return a map of {@link ConnectionDetails} instances
	 * <p>{@link ConnectionDetails} 实例的映射</p>
	 * @throws ConnectionDetailsFactoryNotFoundException if a result is required but no
	 * connection details factory is registered for the source
	 *                                                   <p>如果需要结果但没有为源注册连接详细信息工厂</p>
	 * @throws ConnectionDetailsNotFoundException if a result is required but no
	 * connection details instance was created from a registered factory
	 *                                            <p>如果需要结果但没有从注册的工厂创建连接详细信息实例</p>
	 */
	public <S> Map<Class<?>, ConnectionDetails> getConnectionDetails(S source, boolean required)
			throws ConnectionDetailsFactoryNotFoundException, ConnectionDetailsNotFoundException {
		List<Registration<S, ?>> registrations = getRegistrations(source, required);
		Map<Class<?>, ConnectionDetails> result = new LinkedHashMap<>();
		for (Registration<S, ?> registration : registrations) {
			ConnectionDetails connectionDetails = registration.factory().getConnectionDetails(source);
			if (connectionDetails != null) {
				Class<?> connectionDetailsType = registration.connectionDetailsType();
				ConnectionDetails previous = result.put(connectionDetailsType, connectionDetails);
				Assert.state(previous == null, () -> "Duplicate connection details supplied for %s"
						.formatted(connectionDetailsType.getName()));
			}
		}
		if (required && result.isEmpty()) {
			throw new ConnectionDetailsNotFoundException(source);
		}
		return Map.copyOf(result);
	}

	@SuppressWarnings("unchecked")
	<S> List<Registration<S, ?>> getRegistrations(S source, boolean required) {
		Class<S> sourceType = (Class<S>) source.getClass();
		List<Registration<S, ?>> result = new ArrayList<>();
		for (Registration<?, ?> candidate : this.registrations) {
			if (candidate.sourceType().isAssignableFrom(sourceType)) {
				result.add((Registration<S, ?>) candidate);
			}
		}
		if (required && result.isEmpty()) {
			throw new ConnectionDetailsFactoryNotFoundException(source);
		}
		result.sort(Comparator.comparing(Registration::factory, AnnotationAwareOrderComparator.INSTANCE));
		return List.copyOf(result);
	}

	/**
	 * A {@link ConnectionDetailsFactory} registration.
	 * <p>一个 {@link ConnectionDetailsFactory} 注册。</p>
	 *
	 * @param <S> the source type
	 *            <p>源类型</p>
	 * @param <D> the connection details type
	 *            <p>连接详细信息类型</p>
	 * @param sourceType the source type
	 *                   <p>源类型</p>
	 * @param connectionDetailsType the connection details type
	 *                              <p>连接详细信息类型</p>
	 * @param factory the factory
	 *                <p>工厂</p>
	 */
	record Registration<S, D extends ConnectionDetails>(Class<S> sourceType, Class<D> connectionDetailsType,
														ConnectionDetailsFactory<S, D> factory) {

		@SuppressWarnings("unchecked")
		private static <S, D extends ConnectionDetails> @Nullable Registration<S, D> get(
				ConnectionDetailsFactory<S, D> factory) {
			ResolvableType type = ResolvableType.forClass(ConnectionDetailsFactory.class, factory.getClass());
			@Nullable Class<?>[] generics = type.resolveGenerics();
			Class<S> sourceType = (Class<S>) generics[0];
			Class<D> connectionDetailsType = (Class<D>) generics[1];
			return (sourceType != null && connectionDetailsType != null)
					? new Registration<>(sourceType, connectionDetailsType, factory) : null;
		}

	}

}