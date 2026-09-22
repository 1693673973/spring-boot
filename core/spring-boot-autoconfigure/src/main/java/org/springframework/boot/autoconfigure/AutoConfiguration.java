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
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.annotation.ImportCandidates;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AliasFor;

/**
 * Indicates that a class provides configuration that can be automatically applied by
 * Spring Boot. Auto-configuration classes are regular
 * {@link Configuration @Configuration} with the exception that
 * {@link Configuration#proxyBeanMethods() proxyBeanMethods} is always {@code false}. They
 * are located using {@link ImportCandidates}.
 * <p>指示一个类提供了可由 Spring Boot 自动应用的配置。自动配置类是常规的 {@link Configuration @Configuration}，但例外是 {@link Configuration#proxyBeanMethods() proxyBeanMethods} 始终为 {@code false}。它们使用 {@link ImportCandidates} 定位。</p>
 * <p>
 * Generally, auto-configuration classes are top-level classes that are marked as
 * {@link Conditional @Conditional} (most often using
 * {@link ConditionalOnClass @ConditionalOnClass} and
 * {@link ConditionalOnMissingBean @ConditionalOnMissingBean} annotations).
 * <p>通常，自动配置类是标记为 {@link Conditional @Conditional} 的顶级类（最常使用 {@link ConditionalOnClass @ConditionalOnClass} 和 {@link ConditionalOnMissingBean @ConditionalOnMissingBean} 注解）。</p>
 *
 * @author Moritz Halbritter
 * @see EnableAutoConfiguration
 * @see AutoConfigureBefore
 * @see AutoConfigureAfter
 * @see Conditional
 * @see ConditionalOnClass
 * @see ConditionalOnMissingBean
 * @since 2.7.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore
@AutoConfigureAfter
public @interface AutoConfiguration {

	/**
	 * Explicitly specify the name of the Spring bean definition associated with the
	 * {@code @AutoConfiguration} class. If left unspecified (the common case), a bean
	 * name will be automatically generated.
	 * <p>显式指定与 {@code @AutoConfiguration} 类关联的 Spring bean 定义的名称。如果未指定（常见情况），将自动生成 bean 名称。</p>
	 * <p>
	 * The custom name applies only if the {@code @AutoConfiguration} class is picked up
	 * through component scanning or supplied directly to an
	 * {@link AnnotationConfigApplicationContext}. If the {@code @AutoConfiguration} class
	 * is registered as a traditional XML bean definition, the name/id of the bean element
	 * will take precedence.
	 * <p>仅当 {@code @AutoConfiguration} 类通过组件扫描被发现或直接提供给 {@link AnnotationConfigApplicationContext} 时，自定义名称才适用。如果 {@code @AutoConfiguration} 类注册为传统的 XML bean 定义，则 bean 元素的名称/id 将优先。</p>
	 * @return the explicit component name, if any (or empty String otherwise)
	 * <p>显式组件名称（如果有），否则为空字符串</p>
	 * @see AnnotationBeanNameGenerator
	 */
	@AliasFor(annotation = Configuration.class)
	String value() default "";

	/**
	 * The auto-configuration classes that should have not yet been applied.
	 * <p>尚未应用的自动配置类。</p>
	 * <p>
	 * Since this annotation is parsed by loading class bytecode, it is safe to specify
	 * classes here that may ultimately not be on the classpath, but only if this
	 * annotation is directly on the affected component and <b>not</b> if this annotation
	 * is used as a composed, meta-annotation. In order to use this annotation as a
	 * meta-annotation, only use the {@link #beforeName} attribute.
	 * <p>由于此注解是通过加载类字节码来解析的，因此在此指定最终可能不在类路径上的类是安全的，但前提是此注解直接位于受影响的组件上，而 <b>不是</b> 将此注解用作组合元注解。为了将此注解用作元注解，请仅使用 {@link #beforeName} 属性。</p>
	 * @return the classes
	 * <p>类</p>
	 */
	@AliasFor(annotation = AutoConfigureBefore.class, attribute = "value")
	Class<?>[] before() default {};

	/**
	 * The names of the auto-configuration classes that should have not yet been applied.
	 * In the unusual case that an auto-configuration class is not a top-level class, its
	 * name should use {@code $} to separate it from its containing class, for example
	 * {@code com.example.Outer$NestedAutoConfiguration}.
	 * <p>尚未应用的自动配置类的名称。在自动配置类不是顶级类的特殊情况下，其名称应使用 {@code $} 将其与包含类分隔，例如 {@code com.example.Outer$NestedAutoConfiguration}。</p>
	 * @return the class names
	 * <p>类名</p>
	 */
	@AliasFor(annotation = AutoConfigureBefore.class, attribute = "name")
	String[] beforeName() default {};

	/**
	 * The auto-configuration classes that should have already been applied.
	 * <p>已经应用的自动配置类。</p>
	 * <p>
	 * Since this annotation is parsed by loading class bytecode, it is safe to specify
	 * classes here that may ultimately not be on the classpath, but only if this
	 * annotation is directly on the affected component and <b>not</b> if this annotation
	 * is used as a composed, meta-annotation. In order to use this annotation as a
	 * meta-annotation, only use the {@link #afterName} attribute.
	 * <p>由于此注解是通过加载类字节码来解析的，因此在此指定最终可能不在类路径上的类是安全的，但前提是此注解直接位于受影响的组件上，而 <b>不是</b> 将此注解用作组合元注解。为了将此注解用作元注解，请仅使用 {@link #afterName} 属性。</p>
	 * @return the classes
	 * <p>类</p>
	 */
	@AliasFor(annotation = AutoConfigureAfter.class, attribute = "value")
	Class<?>[] after() default {};

	/**
	 * The names of the auto-configuration classes that should have already been applied.
	 * In the unusual case that an auto-configuration class is not a top-level class, its
	 * class name should use {@code $} to separate it from its containing class, for
	 * example {@code com.example.Outer$NestedAutoConfiguration}.
	 * <p>已经应用的自动配置类的名称。在自动配置类不是顶级类的特殊情况下，其类名应使用 {@code $} 将其与包含类分隔，例如 {@code com.example.Outer$NestedAutoConfiguration}。</p>
	 * @return the class names
	 * <p>类名</p>
	 */
	@AliasFor(annotation = AutoConfigureAfter.class, attribute = "name")
	String[] afterName() default {};

}