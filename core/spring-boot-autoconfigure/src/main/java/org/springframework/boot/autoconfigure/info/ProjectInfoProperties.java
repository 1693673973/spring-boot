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

package org.springframework.boot.autoconfigure.info;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Configuration properties for project information.
 * <p>项目信息的配置属性。</p>
 *
 * @author Stephane Nicoll
 * @since 1.4.0
 */
@ConfigurationProperties("spring.info")
public class ProjectInfoProperties {

	/**
	 * Build specific info properties.
	 * <p>构建特定的信息属性。</p>
	 */
	private final Build build = new Build();

	/**
	 * Git specific info properties.
	 * <p>Git 特定的信息属性。</p>
	 */
	private final Git git = new Git();

	public Build getBuild() {
		return this.build;
	}

	public Git getGit() {
		return this.git;
	}

	/**
	 * Build specific info properties.
	 * <p>构建特定的信息属性。</p>
	 */
	public static class Build {

		/**
		 * Location of the generated build-info.properties file.
		 * <p>生成的 build-info.properties 文件的位置。</p>
		 */
		private Resource location = new ClassPathResource("META-INF/build-info.properties");

		/**
		 * File encoding.
		 * <p>文件编码。</p>
		 */
		private Charset encoding = StandardCharsets.UTF_8;

		public Resource getLocation() {
			return this.location;
		}

		public void setLocation(Resource location) {
			this.location = location;
		}

		public Charset getEncoding() {
			return this.encoding;
		}

		public void setEncoding(Charset encoding) {
			this.encoding = encoding;
		}

	}

	/**
	 * Git specific info properties.
	 * <p>Git 特定的信息属性。</p>
	 */
	public static class Git {

		/**
		 * Location of the generated git.properties file.
		 * <p>生成的 git.properties 文件的位置。</p>
		 */
		private Resource location = new ClassPathResource("git.properties");

		/**
		 * File encoding.
		 * <p>文件编码。</p>
		 */
		private Charset encoding = StandardCharsets.UTF_8;

		public Resource getLocation() {
			return this.location;
		}

		public void setLocation(Resource location) {
			this.location = location;
		}

		public Charset getEncoding() {
			return this.encoding;
		}

		public void setEncoding(Charset encoding) {
			this.encoding = encoding;
		}

	}

}