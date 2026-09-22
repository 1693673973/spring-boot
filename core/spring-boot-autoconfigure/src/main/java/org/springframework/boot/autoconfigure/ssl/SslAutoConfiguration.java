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

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.io.ApplicationResourceLoader;
import org.springframework.boot.ssl.DefaultSslBundleRegistry;
import org.springframework.boot.ssl.SslBundleRegistry;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for SSL.
 * <p>用于 SSL 的 {@link EnableAutoConfiguration 自动配置}。</p>
 *
 * @author Scott Frederick
 * @since 3.1.0
 */
@AutoConfiguration
@EnableConfigurationProperties(SslProperties.class)
public final class SslAutoConfiguration {

	private final ResourceLoader resourceLoader;

	private final SslProperties sslProperties;

	SslAutoConfiguration(ResourceLoader resourceLoader, SslProperties sslProperties) {
		this.resourceLoader = ApplicationResourceLoader.get(resourceLoader, true);
		this.sslProperties = sslProperties;
	}

	@Bean
	FileWatcher fileWatcher() {
		return new FileWatcher(this.sslProperties.getBundle().getWatch().getFile().getQuietPeriod());
	}

	@Bean
	SslPropertiesBundleRegistrar sslPropertiesSslBundleRegistrar(FileWatcher fileWatcher) {
		return new SslPropertiesBundleRegistrar(this.sslProperties, fileWatcher, this.resourceLoader);
	}

	@Bean
	@ConditionalOnMissingBean({ SslBundleRegistry.class, SslBundles.class })
	DefaultSslBundleRegistry sslBundleRegistry(ObjectProvider<SslBundleRegistrar> sslBundleRegistrars) {
		DefaultSslBundleRegistry registry = new DefaultSslBundleRegistry();
		sslBundleRegistrars.orderedStream().forEach((registrar) -> registrar.registerBundles(registry));
		return registry;
	}

}