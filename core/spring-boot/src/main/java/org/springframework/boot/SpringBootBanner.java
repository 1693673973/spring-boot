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

import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.ansi.AnsiStyle;
import org.springframework.core.env.Environment;

/**
 * Default Banner implementation which writes the 'Spring' banner.
 * <p>默认 Banner 实现，用于写入 'Spring' 横幅。</p>
 *
 * @author Phillip Webb
 */
class SpringBootBanner implements Banner {

	private static final String BANNER = """
			  .   ____          _            __ _ _
			 /\\\\ / ___'_ __ _ _(_)_ __  __ _ \\ \\ \\ \\
			( ( )\\___ | '_ | '_| | '_ \\/ _` | \\ \\ \\ \\
			 \\\\/  ___)| |_)| | | | | || (_| |  ) ) ) )
			  '  |____| .__|_| |_|_| |_\\__, | / / / /
			 =========|_|==============|___/=/_/_/_/
			""";

	private static final String SPRING_BOOT = " :: Spring Boot :: ";

	private static final int STRAP_LINE_SIZE = 42;

	@Override
	public void printBanner(Environment environment, @Nullable Class<?> sourceClass, PrintStream printStream) {
		printStream.println();
		printStream.println(BANNER);
		String version = String.format(" (v%s)", SpringBootVersion.getVersion());
		String padding = " ".repeat(Math.max(0, STRAP_LINE_SIZE - (version.length() + SPRING_BOOT.length())));
		printStream.println(AnsiOutput.toString(AnsiColor.GREEN, SPRING_BOOT, AnsiColor.DEFAULT, padding,
				AnsiStyle.FAINT, version));
		printStream.println();
	}

}