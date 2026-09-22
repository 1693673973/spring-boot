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

import java.lang.StackWalker.StackFrame;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.crac.management.CRaCMXBean;
import org.jspecify.annotations.Nullable;

import org.springframework.aot.AotDetector;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.groovy.GroovyBeanDefinitionReader;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.bootstrap.BootstrapRegistry;
import org.springframework.boot.bootstrap.BootstrapRegistryInitializer;
import org.springframework.boot.bootstrap.DefaultBootstrapContext;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.boot.env.DefaultPropertiesPropertySource;
import org.springframework.boot.system.JavaVersion;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.aot.AotApplicationContextInitializer;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.NativeDetector;
import org.springframework.core.OrderComparator;
import org.springframework.core.OrderComparator.OrderSourceProvider;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.CommandLinePropertySource;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.core.io.support.SpringFactoriesLoader.ArgumentResolver;
import org.springframework.core.metrics.ApplicationStartup;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.function.ThrowingConsumer;
import org.springframework.util.function.ThrowingSupplier;

/**
 * Class that can be used to bootstrap and launch a Spring application from a Java main
 * method. By default class will perform the following steps to bootstrap your
 * application:
 * <p>可用于从 Java main 方法引导和启动 Spring 应用程序的类。默认情况下，该类将执行以下步骤来引导您的应用程序：</p>
 *
 * <ul>
 * <li>Create an appropriate {@link ApplicationContext} instance (depending on your
 * classpath)</li>
 * <li>创建适当的 {@link ApplicationContext} 实例（取决于您的类路径）</li>
 * <li>Register a {@link CommandLinePropertySource} to expose command line arguments as
 * Spring properties</li>
 * <li>注册 {@link CommandLinePropertySource} 以将命令行参数公开为 Spring 属性</li>
 * <li>Refresh the application context, loading all singleton beans</li>
 * <li>刷新应用程序上下文，加载所有单例 bean</li>
 * <li>Trigger any {@link CommandLineRunner} beans</li>
 * <li>触发任何 {@link CommandLineRunner} bean</li>
 * </ul>
 *
 * In most circumstances the static {@link #run(Class, String[])} method can be called
 * directly from your {@literal main} method to bootstrap your application:
 * <p>在大多数情况下，可以直接从您的 {@literal main} 方法调用静态 {@link #run(Class, String[])} 方法来引导您的应用程序：</p>
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableAutoConfiguration
 * public class MyApplication  {
 *
 *   // ... Bean definitions
 *   // ... Bean 定义
 *
 *   public static void main(String[] args) {
 *     SpringApplication.run(MyApplication.class, args);
 *   }
 * }
 * </pre>
 *
 * <p>
 * For more advanced configuration a {@link SpringApplication} instance can be created and
 * customized before being run:
 * <p>对于更高级的配置，可以在运行之前创建并自定义 {@link SpringApplication} 实例：</p>
 *
 * <pre class="code">
 * public static void main(String[] args) {
 *   SpringApplication application = new SpringApplication(MyApplication.class);
 *   // ... customize application settings here
 *   // ... 在此处自定义应用程序设置
 *   application.run(args)
 * }
 * </pre>
 *
 * {@link SpringApplication}s can read beans from a variety of different sources. It is
 * generally recommended that a single {@code @Configuration} class is used to bootstrap
 * your application, however, you may also set {@link #getSources() sources} from:
 * <p>{@link SpringApplication} 可以从各种不同的源读取 bean。通常建议使用单个 {@code @Configuration} 类来引导您的应用程序，但是，您也可以从以下位置设置 {@link #getSources() 源}：</p>
 * <ul>
 * <li>The fully qualified class name to be loaded by
 * {@link AnnotatedBeanDefinitionReader}</li>
 * <li>由 {@link AnnotatedBeanDefinitionReader} 加载的完全限定类名</li>
 * <li>The location of an XML resource to be loaded by {@link XmlBeanDefinitionReader}, or
 * a groovy script to be loaded by {@link GroovyBeanDefinitionReader}</li>
 * <li>由 {@link XmlBeanDefinitionReader} 加载的 XML 资源位置，或由 {@link GroovyBeanDefinitionReader} 加载的 groovy 脚本</li>
 * <li>The name of a package to be scanned by {@link ClassPathBeanDefinitionScanner}</li>
 * <li>由 {@link ClassPathBeanDefinitionScanner} 扫描的包名</li>
 * </ul>
 *
 * Configuration properties are also bound to the {@link SpringApplication}. This makes it
 * possible to set {@link SpringApplication} properties dynamically, like additional
 * sources ("spring.main.sources" - a CSV list) the flag to indicate a web environment
 * ("spring.main.web-application-type=none") or the flag to switch off the banner
 * ("spring.main.banner-mode=off").
 * <p>配置属性也会绑定到 {@link SpringApplication}。这使得可以动态设置 {@link SpringApplication} 属性，例如附加源（"spring.main.sources" - CSV 列表）、指示 Web 环境的标志（"spring.main.web-application-type=none"）或关闭横幅的标志（"spring.main.banner-mode=off"）。</p>
 *
 * @author Phillip Webb
 * @author Dave Syer
 * @author Andy Wilkinson
 * @author Christian Dupuis
 * @author Stephane Nicoll
 * @author Jeremy Rickard
 * @author Craig Burke
 * @author Michael Simons
 * @author Madhura Bhave
 * @author Brian Clozel
 * @author Ethan Rubinson
 * @author Chris Bono
 * @author Moritz Halbritter
 * @author Tadaya Tsuyukubo
 * @author Lasse Wulff
 * @author Yanming Zhou
 * @since 1.0.0
 * @see #run(Class, String[])
 * @see #run(Class[], String[])
 * @see #SpringApplication(Class...)
 */
public class SpringApplication {

	/**
	 * Default banner location.
	 * <p>默认横幅位置。</p>
	 */
	public static final String BANNER_LOCATION_PROPERTY_VALUE = SpringApplicationBannerPrinter.DEFAULT_BANNER_LOCATION;

	/**
	 * Banner location property key.
	 * <p>横幅位置属性键。</p>
	 */
	public static final String BANNER_LOCATION_PROPERTY = SpringApplicationBannerPrinter.BANNER_LOCATION_PROPERTY;

	private static final String SYSTEM_PROPERTY_JAVA_AWT_HEADLESS = "java.awt.headless";

	private static final Log logger = LogFactory.getLog(SpringApplication.class);

	static final SpringApplicationShutdownHook shutdownHook = new SpringApplicationShutdownHook();

	private static final ThreadLocal<SpringApplicationHook> applicationHook = new ThreadLocal<>();

	private final Set<Class<?>> primarySources;

	private @Nullable Class<?> mainApplicationClass;

	private boolean addCommandLineProperties = true;

	private boolean addConversionService = true;

	private @Nullable Banner banner;

	private @Nullable ResourceLoader resourceLoader;

	private @Nullable BeanNameGenerator beanNameGenerator;

	private @Nullable ConfigurableEnvironment environment;

	private boolean headless = true;

	private List<ApplicationContextInitializer<?>> initializers = new ArrayList<>();

	private List<ApplicationListener<?>> listeners = new ArrayList<>();

	private @Nullable Map<String, Object> defaultProperties;

	private final List<BootstrapRegistryInitializer> bootstrapRegistryInitializers;

	private Set<String> additionalProfiles = Collections.emptySet();

	private boolean isCustomEnvironment;

	private @Nullable String environmentPrefix;

	private ApplicationContextFactory applicationContextFactory = ApplicationContextFactory.DEFAULT;

	private ApplicationStartup applicationStartup = ApplicationStartup.DEFAULT;

	final ApplicationProperties properties = new ApplicationProperties();

	/**
	 * Create a new {@link SpringApplication} instance. The application context will load
	 * beans from the specified primary sources (see {@link SpringApplication class-level}
	 * documentation for details). The instance can be customized before calling
	 * {@link #run(String...)}.
	 * <p>创建一个新的 {@link SpringApplication} 实例。应用程序上下文将从指定的主源加载 bean（有关详细信息，请参阅 {@link SpringApplication 类级} 文档）。可以在调用 {@link #run(String...)} 之前自定义该实例。</p>
	 * @param primarySources the primary bean sources
	 * <p>主 bean 源</p>
	 * @see #run(Class, String[])
	 * @see #SpringApplication(ResourceLoader, Class...)
	 * @see #setSources(Set)
	 */
	public SpringApplication(Class<?>... primarySources) {
		this(null, primarySources);
	}

	/**
	 * Create a new {@link SpringApplication} instance. The application context will load
	 * beans from the specified primary sources (see {@link SpringApplication class-level}
	 * documentation for details). The instance can be customized before calling
	 * {@link #run(String...)}.
	 * <p>创建一个新的 {@link SpringApplication} 实例。应用程序上下文将从指定的主源加载 bean（有关详细信息，请参阅 {@link SpringApplication 类级} 文档）。可以在调用 {@link #run(String...)} 之前自定义该实例。</p>
	 * @param resourceLoader the resource loader to use
	 * <p>要使用的资源加载器</p>
	 * @param primarySources the primary bean sources
	 * <p>主 bean 源</p>
	 * @see #run(Class, String[])
	 * @see #setSources(Set)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public SpringApplication(@Nullable ResourceLoader resourceLoader, Class<?>... primarySources) {
		this.resourceLoader = resourceLoader;
		Assert.notNull(primarySources, "'primarySources' must not be null");
		this.primarySources = new LinkedHashSet<>(Arrays.asList(primarySources));
		this.properties.setWebApplicationType(WebApplicationType.deduce());
		this.bootstrapRegistryInitializers = new ArrayList<>(
				getSpringFactoriesInstances(BootstrapRegistryInitializer.class));
		setInitializers((Collection) getSpringFactoriesInstances(ApplicationContextInitializer.class));
		setListeners((Collection) getSpringFactoriesInstances(ApplicationListener.class));
		this.mainApplicationClass = deduceMainApplicationClass();
	}

	private @Nullable Class<?> deduceMainApplicationClass() {
		return StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
				.walk(this::findMainClass)
				.orElse(null);
	}

	private Optional<Class<?>> findMainClass(Stream<StackFrame> stack) {
		return stack.filter((frame) -> Objects.equals(frame.getMethodName(), "main"))
				.findFirst()
				.map(StackWalker.StackFrame::getDeclaringClass);
	}

	/**
	 * Run the Spring application, creating and refreshing a new
	 * {@link ApplicationContext}.
	 * <p>运行 Spring 应用程序，创建并刷新新的 {@link ApplicationContext}。</p>
	 * @param args the application arguments (usually passed from a Java main method)
	 * <p>应用程序参数（通常从 Java main 方法传递）</p>
	 * @return a running {@link ApplicationContext}
	 * <p>正在运行的 {@link ApplicationContext}</p>
	 */
	public ConfigurableApplicationContext run(String... args) {
		Startup startup = Startup.create();
		if (this.properties.isRegisterShutdownHook()) {
			SpringApplication.shutdownHook.enableShutdownHookAddition();
		}
		DefaultBootstrapContext bootstrapContext = createBootstrapContext();
		ConfigurableApplicationContext context = null;
		configureHeadlessProperty();
		SpringApplicationRunListeners listeners = getRunListeners(args);
		listeners.starting(bootstrapContext, this.mainApplicationClass);
		try {
			ApplicationArguments applicationArguments = new DefaultApplicationArguments(args);
			ConfigurableEnvironment environment = prepareEnvironment(listeners, bootstrapContext, applicationArguments);
			Banner printedBanner = printBanner(environment);
			context = createApplicationContext();
			context.setApplicationStartup(this.applicationStartup);
			prepareContext(bootstrapContext, context, environment, listeners, applicationArguments, printedBanner);
			refreshContext(context);
			afterRefresh(context, applicationArguments);
			Duration timeTakenToStarted = startup.started();
			if (this.properties.isLogStartupInfo()) {
				new StartupInfoLogger(this.mainApplicationClass, environment).logStarted(getApplicationLog(), startup);
			}
			listeners.started(context, timeTakenToStarted);
			callRunners(context, applicationArguments);
		}
		catch (Throwable ex) {
			throw handleRunFailure(context, ex, listeners);
		}
		try {
			if (context.isRunning()) {
				listeners.ready(context, startup.ready());
			}
		}
		catch (Throwable ex) {
			throw handleRunFailure(context, ex, null);
		}
		return context;
	}

