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

package org.springframework.boot.autoconfigure.template;

import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;

/**
 * Indicates the availability of view templates for a particular templating engine such as
 * FreeMarker or Thymeleaf.
 * <p>指示特定模板引擎（如 FreeMarker 或 Thymeleaf）的视图模板的可用性。</p>
 *
 * @author Andy Wilkinson
 * @since 1.1.0
 */
@FunctionalInterface
public interface TemplateAvailabilityProvider {

	/**
	 * Returns {@code true} if a template is available for the given {@code view}.
	 * <p>如果给定 {@code view} 有可用模板，则返回 {@code true}。</p>
	 *
	 * @param view the view name
	 *
	 * <p>视图名称</p>
	 *
	 * @param environment the environment
	 *
	 * <p>环境</p>
	 *
	 * @param classLoader the class loader
	 *
	 * <p>类加载器</p>
	 *
	 * @param resourceLoader the resource loader
	 *
	 * <p>资源加载器</p>
	 *
	 * @return if the template is available
	 *
	 * <p>模板是否可用</p>
	 */
	boolean isTemplateAvailable(String view, Environment environment, ClassLoader classLoader,
			ResourceLoader resourceLoader);

}