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

/**
 * Interface used to generate an 'exit code' from a running command line
 * {@link SpringApplication}. Can be used on exceptions as well as directly on beans.
 * <p>用于从运行中的命令行 {@link SpringApplication} 生成“退出代码”的接口。可用于异常，也可直接用于 bean。</p>
 *
 * @author Dave Syer
 * @since 1.0.0
 * @see SpringApplication#exit(org.springframework.context.ApplicationContext,
 * ExitCodeGenerator...)
 */
@FunctionalInterface
public interface ExitCodeGenerator {

	/**
	 * Returns the exit code that should be returned from the application.
	 * <p>返回应从应用程序返回的退出代码。</p>
	 * @return the exit code.
	 * <p>退出代码。</p>
	 */
	int getExitCode();

}