	private DefaultBootstrapContext createBootstrapContext() {
		DefaultBootstrapContext bootstrapContext = new DefaultBootstrapContext();
		this.bootstrapRegistryInitializers.forEach((initializer) -> initializer.initialize(bootstrapContext));
		return bootstrapContext;
	}

	private ConfigurableEnvironment prepareEnvironment(SpringApplicationRunListeners listeners,
			DefaultBootstrapContext bootstrapContext, ApplicationArguments applicationArguments) {
		// Create and configure the environment
		// 创建并配置环境
		ConfigurableEnvironment environment = getOrCreateEnvironment();
		configureEnvironment(environment, applicationArguments.getSourceArgs());
		ConfigurationPropertySources.attach(environment);
		listeners.environmentPrepared(bootstrapContext, environment);
		ApplicationInfoPropertySource.moveToEnd(environment);
		DefaultPropertiesPropertySource.moveToEnd(environment);
		Assert.state(!environment.containsProperty("spring.main.environment-prefix"),
				"Environment prefix cannot be set via properties.");
		bindToSpringApplication(environment);
		if (!this.isCustomEnvironment) {
			EnvironmentConverter environmentConverter = new EnvironmentConverter(getClassLoader());
			environment = environmentConverter.convertEnvironmentIfNecessary(environment, deduceEnvironmentClass());
		}
		ConfigurationPropertySources.attach(environment);
		return environment;
	}

	private Class<? extends ConfigurableEnvironment> deduceEnvironmentClass() {
		WebApplicationType webApplicationType = this.properties.getWebApplicationType();
		Class<? extends ConfigurableEnvironment> environmentType = this.applicationContextFactory
				.getEnvironmentType(webApplicationType);
		if (environmentType == null && this.applicationContextFactory != ApplicationContextFactory.DEFAULT) {
			environmentType = ApplicationContextFactory.DEFAULT.getEnvironmentType(webApplicationType);
		}
		return (environmentType != null) ? environmentType : ApplicationEnvironment.class;
	}

	private void prepareContext(DefaultBootstrapContext bootstrapContext, ConfigurableApplicationContext context,
			ConfigurableEnvironment environment, SpringApplicationRunListeners listeners,
			ApplicationArguments applicationArguments, @Nullable Banner printedBanner) {
		context.setEnvironment(environment);
		postProcessApplicationContext(context);
		addAotGeneratedInitializerIfNecessary(this.initializers);
		ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
		if (beanFactory instanceof AbstractAutowireCapableBeanFactory autowireCapableBeanFactory) {
			autowireCapableBeanFactory.setAllowCircularReferences(this.properties.isAllowCircularReferences());
			if (beanFactory instanceof DefaultListableBeanFactory listableBeanFactory) {
				listableBeanFactory.setAllowBeanDefinitionOverriding(this.properties.isAllowBeanDefinitionOverriding());
			}
		}
		applyInitializers(context);
		listeners.contextPrepared(context);
		bootstrapContext.close(context);
		if (this.properties.isLogStartupInfo()) {
			logStartupInfo(context);
			logStartupProfileInfo(context);
		}
		// Add boot specific singleton beans
		// 添加 Boot 特定的单例 bean
		beanFactory.registerSingleton("springApplicationArguments", applicationArguments);
		if (printedBanner != null) {
			beanFactory.registerSingleton("springBootBanner", printedBanner);
		}
		if (this.properties.isLazyInitialization()) {
			context.addBeanFactoryPostProcessor(new LazyInitializationBeanFactoryPostProcessor());
		}
		if (this.properties.isKeepAlive()) {
			context.addApplicationListener(new KeepAlive());
		}
		context.addBeanFactoryPostProcessor(new PropertySourceOrderingBeanFactoryPostProcessor(context));
		if (!AotDetector.useGeneratedArtifacts()) {
			// Load the sources
			// 加载源
			Set<Object> sources = getAllSources();
			Assert.state(!ObjectUtils.isEmpty(sources), "No sources defined");
			load(context, sources.toArray(new Object[0]));
		}
		listeners.contextLoaded(context);
	}

	private void addAotGeneratedInitializerIfNecessary(List<ApplicationContextInitializer<?>> initializers) {
		if (AotDetector.useGeneratedArtifacts()) {
			List<ApplicationContextInitializer<?>> aotInitializers = new ArrayList<>(
					initializers.stream().filter(AotApplicationContextInitializer.class::isInstance).toList());
			if (aotInitializers.isEmpty()) {
				Assert.state(this.mainApplicationClass != null, "No application main class found");
				String initializerClassName = this.mainApplicationClass.getName() + "__ApplicationContextInitializer";
				if (!ClassUtils.isPresent(initializerClassName, getClassLoader())) {
					throw new AotInitializerNotFoundException(this.mainApplicationClass, initializerClassName);
				}
				aotInitializers.add(AotApplicationContextInitializer.forInitializerClasses(initializerClassName));
			}
			initializers.removeAll(aotInitializers);
			initializers.addAll(0, aotInitializers);
		}
		if (NativeDetector.inNativeImage()) {
			NativeImageRequirementsException.throwIfNotMet();
		}
	}

	private void refreshContext(ConfigurableApplicationContext context) {
		if (this.properties.isRegisterShutdownHook()) {
			shutdownHook.registerApplicationContext(context);
		}
		refresh(context);
	}

	private void configureHeadlessProperty() {
		System.setProperty(SYSTEM_PROPERTY_JAVA_AWT_HEADLESS,
				System.getProperty(SYSTEM_PROPERTY_JAVA_AWT_HEADLESS, Boolean.toString(this.headless)));
	}

	private SpringApplicationRunListeners getRunListeners(String[] args) {
		ArgumentResolver argumentResolver = ArgumentResolver.of(SpringApplication.class, this);
		argumentResolver = argumentResolver.and(String[].class, args);
		List<SpringApplicationRunListener> listeners = getSpringFactoriesInstances(SpringApplicationRunListener.class,
				argumentResolver);
		SpringApplicationHook hook = applicationHook.get();
		SpringApplicationRunListener hookListener = (hook != null) ? hook.getRunListener(this) : null;
		if (hookListener != null) {
			listeners = new ArrayList<>(listeners);
			listeners.add(hookListener);
		}
		return new SpringApplicationRunListeners(logger, listeners, this.applicationStartup);
	}

	private <T> List<T> getSpringFactoriesInstances(Class<T> type) {
		return getSpringFactoriesInstances(type, null);
	}

	private <T> List<T> getSpringFactoriesInstances(Class<T> type, @Nullable ArgumentResolver argumentResolver) {
		return SpringFactoriesLoader.forDefaultResourceLocation(getClassLoader()).load(type, argumentResolver);
	}

	private ConfigurableEnvironment getOrCreateEnvironment() {
		if (this.environment != null) {
			return this.environment;
		}
		WebApplicationType webApplicationType = this.properties.getWebApplicationType();
		ConfigurableEnvironment environment = this.applicationContextFactory.createEnvironment(webApplicationType);
		if (environment == null && this.applicationContextFactory != ApplicationContextFactory.DEFAULT) {
			environment = ApplicationContextFactory.DEFAULT.createEnvironment(webApplicationType);
		}
		return (environment != null) ? environment : new ApplicationEnvironment();
	}

	/**
	 * Template method delegating to
	 * {@link #configurePropertySources(ConfigurableEnvironment, String[])} and
	 * {@link #configureProfiles(ConfigurableEnvironment, String[])} in that order.
	 * Override this method for complete control over Environment customization, or one of
	 * the above for fine-grained control over property sources or profiles, respectively.
	 * <p>模板方法，依次委托给 {@link #configurePropertySources(ConfigurableEnvironment, String[])} 和 {@link #configureProfiles(ConfigurableEnvironment, String[])}。重写此方法以完全控制 Environment 自定义，或重写上述方法之一以分别对属性源或配置文件进行细粒度控制。</p>
	 * @param environment this application's environment
	 * <p>此应用程序的环境</p>
	 * @param args arguments passed to the {@code run} method
	 * <p>传递给 {@code run} 方法的参数</p>
	 * @see #configureProfiles(ConfigurableEnvironment, String[])
	 * @see #configurePropertySources(ConfigurableEnvironment, String[])
	 */
	protected void configureEnvironment(ConfigurableEnvironment environment, String[] args) {
		if (this.addConversionService) {
			environment.setConversionService(new ApplicationConversionService());
		}
		configurePropertySources(environment, args);
		configureProfiles(environment, args);
	}

