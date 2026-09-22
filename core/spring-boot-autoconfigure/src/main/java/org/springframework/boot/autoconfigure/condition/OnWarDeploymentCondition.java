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

package org.springframework.boot.autoconfigure.condition;

import jakarta.servlet.ServletContext;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.web.context.WebApplicationContext;

/**
 * {@link Condition} that checks if the application is running as a traditional war
 * deployment.
 * <p>{@link Condition}，用于检查应用程序是否作为传统的 war 部署运行。</p>
 *
 * @author Madhura Bhave
 * @see ConditionalOnWarDeployment
 * @see ConditionalOnNotWarDeployment
 */
class OnWarDeploymentCondition extends SpringBootCondition {

	@Override
	public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
		boolean required = metadata.isAnnotated(ConditionalOnWarDeployment.class.getName());
		ResourceLoader resourceLoader = context.getResourceLoader();
		if (resourceLoader instanceof WebApplicationContext applicationContext) {
			ServletContext servletContext = applicationContext.getServletContext();
			if (servletContext != null) {
				return new ConditionOutcome(required, "Application is deployed as a WAR file.");
			}
		}
		return new ConditionOutcome(!required, ConditionMessage.forCondition(ConditionalOnWarDeployment.class)
				.because("the application is not deployed as a WAR file."));
	}

}