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

package org.springframework.boot.autoconfigure.web;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.jspecify.annotations.Nullable;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.http.CacheControl;

/**
 * {@link ConfigurationProperties Configuration properties} for general web concerns.
 * <p>用于一般 Web 关注点的 {@link ConfigurationProperties 配置属性}。</p>
 *
 * @author Andy Wilkinson
 * @since 2.4.0
 */
@ConfigurationProperties("spring.web")
public class WebProperties {

	/**
	 * Locale to use. By default, this locale is overridden by the "Accept-Language"
	 * header.
	 * <p>要使用的区域设置。默认情况下，此区域设置会被 "Accept-Language" 头覆盖。</p>
	 */
	private @Nullable Locale locale;

	/**
	 * Define how the locale should be resolved.
	 *
	 * <p>定义应如何解析区域设置。</p>
	 */
	private LocaleResolver localeResolver = LocaleResolver.ACCEPT_HEADER;

	private final Resources resources = new Resources();

	@NestedConfigurationProperty
	private final ErrorProperties error = new ErrorProperties();

	public @Nullable Locale getLocale() {
		return this.locale;
	}

	public void setLocale(@Nullable Locale locale) {
		this.locale = locale;
	}

	public LocaleResolver getLocaleResolver() {
		return this.localeResolver;
	}

	public void setLocaleResolver(LocaleResolver localeResolver) {
		this.localeResolver = localeResolver;
	}

	public ErrorProperties getError() {
		return this.error;
	}

	public Resources getResources() {
		return this.resources;
	}

	public enum LocaleResolver {

		/**
		 * Always use the configured locale.
		 *
		 * <p>始终使用配置的区域设置。</p>
		 */
		FIXED,

		/**
		 * Use the "Accept-Language" header or the configured locale if the header is not
		 * set.
		 *
		 * <p>使用 "Accept-Language" 头，如果未设置该头，则使用配置的区域设置。</p>
		 */
		ACCEPT_HEADER

	}

	public static class Resources {

		private static final String[] CLASSPATH_RESOURCE_LOCATIONS = { "classpath:/META-INF/resources/",
				"classpath:/resources/", "classpath:/static/", "classpath:/public/" };

		/**
		 * Locations of static resources. Defaults to classpath:[/META-INF/resources/,
		 * /resources/, /static/, /public/].
		 * <p>静态资源的位置。默认为 classpath:[/META-INF/resources/, /resources/, /static/, /public/]。</p>
		 */
		private String[] staticLocations = CLASSPATH_RESOURCE_LOCATIONS;

		/**
		 * Whether to enable default resource handling.
		 *
		 * <p>是否启用默认资源处理。</p>
		 */
		private boolean addMappings = true;

		private boolean customized;

		private final Chain chain = new Chain();

		private final Cache cache = new Cache();

		public String[] getStaticLocations() {
			return this.staticLocations;
		}

		public void setStaticLocations(String[] staticLocations) {
			this.staticLocations = appendSlashIfNecessary(staticLocations);
			this.customized = true;
		}

		private String[] appendSlashIfNecessary(String[] staticLocations) {
			String[] normalized = new String[staticLocations.length];
			for (int i = 0; i < staticLocations.length; i++) {
				String location = staticLocations[i];
				normalized[i] = location.endsWith("/") ? location : location + "/";
			}
			return normalized;
		}

		public boolean isAddMappings() {
			return this.addMappings;
		}

		public void setAddMappings(boolean addMappings) {
			this.customized = true;
			this.addMappings = addMappings;
		}

		public Chain getChain() {
			return this.chain;
		}

		public Cache getCache() {
			return this.cache;
		}

		public boolean hasBeenCustomized() {
			return this.customized || getChain().hasBeenCustomized() || getCache().hasBeenCustomized();
		}

		/**
		 * Configuration for the Spring Resource Handling chain.
		 *
		 * <p>Spring 资源处理链的配置。</p>
		 */
		public static class Chain {

			boolean customized;

			/**
			 * Whether to enable the Spring Resource Handling chain. By default, disabled
			 * unless at least one strategy has been enabled.
			 *
			 * <p>是否启用 Spring 资源处理链。默认情况下，除非至少启用了一种策略，否则禁用。</p>
			 */
			private @Nullable Boolean enabled;

			/**
			 * Whether to enable caching in the Resource chain.
			 *
			 * <p>是否在资源链中启用缓存。</p>
			 */
			private boolean cache = true;

			/**
			 * Whether to enable resolution of already compressed resources (gzip,
			 * brotli). Checks for a resource name with the '.gz' or '.br' file
			 * extensions.
			 *
			 * <p>是否启用对已压缩资源（gzip、brotli）的解析。检查具有 '.gz' 或 '.br' 文件扩展名的资源名称。</p>
			 */
			private boolean compressed;

