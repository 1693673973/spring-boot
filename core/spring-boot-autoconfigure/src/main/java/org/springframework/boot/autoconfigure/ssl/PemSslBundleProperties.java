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

import org.springframework.boot.ssl.pem.PemSslStoreBundle;

/**
 * {@link SslBundleProperties} for PEM-encoded certificates and private keys.
 * <p>用于 PEM 编码的证书和私钥的 {@link SslBundleProperties}。</p>
 *
 * @author Scott Frederick
 * @author Phillip Webb
 * @author Moritz Halbritter
 * @since 3.1.0
 * @see PemSslStoreBundle
 */
public class PemSslBundleProperties extends SslBundleProperties {

	/**
	 * Keystore properties.
	 * <p>密钥库属性。</p>
	 */
	private final Store keystore = new Store();

	/**
	 * Truststore properties.
	 * <p>信任库属性。</p>
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
	 * <p>存储属性。</p>
	 */
	public static class Store {

		/**
		 * Type of the store to create, e.g. JKS.
		 * <p>要创建的存储类型，例如 JKS。</p>
		 */
		private @Nullable String type;

		/**
		 * Location or content of the certificate or certificate chain in PEM format.
		 * <p>PEM 格式的证书或证书链的位置或内容。</p>
		 */
		private @Nullable String certificate;

		/**
		 * Location or content of the private key in PEM format.
		 * <p>PEM 格式的私钥的位置或内容。</p>
		 */
		private @Nullable String privateKey;

		/**
		 * Password used to decrypt an encrypted private key.
		 * <p>用于解密加密私钥的密码。</p>
		 */
		private @Nullable String privateKeyPassword;

		/**
		 * Whether to verify that the private key matches the public key.
		 * <p>是否验证私钥与公钥匹配。</p>
		 */
		private boolean verifyKeys;

		public @Nullable String getType() {
			return this.type;
		}

		public void setType(@Nullable String type) {
			this.type = type;
		}

		public @Nullable String getCertificate() {
			return this.certificate;
		}

		public void setCertificate(@Nullable String certificate) {
			this.certificate = certificate;
		}

		public @Nullable String getPrivateKey() {
			return this.privateKey;
		}

		public void setPrivateKey(@Nullable String privateKey) {
			this.privateKey = privateKey;
		}

		public @Nullable String getPrivateKeyPassword() {
			return this.privateKeyPassword;
		}

		public void setPrivateKeyPassword(@Nullable String privateKeyPassword) {
			this.privateKeyPassword = privateKeyPassword;
		}

		public boolean isVerifyKeys() {
			return this.verifyKeys;
		}

		public void setVerifyKeys(boolean verifyKeys) {
			this.verifyKeys = verifyKeys;
		}

	}

}