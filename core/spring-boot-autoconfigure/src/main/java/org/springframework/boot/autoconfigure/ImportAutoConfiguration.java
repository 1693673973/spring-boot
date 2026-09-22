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
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.context.annotation.ImportCandidates;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

/**
 * Import and apply the specified auto-configuration classes. Applies the same ordering
 * rules as {@code @EnableAutoConfiguration} but restricts the auto-configuration classes
 * to the specified set, rather than consulting {@link ImportCandidates}.
 * <p>导入并应用指定的自动配置类。应用与 {@code @EnableAutoConfiguration} 相同的排序规则，
 * 但将自动配置类限制为指定的集合，而不是查询 {@link ImportCandidates}。</p>
 *
 * <p>
 * Can also be used to {@link #exclude()} specific auto-configuration classes such that
 * they will never be applied.
 * <p>也可用于 {@link #exclude()} 特定的自动配置类，使其永远不会被应用。</p>
 *
 * <p>
 * Generally, {@code @EnableAutoConfiguration} should be used in preference to this
 * annotation, however, {@code @ImportAutoConfiguration} can be useful in some situations
 * and especially when writing tests.
 * <p>通常，应优先使用 {@code @EnableAutoConfiguration} 而不是此注解，
 * 但是，{@code @ImportAutoConfiguration} 在某些情况下很有用，尤其是在编写测试时。</p>
 *
 * @author Phillip Webb
 * @author Andy Wilkinson
 * @since 1.3.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(ImportAutoConfigurationImportSelector.class)
public @interface ImportAutoConfiguration {

	/**
	 * The auto-configuration classes that should be imported. This is an alias for
	 * {@link #classes()}.
	 * <p>应导入的自动配置类。这是 {@link #classes()} 的别名。</p>
	 *
	 * @return the classes to import
	 * <p>要导入的类</p>
	 */
	@AliasFor("classes")
	Class<?>[] value() default {};

	/**
	 * The auto-configuration classes that should be imported. When empty, the classes are
	 * specified using a file in {@code META-INF/spring} where the file name is the
	 * fully-qualified name of the annotated class, suffixed with {@code .imports}. An
	 * entry in the file may be prefixed with {@code optional:} to indicate that the
	 * imported class should be ignored if it is not on the classpath.
	 * <p>应导入的自动配置类。当为空时，类通过在 {@code META-INF/spring} 中的文件指定，
	 * 文件名是被注解类的全限定名，后缀为 {@code .imports}。文件中的条目可以以
	 * {@code optional:} 为前缀，以指示如果导入的类不在类路径上则应忽略它。</p>
	 *
	 * @return the classes to import
	 * <p>要导入的类</p>
	 */
	@AliasFor("value")
	Class<?>[] classes() default {};

	/**
	 * Exclude specific auto-configuration classes such that they will never be applied.
	 * <p>排除特定的自动配置类，使其永远不会被应用。</p>
	 *
	 * @return the classes to exclude
	 * <p>要排除的类</p>
	 */
	Class<?>[] exclude() default {};

}