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

package org.springframework.boot.autoconfigure.template;

import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.Assert;

/**
 * Contains a location that templates can be loaded from.
 * <p>包含模板可以从中加载的位置。</p>
 *
 * @author Phillip Webb
 * @since 1.2.1
 */
public class TemplateLocation {

	private final String path;

	public TemplateLocation(String path) {
		Assert.notNull(path, "'path' must not be null");
		this.path = path;
	}

	/**
	 * Determine if this template location exists using the specified
	 * {@link ResourcePatternResolver}.
	 * <p>确定此模板位置是否使用指定的 {@link ResourcePatternResolver} 存在。</p>
	 * @param resolver the resolver used to test if the location exists
	 * <p>用于测试位置是否存在的解析器</p>
	 * @return {@code true} if the location exists.
	 * <p>如果位置存在，则为 {@code true}。</p>
	 */
	public boolean exists(ResourcePatternResolver resolver) {
		Assert.notNull(resolver, "'resolver' must not be null");
		if (resolver.getResource(this.path).exists()) {
			return true;
		}
		try {
			return anyExists(resolver);
		}
		catch (IOException ex) {
			return false;
		}
	}

	private boolean anyExists(ResourcePatternResolver resolver) throws IOException {
		String searchPath = this.path;
		if (searchPath.startsWith(ResourceLoader.CLASSPATH_URL_PREFIX)) {
			searchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
					+ searchPath.substring(ResourceLoader.CLASSPATH_URL_PREFIX.length());
		}
		if (searchPath.startsWith(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX)) {
			Resource[] resources = resolver.getResources(searchPath);
			for (Resource resource : resources) {
				if (resource.exists()) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return this.path;
	}

}