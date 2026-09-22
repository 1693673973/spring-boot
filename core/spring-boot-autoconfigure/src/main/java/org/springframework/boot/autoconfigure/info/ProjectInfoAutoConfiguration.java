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

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;

import org.jspecify.annotations.Nullable;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for various project information.
 * <p>用于各种项目信息的 {@link EnableAutoConfiguration 自动配置}。</p>
 *
 * @author Stephane Nicoll
 * @author Madhura Bhave
 * @since 1.4.0
 */
@AutoConfiguration
@EnableConfigurationProperties(ProjectInfoProperties.class)
public final class ProjectInfoAutoConfiguration {

	private final ProjectInfoProperties properties;

	ProjectInfoAutoConfiguration(ProjectInfoProperties properties) {
		this.properties = properties;
	}

	@Conditional(GitResourceAvailableCondition.class)
	@ConditionalOnMissingBean
	@Bean
	GitProperties gitProperties() throws Exception {
		return new GitProperties(
				loadFrom(this.properties.getGit().getLocation(), "git", this.properties.getGit().getEncoding()));
	}

	@ConditionalOnResource(resources = "${spring.info.build.location:classpath:META-INF/build-info.properties}")
	@ConditionalOnMissingBean
	@Bean
	BuildProperties buildProperties() throws Exception {
		return new BuildProperties(
				loadFrom(this.properties.getBuild().getLocation(), "build", this.properties.getBuild().getEncoding()));
	}

	private Properties loadFrom(Resource location, String prefix, Charset encoding) throws IOException {
		prefix = prefix.endsWith(".") ? prefix : prefix + ".";
		Properties source = loadSource(location, encoding);
		Properties target = new Properties();
		for (String key : source.stringPropertyNames()) {
			if (key.startsWith(prefix)) {
				target.put(key.substring(prefix.length()), source.get(key));
			}
		}
		return target;
	}

	private Properties loadSource(Resource location, @Nullable Charset encoding) throws IOException {
		if (encoding != null) {
			return PropertiesLoaderUtils.loadProperties(new EncodedResource(location, encoding));
		}
		return PropertiesLoaderUtils.loadProperties(location);
	}

	static class GitResourceAvailableCondition extends SpringBootCondition {

		@Override
		public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
			ResourceLoader loader = context.getResourceLoader();
			Environment environment = context.getEnvironment();
			String location = environment.getProperty("spring.info.git.location");
			if (location == null) {
				location = "classpath:git.properties";
			}
			ConditionMessage.Builder message = ConditionMessage.forCondition("GitResource");
			if (loader.getResource(location).exists()) {
				return ConditionOutcome.match(message.found("git info at").items(location));
			}
			return ConditionOutcome.noMatch(message.didNotFind("git info at").items(location));
		}

	}

}