	/**
	 * Add, remove or re-order any {@link PropertySource}s in this application's
	 * environment.
	 * <p>在此应用程序的环境中添加、删除或重新排序任何 {@link PropertySource}。</p>
	 * @param environment this application's environment
	 * <p>此应用程序的环境</p>
	 * @param args arguments passed to the {@code run} method
	 * <p>传递给 {@code run} 方法的参数</p>
	 * @see #configureEnvironment(ConfigurableEnvironment, String[])
	 */
	protected void configurePropertySources(ConfigurableEnvironment environment, String[] args) {
		MutablePropertySources sources = environment.getPropertySources();
		if (!CollectionUtils.isEmpty(this.defaultProperties)) {
			DefaultPropertiesPropertySource.addOrMerge(this.defaultProperties, sources);
		}
		if (this.addCommandLineProperties && args.length > 0) {
			String name = CommandLinePropertySource.COMMAND_LINE_PROPERTY_SOURCE_NAME;
			PropertySource<?> source = sources.get(name);
			if (source != null) {
				CompositePropertySource composite = new CompositePropertySource(name);
				composite
						.addPropertySource(new SimpleCommandLinePropertySource("springApplicationCommandLineArgs", args));
				composite.addPropertySource(source);
				sources.replace(name, composite);
			}
			else {
				sources.addFirst(new SimpleCommandLinePropertySource(args));
			}
		}
		environment.getPropertySources().addLast(new ApplicationInfoPropertySource(this.mainApplicationClass));
	}

	/**
	 * Configure which profiles are active (or active by default) for this application
	 * environment. Additional profiles may be activated during configuration file
	 * processing through the {@code spring.profiles.active} property.
	 * <p>配置此应用程序环境中哪些配置文件是活动的（或默认活动的）。在配置文件处理期间，可以通过 {@code spring.profiles.active} 属性激活其他配置文件。</p>
	 * @param environment this application's environment
	 * <p>此应用程序的环境</p>
	 * @param args arguments passed to the {@code run} method
	 * <p>传递给 {@code run} 方法的参数</p>
	 * @see #configureEnvironment(ConfigurableEnvironment, String[])
	 */
	protected void configureProfiles(ConfigurableEnvironment environment, String[] args) {
	}

	/**
	 * Bind the environment to the {@link ApplicationProperties}.
	 * <p>将环境绑定到 {@link ApplicationProperties}。</p>
	 * @param environment the environment to bind
	 * <p>要绑定的环境</p>
	 */
	protected void bindToSpringApplication(ConfigurableEnvironment environment) {
		try {
			Binder.get(environment).bind("spring.main", Bindable.ofInstance(this.properties));
		}
		catch (Exception ex) {
			throw new IllegalStateException("Cannot bind to SpringApplication", ex);
		}
	}

	private @Nullable Banner printBanner(ConfigurableEnvironment environment) {
		if (this.properties.getBannerMode(environment) == Banner.Mode.OFF) {
			return null;
		}
		ResourceLoader resourceLoader = (this.resourceLoader != null) ? this.resourceLoader
				: new DefaultResourceLoader(null);
		SpringApplicationBannerPrinter bannerPrinter = new SpringApplicationBannerPrinter(resourceLoader, this.banner);
		if (this.properties.getBannerMode(environment) == Mode.LOG) {
			return bannerPrinter.print(environment, this.mainApplicationClass, logger);
		}
		return bannerPrinter.print(environment, this.mainApplicationClass, System.out);
	}

	/**
	 * Strategy method used to create the {@link ApplicationContext}. By default this
	 * method will respect any explicitly set application context class or factory before
	 * falling back to a suitable default.
	 * <p>用于创建 {@link ApplicationContext} 的策略方法。默认情况下，此方法将遵循任何显式设置的应用程序上下文类或工厂，然后回退到合适的默认值。</p>
	 * @return the application context (not yet refreshed)
	 * <p>应用程序上下文（尚未刷新）</p>
	 * @see #setApplicationContextFactory(ApplicationContextFactory)
	 */
	protected ConfigurableApplicationContext createApplicationContext() {
		ConfigurableApplicationContext context = this.applicationContextFactory
				.create(this.properties.getWebApplicationType());
		Assert.state(context != null, "ApplicationContextFactory created null context");
		return context;
	}

	/**
	 * Apply any relevant post-processing to the {@link ApplicationContext}. Subclasses
	 * can apply additional processing as required.
	 * <p>对 {@link ApplicationContext} 应用任何相关的后处理。子类可以根据需要应用额外的处理。</p>
	 * @param context the application context
	 * <p>应用程序上下文</p>
	 */
	protected void postProcessApplicationContext(ConfigurableApplicationContext context) {
		if (this.beanNameGenerator != null) {
			context.getBeanFactory()
					.registerSingleton(AnnotationConfigUtils.CONFIGURATION_BEAN_NAME_GENERATOR, this.beanNameGenerator);
		}
		if (this.resourceLoader != null) {
			if (context instanceof GenericApplicationContext genericApplicationContext) {
				genericApplicationContext.setResourceLoader(this.resourceLoader);
			}
			if (context instanceof DefaultResourceLoader defaultResourceLoader) {
				defaultResourceLoader.setClassLoader(this.resourceLoader.getClassLoader());
			}
		}
		if (this.addConversionService) {
			context.getBeanFactory().setConversionService(context.getEnvironment().getConversionService());
		}
	}

	/**
	 * Apply any {@link ApplicationContextInitializer}s to the context before it is
	 * refreshed.
	 * <p>在刷新上下文之前，将任何 {@link ApplicationContextInitializer} 应用于上下文。</p>
	 * @param context the configured ApplicationContext (not refreshed yet)
	 * <p>已配置的 ApplicationContext（尚未刷新）</p>
	 * @see ConfigurableApplicationContext#refresh()
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void applyInitializers(ConfigurableApplicationContext context) {
		for (ApplicationContextInitializer initializer : getInitializers()) {
			Class<?> requiredType = GenericTypeResolver.resolveTypeArgument(initializer.getClass(),
					ApplicationContextInitializer.class);
			Assert.state(requiredType != null,
					() -> "No generic type found for initializr of type " + initializer.getClass());
			Assert.state(requiredType.isInstance(context), "Unable to call initializer");
			initializer.initialize(context);
		}
	}

	/**
	 * Called to log startup information, subclasses may override to add additional
	 * logging.
	 * <p>调用以记录启动信息，子类可以重写以添加额外的日志记录。</p>
	 * @param context the application context
	 * <p>应用程序上下文</p>
	 * @since 3.4.0
	 */
	protected void logStartupInfo(ConfigurableApplicationContext context) {
		boolean isRoot = context.getParent() == null;
		if (isRoot) {
			new StartupInfoLogger(this.mainApplicationClass, context.getEnvironment()).logStarting(getApplicationLog());
		}
	}

	/**
	 * Called to log active profile information.
	 * <p>调用以记录活动配置文件信息。</p>
	 * @param context the application context
	 * <p>应用程序上下文</p>
	 */
	protected void logStartupProfileInfo(ConfigurableApplicationContext context) {
		Log log = getApplicationLog();
		if (log.isInfoEnabled()) {
			List<String> activeProfiles = quoteProfiles(context.getEnvironment().getActiveProfiles());
			if (ObjectUtils.isEmpty(activeProfiles)) {
				List<String> defaultProfiles = quoteProfiles(context.getEnvironment().getDefaultProfiles());
				String message = String.format("%s default %s: ", defaultProfiles.size(),
						(defaultProfiles.size() <= 1) ? "profile" : "profiles");
				log.info("No active profile set, falling back to " + message
						+ StringUtils.collectionToDelimitedString(defaultProfiles, ", "));
			}
			else {
				String message = (activeProfiles.size() == 1) ? "1 profile is active: "
						: activeProfiles.size() + " profiles are active: ";
				log.info("The following " + message + StringUtils.collectionToDelimitedString(activeProfiles, ", "));
			}
		}
	}

	private List<String> quoteProfiles(String[] profiles) {
		return Arrays.stream(profiles).map((profile) -> "\"" + profile + "\"").toList();
	}

	/**
	 * Returns the {@link Log} for the application. By default will be deduced.
	 * <p>返回应用程序的 {@link Log}。默认情况下将被推导出来。</p>
	 * @return the application log
	 * <p>应用程序日志</p>
	 */
	protected Log getApplicationLog() {
		if (this.mainApplicationClass == null) {
			return logger;
		}
		return LogFactory.getLog(this.mainApplicationClass);
	}

	/**
	 * Load beans into the application context.
	 * <p>将 bean 加载到应用程序上下文中。</p>
	 * @param context the context to load beans into
	 * <p>要将 bean 加载到的上下文</p>
	 * @param sources the sources to load
	 * <p>要加载的源</p>
	 */
	protected void load(ApplicationContext context, Object[] sources) {
		if (logger.isDebugEnabled()) {
			logger.debug("Loading source " + StringUtils.arrayToCommaDelimitedString(sources));
		}
		BeanDefinitionLoader loader = createBeanDefinitionLoader(getBeanDefinitionRegistry(context), sources);
		if (this.beanNameGenerator != null) {
			loader.setBeanNameGenerator(this.beanNameGenerator);
		}
		if (this.resourceLoader != null) {
			loader.setResourceLoader(this.resourceLoader);
		}
		if (this.environment != null) {
			loader.setEnvironment(this.environment);
		}
		loader.load();
	}

	/**
	 * The ResourceLoader that will be used in the ApplicationContext.
	 * <p>将在 ApplicationContext 中使用的 ResourceLoader。</p>
	 * @return the resourceLoader the resource loader that will be used in the
	 * ApplicationContext (or {@code null} if the default)
	 * <p>将在 ApplicationContext 中使用的资源加载器（如果为默认值则为 {@code null}）</p>
	 */
	public @Nullable ResourceLoader getResourceLoader() {
		return this.resourceLoader;
	}

	/**
	 * Either the ClassLoader that will be used in the ApplicationContext (if
	 * {@link #setResourceLoader(ResourceLoader) resourceLoader} is set), or the context
	 * class loader (if not null), or the loader of the Spring {@link ClassUtils} class.
	 * <p>将在 ApplicationContext 中使用的 ClassLoader（如果设置了 {@link #setResourceLoader(ResourceLoader) resourceLoader}），或上下文类加载器（如果不为 null），或 Spring {@link ClassUtils} 类的加载器。</p>
	 * @return a ClassLoader (never null)
	 * <p>ClassLoader（永不为 null）</p>
	 */
	public ClassLoader getClassLoader() {
		if (this.resourceLoader != null) {
			ClassLoader classLoader = this.resourceLoader.getClassLoader();
			Assert.state(classLoader != null, "No classloader found");
			return classLoader;
		}
		ClassLoader classLoader = ClassUtils.getDefaultClassLoader();
		Assert.state(classLoader != null, "No classloader found");
		return classLoader;
	}

