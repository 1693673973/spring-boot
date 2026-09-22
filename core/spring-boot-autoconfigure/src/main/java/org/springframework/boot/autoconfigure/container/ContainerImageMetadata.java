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

package org.springframework.boot.autoconfigure.container;

import org.jspecify.annotations.Nullable;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.core.AttributeAccessor;

/**
 * Metadata about a container image that can be added to an {@link AttributeAccessor}.
 * <p>可添加到 {@link AttributeAccessor} 的容器镜像元数据。</p>
 *
 * <p>Primarily designed to be attached to {@link BeanDefinition BeanDefinitions} created in
 * support of Testcontainers or Docker Compose.
 * <p>主要设计用于附加到为支持 Testcontainers 或 Docker Compose 而创建的 {@link BeanDefinition BeanDefinitions} 上。</p>
 *
 * @param imageName the container image name or {@code null} if the image name is not yet
 * known
 * <p>容器镜像名称，如果镜像名称尚未知，则为 {@code null}</p>
 *
 * @author Phillip Webb
 * @since 3.4.0
 */
public record ContainerImageMetadata(@Nullable String imageName) {

	static final String NAME = ContainerImageMetadata.class.getName();

	/**
	 * Add this container image metadata to the given attributes.
	 * <p>将此容器镜像元数据添加到给定的属性中。</p>
	 *
	 * @param attributes the attributes to add the metadata to
	 * <p>要添加元数据的属性</p>
	 */
	public void addTo(@Nullable AttributeAccessor attributes) {
		if (attributes != null) {
			attributes.setAttribute(NAME, this);
		}
	}

	/**
	 * Return {@code true} if {@link ContainerImageMetadata} has been added to the given
	 * attributes.
	 * <p>如果 {@link ContainerImageMetadata} 已添加到给定属性中，则返回 {@code true}。</p>
	 *
	 * @param attributes the attributes to check
	 * <p>要检查的属性</p>
	 * @return if metadata is present
	 * <p>元数据是否存在</p>
	 */
	public static boolean isPresent(@Nullable AttributeAccessor attributes) {
		return getFrom(attributes) != null;
	}

	/**
	 * Return {@link ContainerImageMetadata} from the given attributes or {@code null} if
	 * no metadata has been added.
	 * <p>从给定属性中返回 {@link ContainerImageMetadata}，如果未添加元数据则返回 {@code null}。</p>
	 *
	 * @param attributes the attributes
	 * <p>属性</p>
	 * @return the metadata or {@code null}
	 * <p>元数据或 {@code null}</p>
	 */
	public static @Nullable ContainerImageMetadata getFrom(@Nullable AttributeAccessor attributes) {
		return (attributes != null) ? (ContainerImageMetadata) attributes.getAttribute(NAME) : null;
	}

}