			private final Strategy strategy = new Strategy();

			/**
			 * Return whether the resource chain is enabled. Return {@code null} if no
			 * specific settings are present.
			 * <p>返回资源链是否已启用。如果不存在特定设置，则返回 {@code null}。</p>
			 * @return whether the resource chain is enabled or {@code null} if no
			 * specified settings are present.
			 * <p>资源链是否已启用，如果不存在指定设置则返回 {@code null}。</p>
			 */
			public @Nullable Boolean getEnabled() {
				return getEnabled(getStrategy().getFixed().isEnabled(), getStrategy().getContent().isEnabled(),
						this.enabled);
			}

			private boolean hasBeenCustomized() {
				return this.customized || getStrategy().hasBeenCustomized();
			}

			public void setEnabled(Boolean enabled) {
				this.enabled = enabled;
				this.customized = true;
			}

			public boolean isCache() {
				return this.cache;
			}

			public void setCache(boolean cache) {
				this.cache = cache;
				this.customized = true;
			}

			public Strategy getStrategy() {
				return this.strategy;
			}

			public boolean isCompressed() {
				return this.compressed;
			}

			public void setCompressed(boolean compressed) {
				this.compressed = compressed;
				this.customized = true;
			}

			static @Nullable Boolean getEnabled(boolean fixedEnabled, boolean contentEnabled,
					@Nullable Boolean chainEnabled) {
				return (fixedEnabled || contentEnabled) ? Boolean.TRUE : chainEnabled;
			}

			/**
			 * Strategies for extracting and embedding a resource version in its URL path.
			 *
			 * <p>用于在资源 URL 路径中提取和嵌入资源版本的策略。</p>
			 */
			public static class Strategy {

				private final Fixed fixed = new Fixed();

				private final Content content = new Content();

				public Fixed getFixed() {
					return this.fixed;
				}

				public Content getContent() {
					return this.content;
				}

				private boolean hasBeenCustomized() {
					return getFixed().hasBeenCustomized() || getContent().hasBeenCustomized();
				}

				/**
				 * Version Strategy based on content hashing.
				 *
				 * <p>基于内容哈希的版本策略。</p>
				 */
				public static class Content {

					private boolean customized;

					/**
					 * Whether to enable the content Version Strategy.
					 *
					 * <p>是否启用内容版本策略。</p>
					 */
					private boolean enabled;

					/**
					 * List of patterns to apply to the content Version Strategy.
					 *
					 * <p>应用于内容版本策略的模式列表。</p>
					 */
					private String[] paths = new String[] { "/**" };

					public boolean isEnabled() {
						return this.enabled;
					}

					public void setEnabled(boolean enabled) {
						this.customized = true;
						this.enabled = enabled;
					}

					public String[] getPaths() {
						return this.paths;
					}

					public void setPaths(String[] paths) {
						this.customized = true;
						this.paths = paths;
					}

					private boolean hasBeenCustomized() {
						return this.customized;
					}

				}

				/**
				 * Version Strategy based on a fixed version string.
				 *
				 * <p>基于固定版本字符串的版本策略。</p>
				 */
				public static class Fixed {

					private boolean customized;

					/**
					 * Whether to enable the fixed Version Strategy.
					 *
					 * <p>是否启用固定版本策略。</p>
					 */
					private boolean enabled;

					/**
					 * List of patterns to apply to the fixed Version Strategy.
					 *
					 * <p>应用于固定版本策略的模式列表。</p>
					 */
					private String[] paths = new String[] { "/**" };

					/**
					 * Version string to use for the fixed Version Strategy.
					 *
					 * <p>用于固定版本策略的版本字符串。</p>
					 */
					private @Nullable String version;

					public boolean isEnabled() {
						return this.enabled;
					}

					public void setEnabled(boolean enabled) {
						this.customized = true;
						this.enabled = enabled;
					}

					public String[] getPaths() {
						return this.paths;
					}

					public void setPaths(String[] paths) {
						this.customized = true;
						this.paths = paths;
					}

					public @Nullable String getVersion() {
						return this.version;
					}

					public void setVersion(@Nullable String version) {
						this.customized = true;
						this.version = version;
					}

					private boolean hasBeenCustomized() {
						return this.customized;
					}

				}

			}

		}

		/**
		 * Cache configuration.
		 *
		 * <p>缓存配置。</p>
		 */
		public static class Cache {

			private boolean customized;

