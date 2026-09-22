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

package org.springframework.boot.autoconfigure;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * Hint that an {@link EnableAutoConfiguration auto-configuration} should be applied
 * before other specified auto-configuration classes.
 * <p>提示某个 {@link EnableAutoConfiguration 自动配置} 应在其他指定的自动配置类之前应用。</p>
 *
 * <p>
 * As with standard {@link Configuration @Configuration} classes, the order in which
 * auto-configuration classes are applied only affects the order in which their beans are
 * defined. The order in which those beans are subsequently created is unaffected and is
 * determined by each bean's dependencies and any {@link DependsOn @DependsOn}
 * relationships.
 * <p>与标准的 {@link Configuration @Configuration} 类一样，自动配置类的应用顺序仅影响其 bean 的定义顺序。这些 bean 随后创建的顺序不受影响，而是由每个 bean 的依赖关系以及任何 {@link DependsOn @DependsOn} 关系决定。</p>
 *
 * @author Phillip Webb
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Documented
public @interface AutoConfigureBefore {

	/**
	 * The auto-configuration classes that should have not yet been applied.
	 * <p>应尚未应用的自动配置类。</p>
	 *
	 * <p>
	 * Since this annotation is parsed by loading class bytecode, it is safe to specify
	 * classes here that may ultimately not be on the classpath, but only if this
	 * annotation is directly on the affected component and <b>not</b> if this annotation
	 * is used as a composed, meta-annotation. In order to use this annotation as a
	 * meta-annotation, only use the {@link #name} attribute.
	 * <p>由于此注解是通过加载类字节码来解析的，因此在此指定最终可能不在类路径上的类是安全的，但前提是此注解直接位于受影响的组件上，而 <b>不是</b> 将此注解用作组合元注解。为了将此注解用作元注解，请仅使用 {@link #name} 属性。</p>
	 *
	 * @return the classes
	 * <p>类</p>
	 */
	Class<?>[] value() default {};

	/**
	 * The names of the auto-configuration classes that should have not yet been applied.
	 * In the unusual case that an auto-configuration class is not a top-level class, its
	 * class name should use {@code $} to separate it from its containing class, for
	 * example {@code com.example.Outer$NestedAutoConfiguration}.
	 * <p>应尚未应用的自动配置类的名称。在自动配置类不是顶级类的特殊情况下，其类名应使用 {@code $} 将其与包含类分隔，例如 {@code com.example.Outer$NestedAutoConfiguration}。</p>
	 *
	 * @return the class names
	 * <p>类名</p>
	 * @since 1.2.2
	 */
	String[] name() default {};

}