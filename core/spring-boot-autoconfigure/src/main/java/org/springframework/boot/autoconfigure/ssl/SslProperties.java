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

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties for centralized SSL trust material configuration.
 * <p>集中式 SSL 信任材料配置的属性。</p>
 *
 * @author Scott Frederick
 * @author Moritz Halbritter
 * @since 3.1.0
 */
@ConfigurationProperties("spring.ssl")
public class SslProperties {

	/**
	 * SSL bundles.
	 *
	 * <p>SSL 捆绑。</p>
	 */
	private final Bundles bundle = new Bundles();

	public Bundles getBundle() {
		return this.bundle;
	}

	/**
	 * Properties to define SSL Bundles.
	 * <p>用于定义 SSL 捆绑的属性。</p>
	 */
	public static class Bundles {

		/**
		 * PEM-encoded SSL trust material.
		 *
		 * <p>PEM 编码的 SSL 信任材料。</p>
		 */
		private final Map<String, PemSslBundleProperties> pem = new LinkedHashMap<>();

		/**
		 * Java keystore SSL trust material.
		 *
		 * <p>Java 密钥库 SSL 信任材料。</p>
		 */
		private final Map<String, JksSslBundleProperties> jks = new LinkedHashMap<>();

		/**
		 * Trust material watching.
		 *
		 * <p>信任材料监视。</p>
		 */
		private final Watch watch = new Watch();

		public Map<String, PemSslBundleProperties> getPem() {
			return this.pem;
		}

		public Map<String, JksSslBundleProperties> getJks() {
			return this.jks;
		}

		public Watch getWatch() {
			return this.watch;
		}

		public static class Watch {

			/**
			 * File watching.
			 *
			 * <p>文件监视。</p>
			 */
			private final File file = new File();

			public File getFile() {
				return this.file;
			}

			public static class File {

				/**
				 * Quiet period, after which changes are detected.
				 *
				 * <p>静默期，在此之后检测到更改。</p>
				 */
				private Duration quietPeriod = Duration.ofSeconds(10);

				public Duration getQuietPeriod() {
					return this.quietPeriod;
				}

				public void setQuietPeriod(Duration quietPeriod) {
					this.quietPeriod = quietPeriod;
				}

			}

		}

	}

}