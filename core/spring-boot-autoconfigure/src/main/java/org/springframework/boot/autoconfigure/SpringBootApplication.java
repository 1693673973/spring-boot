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

import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.TypeExcludeFilter;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.annotation.AliasFor;

/**
 * Indicates a {@link Configuration configuration} class that declares one or more
 * {@link Bean @Bean} methods and also triggers {@link EnableAutoConfiguration
 * auto-configuration} and {@link ComponentScan component scanning}. This is a convenience
 * annotation that is equivalent to declaring {@code @SpringBootConfiguration},
 * {@code @EnableAutoConfiguration} and {@code @ComponentScan}.
 * <p>指示一个 {@link Configuration 配置} 类，它声明一个或多个 {@link Bean @Bean} 方法，并触发 {@link EnableAutoConfiguration 自动配置} 和 {@link ComponentScan 组件扫描}。这是一个便捷注解，等同于声明 {@code @SpringBootConfiguration}、{@code @EnableAutoConfiguration} 和 {@code @ComponentScan}。</p>
 *
 * @author Phillip Webb
 * @author Stephane Nicoll
 * @author Andy Wilkinson
 * @since 1.2.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(excludeFilters = { @Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
		@Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class) })
public @interface SpringBootApplication {

	/**
	 * Exclude specific auto-configuration classes such that they will never be applied.
	 * <p>排除特定的自动配置类，使其永远不会被应用。</p>
	 *
	 * <p>
	 * Since this annotation is parsed by loading class bytecode, it is safe to specify
	 * classes here that may ultimately not be on the classpath, but only if this
	 * annotation is directly on the affected component and <b>not</b> if this annotation
	 * is used as a composed, meta-annotation. In order to use this annotation as a
	 * meta-annotation, only use the {@link #excludeName} attribute.
	 * <p>由于此注解是通过加载类字节码来解析的，因此在此指定最终可能不在类路径上的类是安全的，但前提是此注解直接位于受影响的组件上，而 <b>不是</b> 将此注解用作组合元注解。为了将此注解用作元注解，请仅使用 {@link #excludeName} 属性。</p>
	 * @return the classes to exclude
	 * <p>要排除的类</p>
	 */
	@AliasFor(annotation = EnableAutoConfiguration.class)
	Class<?>[] exclude() default {};

	/**
	 * Exclude specific auto-configuration class names such that they will never be
	 * applied.
	 * <p>排除特定的自动配置类名，使其永远不会被应用。</p>
	 * @return the class names to exclude
	 * <p>要排除的类名</p>
	 * @since 1.3.0
	 */
	@AliasFor(annotation = EnableAutoConfiguration.class)
	String[] excludeName() default {};

	/**
	 * Base packages to scan for annotated components. Use {@link #scanBasePackageClasses}
	 * for a type-safe alternative to String-based package names.
	 * <p>用于扫描带注解组件的基础包。使用 {@link #scanBasePackageClasses} 作为基于字符串的包名的类型安全替代方案。</p>
	 *
	 * <p>
	 * <strong>Note:</strong> this setting is an alias for
	 * {@link ComponentScan @ComponentScan} only. It has no effect on {@code @Entity}
	 * scanning or Spring Data repository scanning. For those you should add
	 * {@code @EntityScan} and {@code @Enable...Repositories} annotations.
	 * <p><strong>注意：</strong>此设置仅是 {@link ComponentScan @ComponentScan} 的别名。它对 {@code @Entity} 扫描或 Spring Data 仓库扫描没有影响。对于这些，您应该添加 {@code @EntityScan} 和 {@code @Enable...Repositories} 注解。</p>
	 * @return base packages to scan
	 * <p>要扫描的基础包</p>
	 * @since 1.3.0
	 */
	@AliasFor(annotation = ComponentScan.class, attribute = "basePackages")
	String[] scanBasePackages() default {};

	/**
	 * Type-safe alternative to {@link #scanBasePackages} for specifying the packages to
	 * scan for annotated components. The package of each class specified will be scanned.
	 * <p>用于指定要扫描带注解组件的包的类型安全替代方案。将扫描每个指定类的包。</p>
	 *
	 * <p>
	 * Consider creating a special no-op marker class or interface in each package that
	 * serves no purpose other than being referenced by this attribute.
	 * <p>考虑在每个包中创建一个特殊的无操作标记类或接口，其唯一目的就是被此属性引用。</p>
	 *
	 * <p>
	 * <strong>Note:</strong> this setting is an alias for
	 * {@link ComponentScan @ComponentScan} only. It has no effect on {@code @Entity}
	 * scanning or Spring Data repository scanning. For those you should add
	 * {@code @EntityScan} and {@code @Enable...Repositories} annotations.
	 * <p><strong>注意：</strong>此设置仅是 {@link ComponentScan @ComponentScan} 的别名。它对 {@code @Entity} 扫描或 Spring Data 仓库扫描没有影响。对于这些，您应该添加 {@code @EntityScan} 和 {@code @Enable...Repositories} 注解。</p>
	 * @return base packages to scan
	 * <p>要扫描的基础包</p>
	 * @since 1.3.0
	 */
	@AliasFor(annotation = ComponentScan.class, attribute = "basePackageClasses")
	Class<?>[] scanBasePackageClasses() default {};

	/**
	 * The {@link BeanNameGenerator} class to be used for naming detected components
	 * within the Spring container.
	 * <p>用于命名 Spring 容器内检测到的组件的 {@link BeanNameGenerator} 类。</p>
	 *
	 * <p>
	 * The default value of the {@link BeanNameGenerator} interface itself indicates that
	 * the scanner used to process this {@code @SpringBootApplication} annotation should
	 * use its inherited bean name generator, e.g. the default
	 * {@link AnnotationBeanNameGenerator} or any custom instance supplied to the
	 * application context at bootstrap time.
	 * <p>{@link BeanNameGenerator} 接口本身的默认值表示，用于处理此 {@code @SpringBootApplication} 注解的扫描器应使用其继承的 bean 名称生成器，例如默认的 {@link AnnotationBeanNameGenerator} 或在引导时提供给应用程序上下文的任何自定义实例。</p>
	 * @return {@link BeanNameGenerator} to use
	 * <p>要使用的 {@link BeanNameGenerator}</p>
	 * @see SpringApplication#setBeanNameGenerator(BeanNameGenerator)
	 * @since 2.3.0
	 */
	@AliasFor(annotation = ComponentScan.class, attribute = "nameGenerator")
	Class<? extends BeanNameGenerator> nameGenerator() default BeanNameGenerator.class;

	/**
	 * Specify whether {@link Bean @Bean} methods should get proxied in order to enforce
	 * bean lifecycle behavior, e.g. to return shared singleton bean instances even in
	 * case of direct {@code @Bean} method calls in user code. This feature requires
	 * method interception, implemented through a runtime-generated CGLIB subclass which
	 * comes with limitations such as the configuration class and its methods not being
	 * allowed to declare {@code final}.
	 * <p>指定是否应对 {@link Bean @Bean} 方法进行代理，以强制执行 bean 生命周期行为，例如在用户代码中直接调用 {@code @Bean} 方法时也返回共享的单例 bean 实例。此功能需要方法拦截，通过运行时生成的 CGLIB 子类实现，这带来了一些限制，例如配置类及其方法不允许声明为 {@code final}。</p>
	 *
	 * <p>
	 * The default is {@code true}, allowing for 'inter-bean references' within the
	 * configuration class as well as for external calls to this configuration's
	 * {@code @Bean} methods, e.g. from another configuration class. If this is not needed
	 * since each of this particular configuration's {@code @Bean} methods is
	 * self-contained and designed as a plain factory method for container use, switch
	 * this flag to {@code false} in order to avoid CGLIB subclass processing.
	 * <p>默认值为 {@code true}，允许在配置类内进行“bean 间引用”，以及从另一个配置类等外部调用此配置的 {@code @Bean} 方法。如果不需要这样做，因为此特定配置的每个 {@code @Bean} 方法都是自包含的，并设计为供容器使用的普通工厂方法，请将此标志切换为 {@code false} 以避免 CGLIB 子类处理。</p>
	 *
	 * <p>
	 * Turning off bean method interception effectively processes {@code @Bean} methods
	 * individually like when declared on non-{@code @Configuration} classes, a.k.a.
	 * "@Bean Lite Mode" (see {@link Bean @Bean's javadoc}). It is therefore behaviorally
	 * equivalent to removing the {@code @Configuration} stereotype.
	 * <p>关闭 bean 方法拦截实际上会像在非 {@code @Configuration} 类上声明时那样单独处理 {@code @Bean} 方法，也称为“@Bean Lite 模式”（参见 {@link Bean @Bean 的 javadoc}）。因此，它在行为上等同于移除 {@code @Configuration} 构造型。</p>
	 * @since 2.2
	 * @return whether to proxy {@code @Bean} methods
	 * <p>是否代理 {@code @Bean} 方法</p>
	 */
	@AliasFor(annotation = Configuration.class)
	boolean proxyBeanMethods() default true;

}