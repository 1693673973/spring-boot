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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jspecify.annotations.Nullable;

import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.util.Assert;

/**
 * Collection of {@link TemplateAvailabilityProvider} beans that can be used to check
 * which (if any) templating engine supports a given view. Caches responses unless the
 * {@code spring.template.provider.cache} property is set to {@code false}.
 * <p>{@link TemplateAvailabilityProvider} bean 的集合，可用于检查哪个（如果有）模板引擎支持给定视图。
 * 除非将 {@code spring.template.provider.cache} 属性设置为 {@code false}，否则会缓存响应。</p>
 *
 * @author Phillip Webb
 * @author Madhura Bhave
 * @since 1.4.0
 */
public class TemplateAvailabilityProviders {

	private final List<TemplateAvailabilityProvider> providers;

	private static final int CACHE_LIMIT = 1024;

	private static final TemplateAvailabilityProvider NONE = new NoTemplateAvailabilityProvider();

	/**
	 * Resolved template views, returning already cached instances without a global lock.
	 * <p>已解析的模板视图，无需全局锁即可返回已缓存的实例。</p>
	 */
	private final Map<String, TemplateAvailabilityProvider> resolved = new ConcurrentHashMap<>(CACHE_LIMIT);

	/**
	 * Map from view name resolve template view, synchronized when accessed.
	 * <p>从视图名称到已解析模板视图的映射，访问时进行同步。</p>
	 */
	private final Map<String, TemplateAvailabilityProvider> cache = new LinkedHashMap<>(CACHE_LIMIT, 0.75f, true) {

		@Override
		protected boolean removeEldestEntry(Map.Entry<String, TemplateAvailabilityProvider> eldest) {
			if (size() > CACHE_LIMIT) {
				TemplateAvailabilityProviders.this.resolved.remove(eldest.getKey());
				return true;
			}
			return false;
		}

	};

	/**
	 * Create a new {@link TemplateAvailabilityProviders} instance.
	 * <p>创建一个新的 {@link TemplateAvailabilityProviders} 实例。</p>
	 *
	 * @param applicationContext the source application context
	 *
	 * <p>源应用程序上下文</p>
	 */
	public TemplateAvailabilityProviders(ApplicationContext applicationContext) {
		this(getClassLoader(applicationContext));
	}

	private static ClassLoader getClassLoader(ApplicationContext applicationContext) {
		Assert.notNull(applicationContext, "'applicationContext' must not be null");
		ClassLoader classLoader = applicationContext.getClassLoader();
		Assert.state(classLoader != null, "'classLoader' must not be null");
		return classLoader;
	}

	/**
	 * Create a new {@link TemplateAvailabilityProviders} instance.
	 * <p>创建一个新的 {@link TemplateAvailabilityProviders} 实例。</p>
	 *
	 * @param classLoader the source class loader
	 *
	 * <p>源类加载器</p>
	 */
	public TemplateAvailabilityProviders(ClassLoader classLoader) {
		Assert.notNull(classLoader, "'classLoader' must not be null");
		this.providers = SpringFactoriesLoader.loadFactories(TemplateAvailabilityProvider.class, classLoader);
	}

	/**
	 * Create a new {@link TemplateAvailabilityProviders} instance.
	 * <p>创建一个新的 {@link TemplateAvailabilityProviders} 实例。</p>
	 *
	 * @param providers the underlying providers
	 *
	 * <p>底层提供者</p>
	 */
	protected TemplateAvailabilityProviders(Collection<? extends TemplateAvailabilityProvider> providers) {
		Assert.notNull(providers, "'providers' must not be null");
		this.providers = new ArrayList<>(providers);
	}

	/**
	 * Return the underlying providers being used.
	 * <p>返回正在使用的底层提供者。</p>
	 *
	 * @return the providers being used
	 *
	 * <p>正在使用的提供者</p>
	 */
	public List<TemplateAvailabilityProvider> getProviders() {
		return this.providers;
	}

	/**
	 * Get the provider that can be used to render the given view.
	 * <p>获取可用于渲染给定视图的提供者。</p>
	 *
	 * @param view the view to render
	 *
	 * <p>要渲染的视图</p>
	 *
	 * @param applicationContext the application context
	 *
	 * <p>应用程序上下文</p>
	 *
	 * @return a {@link TemplateAvailabilityProvider} or null
	 *
	 * <p>一个 {@link TemplateAvailabilityProvider} 或 null</p>
	 */
	public @Nullable TemplateAvailabilityProvider getProvider(String view, ApplicationContext applicationContext) {
		Assert.notNull(applicationContext, "'applicationContext' must not be null");
		ClassLoader classLoader = applicationContext.getClassLoader();
		Assert.state(classLoader != null, "'classLoader' must not be null");
		return getProvider(view, applicationContext.getEnvironment(), classLoader, applicationContext);
	}

	/**
	 * Get the provider that can be used to render the given view.
	 * <p>获取可用于渲染给定视图的提供者。</p>
	 *
	 * @param view the view to render
	 *
	 * <p>要渲染的视图</p>
	 *
	 * @param environment the environment
	 *
	 * <p>环境</p>
	 *
	 * @param classLoader the class loader
	 *
	 * <p>类加载器</p>
	 *
	 * @param resourceLoader the resource loader
	 *
	 * <p>资源加载器</p>
	 *
	 * @return a {@link TemplateAvailabilityProvider} or null
	 *
	 * <p>一个 {@link TemplateAvailabilityProvider} 或 null</p>
	 */
	public @Nullable TemplateAvailabilityProvider getProvider(String view, Environment environment,
			ClassLoader classLoader, ResourceLoader resourceLoader) {
		Assert.notNull(view, "'view' must not be null");
		Assert.notNull(environment, "'environment' must not be null");
		Assert.notNull(classLoader, "'classLoader' must not be null");
		Assert.notNull(resourceLoader, "'resourceLoader' must not be null");
		Boolean useCache = environment.getProperty("spring.template.provider.cache", Boolean.class, true);
		if (!useCache) {
			return findProvider(view, environment, classLoader, resourceLoader);
		}
		TemplateAvailabilityProvider provider = this.resolved.get(view);
		if (provider == null) {
			synchronized (this.cache) {
				provider = findProvider(view, environment, classLoader, resourceLoader);
				provider = (provider != null) ? provider : NONE;
				this.resolved.put(view, provider);
				this.cache.put(view, provider);
			}
		}
		return (provider != NONE) ? provider : null;
	}

	private @Nullable TemplateAvailabilityProvider findProvider(String view, Environment environment,
			ClassLoader classLoader, ResourceLoader resourceLoader) {
		for (TemplateAvailabilityProvider candidate : this.providers) {
			if (candidate.isTemplateAvailable(view, environment, classLoader, resourceLoader)) {
				return candidate;
			}
		}
		return null;
	}

	private static final class NoTemplateAvailabilityProvider implements TemplateAvailabilityProvider {

		@Override
		public boolean isTemplateAvailable(String view, Environment environment, ClassLoader classLoader,
				ResourceLoader resourceLoader) {
			return false;
		}

	}

}