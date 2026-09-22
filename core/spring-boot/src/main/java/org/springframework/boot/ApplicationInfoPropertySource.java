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

package org.springframework.boot;

import java.util.HashMap;
import java.util.Map;

import org.jspecify.annotations.Nullable;

import org.springframework.boot.env.PropertySourceInfo;
import org.springframework.boot.system.ApplicationPid;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.util.StringUtils;

/**
 * {@link PropertySource} which provides information about the application, like the
 * process ID (PID) or the version.
 * <p>提供关于应用程序信息的 {@link PropertySource}，例如进程 ID (PID) 或版本。</p>
 *
 * @author Moritz Halbritter
 */
class ApplicationInfoPropertySource extends MapPropertySource implements PropertySourceInfo {

	static final String NAME = "applicationInfo";

	ApplicationInfoPropertySource(@Nullable Class<?> mainClass) {
		super(NAME, getProperties(readVersion(mainClass)));
	}

	ApplicationInfoPropertySource(@Nullable String applicationVersion) {
		super(NAME, getProperties(applicationVersion));
	}

	@Override
	public boolean isImmutable() {
		return true;
	}

	private static Map<String, Object> getProperties(@Nullable String applicationVersion) {
		Map<String, Object> result = new HashMap<>();
		if (StringUtils.hasText(applicationVersion)) {
			result.put("spring.application.version", applicationVersion);
		}
		ApplicationPid applicationPid = new ApplicationPid();
		Long pid = applicationPid.toLong();
		if (pid != null) {
			result.put("spring.application.pid", pid);
		}
		return result;
	}

	private static @Nullable String readVersion(@Nullable Class<?> applicationClass) {
		Package sourcePackage = (applicationClass != null) ? applicationClass.getPackage() : null;
		return (sourcePackage != null) ? sourcePackage.getImplementationVersion() : null;
	}

	/**
	 * Moves the {@link ApplicationInfoPropertySource} to the end of the environment's
	 * property sources.
	 * <p>将 {@link ApplicationInfoPropertySource} 移动到环境属性源的末尾。</p>
	 * @param environment the environment
	 * <p>环境</p>
	 */
	static void moveToEnd(ConfigurableEnvironment environment) {
		MutablePropertySources propertySources = environment.getPropertySources();
		PropertySource<?> propertySource = propertySources.remove(NAME);
		if (propertySource != null) {
			propertySources.addLast(propertySource);
		}
	}

}