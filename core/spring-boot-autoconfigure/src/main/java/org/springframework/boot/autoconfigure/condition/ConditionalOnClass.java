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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Conditional;

/**
 * {@link Conditional @Conditional} that only matches when the specified classes are on
 * the classpath.
 * <p>{@link Conditional @Conditional}，仅当指定的类位于类路径上时才匹配。</p>
 *
 * <p>
 * A {@code Class} {@link #value() value} can be safely specified on
 * {@code @Configuration} classes as the annotation metadata is parsed by using ASM before
 * the class is loaded. If a class reference cannot be used then a {@link #name() name}
 * {@code String} attribute can be used.
 * <p>{@code Class} {@link #value() 值}可以安全地在 {@code @Configuration} 类上指定，因为注解元数据在类加载之前使用 ASM 解析。如果无法使用类引用，则可以使用 {@link #name() 名称} {@code String} 属性。</p>
 *
 * <p>
 * <b>Note:</b> Extra care must be taken when using {@code @ConditionalOnClass} on
 * {@code @Bean} methods where typically the return type is the target of the condition.
 * Before the condition on the method applies, the JVM will have loaded the class and
 * potentially processed method references which will fail if the class is not present. To
 * handle this scenario, a separate {@code @Configuration} class should be used to isolate
 * the condition. For example: <pre class="code">
 * &#064;AutoConfiguration
 * public class MyAutoConfiguration {
 *
 * 	&#64;Configuration(proxyBeanMethods = false)
 * 	&#64;ConditionalOnClass(SomeService.class)
 * 	public static class SomeServiceConfiguration {
 *
 * 		&#064;Bean
 * 		&#064;ConditionalOnMissingBean
 * 		public SomeService someService() {
 * 			return new SomeService();
 * 		}
 *
 * 	}
 *
 * }</pre>
 * <p><b>注意：</b>在 {@code @Bean} 方法上使用 {@code @ConditionalOnClass} 时必须格外小心，因为通常返回类型是条件的目标。在方法上的条件应用之前，JVM 将已加载该类并可能处理方法引用，如果该类不存在则会失败。为了处理这种情况，应使用单独的 {@code @Configuration} 类来隔离条件。例如：</p>
 *
 * @author Phillip Webb
 * @since 1.0.0
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(OnClassCondition.class)
public @interface ConditionalOnClass {

	/**
	 * The classes that must be present.
	 * <p>必须存在的类。</p>
	 *
	 * <p>
	 * Since this annotation is parsed by loading class bytecode, it is safe to specify
	 * classes here that may ultimately not be on the classpath, but only if this
	 * annotation is directly on the affected component and <b>not</b> if this annotation
	 * is used as a composed, meta-annotation. In order to use this annotation as a
	 * meta-annotation, only use the {@link #name} attribute.
	 * <p>由于此注解是通过加载类字节码来解析的，因此在此指定最终可能不在类路径上的类是安全的，但前提是此注解直接位于受影响的组件上，而 <b>不是</b> 将此注解用作组合元注解。为了将此注解用作元注解，请仅使用 {@link #name} 属性。</p>
	 *
	 * @return the classes that must be present
	 * <p>必须存在的类</p>
	 */
	Class<?>[] value() default {};

	/**
	 * The classes names that must be present.
	 * <p>必须存在的类名。</p>
	 *
	 * @return the class names that must be present.
	 * <p>必须存在的类名</p>
	 */
	String[] name() default {};

}