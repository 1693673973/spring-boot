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

import java.util.Set;

import org.jspecify.annotations.Nullable;

import org.springframework.boot.ssl.SslBundle;

/**
 * Base class for SSL Bundle properties.
 * <p>SSL Bundle 属性的基类。</p>
 *
 * @author Scott Frederick
 * @author Phillip Webb
 * @since 3.1.0
 * @see SslBundle
 */
public abstract class SslBundleProperties {

	/**
	 * Key details for the bundle.
	 * <p>捆绑的密钥详细信息。</p>
	 */
	private final Key key = new Key();

	/**
	 * Options for the SSL connection.
	 * <p>SSL 连接的选项。</p>
	 */
	private final Options options = new Options();

	/**
	 * SSL Protocol to use.
	 * <p>要使用的 SSL 协议。</p>
	 */
	private String protocol = SslBundle.DEFAULT_PROTOCOL;

	/**
	 * Whether to reload the SSL bundle.
	 * <p>是否重新加载 SSL 捆绑。</p>
	 */
	private boolean reloadOnUpdate;

	public Key getKey() {
		return this.key;
	}

	public Options getOptions() {
		return this.options;
	}

	public String getProtocol() {
		return this.protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public boolean isReloadOnUpdate() {
		return this.reloadOnUpdate;
	}

	public void setReloadOnUpdate(boolean reloadOnUpdate) {
		this.reloadOnUpdate = reloadOnUpdate;
	}

	public static class Options {

		/**
		 * Supported SSL ciphers.
		 * <p>支持的 SSL 密码套件。</p>
		 */
		private @Nullable Set<String> ciphers;

		/**
		 * Enabled SSL protocols.
		 * <p>启用的 SSL 协议。</p>
		 */
		private @Nullable Set<String> enabledProtocols;

		public @Nullable Set<String> getCiphers() {
			return this.ciphers;
		}

		public void setCiphers(@Nullable Set<String> ciphers) {
			this.ciphers = ciphers;
		}

		public @Nullable Set<String> getEnabledProtocols() {
			return this.enabledProtocols;
		}

		public void setEnabledProtocols(@Nullable Set<String> enabledProtocols) {
			this.enabledProtocols = enabledProtocols;
		}

	}

	public static class Key {

		/**
		 * The password used to access the key in the key store.
		 * <p>用于访问密钥库中密钥的密码。</p>
		 */
		private @Nullable String password;

		/**
		 * The alias that identifies the key in the key store.
		 * <p>用于标识密钥库中密钥的别名。</p>
		 */
		private @Nullable String alias;

		public @Nullable String getPassword() {
			return this.password;
		}

		public void setPassword(@Nullable String password) {
			this.password = password;
		}

		public @Nullable String getAlias() {
			return this.alias;
		}

		public void setAlias(@Nullable String alias) {
			this.alias = alias;
		}

	}

}