	/**
	 * Get the bean definition registry.
	 * <p>获取 bean 定义注册表。</p>
	 * @param context the application context
	 * <p>应用程序上下文</p>
	 * @return the BeanDefinitionRegistry if it can be determined
	 * <p>如果可以确定，则返回 BeanDefinitionRegistry</p>
	 */
	private BeanDefinitionRegistry getBeanDefinitionRegistry(ApplicationContext context) {
		if (context instanceof BeanDefinitionRegistry registry) {
			return registry;
		}
		if (context instanceof AbstractApplicationContext abstractApplicationContext) {
			return (BeanDefinitionRegistry) abstractApplicationContext.getBeanFactory();
		}
		throw new IllegalStateException("Could not locate BeanDefinitionRegistry");
	}

	/**
	 * Factory method used to create the {@link BeanDefinitionLoader}.
	 * <p>用于创建 {@link BeanDefinitionLoader} 的工厂方法。</p>
	 * @param registry the bean definition registry
	 * <p>bean 定义注册表</p>
	 * @param sources the sources to load
	 * <p>要加载的源</p>
	 * @return the {@link BeanDefinitionLoader} that will be used to load beans
	 * <p>将用于加载 bean 的 {@link BeanDefinitionLoader}</p>
	 */
	protected BeanDefinitionLoader createBeanDefinitionLoader(BeanDefinitionRegistry registry, Object[] sources) {
		return new BeanDefinitionLoader(registry, sources);
	}

	/**
	 * Refresh the underlying {@link ApplicationContext}.
	 * <p>刷新底层 {@link ApplicationContext}。</p>
	 * @param applicationContext the application context to refresh
	 * <p>要刷新的应用程序上下文</p>
	 */
	protected void refresh(ConfigurableApplicationContext applicationContext) {
		applicationContext.refresh();
	}

	/**
	 * Called after the context has been refreshed.
	 * <p>在上下文刷新后调用。</p>
	 * @param context the application context
	 * <p>应用程序上下文</p>
	 * @param args the application arguments
	 * <p>应用程序参数</p>
	 */
	protected void afterRefresh(ConfigurableApplicationContext context, ApplicationArguments args) {
	}

	private void callRunners(ConfigurableApplicationContext context, ApplicationArguments args) {
		ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
		String[] beanNames = beanFactory.getBeanNamesForType(Runner.class);
		Map<Runner, String> instancesToBeanNames = new IdentityHashMap<>();
		for (String beanName : beanNames) {
			instancesToBeanNames.put(beanFactory.getBean(beanName, Runner.class), beanName);
		}
		Comparator<Object> comparator = getOrderComparator(beanFactory)
				.withSourceProvider(new FactoryAwareOrderSourceProvider(beanFactory, instancesToBeanNames));
		instancesToBeanNames.keySet().stream().sorted(comparator).forEach((runner) -> callRunner(runner, args));
	}

	private OrderComparator getOrderComparator(ConfigurableListableBeanFactory beanFactory) {
		Comparator<?> dependencyComparator = (beanFactory instanceof DefaultListableBeanFactory defaultListableBeanFactory)
				? defaultListableBeanFactory.getDependencyComparator() : null;
		return (dependencyComparator instanceof OrderComparator orderComparator) ? orderComparator
				: AnnotationAwareOrderComparator.INSTANCE;
	}

	private void callRunner(Runner runner, ApplicationArguments args) {
		if (runner instanceof ApplicationRunner) {
			callRunner(ApplicationRunner.class, runner, (applicationRunner) -> applicationRunner.run(args));
		}
		if (runner instanceof CommandLineRunner) {
			callRunner(CommandLineRunner.class, runner,
					(commandLineRunner) -> commandLineRunner.run(args.getSourceArgs()));
		}
	}

	@SuppressWarnings("unchecked")
	private <R extends Runner> void callRunner(Class<R> type, Runner runner, ThrowingConsumer<R> call) {
		call.throwing(
						(message, ex) -> new IllegalStateException("Failed to execute " + ClassUtils.getShortName(type), ex))
				.accept((R) runner);
	}

	private RuntimeException handleRunFailure(@Nullable ConfigurableApplicationContext context, Throwable exception,
			@Nullable SpringApplicationRunListeners listeners) {
		if (exception instanceof AbandonedRunException abandonedRunException) {
			return abandonedRunException;
		}
		try {
			try {
				handleExitCode(context, exception);
				if (listeners != null) {
					listeners.failed(context, exception);
				}
			}
			finally {
				reportFailure(getExceptionReporters(context), exception);
				if (context != null) {
					context.close();
					shutdownHook.deregisterFailedApplicationContext(context);
				}
			}
		}
		catch (Exception ex) {
			logger.warn("Unable to close ApplicationContext", ex);
		}
		return (exception instanceof RuntimeException runtimeException) ? runtimeException
				: new IllegalStateException(exception);
	}

	private Collection<SpringBootExceptionReporter> getExceptionReporters(
			@Nullable ConfigurableApplicationContext context) {
		try {
			ArgumentResolver argumentResolver = (context != null)
					? ArgumentResolver.of(ConfigurableApplicationContext.class, context) : ArgumentResolver.none();
			return getSpringFactoriesInstances(SpringBootExceptionReporter.class, argumentResolver);
		}
		catch (Throwable ex) {
			return Collections.emptyList();
		}
	}

	private void reportFailure(Collection<SpringBootExceptionReporter> exceptionReporters, Throwable failure) {
		try {
			for (SpringBootExceptionReporter reporter : exceptionReporters) {
				if (reporter.reportException(failure)) {
					registerLoggedException(failure);
					return;
				}
			}
		}
		catch (Throwable ex) {
			// Continue with normal handling of the original failure
			// 继续对原始失败进行正常处理
		}
		if (logger.isErrorEnabled()) {
			if (NativeDetector.inNativeImage()) {
				// Depending on how early the failure was, logging may not work in a
				// 根据失败发生的早期程度，日志记录在
				// native image so we output the stack trace directly to System.out
				// 原生镜像中可能无法工作，因此我们直接将堆栈跟踪输出到 System.out
				// instead.
				// 代替。
				System.out.println("Application run failed");
				failure.printStackTrace(System.out);
			}
			else {
				logger.error("Application run failed", failure);
			}
			registerLoggedException(failure);
		}
	}

	/**
	 * Register that the given exception has been logged. By default, if the running in
	 * the main thread, this method will suppress additional printing of the stacktrace.
	 * <p>注册给定异常已被记录。默认情况下，如果在主线程中运行，此方法将抑制堆栈跟踪的额外打印。</p>
	 * @param exception the exception that was logged
	 * <p>已记录的异常</p>
	 */
	protected void registerLoggedException(Throwable exception) {
		SpringBootExceptionHandler handler = getSpringBootExceptionHandler();
		if (handler != null) {
			handler.registerLoggedException(exception);
		}
	}

	private void handleExitCode(@Nullable ConfigurableApplicationContext context, Throwable exception) {
		int exitCode = getExitCodeFromException(context, exception);
		if (exitCode != 0) {
			if (context != null) {
				context.publishEvent(new ExitCodeEvent(context, exitCode));
			}
			SpringBootExceptionHandler handler = getSpringBootExceptionHandler();
			if (handler != null) {
				handler.registerExitCode(exitCode);
			}
		}
	}

	private int getExitCodeFromException(@Nullable ConfigurableApplicationContext context, Throwable exception) {
		int exitCode = getExitCodeFromMappedException(context, exception);
		if (exitCode == 0) {
			exitCode = getExitCodeFromExitCodeGeneratorException(exception);
		}
		return exitCode;
	}

	private int getExitCodeFromMappedException(@Nullable ConfigurableApplicationContext context, Throwable exception) {
		if (context == null || !context.isActive()) {
			return 0;
		}
		ExitCodeGenerators generators = new ExitCodeGenerators();
		Collection<ExitCodeExceptionMapper> beans = context.getBeansOfType(ExitCodeExceptionMapper.class).values();
		generators.addAll(exception, beans);
		return generators.getExitCode();
	}

	private int getExitCodeFromExitCodeGeneratorException(@Nullable Throwable exception) {
		if (exception == null) {
			return 0;
		}
		if (exception instanceof ExitCodeGenerator generator) {
			return generator.getExitCode();
		}
		return getExitCodeFromExitCodeGeneratorException(exception.getCause());
	}

	@Nullable SpringBootExceptionHandler getSpringBootExceptionHandler() {
		if (isMainThread(Thread.currentThread())) {
			return SpringBootExceptionHandler.forCurrentThread();
		}
		return null;
	}

	private boolean isMainThread(Thread currentThread) {
		return ("main".equals(currentThread.getName()) || "restartedMain".equals(currentThread.getName()))
				&& "main".equals(currentThread.getThreadGroup().getName());
	}

	/**
	 * Returns the main application class that has been deduced or explicitly configured.
	 * <p>返回已推导或显式配置的主应用程序类。</p>
	 * @return the main application class or {@code null}
	 * <p>主应用程序类或 {@code null}</p>
	 */
	public @Nullable Class<?> getMainApplicationClass() {
		return this.mainApplicationClass;
	}

	/**
	 * Set a specific main application class that will be used as a log source and to
	 * obtain version information. By default the main application class will be deduced.
	 * Can be set to {@code null} if there is no explicit application class.
	 * <p>设置特定的主应用程序类，将用作日志源并获取版本信息。默认情况下，将推导主应用程序类。如果没有显式的应用程序类，可以设置为 {@code null}。</p>
	 * @param mainApplicationClass the mainApplicationClass to set or {@code null}
	 * <p>要设置的主应用程序类或 {@code null}</p>
	 */
	public void setMainApplicationClass(@Nullable Class<?> mainApplicationClass) {
		this.mainApplicationClass = mainApplicationClass;
	}

	/**
	 * Returns the type of web application that is being run.
	 * <p>返回正在运行的 Web 应用程序类型。</p>
	 * @return the type of web application
	 * <p>Web 应用程序类型</p>
	 * @since 2.0.0
	 */
	public @Nullable WebApplicationType getWebApplicationType() {
		return this.properties.getWebApplicationType();
	}

