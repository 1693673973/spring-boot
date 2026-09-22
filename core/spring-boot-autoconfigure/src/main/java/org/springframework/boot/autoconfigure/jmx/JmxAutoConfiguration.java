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

package org.springframework.boot.autoconfigure.jmx;

import javax.management.MBeanServer;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.context.annotation.Primary;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.jmx.export.annotation.AnnotationJmxAttributeSource;
import org.springframework.jmx.export.annotation.AnnotationMBeanExporter;
import org.springframework.jmx.export.naming.ObjectNamingStrategy;
import org.springframework.jmx.support.MBeanServerFactoryBean;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * {@link EnableAutoConfiguration Auto-configuration} to enable/disable Spring's
 * {@link EnableMBeanExport @EnableMBeanExport} mechanism based on configuration
 * properties.
 * <p>{@link EnableAutoConfiguration 自动配置}，用于基于配置属性启用/禁用 Spring 的
 * {@link EnableMBeanExport @EnableMBeanExport} 机制。</p>
 *
 * <p>
 * To enable auto export of annotation beans set {@code spring.jmx.enabled: true}.
 * <p>要启用注解 bean 的自动导出，请设置 {@code spring.jmx.enabled: true}。</p>
 *
 * @author Christian Dupuis
 * @author Madhura Bhave
 * @author Artsiom Yudovin
 * @author Scott Frederick
 * @since 1.0.0
 */
@AutoConfiguration
@EnableConfigurationProperties(JmxProperties.class)
@ConditionalOnClass({ MBeanExporter.class })
@ConditionalOnBooleanProperty("spring.jmx.enabled")
public final class JmxAutoConfiguration {

	private final JmxProperties properties;

	JmxAutoConfiguration(JmxProperties properties) {
		this.properties = properties;
	}

	@Bean
	@Primary
	@ConditionalOnMissingBean(value = MBeanExporter.class, search = SearchStrategy.CURRENT)
	AnnotationMBeanExporter mbeanExporter(ObjectNamingStrategy namingStrategy, BeanFactory beanFactory) {
		AnnotationMBeanExporter exporter = new AnnotationMBeanExporter();
		exporter.setRegistrationPolicy(this.properties.getRegistrationPolicy());
		exporter.setNamingStrategy(namingStrategy);
		String serverBean = this.properties.getServer();
		if (StringUtils.hasLength(serverBean)) {
			exporter.setServer(beanFactory.getBean(serverBean, MBeanServer.class));
		}
		exporter.setEnsureUniqueRuntimeObjectNames(this.properties.isUniqueNames());
		return exporter;
	}

	@Bean
	@ConditionalOnMissingBean(value = ObjectNamingStrategy.class, search = SearchStrategy.CURRENT)
	ParentAwareNamingStrategy objectNamingStrategy() {
		ParentAwareNamingStrategy namingStrategy = new ParentAwareNamingStrategy(new AnnotationJmxAttributeSource());
		String defaultDomain = this.properties.getDefaultDomain();
		if (StringUtils.hasLength(defaultDomain)) {
			namingStrategy.setDefaultDomain(defaultDomain);
		}
		namingStrategy.setEnsureUniqueRuntimeObjectNames(this.properties.isUniqueNames());
		return namingStrategy;
	}

	@Bean
	@ConditionalOnMissingBean
	MBeanServer mbeanServer() {
		MBeanServerFactoryBean factory = new MBeanServerFactoryBean();
		factory.setLocateExistingServerIfPossible(true);
		factory.afterPropertiesSet();
		MBeanServer mBeanServer = factory.getObject();
		Assert.state(mBeanServer != null, "'mBeanServer' must not be null");
		return mBeanServer;
	}

}