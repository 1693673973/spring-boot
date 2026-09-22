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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 请查看许可证以了解管辖权限和限制的具体语言。
 */

package org.springframework.boot.autoconfigure.ssl;

import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundleRegistry;

/**
 * Interface to be implemented by types that register {@link SslBundle} instances with an
 * {@link SslBundleRegistry}.
 * <p>该接口由向 {@link SslBundleRegistry} 注册 {@link SslBundle} 实例的类型实现。</p>
 *
 * @author Scott Frederick
 * @since 3.1.0
 */
@FunctionalInterface
public interface SslBundleRegistrar {

	/**
	 * Callback method for registering {@link SslBundle}s with an
	 * {@link SslBundleRegistry}.
	 * <p>用于向 {@link SslBundleRegistry} 注册 {@link SslBundle} 的回调方法。</p>
	 * @param registry the registry that accepts {@code SslBundle}s
	 * <p>接受 {@code SslBundle} 的注册表</p>
	 */
	void registerBundles(SslBundleRegistry registry);

}