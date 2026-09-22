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

package org.springframework.boot.autoconfigure.web;

import java.util.List;

import org.jspecify.annotations.Nullable;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

/**
 * {@link RuntimeHintsRegistrar} for default locations of web resources.
 * <p>用于 Web 资源默认位置的 {@link RuntimeHintsRegistrar}。</p>
 *
 * @author Stephane Nicoll
 * @since 3.0.0
 */
public class WebResourcesRuntimeHints implements RuntimeHintsRegistrar {

	private static final List<String> DEFAULT_LOCATIONS = List.of("META-INF/resources/", "resources/", "static/",
			"public/");

	@Override
	public void registerHints(RuntimeHints hints, @Nullable ClassLoader classLoader) {
		ClassLoader classLoaderToUse = (classLoader != null) ? classLoader : getClass().getClassLoader();
		String[] locations = DEFAULT_LOCATIONS.stream()
				.filter((candidate) -> classLoaderToUse.getResource(candidate) != null)
				.map((location) -> location + "**")
				.toArray(String[]::new);
		if (locations.length > 0) {
			hints.resources().registerPattern((hint) -> hint.includes(locations));
		}
	}

}