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

package org.springframework.boot.autoconfigure.jmx;

import java.util.Hashtable;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.jspecify.annotations.Nullable;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jmx.export.metadata.JmxAttributeSource;
import org.springframework.jmx.export.naming.MetadataNamingStrategy;
import org.springframework.jmx.support.JmxUtils;
import org.springframework.jmx.support.ObjectNameManager;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * Extension of {@link MetadataNamingStrategy} that supports a parent
 * {@link ApplicationContext}.
 * <p>{@link MetadataNamingStrategy} 的扩展，支持父级 {@link ApplicationContext}。</p>
 *
 * @author Dave Syer
 * @since 1.1.1
 */
public class ParentAwareNamingStrategy extends MetadataNamingStrategy implements ApplicationContextAware {

	@SuppressWarnings("NullAway.Init")
	private ApplicationContext applicationContext;

	private boolean ensureUniqueRuntimeObjectNames;

	public ParentAwareNamingStrategy(JmxAttributeSource attributeSource) {
		super(attributeSource);
	}

	/**
	 * Set if unique runtime object names should be ensured.
	 * <p>设置是否应确保唯一的运行时对象名称。</p>
	 *
	 * @param ensureUniqueRuntimeObjectNames {@code true} if unique names should be
	 * ensured.
	 *                                       <p>如果应确保唯一名称，则为 {@code true}。</p>
	 */
	public void setEnsureUniqueRuntimeObjectNames(boolean ensureUniqueRuntimeObjectNames) {
		this.ensureUniqueRuntimeObjectNames = ensureUniqueRuntimeObjectNames;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public ObjectName getObjectName(Object managedBean, @Nullable String beanKey) throws MalformedObjectNameException {
		ObjectName name = super.getObjectName(managedBean, beanKey);
		if (this.ensureUniqueRuntimeObjectNames) {
			return JmxUtils.appendIdentityToObjectName(name, managedBean);
		}
		if (parentContextContainsSameBean(this.applicationContext, beanKey)) {
			return appendToObjectName(name, "context", ObjectUtils.getIdentityHexString(this.applicationContext));
		}
		return name;
	}

	private boolean parentContextContainsSameBean(ApplicationContext context, @Nullable String beanKey) {
		if (context.getParent() == null) {
			return false;
		}
		try {
			ApplicationContext parent = this.applicationContext.getParent();
			Assert.state(parent != null, "'parent' must not be null");
			Assert.state(beanKey != null, "'beanKey' must not be null");
			parent.getBean(beanKey);
			return true;
		}
		catch (BeansException ex) {
			return parentContextContainsSameBean(context.getParent(), beanKey);
		}
	}

	private ObjectName appendToObjectName(ObjectName name, String key, String value)
			throws MalformedObjectNameException {
		Hashtable<String, String> keyProperties = name.getKeyPropertyList();
		keyProperties.put(key, value);
		return ObjectNameManager.getInstance(name.getDomain(), keyProperties);
	}

}