			/**
			 * Cache period for the resources served by the resource handler. If a
			 * duration suffix is not specified, seconds will be used. Can be overridden
			 * by the 'spring.web.resources.cache.cachecontrol' properties.
			 *
			 * <p>资源处理器所提供资源的缓存周期。如果未指定持续时间后缀，则使用秒。可以被 'spring.web.resources.cache.cachecontrol' 属性覆盖。</p>
			 */
			@DurationUnit(ChronoUnit.SECONDS)
			private @Nullable Duration period;

			/**
			 * Cache control HTTP headers, only allows valid directive combinations.
			 * Overrides the 'spring.web.resources.cache.period' property.
			 *
			 * <p>缓存控制 HTTP 头，仅允许有效的指令组合。覆盖 'spring.web.resources.cache.period' 属性。</p>
			 */
			private final Cachecontrol cachecontrol = new Cachecontrol();

			/**
			 * Whether we should use the "lastModified" metadata of the files in HTTP
			 * caching headers.
			 *
			 * <p>我们是否应在 HTTP 缓存头中使用文件的 "lastModified" 元数据。</p>
			 */
			private boolean useLastModified = true;

			public @Nullable Duration getPeriod() {
				return this.period;
			}

			public void setPeriod(@Nullable Duration period) {
				this.customized = true;
				this.period = period;
			}

			public Cachecontrol getCachecontrol() {
				return this.cachecontrol;
			}

			public boolean isUseLastModified() {
				return this.useLastModified;
			}

			public void setUseLastModified(boolean useLastModified) {
				this.useLastModified = useLastModified;
			}

			private boolean hasBeenCustomized() {
				return this.customized || getCachecontrol().hasBeenCustomized();
			}

			/**
			 * Cache Control HTTP header configuration.
			 *
			 * <p>缓存控制 HTTP 头配置。</p>
			 */
			public static class Cachecontrol {

				private boolean customized;

				/**
				 * Maximum time the response should be cached, in seconds if no duration
				 * suffix is not specified.
				 *
				 * <p>响应应被缓存的最长时间，如果未指定持续时间后缀，则以秒为单位。</p>
				 */
				@DurationUnit(ChronoUnit.SECONDS)
				private @Nullable Duration maxAge;

				/**
				 * Indicate that the cached response can be reused only if re-validated
				 * with the server.
				 *
				 * <p>指示仅当与服务器重新验证后，缓存的响应才可被重用。</p>
				 */
				private @Nullable Boolean noCache;

				/**
				 * Indicate to not cache the response in any case.
				 *
				 * <p>指示在任何情况下都不缓存响应。</p>
				 */
				private @Nullable Boolean noStore;

				/**
				 * Indicate that once it has become stale, a cache must not use the
				 * response without re-validating it with the server.
				 *
				 * <p>指示一旦响应过期，缓存不得在未与服务器重新验证的情况下使用该响应。</p>
				 */
				private @Nullable Boolean mustRevalidate;

				/**
				 * Indicate intermediaries (caches and others) that they should not
				 * transform the response content.
				 *
				 * <p>指示中间节点（缓存及其他）不应转换响应内容。</p>
				 */
				private @Nullable Boolean noTransform;

				/**
				 * Indicate that any cache may store the response.
				 *
				 * <p>指示任何缓存都可以存储该响应。</p>
				 */
				private @Nullable Boolean cachePublic;

				/**
				 * Indicate that the response message is intended for a single user and
				 * must not be stored by a shared cache.
				 *
				 * <p>指示响应消息仅供单个用户使用，且不得由共享缓存存储。</p>
				 */
				private @Nullable Boolean cachePrivate;

				/**
				 * Same meaning as the "must-revalidate" directive, except that it does
				 * not apply to private caches.
				 *
				 * <p>与 "must-revalidate" 指令含义相同，但不适用于私有缓存。</p>
				 */
				private @Nullable Boolean proxyRevalidate;

				/**
				 * Maximum time the response can be served after it becomes stale, in
				 * seconds if no duration suffix is not specified.
				 *
				 * <p>响应过期后仍可被提供的最长时间，如果未指定持续时间后缀，则以秒为单位。</p>
				 */
				@DurationUnit(ChronoUnit.SECONDS)
				private @Nullable Duration staleWhileRevalidate;

				/**
				 * Maximum time the response may be used when errors are encountered, in
				 * seconds if no duration suffix is not specified.
				 *
				 * <p>遇到错误时响应可被使用的最长时间，如果未指定持续时间后缀，则以秒为单位。</p>
				 */
				@DurationUnit(ChronoUnit.SECONDS)
				private @Nullable Duration staleIfError;

