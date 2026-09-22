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

import java.io.PrintStream;

import org.jspecify.annotations.Nullable;

import org.springframework.core.env.Environment;

/**
 * Interface class for writing a banner programmatically.
 * <p>用于以编程方式编写横幅的接口类。</p>
 *
 * @author Phillip Webb
 * @author Michael Stummvoll
 * @author Jeremy Rickard
 * @since 1.2.0
 */
@FunctionalInterface
public interface Banner {

	/**
	 * Print the banner to the specified print stream.
	 * <p>将横幅打印到指定的打印流。</p>
	 * @param environment the spring environment
	 * <p>spring 环境</p>
	 * @param sourceClass the source class for the application or {@code null}
	 * <p>应用程序的源类，或 {@code null}</p>
	 * @param out the output print stream
	 * <p>输出打印流</p>
	 */
	void printBanner(Environment environment, @Nullable Class<?> sourceClass, PrintStream out);

	/**
	 * An enumeration of possible values for configuring the Banner.
	 * <p>用于配置 Banner 的可能值的枚举。</p>
	 */
	enum Mode {

		/**
		 * Disable printing of the banner.
		 *
		 * <p>禁用横幅打印。</p>
		 */
		OFF,

		/**
		 * Print the banner to System.out.
		 *
		 * <p>将横幅打印到 System.out。</p>
		 */
		CONSOLE,

		/**
		 * Print the banner to the log file.
		 *
		 * <p>将横幅打印到日志文件。</p>
		 */
		LOG

	}

}