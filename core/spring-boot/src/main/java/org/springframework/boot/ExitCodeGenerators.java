/*
 * Copyright 2012-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// 版权所有 2012-至今 原始作者
// 根据 Apache 许可证 2.0 版本（"许可证"）授权；
// 您仅在遵守许可证的情况下才可使用本文件。
// 您可以从以下地址获取许可证副本：
//      https://www.apache.org/licenses/LICENSE-2.0
// 除非适用法律要求或书面同意，按许可证分发的软件是基于"按原样"基础分发的，
// 不附带任何明示或暗示的保证或条件。
// 请查看许可证以了解管辖权限和限制的具体语言。

package org.springframework.boot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.annotation.Order;
import org.springframework.util.Assert;

/**
 * Maintains an ordered collection of {@link ExitCodeGenerator} instances and allows the
 * final exit code to be calculated. Generators are ordered by {@link Order @Order} and
 * {@link Ordered}.
 * <p>维护一个有序的 {@link ExitCodeGenerator} 实例集合，并允许计算最终的退出代码。生成器通过 {@link Order @Order} 和 {@link Ordered} 进行排序。</p>
 *
 * @author Dave Syer
 * @author Phillip Webb
 * @author GenKui Du
 * @see #getExitCode()
 * @see ExitCodeGenerator
 */
class ExitCodeGenerators implements Iterable<ExitCodeGenerator> {

	private final List<ExitCodeGenerator> generators = new ArrayList<>();

	void addAll(Throwable exception, ExitCodeExceptionMapper... mappers) {
		Assert.notNull(exception, "'exception' must not be null");
		Assert.notNull(mappers, "'mappers' must not be null");
		addAll(exception, Arrays.asList(mappers));
	}

	void addAll(Throwable exception, Iterable<? extends ExitCodeExceptionMapper> mappers) {
		Assert.notNull(exception, "'exception' must not be null");
		Assert.notNull(mappers, "'mappers' must not be null");
		for (ExitCodeExceptionMapper mapper : mappers) {
			add(exception, mapper);
		}
	}

	void add(Throwable exception, ExitCodeExceptionMapper mapper) {
		Assert.notNull(exception, "'exception' must not be null");
		Assert.notNull(mapper, "'mapper' must not be null");
		add(new MappedExitCodeGenerator(exception, mapper));
	}

	void addAll(ExitCodeGenerator... generators) {
		Assert.notNull(generators, "'generators' must not be null");
		addAll(Arrays.asList(generators));
	}

	void addAll(Iterable<? extends ExitCodeGenerator> generators) {
		Assert.notNull(generators, "'generators' must not be null");
		for (ExitCodeGenerator generator : generators) {
			add(generator);
		}
	}

	void add(ExitCodeGenerator generator) {
		Assert.notNull(generator, "'generator' must not be null");
		this.generators.add(generator);
		AnnotationAwareOrderComparator.sort(this.generators);
	}

	@Override
	public Iterator<ExitCodeGenerator> iterator() {
		return this.generators.iterator();
	}

	/**
	 * Get the final exit code that should be returned. The final exit code is the first
	 * non-zero exit code that is {@link ExitCodeGenerator#getExitCode generated}.
	 * <p>获取应返回的最终退出代码。最终退出代码是第一个非零的退出代码，该代码由 {@link ExitCodeGenerator#getExitCode} 生成。</p>
	 * @return the final exit code.
	 * <p>最终退出代码。</p>
	 */
	int getExitCode() {
		int exitCode = 0;
		for (ExitCodeGenerator generator : this.generators) {
			try {
				int value = generator.getExitCode();
				if (value != 0) {
					exitCode = value;
					break;
				}
			}
			catch (Exception ex) {
				exitCode = 1;
				ex.printStackTrace();
			}
		}
		return exitCode;
	}

	/**
	 * Adapts an {@link ExitCodeExceptionMapper} to an {@link ExitCodeGenerator}.
	 * <p>将 {@link ExitCodeExceptionMapper} 适配为 {@link ExitCodeGenerator}。</p>
	 */
	private static class MappedExitCodeGenerator implements ExitCodeGenerator {

		private final Throwable exception;

		private final ExitCodeExceptionMapper mapper;

		MappedExitCodeGenerator(Throwable exception, ExitCodeExceptionMapper mapper) {
			this.exception = exception;
			this.mapper = mapper;
		}

		@Override
		public int getExitCode() {
			return this.mapper.getExitCode(this.exception);
		}

	}

}