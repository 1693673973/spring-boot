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

import org.jspecify.annotations.Nullable;

import org.springframework.boot.ssl.jks.JksSslStoreBundle;

/**
 * {@link SslBundleProperties} for Java keystores.
 * <p>用于 Java 密钥库的 {@link SslBundleProperties}。</p>
 *
 * @author Scott Frederick
 * @author Phillip Webb
 * @since 3.1.0
 * @see JksSslStoreBundle
 */
public class JksSslBundleProperties extends SslBundleProperties {

	/**
	 * Keystore properties.
	 *
	 * <p>密钥库属性。</p>
	 *
	 */
	private final Store keystore = new Store();

	/**
	 * Truststore properties.
	 *
	 * <p>信任库属性。</p>
	 *
	 */
	private final Store truststore = new Store();

	public Store getKeystore() {
		return this.keystore;
	}

	public Store getTruststore() {
		return this.truststore;
	}

	/**
	 * Store properties.
	 *
	 * <p>存储属性。</p>
	 *
	 */
	public static class Store {

		/**
		 * Type of the store to create, e.g. JKS.
		 *
		 * <p>要创建的存储类型，例如 JKS。</p>
		 *
		 */
		private @Nullable String type;

		/**
		 * Provider for the store.
		 *
		 * <p>存储的提供程序。</p>
		 *
		 */
		private @Nullable String provider;

		/**
		 * Location of the resource containing the store content.
		 *
		 * <p>包含存储内容的资源位置。</p>
		 *
		 */
		private @Nullable String location;

		/**
		 * Password used to access the store.
		 *
		 * <p>用于访问存储的密码。</p>
		 *
		 */
		private @Nullable String password;

		public @Nullable String getType() {
			return this.type;
		}

		public void setType(@Nullable String type) {
			this.type = type;
		}

		public @Nullable String getProvider() {
			return this.provider;
		}

		public void setProvider(@Nullable String provider) {
			this.provider = provider;
		}

		public @Nullable String getLocation() {
			return this.location;
		}

		public void setLocation(@Nullable String location) {
			this.location = location;
		}

		public @Nullable String getPassword() {
			return this.password;
		}

		public void setPassword(@Nullable String password) {
			this.password = password;
		}

	}

}