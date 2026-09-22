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

import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.Arrays;

import org.springframework.boot.SpringApplication.AbandonedRunException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.aot.ContextAotProcessor;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.function.ThrowingSupplier;

/**
 * Entry point for AOT processing of a {@link SpringApplication}.
 * <p>它是 {@link SpringApplication} 的 AOT 处理入口点。</p>
 *
 * <p>
 * <strong>For internal use only.</strong>
 * <p><strong>仅供内部使用。</strong></p>
 *
 * @author Stephane Nicoll
 * @author Andy Wilkinson
 * @author Phillip Webb
 * @since 3.0.0
 */
public class SpringApplicationAotProcessor extends ContextAotProcessor {

	private final String[] applicationArgs;

	/**
	 * Create a new processor for the specified application and settings.
	 * <p>为指定的应用程序和设置创建一个新的处理器。</p>
	 * @param application the application main class
	 * <p>应用程序主类</p>
	 * @param settings the general AOT processor settings
	 * <p>通用 AOT 处理器设置</p>
	 * @param applicationArgs the arguments to provide to the main method
	 * <p>提供给 main 方法的参数</p>
	 */
	public SpringApplicationAotProcessor(Class<?> application, Settings settings, String[] applicationArgs) {
		super(application, settings);
		this.applicationArgs = applicationArgs;
	}

	@Override
	protected GenericApplicationContext prepareApplicationContext(Class<?> application) {
		return new AotProcessorHook(application).run(() -> {
			Method mainMethod = getMainMethod(application);
			mainMethod.setAccessible(true);
			if (mainMethod.getParameterCount() == 0) {
				ReflectionUtils.invokeMethod(mainMethod, null);
			}
			else {
				ReflectionUtils.invokeMethod(mainMethod, null, new Object[] { this.applicationArgs });
			}
			return Void.class;
		});
	}

	private static Method getMainMethod(Class<?> application) throws Exception {
		try {
			return application.getDeclaredMethod("main", String[].class);
		}
		catch (NoSuchMethodException ex) {
			return application.getDeclaredMethod("main");
		}
	}

	public static void main(String[] args) throws Exception {
		int requiredArgs = 6;
		Assert.state(args.length >= requiredArgs, () -> "Usage: " + SpringApplicationAotProcessor.class.getName()
				+ " <applicationMainClass> <sourceOutput> <resourceOutput> <classOutput> <groupId> <artifactId> <originalArgs...>");
		Class<?> application = Class.forName(args[0]);
		Settings settings = Settings.builder()
				.sourceOutput(Paths.get(args[1]))
				.resourceOutput(Paths.get(args[2]))
				.classOutput(Paths.get(args[3]))
				.groupId((StringUtils.hasText(args[4])) ? args[4] : "unspecified")
				.artifactId(args[5])
				.build();
		String[] applicationArgs = (args.length > requiredArgs) ? Arrays.copyOfRange(args, requiredArgs, args.length)
				: new String[0];
		new SpringApplicationAotProcessor(application, settings, applicationArgs).process();
	}

	/**
	 * {@link SpringApplicationHook} used to capture the {@link ApplicationContext} and
	 * trigger early exit of main method.
	 * <p>用于捕获 {@link ApplicationContext} 并触发 main 方法提前退出的 {@link SpringApplicationHook}。</p>
	 */
	private static final class AotProcessorHook implements SpringApplicationHook {

		private final Class<?> application;

		private AotProcessorHook(Class<?> application) {
			this.application = application;
		}

		@Override
		public SpringApplicationRunListener getRunListener(SpringApplication application) {
			return new SpringApplicationRunListener() {

				@Override
				public void contextLoaded(ConfigurableApplicationContext context) {
					throw new AbandonedRunException(context);
				}

			};
		}

		private <T> GenericApplicationContext run(ThrowingSupplier<T> action) {
			try {
				SpringApplication.withHook(this, action);
			}
			catch (AbandonedRunException ex) {
				ApplicationContext context = ex.getApplicationContext();
				Assert.state(context instanceof GenericApplicationContext,
						() -> "AOT processing requires a GenericApplicationContext but got a "
								+ ((context != null) ? context.getClass().getName() : "null"));
				return (GenericApplicationContext) context;
			}
			throw new IllegalStateException(
					"No application context available after calling main method of '%s'. Does it run a SpringApplication?"
							.formatted(this.application.getName()));
		}

	}

}