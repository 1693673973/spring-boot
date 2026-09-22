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

import java.util.EventListener;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;

/**
 * Listener that can be registered with {@code spring.factories} to receive details of
 * imported auto-configurations.
 * <p>可以注册到 {@code spring.factories} 以接收导入的自动配置详细信息的监听器。</p>
 *
 * <p>
 * An {@link AutoConfigurationImportListener} may implement any of the following
 * {@link org.springframework.beans.factory.Aware Aware} interfaces, and their respective
 * methods will be called prior to
 * {@link #onAutoConfigurationImportEvent(AutoConfigurationImportEvent)}:
 * <p>{@link AutoConfigurationImportListener} 可以实现以下任意 {@link org.springframework.beans.factory.Aware Aware} 接口，它们各自的方法将在 {@link #onAutoConfigurationImportEvent(AutoConfigurationImportEvent)} 之前被调用：</p>
 *
 * <ul>
 * <li>{@link EnvironmentAware}</li>
 * <li>{@link EnvironmentAware} 环境感知接口</li>
 * <li>{@link BeanFactoryAware}</li>
 * <li>{@link BeanFactoryAware} Bean 工厂感知接口</li>
 * <li>{@link BeanClassLoaderAware}</li>
 * <li>{@link BeanClassLoaderAware} Bean 类加载器感知接口</li>
 * <li>{@link ResourceLoaderAware}</li>
 * <li>{@link ResourceLoaderAware} 资源加载器感知接口</li>
 * </ul>
 *
 * @author Phillip Webb
 * @since 1.5.0
 */
@FunctionalInterface
public interface AutoConfigurationImportListener extends EventListener {

	/**
	 * Handle an auto-configuration import event.
	 * <p>处理自动配置导入事件。</p>
	 *
	 * @param event the event to respond to
	 *              <p>要响应的事件</p>
	 */
	void onAutoConfigurationImportEvent(AutoConfigurationImportEvent event);

}