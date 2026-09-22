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

package org.springframework.boot.autoconfigure.cache;

/**
 * Supported cache types (defined in order of precedence).
 * <p>支持的缓存类型（按优先级顺序定义）。</p>
 *
 * @author Stephane Nicoll
 * @author Phillip Webb
 * @author Eddú Meléndez
 * @since 4.0.0
 */
public enum CacheType {

	/**
	 * Generic caching using 'Cache' beans from the context.
	 * <p>使用来自上下文的 'Cache' bean 进行通用缓存。</p>
	 */
	GENERIC,

	/**
	 * JCache (JSR-107) backed caching.
	 * <p>基于 JCache (JSR-107) 的缓存。</p>
	 */
	JCACHE,

	/**
	 * Hazelcast backed caching.
	 * <p>基于 Hazelcast 的缓存。</p>
	 */
	HAZELCAST,

	/**
	 * Couchbase backed caching.
	 * <p>基于 Couchbase 的缓存。</p>
	 */
	COUCHBASE,

	/**
	 * Infinispan backed caching.
	 * <p>基于 Infinispan 的缓存。</p>
	 */
	INFINISPAN,

	/**
	 * Redis backed caching.
	 * <p>基于 Redis 的缓存。</p>
	 */
	REDIS,

	/**
	 * Cache2k backed caching.
	 * <p>基于 Cache2k 的缓存。</p>
	 */
	CACHE2K,

	/**
	 * Caffeine backed caching.
	 * <p>基于 Caffeine 的缓存。</p>
	 */
	CAFFEINE,

	/**
	 * Simple in-memory caching.
	 * <p>简单的内存缓存。</p>
	 */
	SIMPLE,

	/**
	 * No caching.
	 * <p>无缓存。</p>
	 */
	NONE

}