				/**
				 * Maximum time the response should be cached by shared caches, in seconds
				 * if no duration suffix is not specified.
				 *
				 * <p>共享缓存应缓存响应的最长时间，如果未指定持续时间后缀，则以秒为单位。</p>
				 */
				@DurationUnit(ChronoUnit.SECONDS)
				private @Nullable Duration sMaxAge;

				public @Nullable Duration getMaxAge() {
					return this.maxAge;
				}

				public void setMaxAge(@Nullable Duration maxAge) {
					this.customized = true;
					this.maxAge = maxAge;
				}

				public @Nullable Boolean getNoCache() {
					return this.noCache;
				}

				public void setNoCache(@Nullable Boolean noCache) {
					this.customized = true;
					this.noCache = noCache;
				}

				public @Nullable Boolean getNoStore() {
					return this.noStore;
				}

				public void setNoStore(@Nullable Boolean noStore) {
					this.customized = true;
					this.noStore = noStore;
				}

				public @Nullable Boolean getMustRevalidate() {
					return this.mustRevalidate;
				}

				public void setMustRevalidate(@Nullable Boolean mustRevalidate) {
					this.customized = true;
					this.mustRevalidate = mustRevalidate;
				}

				public @Nullable Boolean getNoTransform() {
					return this.noTransform;
				}

				public void setNoTransform(@Nullable Boolean noTransform) {
					this.customized = true;
					this.noTransform = noTransform;
				}

				public @Nullable Boolean getCachePublic() {
					return this.cachePublic;
				}

				public void setCachePublic(@Nullable Boolean cachePublic) {
					this.customized = true;
					this.cachePublic = cachePublic;
				}

				public @Nullable Boolean getCachePrivate() {
					return this.cachePrivate;
				}

				public void setCachePrivate(@Nullable Boolean cachePrivate) {
					this.customized = true;
					this.cachePrivate = cachePrivate;
				}

				public @Nullable Boolean getProxyRevalidate() {
					return this.proxyRevalidate;
				}

				public void setProxyRevalidate(@Nullable Boolean proxyRevalidate) {
					this.customized = true;
					this.proxyRevalidate = proxyRevalidate;
				}

				public @Nullable Duration getStaleWhileRevalidate() {
					return this.staleWhileRevalidate;
				}

				public void setStaleWhileRevalidate(@Nullable Duration staleWhileRevalidate) {
					this.customized = true;
					this.staleWhileRevalidate = staleWhileRevalidate;
				}

				public @Nullable Duration getStaleIfError() {
					return this.staleIfError;
				}

				public void setStaleIfError(@Nullable Duration staleIfError) {
					this.customized = true;
					this.staleIfError = staleIfError;
				}

				public @Nullable Duration getSMaxAge() {
					return this.sMaxAge;
				}

				public void setSMaxAge(@Nullable Duration sMaxAge) {
					this.customized = true;
					this.sMaxAge = sMaxAge;
				}

				public @Nullable CacheControl toHttpCacheControl() {
					PropertyMapper map = PropertyMapper.get();
					CacheControl control = createCacheControl();
					map.from(this::getMustRevalidate).whenTrue().toCall(control::mustRevalidate);
					map.from(this::getNoTransform).whenTrue().toCall(control::noTransform);
					map.from(this::getCachePublic).whenTrue().toCall(control::cachePublic);
					map.from(this::getCachePrivate).whenTrue().toCall(control::cachePrivate);
					map.from(this::getProxyRevalidate).whenTrue().toCall(control::proxyRevalidate);
					map.from(this::getStaleWhileRevalidate)
							.to((duration) -> control.staleWhileRevalidate(duration.getSeconds(), TimeUnit.SECONDS));
					map.from(this::getStaleIfError)
							.to((duration) -> control.staleIfError(duration.getSeconds(), TimeUnit.SECONDS));
					map.from(this::getSMaxAge)
							.to((duration) -> control.sMaxAge(duration.getSeconds(), TimeUnit.SECONDS));
					// check if cacheControl remained untouched
					// 检查 cacheControl 是否未被修改
					if (control.getHeaderValue() == null) {
						return null;
					}
					return control;
				}

				private CacheControl createCacheControl() {
					if (Boolean.TRUE.equals(this.noStore)) {
						return CacheControl.noStore();
					}
					if (Boolean.TRUE.equals(this.noCache)) {
						return CacheControl.noCache();
					}
					if (this.maxAge != null) {
						return CacheControl.maxAge(this.maxAge.getSeconds(), TimeUnit.SECONDS);
					}
					return CacheControl.empty();
				}

				private boolean hasBeenCustomized() {
					return this.customized;
				}

			}

		}

	}

}