	/**
	 * Sets the type of web application to be run. If not explicitly set the type of web
	 * application will be deduced based on the classpath.
	 * <p>设置要运行的 Web 应用程序类型。如果未显式设置，将根据类路径推导 Web 应用程序类型。</p>
	 * @param webApplicationType the web application type
	 * <p>Web 应用程序类型</p>
	 * @since 2.0.0
	 */
	public void setWebApplicationType(WebApplicationType webApplicationType) {
		Assert.notNull(webApplicationType, "'webApplicationType' must not be null");
		this.properties.setWebApplicationType(webApplicationType);
	}

	/**
	 * Sets if bean definition overriding, by registering a definition with the same name
	 * as an existing definition, should be allowed. Defaults to {@code false}.
	 * <p>设置是否允许通过注册与现有定义同名的定义来覆盖 bean 定义。默认为 {@code false}。</p>
	 * @param allowBeanDefinitionOverriding if overriding is allowed
	 * <p>是否允许覆盖</p>
	 * @since 2.1.0
	 * @see DefaultListableBeanFactory#setAllowBeanDefinitionOverriding(boolean)
	 */
	public void setAllowBeanDefinitionOverriding(boolean allowBeanDefinitionOverriding) {
		this.properties.setAllowBeanDefinitionOverriding(allowBeanDefinitionOverriding);
	}

	/**
	 * Sets whether to allow circular references between beans and automatically try to
	 * resolve them. Defaults to {@code false}.
	 * <p>设置是否允许 bean 之间的循环引用并自动尝试解析它们。默认为 {@code false}。</p>
	 * @param allowCircularReferences if circular references are allowed
	 * <p>是否允许循环引用</p>
	 * @since 2.6.0
	 * @see AbstractAutowireCapableBeanFactory#setAllowCircularReferences(boolean)
	 */
	public void setAllowCircularReferences(boolean allowCircularReferences) {
		this.properties.setAllowCircularReferences(allowCircularReferences);
	}

	/**
	 * Sets if beans should be initialized lazily. Defaults to {@code false}.
	 * <p>设置 bean 是否应延迟初始化。默认为 {@code false}。</p>
	 * @param lazyInitialization if initialization should be lazy
	 * <p>是否应延迟初始化</p>
	 * @since 2.2
	 * @see BeanDefinition#setLazyInit(boolean)
	 */
	public void setLazyInitialization(boolean lazyInitialization) {
		this.properties.setLazyInitialization(lazyInitialization);
	}

	/**
	 * Sets if the application is headless and should not instantiate AWT. Defaults to
	 * {@code true} to prevent java icons appearing.
	 * <p>设置应用程序是否为无头模式且不应实例化 AWT。默认为 {@code true} 以防止出现 Java 图标。</p>
	 * @param headless if the application is headless
	 * <p>应用程序是否为无头模式</p>
	 */
	public void setHeadless(boolean headless) {
		this.headless = headless;
	}

	/**
	 * Sets if the created {@link ApplicationContext} should have a shutdown hook
	 * registered. Defaults to {@code true} to ensure that JVM shutdowns are handled
	 * gracefully.
	 * <p>设置创建的 {@link ApplicationContext} 是否应注册关闭钩子。默认为 {@code true} 以确保优雅地处理 JVM 关闭。</p>
	 * @param registerShutdownHook if the shutdown hook should be registered
	 * <p>是否应注册关闭钩子</p>
	 * @see #getShutdownHandlers()
	 */
	public void setRegisterShutdownHook(boolean registerShutdownHook) {
		this.properties.setRegisterShutdownHook(registerShutdownHook);
	}

	/**
	 * Sets the {@link Banner} instance which will be used to print the banner when no
	 * static banner file is provided.
	 * <p>设置当未提供静态横幅文件时将用于打印横幅的 {@link Banner} 实例。</p>
	 * @param banner the Banner instance to use
	 * <p>要使用的 Banner 实例</p>
	 */
	public void setBanner(Banner banner) {
		this.banner = banner;
	}

	/**
	 * Sets the mode used to display the banner when the application runs. Defaults to
	 * {@code Banner.Mode.CONSOLE}.
	 * <p>设置应用程序运行时用于显示横幅的模式。默认为 {@code Banner.Mode.CONSOLE}。</p>
	 * @param bannerMode the mode used to display the banner
	 * <p>用于显示横幅的模式</p>
	 */
	public void setBannerMode(Banner.Mode bannerMode) {
		this.properties.setBannerMode(bannerMode);
	}

	/**
	 * Sets if the application information should be logged when the application starts.
	 * Defaults to {@code true}.
	 * <p>设置应用程序启动时是否应记录应用程序信息。默认为 {@code true}。</p>
	 * @param logStartupInfo if startup info should be logged.
	 * <p>是否应记录启动信息。</p>
	 */
	public void setLogStartupInfo(boolean logStartupInfo) {
		this.properties.setLogStartupInfo(logStartupInfo);
	}

	/**
	 * Sets if a {@link CommandLinePropertySource} should be added to the application
	 * context in order to expose arguments. Defaults to {@code true}.
	 * <p>设置是否应将 {@link CommandLinePropertySource} 添加到应用程序上下文以公开参数。默认为 {@code true}。</p>
	 * @param addCommandLineProperties if command line arguments should be exposed
	 * <p>是否应公开命令行参数</p>
	 */
	public void setAddCommandLineProperties(boolean addCommandLineProperties) {
		this.addCommandLineProperties = addCommandLineProperties;
	}

	/**
	 * Sets if the {@link ApplicationConversionService} should be added to the application
	 * context's {@link Environment}.
	 * <p>设置是否应将 {@link ApplicationConversionService} 添加到应用程序上下文的 {@link Environment}。</p>
	 * @param addConversionService if the application conversion service should be added
	 * <p>是否应添加应用程序转换服务</p>
	 * @since 2.1.0
	 */
	public void setAddConversionService(boolean addConversionService) {
		this.addConversionService = addConversionService;
	}

	/**
	 * Adds {@link BootstrapRegistryInitializer} instances that can be used to initialize
	 * the {@link BootstrapRegistry}.
	 * <p>添加可用于初始化 {@link BootstrapRegistry} 的 {@link BootstrapRegistryInitializer} 实例。</p>
	 * @param bootstrapRegistryInitializer the bootstrap registry initializer to add
	 * <p>要添加的引导注册表初始化器</p>
	 * @since 2.4.5
	 */
	public void addBootstrapRegistryInitializer(BootstrapRegistryInitializer bootstrapRegistryInitializer) {
		Assert.notNull(bootstrapRegistryInitializer, "'bootstrapRegistryInitializer' must not be null");
		this.bootstrapRegistryInitializers.addAll(Arrays.asList(bootstrapRegistryInitializer));
	}

	/**
	 * Set default environment properties which will be used in addition to those in the
	 * existing {@link Environment}.
	 * <p>设置默认环境属性，这些属性将作为现有 {@link Environment} 中属性的补充。</p>
	 * @param defaultProperties the additional properties to set
	 * <p>要设置的附加属性</p>
	 */
	public void setDefaultProperties(Map<String, Object> defaultProperties) {
		this.defaultProperties = defaultProperties;
	}

	/**
	 * Convenient alternative to {@link #setDefaultProperties(Map)}.
	 * <p>{@link #setDefaultProperties(Map)} 的便捷替代方法。</p>
	 * @param defaultProperties some {@link Properties}
	 * <p>一些 {@link Properties}</p>
	 */
	public void setDefaultProperties(Properties defaultProperties) {
		this.defaultProperties = new HashMap<>();
		for (Object key : Collections.list(defaultProperties.propertyNames())) {
			this.defaultProperties.put((String) key, defaultProperties.get(key));
		}
	}

