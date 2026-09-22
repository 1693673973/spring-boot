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

package org.springframework.boot.autoconfigure.data;

import java.lang.annotation.Annotation;

import org.jspecify.annotations.Nullable;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.repository.config.AnnotationRepositoryConfigurationSource;
import org.springframework.data.repository.config.BootstrapMode;
import org.springframework.data.repository.config.RepositoryConfigurationDelegate;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;
import org.springframework.data.util.Streamable;

/**
 * Base {@link ImportBeanDefinitionRegistrar} used to auto-configure Spring Data
 * Repositories.
 *
 * <p>用于自动配置 Spring Data 仓库的基础 {@link ImportBeanDefinitionRegistrar}。</p>
 *
 * @author Phillip Webb
 * @author Dave Syer
 * @author Oliver Gierke
 * @since 1.0.0
 */
public abstract class AbstractRepositoryConfigurationSourceSupport
		implements ImportBeanDefinitionRegistrar, BeanFactoryAware, ResourceLoaderAware, EnvironmentAware {

	@SuppressWarnings("NullAway.Init")
	private ResourceLoader resourceLoader;

	@SuppressWarnings("NullAway.Init")
	private BeanFactory beanFactory;

	@SuppressWarnings("NullAway.Init")
	private Environment environment;

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry,
			@Nullable BeanNameGenerator importBeanNameGenerator) {
		RepositoryConfigurationDelegate delegate = new RepositoryConfigurationDelegate(
				getConfigurationSource(registry, importBeanNameGenerator), this.resourceLoader, this.environment);
		delegate.registerRepositoriesIn(registry, getRepositoryConfigurationExtension());
	}

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		registerBeanDefinitions(importingClassMetadata, registry, null);
	}

	private AnnotationRepositoryConfigurationSource getConfigurationSource(BeanDefinitionRegistry registry,
			@Nullable BeanNameGenerator importBeanNameGenerator) {
		AnnotationMetadata metadata = AnnotationMetadata.introspect(getConfiguration());
		return new AutoConfiguredAnnotationRepositoryConfigurationSource(metadata, getAnnotation(), this.resourceLoader,
				this.environment, registry, importBeanNameGenerator) {
		};
	}

	protected Streamable<String> getBasePackages() {
		return Streamable.of(AutoConfigurationPackages.get(this.beanFactory));
	}

	/**
	 * The Spring Data annotation used to enable the particular repository support.
	 *
	 * <p>用于启用特定仓库支持的 Spring Data 注解。</p>
	 *
	 * @return the annotation class
	 *
	 * <p>注解类</p>
	 */
	protected abstract Class<? extends Annotation> getAnnotation();

	/**
	 * The configuration class that will be used by Spring Boot as a template.
	 *
	 * <p>Spring Boot 将用作模板的配置类。</p>
	 *
	 * @return the configuration class
	 *
	 * <p>配置类</p>
	 */
	protected abstract Class<?> getConfiguration();

	/**
	 * The {@link RepositoryConfigurationExtension} for the particular repository support.
	 *
	 * <p>用于特定仓库支持的 {@link RepositoryConfigurationExtension}。</p>
	 *
	 * @return the repository configuration extension
	 *
	 * <p>仓库配置扩展</p>
	 */
	protected abstract RepositoryConfigurationExtension getRepositoryConfigurationExtension();

	/**
	 * The {@link BootstrapMode} for the particular repository support. Defaults to
	 * {@link BootstrapMode#DEFAULT}.
	 *
	 * <p>用于特定仓库支持的 {@link BootstrapMode}。默认为 {@link BootstrapMode#DEFAULT}。</p>
	 *
	 * @return the bootstrap mode
	 *
	 * <p>引导模式</p>
	 */
	protected BootstrapMode getBootstrapMode() {
		return BootstrapMode.DEFAULT;
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	/**
	 * An auto-configured {@link AnnotationRepositoryConfigurationSource}.
	 *
	 * <p>一个自动配置的 {@link AnnotationRepositoryConfigurationSource}。</p>
	 */
	private class AutoConfiguredAnnotationRepositoryConfigurationSource
			extends AnnotationRepositoryConfigurationSource {

		AutoConfiguredAnnotationRepositoryConfigurationSource(AnnotationMetadata metadata,
				Class<? extends Annotation> annotation, ResourceLoader resourceLoader, Environment environment,
				BeanDefinitionRegistry registry, @Nullable BeanNameGenerator generator) {
			super(metadata, annotation, resourceLoader, environment, registry, generator);
		}

		@Override
		public Streamable<String> getBasePackages() {
			return AbstractRepositoryConfigurationSourceSupport.this.getBasePackages();
		}

		@Override
		public BootstrapMode getBootstrapMode() {
			return AbstractRepositoryConfigurationSourceSupport.this.getBootstrapMode();
		}

	}

}