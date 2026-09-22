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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
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

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.annotation.ImportCandidates;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Enable auto-configuration of the Spring Application Context, attempting to guess and
 * configure beans that you are likely to need. Auto-configuration classes are usually
 * applied based on your classpath and what beans you have defined.
 * <p>启用 Spring 应用程序上下文的自动配置，尝试猜测并配置您可能需要的 bean。自动配置类通常根据您的类路径和您已定义的 bean 来应用。</p>
 *
 * <p>
 * When using {@link SpringBootApplication @SpringBootApplication}, the auto-configuration
 * of the context is automatically enabled and adding this annotation has therefore no
 * additional effect.
 * <p>使用 {@link SpringBootApplication @SpringBootApplication} 时，上下文的自动配置会自动启用，因此添加此注解没有额外效果。</p>
 *
 * <p>
 * Auto-configuration tries to be as intelligent as possible and will back-away as you
 * define more of your own configuration. You can always manually {@link #exclude()} any
 * configuration that you never want to apply (use {@link #excludeName()} if you don't
 * have access to them). You can also exclude them through the
 * {@code spring.autoconfigure.exclude} property. Auto-configuration is always applied
 * after user-defined beans have been registered.
 * <p>自动配置会尽可能智能，并会在您定义更多自己的配置时退让。您可以随时手动 {@link #exclude()} 任何您永远不想应用的配置（如果您无法访问它们，请使用 {@link #excludeName()}）。您还可以通过 {@code spring.autoconfigure.exclude} 属性排除它们。自动配置总是在用户定义的 bean 注册之后应用。</p>
 *
 * <p>
 * The package of the class that is annotated with {@code @EnableAutoConfiguration},
 * usually through {@code @SpringBootApplication}, has specific significance and is often
 * used as a 'default'. For example, it will be used when scanning for {@code @Entity}
 * classes. It is generally recommended that you place {@code @EnableAutoConfiguration}
 * (if you're not using {@code @SpringBootApplication}) in a root package so that all
 * sub-packages and classes can be searched.
 * <p>使用 {@code @EnableAutoConfiguration}（通常通过 {@code @SpringBootApplication}）标注的类的包具有特殊意义，通常用作“默认值”。例如，在扫描 {@code @Entity} 类时将使用它。通常建议将 {@code @EnableAutoConfiguration}（如果您不使用 {@code @SpringBootApplication}）放在根包中，以便可以搜索所有子包和类。</p>
 *
 * <p>
 * Auto-configuration classes are regular Spring {@link Configuration @Configuration}
 * beans. They are located using {@link ImportCandidates}. Generally auto-configuration
 * beans are {@link Conditional @Conditional} beans (most often using
 * {@link ConditionalOnClass @ConditionalOnClass} and
 * {@link ConditionalOnMissingBean @ConditionalOnMissingBean} annotations).
 * <p>自动配置类是常规的 Spring {@link Configuration @Configuration} bean。它们使用 {@link ImportCandidates} 定位。通常自动配置 bean 是 {@link Conditional @Conditional} bean（最常使用 {@link ConditionalOnClass @ConditionalOnClass} 和 {@link ConditionalOnMissingBean @ConditionalOnMissingBean} 注解）。</p>
 *
 * @author Phillip Webb
 * @author Stephane Nicoll
 * @since 1.0.0
 * @see ConditionalOnBean
 * @see ConditionalOnMissingBean
 * @see ConditionalOnClass
 * @see AutoConfigureAfter
 * @see SpringBootApplication
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@AutoConfigurationPackage
@Import(AutoConfigurationImportSelector.class)
public @interface EnableAutoConfiguration {

	/**
	 * Environment property that can be used to override when auto-configuration is
	 * enabled.
	 *
	 * <p>可用于覆盖自动配置何时启用的环境属性。</p>
	 *
	 */
	String ENABLED_OVERRIDE_PROPERTY = "spring.boot.enableautoconfiguration";

	/**
	 * Exclude specific auto-configuration classes such that they will never be applied.
	 * <p>排除特定的自动配置类，使其永远不会被应用。</p>
	 *
	 * <p>
	 * Since this annotation is parsed by loading class bytecode, it is safe to specify
	 * classes here that may ultimately not be on the classpath, but only if this
	 * annotation is directly on the affected component and <b>not</b> if this annotation
	 * is used as a composed, meta-annotation. In order to use this annotation as a
	 * meta-annotation, only use the {@link #excludeName()} attribute.
	 * <p>由于此注解是通过加载类字节码来解析的，因此在此指定最终可能不在类路径上的类是安全的，但前提是此注解直接位于受影响的组件上，而 <b>不是</b> 将此注解用作组合元注解。为了将此注解用作元注解，请仅使用 {@link #excludeName()} 属性。</p>
	 * @return the classes to exclude
	 * <p>要排除的类</p>
	 */
	Class<?>[] exclude() default {};

	/**
	 * Exclude specific auto-configuration class names such that they will never be
	 * applied.
	 * <p>排除特定的自动配置类名，使其永远不会被应用。</p>
	 * @return the class names to exclude
	 * <p>要排除的类名</p>
	 * @since 1.3.0
	 */
	String[] excludeName() default {};

}