	/**
	 * Set additional profile values to use (on top of those set in system or command line
	 * properties).
	 * <p>设置要使用的附加配置文件值（在系统或命令行属性中设置的值之上）。</p>
	 * @param profiles the additional profiles to set
	 * <p>要设置的附加配置文件</p>
	 */
	public void setAdditionalProfiles(String... profiles) {
		this.additionalProfiles = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(profiles)));
	}

	/**
	 * Return an immutable set of any additional profiles in use.
	 * <p>返回正在使用的任何附加配置文件的不可变集合。</p>
	 * @return the additional profiles
	 * <p>附加配置文件</p>
	 */
	public Set<String> getAdditionalProfiles() {
		return this.additionalProfiles;
	}

	/**
	 * Sets the bean name generator that should be used when generating bean names.
	 * <p>设置生成 bean 名称时应使用的 bean 名称生成器。</p>
	 * @param beanNameGenerator the bean name generator
	 * <p>bean 名称生成器</p>
	 */
	public void setBeanNameGenerator(BeanNameGenerator beanNameGenerator) {
		this.beanNameGenerator = beanNameGenerator;
	}

	/**
	 * Sets the underlying environment that should be used with the created application
	 * context.
	 * <p>设置应与创建的应用程序上下文一起使用的底层环境。</p>
	 * @param environment the environment
	 * <p>环境</p>
	 */
	public void setEnvironment(@Nullable ConfigurableEnvironment environment) {
		this.isCustomEnvironment = true;
		this.environment = environment;
	}

	/**
	 * Add additional items to the primary sources that will be added to an
	 * ApplicationContext when {@link #run(String...)} is called.
	 * <p>将附加项添加到主源中，这些源将在调用 {@link #run(String...)} 时添加到 ApplicationContext。</p>
	 * <p>
	 * The sources here are added to those that were set in the constructor. Most users
	 * should consider using {@link #getSources()}/{@link #setSources(Set)} rather than
	 * calling this method.
	 * <p>此处的源会添加到构造函数中设置的源中。大多数用户应考虑使用 {@link #getSources()}/{@link #setSources(Set)} 而不是调用此方法。</p>
	 * @param additionalPrimarySources the additional primary sources to add
	 * <p>要添加的附加主源</p>
	 * @see #SpringApplication(Class...)
	 * @see #getSources()
	 * @see #setSources(Set)
	 * @see #getAllSources()
	 */
	public void addPrimarySources(Collection<Class<?>> additionalPrimarySources) {
		this.primarySources.addAll(additionalPrimarySources);
	}

	/**
	 * Returns a mutable set of the sources that will be added to an ApplicationContext
	 * when {@link #run(String...)} is called.
	 * <p>返回将在调用 {@link #run(String...)} 时添加到 ApplicationContext 的源的可变集合。</p>
	 * <p>
	 * Sources set here will be used in addition to any primary sources set in the
	 * constructor.
	 * <p>此处设置的源将作为构造函数中设置的任何主源的补充。</p>
	 * @return the application sources.
	 * <p>应用程序源。</p>
	 * @see #SpringApplication(Class...)
	 * @see #getAllSources()
	 */
	public Set<String> getSources() {
		return this.properties.getSources();
	}

	/**
	 * Set additional sources that will be used to create an ApplicationContext. A source
	 * can be: a class name, package name, or an XML resource location.
	 * <p>设置将用于创建 ApplicationContext 的附加源。源可以是：类名、包名或 XML 资源位置。</p>
	 * <p>
	 * Sources set here will be used in addition to any primary sources set in the
	 * constructor.
	 * <p>此处设置的源将作为构造函数中设置的任何主源的补充。</p>
	 * @param sources the application sources to set
	 * <p>要设置的应用程序源</p>
	 * @see #SpringApplication(Class...)
	 * @see #getAllSources()
	 */
	public void setSources(Set<String> sources) {
		Assert.notNull(sources, "'sources' must not be null");
		this.properties.setSources(sources);
	}

	/**
	 * Return an immutable set of all the sources that will be added to an
	 * ApplicationContext when {@link #run(String...)} is called. This method combines any
	 * primary sources specified in the constructor with any additional ones that have
	 * been {@link #setSources(Set) explicitly set}.
	 * <p>返回将在调用 {@link #run(String...)} 时添加到 ApplicationContext 的所有源的不可变集合。此方法将构造函数中指定的任何主源与已 {@link #setSources(Set) 显式设置} 的任何附加源组合在一起。</p>
	 * @return an immutable set of all sources
	 * <p>所有源的不可变集合</p>
	 */
	public Set<Object> getAllSources() {
		Set<Object> allSources = new LinkedHashSet<>();
		if (!CollectionUtils.isEmpty(this.primarySources)) {
			allSources.addAll(this.primarySources);
		}
		if (!CollectionUtils.isEmpty(this.properties.getSources())) {
			allSources.addAll(this.properties.getSources());
		}
		return Collections.unmodifiableSet(allSources);
	}

	/**
	 * Sets the {@link ResourceLoader} that should be used when loading resources.
	 * <p>设置加载资源时应使用的 {@link ResourceLoader}。</p>
	 * @param resourceLoader the resource loader
	 * <p>资源加载器</p>
	 */
	public void setResourceLoader(ResourceLoader resourceLoader) {
		Assert.notNull(resourceLoader, "'resourceLoader' must not be null");
		this.resourceLoader = resourceLoader;
	}

	/**
	 * Return a prefix that should be applied when obtaining configuration properties from
	 * the system environment.
	 * <p>返回从系统环境获取配置属性时应应用的前缀。</p>
	 * @return the environment property prefix
	 * <p>环境属性前缀</p>
	 * @since 2.5.0
	 */
	public @Nullable String getEnvironmentPrefix() {
		return this.environmentPrefix;
	}

	/**
	 * Set the prefix that should be applied when obtaining configuration properties from
	 * the system environment.
	 * <p>设置从系统环境获取配置属性时应应用的前缀。</p>
	 * @param environmentPrefix the environment property prefix to set
	 * <p>要设置的环境属性前缀</p>
	 * @since 2.5.0
	 */
	public void setEnvironmentPrefix(String environmentPrefix) {
		this.environmentPrefix = environmentPrefix;
	}

	/**
	 * Sets the factory that will be called to create the application context. If not set,
	 * defaults to a factory that will create a context that is appropriate for the
	 * application's type (a reactive web application, a servlet web application, or a
	 * non-web application).
	 * <p>设置将调用以创建应用程序上下文的工厂。如果未设置，则默认为将创建适合应用程序类型的上下文的工厂（响应式 Web 应用程序、Servlet Web 应用程序或非 Web 应用程序）。</p>
	 * @param applicationContextFactory the factory for the context
	 * <p>上下文的工厂</p>
	 * @since 2.4.0
	 */
	public void setApplicationContextFactory(@Nullable ApplicationContextFactory applicationContextFactory) {
		this.applicationContextFactory = (applicationContextFactory != null) ? applicationContextFactory
				: ApplicationContextFactory.DEFAULT;
	}

	/**
	 * Sets the {@link ApplicationContextInitializer} that will be applied to the Spring
	 * {@link ApplicationContext}.
	 * <p>设置将应用于 Spring {@link ApplicationContext} 的 {@link ApplicationContextInitializer}。</p>
	 * @param initializers the initializers to set
	 * <p>要设置的初始化器</p>
	 */
	public void setInitializers(Collection<? extends ApplicationContextInitializer<?>> initializers) {
		this.initializers = new ArrayList<>(initializers);
	}

	/**
	 * Add {@link ApplicationContextInitializer}s to be applied to the Spring
	 * {@link ApplicationContext}.
	 * <p>添加要应用于 Spring {@link ApplicationContext} 的 {@link ApplicationContextInitializer}。</p>
	 * @param initializers the initializers to add
	 * <p>要添加的初始化器</p>
	 */
	public void addInitializers(ApplicationContextInitializer<?>... initializers) {
		this.initializers.addAll(Arrays.asList(initializers));
	}

	/**
	 * Returns read-only ordered Set of the {@link ApplicationContextInitializer}s that
	 * will be applied to the Spring {@link ApplicationContext}.
	 * <p>返回将应用于 Spring {@link ApplicationContext} 的 {@link ApplicationContextInitializer} 的只读有序 Set。</p>
	 * @return the initializers
	 * <p>初始化器</p>
	 */
	public Set<ApplicationContextInitializer<?>> getInitializers() {
		return asUnmodifiableOrderedSet(this.initializers);
	}

	/**
	 * Sets the {@link ApplicationListener}s that will be applied to the SpringApplication
	 * and registered with the {@link ApplicationContext}.
	 * <p>设置将应用于 SpringApplication 并向 {@link ApplicationContext} 注册的 {@link ApplicationListener}。</p>
	 * @param listeners the listeners to set
	 * <p>要设置的监听器</p>
	 */
	public void setListeners(Collection<? extends ApplicationListener<?>> listeners) {
		this.listeners = new ArrayList<>(listeners);
	}

	/**
	 * Add {@link ApplicationListener}s to be applied to the SpringApplication and
	 * registered with the {@link ApplicationContext}.
	 * <p>添加要应用于 SpringApplication 并向 {@link ApplicationContext} 注册的 {@link ApplicationListener}。</p>
	 * @param listeners the listeners to add
	 * <p>要添加的监听器</p>
	 */
	public void addListeners(ApplicationListener<?>... listeners) {
		this.listeners.addAll(Arrays.asList(listeners));
	}

	/**
	 * Returns read-only ordered Set of the {@link ApplicationListener}s that will be
	 * applied to the SpringApplication and registered with the {@link ApplicationContext}
	 * .
	 * <p>返回将应用于 SpringApplication 并向 {@link ApplicationContext} 注册的 {@link ApplicationListener} 的只读有序 Set。</p>
	 * @return the listeners
	 * <p>监听器</p>
	 */
	public Set<ApplicationListener<?>> getListeners() {
		return asUnmodifiableOrderedSet(this.listeners);
	}

	/**
	 * Set the {@link ApplicationStartup} to use for collecting startup metrics.
	 * <p>设置用于收集启动指标的 {@link ApplicationStartup}。</p>
	 * @param applicationStartup the application startup to use
	 * <p>要使用的应用程序启动</p>
	 * @since 2.4.0
	 */
	public void setApplicationStartup(ApplicationStartup applicationStartup) {
		this.applicationStartup = (applicationStartup != null) ? applicationStartup : ApplicationStartup.DEFAULT;
	}

	/**
	 * Returns the {@link ApplicationStartup} used for collecting startup metrics.
	 * <p>返回用于收集启动指标的 {@link ApplicationStartup}。</p>
	 * @return the application startup
	 * <p>应用程序启动</p>
	 * @since 2.4.0
	 */
	public ApplicationStartup getApplicationStartup() {
		return this.applicationStartup;
	}

	/**
	 * Whether to keep the application alive even if there are no more non-daemon threads.
	 * <p>即使没有更多非守护线程，是否保持应用程序存活。</p>
	 * @return whether to keep the application alive even if there are no more non-daemon
	 * threads
	 * <p>即使没有更多非守护线程，是否保持应用程序存活</p>
	 * @since 3.2.0
	 */
	public boolean isKeepAlive() {
		return this.properties.isKeepAlive();
	}

	/**
	 * Set whether to keep the application alive even if there are no more non-daemon
	 * threads.
	 * <p>设置即使没有更多非守护线程，是否保持应用程序存活。</p>
	 * @param keepAlive whether to keep the application alive even if there are no more
	 * non-daemon threads
	 * <p>即使没有更多非守护线程，是否保持应用程序存活</p>
	 * @since 3.2.0
	 */
	public void setKeepAlive(boolean keepAlive) {
		this.properties.setKeepAlive(keepAlive);
	}

	/**
	 * Return a {@link SpringApplicationShutdownHandlers} instance that can be used to add
	 * or remove handlers that perform actions before the JVM is shutdown.
	 * <p>返回一个 {@link SpringApplicationShutdownHandlers} 实例，可用于添加或删除在 JVM 关闭之前执行操作的处理器。</p>
	 * @return a {@link SpringApplicationShutdownHandlers} instance
	 * <p>一个 {@link SpringApplicationShutdownHandlers} 实例</p>
	 * @since 2.5.1
	 */
	public static SpringApplicationShutdownHandlers getShutdownHandlers() {
		return shutdownHook.getHandlers();
	}

	/**
	 * Static helper that can be used to run a {@link SpringApplication} from the
	 * specified source using default settings.
	 * <p>静态辅助方法，可用于使用默认设置从指定源运行 {@link SpringApplication}。</p>
	 * @param primarySource the primary source to load
	 * <p>要加载的主源</p>
	 * @param args the application arguments (usually passed from a Java main method)
	 * <p>应用程序参数（通常从 Java main 方法传递）</p>
	 * @return the running {@link ApplicationContext}
	 * <p>正在运行的 {@link ApplicationContext}</p>
	 */
	public static ConfigurableApplicationContext run(Class<?> primarySource, String... args) {
		return run(new Class<?>[] { primarySource }, args);
	}

	/**
	 * Static helper that can be used to run a {@link SpringApplication} from the
	 * specified sources using default settings and user supplied arguments.
	 * <p>静态辅助方法，可用于使用默认设置和用户提供的参数从指定源运行 {@link SpringApplication}。</p>
	 * @param primarySources the primary sources to load
	 * <p>要加载的主源</p>
	 * @param args the application arguments (usually passed from a Java main method)
	 * <p>应用程序参数（通常从 Java main 方法传递）</p>
	 * @return the running {@link ApplicationContext}
	 * <p>正在运行的 {@link ApplicationContext}</p>
	 */
	public static ConfigurableApplicationContext run(Class<?>[] primarySources, String[] args) {
		return new SpringApplication(primarySources).run(args);
	}

	/**
	 * A basic main that can be used to launch an application. This method is useful when
	 * application sources are defined through a {@literal --spring.main.sources} command
	 * line argument.
	 * <p>可用于启动应用程序的基本 main。当应用程序源通过 {@literal --spring.main.sources} 命令行参数定义时，此方法很有用。</p>
	 * <p>
	 * Most developers will want to define their own main method and call the
	 * {@link #run(Class, String...) run} method instead.
	 * <p>大多数开发者会希望定义自己的 main 方法并改为调用 {@link #run(Class, String...) run} 方法。</p>
	 * @param args command line arguments
	 * <p>命令行参数</p>
	 * @throws Exception if the application cannot be started
	 * <p>如果应用程序无法启动</p>
	 * @see SpringApplication#run(Class[], String[])
	 * @see SpringApplication#run(Class, String...)
	 */
	public static void main(String[] args) throws Exception {
		SpringApplication.run(new Class<?>[0], args);
	}

	/**
	 * Static helper that can be used to exit a {@link SpringApplication} and obtain a
	 * code indicating success (0) or otherwise. Does not throw exceptions but should
	 * print stack traces of any encountered. Applies the specified
	 * {@link ExitCodeGenerator ExitCodeGenerators} in addition to any Spring beans that
	 * implement {@link ExitCodeGenerator}. When multiple generators are available, the
	 * first non-zero exit code is used. Generators are ordered based on their
	 * {@link Ordered} implementation and {@link Order @Order} annotation.
	 * <p>静态辅助方法，可用于退出 {@link SpringApplication} 并获取指示成功 (0) 或其他情况的代码。不抛出异常，但应打印遇到的任何堆栈跟踪。除了实现 {@link ExitCodeGenerator} 的任何 Spring bean 之外，还应用指定的 {@link ExitCodeGenerator ExitCodeGenerators}。当有多个生成器可用时，使用第一个非零退出代码。生成器根据其 {@link Ordered} 实现和 {@link Order @Order} 注解进行排序。</p>
	 * @param context the context to close if possible
	 * <p>如果可能，要关闭的上下文</p>
	 * @param exitCodeGenerators exit code generators
	 * <p>退出代码生成器</p>
	 * @return the outcome (0 if successful)
	 * <p>结果（如果成功则为 0）</p>
	 */
	public static int exit(ApplicationContext context, ExitCodeGenerator... exitCodeGenerators) {
		Assert.notNull(context, "'context' must not be null");
		int exitCode = 0;
		try {
			try {
				ExitCodeGenerators generators = new ExitCodeGenerators();
				Collection<ExitCodeGenerator> beans = context.getBeansOfType(ExitCodeGenerator.class).values();
				generators.addAll(exitCodeGenerators);
				generators.addAll(beans);
				exitCode = generators.getExitCode();
				if (exitCode != 0) {
					context.publishEvent(new ExitCodeEvent(context, exitCode));
				}
			}
			finally {
				close(context);
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			exitCode = (exitCode != 0) ? exitCode : 1;
		}
		return exitCode;
	}

	/**
	 * Create an application from an existing {@code main} method that can run with
	 * additional {@code @Configuration} or bean classes. This method can be helpful when
	 * writing a test harness that needs to start an application with additional
	 * configuration.
	 * <p>从现有的 {@code main} 方法创建应用程序，该方法可以运行附加的 {@code @Configuration} 或 bean 类。在编写需要以附加配置启动应用程序的测试工具时，此方法可能会有所帮助。</p>
	 * @param main the main method entry point that runs the {@link SpringApplication}
	 * <p>运行 {@link SpringApplication} 的 main 方法入口点</p>
	 * @return a {@link SpringApplication.Augmented} instance that can be used to add
	 * configuration and run the application
	 * <p>可用于添加配置并运行应用程序的 {@link SpringApplication.Augmented} 实例</p>
	 * @since 3.1.0
	 * @see #withHook(SpringApplicationHook, Runnable)
	 */
	public static SpringApplication.Augmented from(ThrowingConsumer<String[]> main) {
		Assert.notNull(main, "'main' must not be null");
		return new Augmented(main, Collections.emptySet(), Collections.emptySet());
	}

	/**
	 * Perform the given action with the given {@link SpringApplicationHook} attached if
	 * the action triggers an {@link SpringApplication#run(String...) application run}.
	 * <p>如果操作触发 {@link SpringApplication#run(String...) 应用程序运行}，则在附加给定 {@link SpringApplicationHook} 的情况下执行给定操作。</p>
	 * @param hook the hook to apply
	 * <p>要应用的钩子</p>
	 * @param action the action to run
	 * <p>要运行的操作</p>
	 * @since 3.0.0
	 * @see #withHook(SpringApplicationHook, ThrowingSupplier)
	 */
	public static void withHook(SpringApplicationHook hook, Runnable action) {
		withHook(hook, () -> {
			action.run();
			return Void.class;
		});
	}

	/**
	 * Perform the given action with the given {@link SpringApplicationHook} attached if
	 * the action triggers an {@link SpringApplication#run(String...) application run}.
	 * <p>如果操作触发 {@link SpringApplication#run(String...) 应用程序运行}，则在附加给定 {@link SpringApplicationHook} 的情况下执行给定操作。</p>
	 * @param <T> the result type
	 * <p>结果类型</p>
	 * @param hook the hook to apply
	 * <p>要应用的钩子</p>
	 * @param action the action to run
	 * <p>要运行的操作</p>
	 * @return the result of the action
	 * <p>操作的结果</p>
	 * @since 3.0.0
	 * @see #withHook(SpringApplicationHook, Runnable)
	 */
	public static <T> T withHook(SpringApplicationHook hook, ThrowingSupplier<T> action) {
		applicationHook.set(hook);
		try {
			return action.get();
		}
		finally {
			applicationHook.remove();
		}
	}

	private static void close(ApplicationContext context) {
		if (context instanceof ConfigurableApplicationContext closable) {
			closable.close();
		}
	}

	private static <E> Set<E> asUnmodifiableOrderedSet(Collection<E> elements) {
		List<E> list = new ArrayList<>(elements);
		list.sort(AnnotationAwareOrderComparator.INSTANCE);
		return new LinkedHashSet<>(list);
	}

	/**
	 * Used to configure and run an augmented {@link SpringApplication} where additional
	 * configuration should be applied.
	 * <p>用于配置和运行应应用附加配置的增强 {@link SpringApplication}。</p>
	 *
	 * @since 3.1.0
	 */
	public static class Augmented {

		private final ThrowingConsumer<String[]> main;

		private final Set<Class<?>> sources;

		private final Set<String> additionalProfiles;

		Augmented(ThrowingConsumer<String[]> main, Set<Class<?>> sources, Set<String> additionalProfiles) {
			this.main = main;
			this.sources = Set.copyOf(sources);
			this.additionalProfiles = additionalProfiles;
		}

		/**
		 * Return a new {@link SpringApplication.Augmented} instance with additional
		 * sources that should be applied when the application runs.
		 * <p>返回一个新的 {@link SpringApplication.Augmented} 实例，其中包含应用程序运行时应应用的附加源。</p>
		 * @param sources the sources that should be applied
		 * <p>应应用的源</p>
		 * @return a new {@link SpringApplication.Augmented} instance
		 * <p>新的 {@link SpringApplication.Augmented} 实例</p>
		 */
		public Augmented with(Class<?>... sources) {
			LinkedHashSet<Class<?>> merged = new LinkedHashSet<>(this.sources);
			merged.addAll(Arrays.asList(sources));
			return new Augmented(this.main, merged, this.additionalProfiles);
		}

		/**
		 * Return a new {@link SpringApplication.Augmented} instance with additional
		 * profiles that should be applied when the application runs.
		 * <p>返回一个新的 {@link SpringApplication.Augmented} 实例，其中包含应用程序运行时应应用的附加配置文件。</p>
		 * @param profiles the profiles that should be applied
		 * <p>应应用的配置文件</p>
		 * @return a new {@link SpringApplication.Augmented} instance
		 * <p>新的 {@link SpringApplication.Augmented} 实例</p>
		 * @since 3.4.0
		 */
		public Augmented withAdditionalProfiles(String... profiles) {
			Set<String> merged = new LinkedHashSet<>(this.additionalProfiles);
			merged.addAll(Arrays.asList(profiles));
			return new Augmented(this.main, this.sources, merged);
		}

		/**
		 * Run the application using the given args.
		 * <p>使用给定参数运行应用程序。</p>
		 * @param args the main method args
		 * <p>main 方法参数</p>
		 * @return the running {@link ApplicationContext}
		 * <p>正在运行的 {@link ApplicationContext}</p>
		 */
		public SpringApplication.Running run(String... args) {
			RunListener runListener = new RunListener();
			SpringApplicationHook hook = new SingleUseSpringApplicationHook((springApplication) -> {
				springApplication.addPrimarySources(this.sources);
				springApplication.setAdditionalProfiles(this.additionalProfiles.toArray(String[]::new));
				return runListener;
			});
			withHook(hook, () -> this.main.accept(args));
			return runListener;
		}

		/**
		 * {@link SpringApplicationRunListener} to capture {@link Running} application
		 * details.
		 * <p>用于捕获 {@link Running} 应用程序详细信息的 {@link SpringApplicationRunListener}。</p>
		 */
		private static final class RunListener implements SpringApplicationRunListener, Running {

			private final List<ConfigurableApplicationContext> contexts = Collections
					.synchronizedList(new ArrayList<>());

			@Override
			public void contextLoaded(ConfigurableApplicationContext context) {
				this.contexts.add(context);
			}

			@Override
			public ConfigurableApplicationContext getApplicationContext() {
				List<ConfigurableApplicationContext> rootContexts = this.contexts.stream()
						.filter((context) -> context.getParent() == null)
						.toList();
				Assert.state(!rootContexts.isEmpty(), "No root application context located");
				Assert.state(rootContexts.size() == 1, "No unique root application context located");
				return rootContexts.get(0);
			}

		}

	}

	/**
	 * Provides access to details of a {@link SpringApplication} run using
	 * {@link Augmented#run(String...)}.
	 * <p>提供对使用 {@link Augmented#run(String...)} 运行的 {@link SpringApplication} 详细信息的访问。</p>
	 *
	 * @since 3.1.0
	 */
	public interface Running {

		/**
		 * Return the root {@link ConfigurableApplicationContext} of the running
		 * application.
		 * <p>返回正在运行的应用程序的根 {@link ConfigurableApplicationContext}。</p>
		 * @return the root application context
		 * <p>根应用程序上下文</p>
		 */
		ConfigurableApplicationContext getApplicationContext();

	}

	/**
	 * {@link BeanFactoryPostProcessor} to re-order our property sources below any
	 * {@code @PropertySource} items added by the {@link ConfigurationClassPostProcessor}.
	 * <p>{@link BeanFactoryPostProcessor} 用于将我们的属性源重新排序到由 {@link ConfigurationClassPostProcessor} 添加的任何 {@code @PropertySource} 项之下。</p>
	 */
	private static class PropertySourceOrderingBeanFactoryPostProcessor implements BeanFactoryPostProcessor, Ordered {

		private final ConfigurableApplicationContext context;

		PropertySourceOrderingBeanFactoryPostProcessor(ConfigurableApplicationContext context) {
			this.context = context;
		}

		@Override
		public int getOrder() {
			return Ordered.HIGHEST_PRECEDENCE;
		}

		@Override
		public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
			DefaultPropertiesPropertySource.moveToEnd(this.context.getEnvironment());
		}

	}

	/**
	 * Exception that can be thrown to silently exit a running {@link SpringApplication}
	 * without handling run failures.
	 * <p>可抛出以静默退出正在运行的 {@link SpringApplication} 而不处理运行失败的异常。</p>
	 *
	 * @since 3.0.0
	 */
	public static class AbandonedRunException extends RuntimeException {

		private final @Nullable ConfigurableApplicationContext applicationContext;

		/**
		 * Create a new {@link AbandonedRunException} instance.
		 * <p>创建一个新的 {@link AbandonedRunException} 实例。</p>
		 */
		public AbandonedRunException() {
			this(null);
		}

		/**
		 * Create a new {@link AbandonedRunException} instance with the given application
		 * context.
		 * <p>使用给定的应用程序上下文创建一个新的 {@link AbandonedRunException} 实例。</p>
		 * @param applicationContext the application context that was available when the
		 * run was abandoned
		 * <p>运行被放弃时可用的应用程序上下文</p>
		 */
		public AbandonedRunException(@Nullable ConfigurableApplicationContext applicationContext) {
			this.applicationContext = applicationContext;
		}

		/**
		 * Return the application context that was available when the run was abandoned or
		 * {@code null} if no context was available.
		 * <p>返回运行被放弃时可用的应用程序上下文，如果没有可用上下文则返回 {@code null}。</p>
		 * @return the application context
		 * <p>应用程序上下文</p>
		 */
		public @Nullable ConfigurableApplicationContext getApplicationContext() {
			return this.applicationContext;
		}

	}

	/**
	 * Exception which is thrown if GraalVM's native-image requirements aren't met.
	 * <p>如果未满足 GraalVM 原生镜像要求，则抛出的异常。</p>
	 */
	static final class NativeImageRequirementsException extends RuntimeException {

		private static final JavaVersion MINIMUM_REQUIRED_JAVA_VERSION = JavaVersion.TWENTY_FIVE;

		private static final JavaVersion CURRENT_JAVA_VERSION = JavaVersion.getJavaVersion();

		NativeImageRequirementsException(String message) {
			super(message);
		}

		static void throwIfNotMet() {
			if (CURRENT_JAVA_VERSION.isOlderThan(MINIMUM_REQUIRED_JAVA_VERSION)) {
				throw new NativeImageRequirementsException("Native Image requirements not met. "
						+ "Native Image must support at least Java %s but Java %s was detected"
						.formatted(MINIMUM_REQUIRED_JAVA_VERSION, CURRENT_JAVA_VERSION));
			}
		}

	}

	/**
	 * {@link SpringApplicationHook} decorator that ensures the hook is only used once.
	 * <p>{@link SpringApplicationHook} 装饰器，确保钩子仅使用一次。</p>
	 */
	private static final class SingleUseSpringApplicationHook implements SpringApplicationHook {

		private final AtomicBoolean used = new AtomicBoolean();

		private final SpringApplicationHook delegate;

		private SingleUseSpringApplicationHook(SpringApplicationHook delegate) {
			this.delegate = delegate;
		}

		@Override
		public @Nullable SpringApplicationRunListener getRunListener(SpringApplication springApplication) {
			return this.used.compareAndSet(false, true) ? this.delegate.getRunListener(springApplication) : null;
		}

	}

	/**
	 * Starts a non-daemon thread to keep the JVM alive on {@link ContextRefreshedEvent}.
	 * Stops the thread on {@link ContextClosedEvent}.
	 * <p>启动一个非守护线程以在 {@link ContextRefreshedEvent} 上保持 JVM 存活。在 {@link ContextClosedEvent} 上停止该线程。</p>
	 */
	private static final class KeepAlive implements ApplicationListener<ApplicationContextEvent> {

		private final AtomicReference<@Nullable Thread> thread = new AtomicReference<>();

		@Override
		public void onApplicationEvent(ApplicationContextEvent event) {
			if (event instanceof ContextRefreshedEvent) {
				startKeepAliveThread();
			}
			else if (event instanceof ContextClosedEvent) {
				stopKeepAliveThread();
			}
		}

		private void startKeepAliveThread() {
			Thread thread = new Thread(() -> {
				while (true) {
					try {
						Thread.sleep(Long.MAX_VALUE);
					}
					catch (InterruptedException ex) {
						break;
					}
				}
			});
			if (this.thread.compareAndSet(null, thread)) {
				thread.setDaemon(false);
				thread.setName("keep-alive");
				thread.start();
			}
		}

		private void stopKeepAliveThread() {
			Thread thread = this.thread.getAndSet(null);
			if (thread == null) {
				return;
			}
			thread.interrupt();
		}

	}

	/**
	 * Strategy used to handle startup concerns.
	 * <p>用于处理启动关注点的策略。</p>
	 */
	abstract static class Startup {

		private @Nullable Duration timeTakenToStarted;

		protected abstract long startTime();

		protected abstract @Nullable Long processUptime();

		protected abstract String action();

		final Duration started() {
			long now = System.currentTimeMillis();
			this.timeTakenToStarted = Duration.ofMillis(now - startTime());
			return this.timeTakenToStarted;
		}

		Duration timeTakenToStarted() {
			Assert.state(this.timeTakenToStarted != null,
					"timeTakenToStarted is not set. Make sure to call started() before this method");
			return this.timeTakenToStarted;
		}

		private Duration ready() {
			long now = System.currentTimeMillis();
			return Duration.ofMillis(now - startTime());
		}

		static Startup create() {
			ClassLoader classLoader = Startup.class.getClassLoader();
			return (ClassUtils.isPresent("jdk.crac.management.CRaCMXBean", classLoader)
					&& ClassUtils.isPresent("org.crac.management.CRaCMXBean", classLoader))
					? new CoordinatedRestoreAtCheckpointStartup() : new StandardStartup();
		}

	}

	/**
	 * Standard {@link Startup} implementation.
	 * <p>标准 {@link Startup} 实现。</p>
	 */
	private static final class StandardStartup extends Startup {

		private final Long startTime = System.currentTimeMillis();

		@Override
		protected long startTime() {
			return this.startTime;
		}

		@Override
		protected @Nullable Long processUptime() {
			try {
				return ManagementFactory.getRuntimeMXBean().getUptime();
			}
			catch (Throwable ex) {
				return null;
			}
		}

		@Override
		protected String action() {
			return "Started";
		}

	}

	/**
	 * Coordinated-Restore-At-Checkpoint {@link Startup} implementation.
	 * <p>协调恢复检查点 {@link Startup} 实现。</p>
	 */
	private static final class CoordinatedRestoreAtCheckpointStartup extends Startup {

		private final StandardStartup fallback = new StandardStartup();

		@Override
		protected @Nullable Long processUptime() {
			Long uptime = CRaCMXBean.getCRaCMXBean().getUptimeSinceRestore();
			return (uptime >= 0) ? uptime : this.fallback.processUptime();
		}

		@Override
		protected String action() {
			return (restoreTime() >= 0) ? "Restored" : this.fallback.action();
		}

		private long restoreTime() {
			return CRaCMXBean.getCRaCMXBean().getRestoreTime();
		}

		@Override
		protected long startTime() {
			long restoreTime = restoreTime();
			return (restoreTime >= 0) ? restoreTime : this.fallback.startTime();
		}

	}

	/**
	 * {@link OrderSourceProvider} used to obtain factory method and target type order
	 * sources. Based on internal {@link DefaultListableBeanFactory} code.
	 * <p>用于获取工厂方法和目标类型排序源的 {@link OrderSourceProvider}。基于内部 {@link DefaultListableBeanFactory} 代码。</p>
	 */
	private static class FactoryAwareOrderSourceProvider implements OrderSourceProvider {

		private final ConfigurableBeanFactory beanFactory;

		private final Map<?, String> instancesToBeanNames;

		FactoryAwareOrderSourceProvider(ConfigurableBeanFactory beanFactory, Map<?, String> instancesToBeanNames) {
			this.beanFactory = beanFactory;
			this.instancesToBeanNames = instancesToBeanNames;
		}

		@Override
		public @Nullable Object getOrderSource(Object obj) {
			String beanName = this.instancesToBeanNames.get(obj);
			return (beanName != null) ? getOrderSource(beanName, obj.getClass()) : null;
		}

		private @Nullable Object getOrderSource(String beanName, Class<?> instanceType) {
			try {
				RootBeanDefinition beanDefinition = (RootBeanDefinition) this.beanFactory
						.getMergedBeanDefinition(beanName);
				Method factoryMethod = beanDefinition.getResolvedFactoryMethod();
				Class<?> targetType = beanDefinition.getTargetType();
				targetType = (targetType != instanceType) ? targetType : null;
				return Stream.of(factoryMethod, targetType).filter(Objects::nonNull).toArray();
			}
			catch (NoSuchBeanDefinitionException ex) {
				return null;
			}
		}

	}

}