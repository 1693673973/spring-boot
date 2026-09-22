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

import org.jspecify.annotations.Nullable;

/**
 * A factory to create {@link ConnectionDetails} from a given {@code source}.
 * Implementations should be registered in {@code META-INF/spring.factories}.
 *
 * <p>一个工厂，用于从给定的 {@code source} 创建 {@link ConnectionDetails}。实现应注册在 {@code META-INF/spring.factories} 中。</p>
 *
 * @param <S> the source type accepted by the factory. Implementations are expected to
 * provide a valid {@code toString}.
 * <p>工厂接受的源类型。实现应提供有效的 {@code toString}。</p>
 *
 * @param <D> the type of {@link ConnectionDetails} produced by the factory
 * <p>工厂产生的 {@link ConnectionDetails} 的类型。</p>
 *
 * @author Moritz Halbritter
 * @author Andy Wilkinson
 * @author Phillip Webb
 * @since 3.1.0
 */
public interface ConnectionDetailsFactory<S, D extends ConnectionDetails> {

	/**
	 * Get the {@link ConnectionDetails} from the given {@code source}. May return
	 * {@code null} if no details can be created.
	 * <p>从给定的 {@code source} 获取 {@link ConnectionDetails}。如果无法创建详细信息，则可能返回 {@code null}。</p>
	 *
	 * @param source the source
	 * <p>源</p>
	 *
	 * @return the connection details or {@code null}
	 * <p>连接详细信息或 {@code null}</p>
	 */
	@Nullable D getConnectionDetails(S source);

}