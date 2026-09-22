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

package org.springframework.boot.autoconfigure.ssl;

import java.nio.file.Path;

import org.jspecify.annotations.Nullable;

import org.springframework.boot.ssl.pem.PemContent;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Helper utility to manage a single bundle content configuration property. May possibly
 * contain PEM content, a location or a directory search pattern.
 * <p>辅助工具，用于管理单个捆绑内容配置属性。可能包含 PEM 内容、位置或目录搜索模式。</p>
 *
 * @param name the configuration property name (excluding any prefix)
 * <p>配置属性名称（不包括任何前缀）</p>
 * @param value the configuration property value
 * <p>配置属性值</p>
 * @author Phillip Webb
 * @author Moritz Halbritter
 */
record BundleContentProperty(String name, @Nullable String value) {

	/**
	 * Return if the property value is PEM content.
	 * <p>返回属性值是否为 PEM 内容。</p>
	 *
	 * @return if the value is PEM content
	 * <p>如果值是 PEM 内容则返回 true</p>
	 */
	boolean isPemContent() {
		return PemContent.isPresentInText(this.value);
	}

	/**
	 * Return if there is any property value present.
	 * <p>返回是否存在任何属性值。</p>
	 *
	 * @return if the value is present
	 * <p>如果值存在则返回 true</p>
	 */
	boolean hasValue() {
		return StringUtils.hasText(this.value);
	}

	Path toWatchPath(ResourceLoader resourceLoader) {
		try {
			Assert.state(!isPemContent(), "Value contains PEM content");
			Assert.state(this.value != null, "Value must not be null");
			Resource resource = resourceLoader.getResource(this.value);
			if (!resource.isFile()) {
				throw new BundleContentNotWatchableException(this);
			}
			return Path.of(resource.getFile().getAbsolutePath());
		}
		catch (Exception ex) {
			if (ex instanceof BundleContentNotWatchableException bundleContentNotWatchableException) {
				throw bundleContentNotWatchableException;
			}
			throw new IllegalStateException("Unable to convert value of property '%s' to a path".formatted(this.name),
					ex);
		}
	}

}