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

package org.springframework.boot.autoconfigure.context;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.util.List;
import java.util.Properties;

import org.jspecify.annotations.Nullable;

import org.springframework.context.support.AbstractResourceBasedMessageSource;
import org.springframework.core.CollectionFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/**
 * Configure an {@link AbstractResourceBasedMessageSource} with sensible defaults tuned
 * using configuration properties.
 *
 * <p>使用通过配置属性调整的合理默认值来配置 {@link AbstractResourceBasedMessageSource}。</p>
 *
 * <p>
 * Can be injected into application code and used to define a custom message source whose
 * configuration is based upon that produced by auto-configuration.
 *
 * <p>可以注入到应用程序代码中，并用于定义自定义消息源，其配置基于自动配置所产生的配置。</p>
 *
 * @author Henrique (henriquejsza)
 * @since 4.2.0
 */
public class ResourceBasedMessageSourceConfigurer {

	private final MessageSourceProperties properties;

	/**
	 * Creates a new configurer that will use the given {@code properties}.
	 *
	 * <p>创建一个将使用给定 {@code properties} 的新配置器。</p>
	 *
	 * @param properties properties to use
	 *
	 * <p>要使用的属性</p>
	 */
	public ResourceBasedMessageSourceConfigurer(MessageSourceProperties properties) {
		Assert.notNull(properties, "'properties' must not be null");
		this.properties = properties;
	}

	/**
	 * Configure the specified message source. The message source can be further tuned and
	 * default settings can be overridden.
	 *
	 * <p>配置指定的消息源。消息源可以进一步调整，并且可以覆盖默认设置。</p>
	 *
	 * @param messageSource the {@link AbstractResourceBasedMessageSource} instance to
	 * configure
	 *                      <p>要配置的 {@link AbstractResourceBasedMessageSource} 实例</p>
	 */
	public void configure(AbstractResourceBasedMessageSource messageSource) {
		Assert.notNull(messageSource, "'messageSource' must not be null");
		if (!CollectionUtils.isEmpty(this.properties.getBasename())) {
			messageSource.setBasenames(this.properties.getBasename().toArray(new String[0]));
		}
		if (this.properties.getEncoding() != null) {
			messageSource.setDefaultEncoding(this.properties.getEncoding().name());
		}
		messageSource.setFallbackToSystemLocale(this.properties.isFallbackToSystemLocale());
		Duration cacheDuration = this.properties.getCacheDuration();
		if (cacheDuration != null) {
			messageSource.setCacheMillis(cacheDuration.toMillis());
		}
		messageSource.setAlwaysUseMessageFormat(this.properties.isAlwaysUseMessageFormat());
		messageSource.setUseCodeAsDefaultMessage(this.properties.isUseCodeAsDefaultMessage());
		messageSource.setCommonMessages(loadCommonMessages(this.properties.getCommonMessages()));
	}

	private @Nullable Properties loadCommonMessages(@Nullable List<Resource> resources) {
		if (CollectionUtils.isEmpty(resources)) {
			return null;
		}
		Properties properties = CollectionFactory.createSortedProperties(false);
		for (Resource resource : resources) {
			try {
				PropertiesLoaderUtils.fillProperties(properties, resource);
			}
			catch (IOException ex) {
				throw new UncheckedIOException("Failed to load common messages from '%s'".formatted(resource), ex);
			}
		}
		return properties;
	}

}