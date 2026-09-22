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

package org.springframework.boot.autoconfigure;

import java.util.Set;

import org.jspecify.annotations.Nullable;

import org.springframework.lang.Contract;

/**
 * Provides access to meta-data written by the auto-configure annotation processor.
 * <p>提供对由自动配置注解处理器写入的元数据的访问。</p>
 *
 * @author Phillip Webb
 * @since 1.5.0
 */
public interface AutoConfigurationMetadata {

	/**
	 * Return {@code true} if the specified class name was processed by the annotation
	 * processor.
	 * <p>如果指定的类名由注解处理器处理过，则返回 {@code true}。</p>
	 *
	 * @param className the source class
	 *                  <p>源类</p>
	 * @return if the class was processed
	 * <p>该类是否已被处理</p>
	 */
	boolean wasProcessed(String className);

	/**
	 * Get an {@link Integer} value from the meta-data.
	 * <p>从元数据中获取一个 {@link Integer} 值。</p>
	 *
	 * @param className the source class
	 *                  <p>源类</p>
	 * @param key the meta-data key
	 *            <p>元数据键</p>
	 * @return the meta-data value or {@code null}
	 * <p>元数据值或 {@code null}</p>
	 */
	@Nullable Integer getInteger(String className, String key);

	/**
	 * Get an {@link Integer} value from the meta-data.
	 * <p>从元数据中获取一个 {@link Integer} 值。</p>
	 *
	 * @param className the source class
	 *                  <p>源类</p>
	 * @param key the meta-data key
	 *            <p>元数据键</p>
	 * @param defaultValue the default value
	 *                     <p>默认值</p>
	 * @return the meta-data value or {@code defaultValue}
	 * <p>元数据值或 {@code defaultValue}</p>
	 */
	@Contract("_, _, !null -> !null")
	@Nullable Integer getInteger(String className, String key, @Nullable Integer defaultValue);

	/**
	 * Get a {@link Set} value from the meta-data.
	 * <p>从元数据中获取一个 {@link Set} 值。</p>
	 *
	 * @param className the source class
	 *                  <p>源类</p>
	 * @param key the meta-data key
	 *            <p>元数据键</p>
	 * @return the meta-data value or {@code null}
	 * <p>元数据值或 {@code null}</p>
	 */
	@Nullable Set<String> getSet(String className, String key);

	/**
	 * Get a {@link Set} value from the meta-data.
	 * <p>从元数据中获取一个 {@link Set} 值。</p>
	 *
	 * @param className the source class
	 *                  <p>源类</p>
	 * @param key the meta-data key
	 *            <p>元数据键</p>
	 * @param defaultValue the default value
	 *                     <p>默认值</p>
	 * @return the meta-data value or {@code defaultValue}
	 * <p>元数据值或 {@code defaultValue}</p>
	 */
	@Contract("_, _, !null -> !null")
	@Nullable Set<String> getSet(String className, String key, @Nullable Set<String> defaultValue);

	/**
	 * Get an {@link String} value from the meta-data.
	 * <p>从元数据中获取一个 {@link String} 值。</p>
	 *
	 * @param className the source class
	 *                  <p>源类</p>
	 * @param key the meta-data key
	 *            <p>元数据键</p>
	 * @return the meta-data value or {@code null}
	 * <p>元数据值或 {@code null}</p>
	 */
	@Nullable String get(String className, String key);

	/**
	 * Get an {@link String} value from the meta-data.
	 * <p>从元数据中获取一个 {@link String} 值。</p>
	 *
	 * @param className the source class
	 *                  <p>源类</p>
	 * @param key the meta-data key
	 *            <p>元数据键</p>
	 * @param defaultValue the default value
	 *                     <p>默认值</p>
	 * @return the meta-data value or {@code defaultValue}
	 * <p>元数据值或 {@code defaultValue}</p>
	 */
	@Contract("_, _, !null -> !null")
	@Nullable String get(String className, String key, @Nullable String defaultValue);

}