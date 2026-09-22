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

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jspecify.annotations.Nullable;

import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * A message associated with a {@link ConditionOutcome}. Provides a fluent builder style
 * API to encourage consistency across all condition messages.
 * <p>与 {@link ConditionOutcome} 关联的消息。提供流畅的构建器风格 API，以鼓励所有条件消息的一致性。</p>
 *
 * @author Phillip Webb
 * @since 1.4.1
 */
public final class ConditionMessage {

	private final @Nullable String message;

	private ConditionMessage() {
		this(null);
	}

	private ConditionMessage(@Nullable String message) {
		this.message = message;
	}

	private ConditionMessage(ConditionMessage prior, String message) {
		this.message = prior.isEmpty() ? message : prior + "; " + message;
	}

	/**
	 * Return {@code true} if the message is empty.
	 * <p>如果消息为空，则返回 {@code true}。</p>
	 *
	 * @return if the message is empty
	 * <p>消息是否为空</p>
	 */
	public boolean isEmpty() {
		return !StringUtils.hasLength(this.message);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof ConditionMessage other) {
			return ObjectUtils.nullSafeEquals(other.message, this.message);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return ObjectUtils.nullSafeHashCode(this.message);
	}

	@Override
	public String toString() {
		return (this.message != null) ? this.message : "";
	}

	/**
	 * Return a new {@link ConditionMessage} based on the instance and an appended
	 * message.
	 * <p>基于当前实例和追加的消息返回一个新的 {@link ConditionMessage}。</p>
	 *
	 * @param message the message to append
	 *                <p>要追加的消息</p>
	 * @return a new {@link ConditionMessage} instance
	 * <p>一个新的 {@link ConditionMessage} 实例</p>
	 */
	public ConditionMessage append(@Nullable String message) {
		if (!StringUtils.hasLength(message)) {
			return this;
		}
		if (!StringUtils.hasLength(this.message)) {
			return new ConditionMessage(message);
		}

		return new ConditionMessage(this.message + " " + message);
	}

	/**
	 * Return a new builder to construct a new {@link ConditionMessage} based on the
	 * instance and a new condition outcome.
	 * <p>基于当前实例和新的条件结果返回一个新的构建器，用于构造新的 {@link ConditionMessage}。</p>
	 *
	 * @param condition the condition
	 *                  <p>条件</p>
	 * @param details details of the condition
	 *                <p>条件的详细信息</p>
	 * @return a {@link Builder} builder
	 * <p>一个 {@link Builder} 构建器</p>
	 * @see #andCondition(String, Object...)
	 * @see #forCondition(Class, Object...)
	 */
	public Builder andCondition(Class<? extends Annotation> condition, Object... details) {
		Assert.notNull(condition, "'condition' must not be null");
		return andCondition("@" + ClassUtils.getShortName(condition), details);
	}

	/**
	 * Return a new builder to construct a new {@link ConditionMessage} based on the
	 * instance and a new condition outcome.
	 * <p>基于当前实例和新的条件结果返回一个新的构建器，用于构造新的 {@link ConditionMessage}。</p>
	 *
	 * @param condition the condition
	 *                  <p>条件</p>
	 * @param details details of the condition
	 *                <p>条件的详细信息</p>
	 * @return a {@link Builder} builder
	 * <p>一个 {@link Builder} 构建器</p>
	 * @see #andCondition(Class, Object...)
	 * @see #forCondition(String, Object...)
	 */
	public Builder andCondition(String condition, Object... details) {
		Assert.notNull(condition, "'condition' must not be null");
		String detail = StringUtils.arrayToDelimitedString(details, " ");
		if (StringUtils.hasLength(detail)) {
			return new Builder(condition + " " + detail);
		}
		return new Builder(condition);
	}

	/**
	 * Factory method to return a new empty {@link ConditionMessage}.
	 * <p>返回新的空 {@link ConditionMessage} 的工厂方法。</p>
	 *
	 * @return a new empty {@link ConditionMessage}
	 * <p>一个新的空 {@link ConditionMessage}</p>
	 */
	public static ConditionMessage empty() {
		return new ConditionMessage();
	}

	/**
	 * Factory method to create a new {@link ConditionMessage} with a specific message.
	 * <p>创建具有特定消息的新 {@link ConditionMessage} 的工厂方法。</p>
	 *
	 * @param message the source message (may be a format string if {@code args} are
	 * specified)
	 *                <p>源消息（如果指定了 {@code args}，则可以是格式字符串）</p>
	 * @param args format arguments for the message
	 *             <p>消息的格式参数</p>
	 * @return a new {@link ConditionMessage} instance
	 * <p>一个新的 {@link ConditionMessage} 实例</p>
	 */
	public static ConditionMessage of(String message, Object... args) {
		if (ObjectUtils.isEmpty(args)) {
			return new ConditionMessage(message);
		}
		return new ConditionMessage(String.format(message, args));
	}

	/**
	 * Factory method to create a new {@link ConditionMessage} comprised of the specified
	 * messages.
	 * <p>创建由指定消息组成的新 {@link ConditionMessage} 的工厂方法。</p>
	 *
	 * @param messages the source messages (may be {@code null})
	 *                 <p>源消息（可以为 {@code null}）</p>
	 * @return a new {@link ConditionMessage} instance
	 * <p>一个新的 {@link ConditionMessage} 实例</p>
	 */
	public static ConditionMessage of(@Nullable Collection<? extends ConditionMessage> messages) {
		ConditionMessage result = new ConditionMessage();
		if (messages != null) {
			for (ConditionMessage message : messages) {
				result = new ConditionMessage(result, message.toString());
			}
		}
		return result;
	}

	/**
	 * Factory method for a builder to construct a new {@link ConditionMessage} for a
	 * condition.
	 * <p>用于为条件构造新 {@link ConditionMessage} 的构建器的工厂方法。</p>
	 *
	 * @param condition the condition
	 *                  <p>条件</p>
	 * @param details details of the condition
	 *                <p>条件的详细信息</p>
	 * @return a {@link Builder} builder
	 * <p>一个 {@link Builder} 构建器</p>
	 * @see #forCondition(String, Object...)
	 * @see #andCondition(String, Object...)
	 */
	public static Builder forCondition(Class<? extends Annotation> condition, Object... details) {
		return new ConditionMessage().andCondition(condition, details);
	}

	/**
	 * Factory method for a builder to construct a new {@link ConditionMessage} for a
	 * condition.
	 * <p>用于为条件构造新 {@link ConditionMessage} 的构建器的工厂方法。</p>
	 *
	 * @param condition the condition
	 *                  <p>条件</p>
	 * @param details details of the condition
	 *                <p>条件的详细信息</p>
	 * @return a {@link Builder} builder
	 * <p>一个 {@link Builder} 构建器</p>
	 * @see #forCondition(Class, Object...)
	 * @see #andCondition(String, Object...)
	 */
	public static Builder forCondition(String condition, Object... details) {
		return new ConditionMessage().andCondition(condition, details);
	}

	/**
	 * Builder used to create a {@link ConditionMessage} for a condition.
	 * <p>用于为条件创建 {@link ConditionMessage} 的构建器。</p>
	 */
	public final class Builder {

		private final String condition;

		private Builder(String condition) {
			this.condition = condition;
		}

		/**
		 * Indicate that an exact result was found. For example
		 * {@code foundExactly("foo")} results in the message "found foo".
		 * <p>表示找到了精确的结果。例如，{@code foundExactly("foo")} 会生成消息 "found foo"。</p>
		 *
		 * @param result the result that was found
		 *               <p>找到的结果</p>
		 * @return a built {@link ConditionMessage}
		 * <p>构建好的 {@link ConditionMessage}</p>
		 */
		public ConditionMessage foundExactly(Object result) {
			return found("").items(result);
		}

		/**
		 * Indicate that one or more results were found. For example
		 * {@code found("bean").items("x")} results in the message "found bean x".
		 * <p>表示找到了一个或多个结果。例如，{@code found("bean").items("x")} 会生成消息 "found bean x"。</p>
		 *
		 * @param article the article found
		 *                <p>找到的冠词</p>
		 * @return an {@link ItemsBuilder}
		 * <p>一个 {@link ItemsBuilder}</p>
		 */
		public ItemsBuilder found(String article) {
			return found(article, article);
		}

		/**
		 * Indicate that one or more results were found. For example
		 * {@code found("bean", "beans").items("x", "y")} results in the message "found
		 * beans x, y".
		 * <p>表示找到了一个或多个结果。例如，{@code found("bean", "beans").items("x", "y")} 会生成消息 "found beans x, y"。</p>
		 *
		 * @param singular the article found in singular form
		 *                 <p>找到的单数形式的冠词</p>
		 * @param plural the article found in plural form
		 *               <p>找到的复数形式的冠词</p>
		 * @return an {@link ItemsBuilder}
		 * <p>一个 {@link ItemsBuilder}</p>
		 */
		public ItemsBuilder found(String singular, String plural) {
			return new ItemsBuilder(this, "found", singular, plural);
		}

		/**
		 * Indicate that one or more results were not found. For example
		 * {@code didNotFind("bean").items("x")} results in the message "did not find bean
		 * x".
		 * <p>表示未找到一个或多个结果。例如，{@code didNotFind("bean").items("x")} 会生成消息 "did not find bean x"。</p>
		 *
		 * @param article the article found
		 *                <p>找到的冠词</p>
		 * @return an {@link ItemsBuilder}
		 * <p>一个 {@link ItemsBuilder}</p>
		 */
		public ItemsBuilder didNotFind(String article) {
			return didNotFind(article, article);
		}

		/**
		 * Indicate that one or more results were found. For example
		 * {@code didNotFind("bean", "beans").items("x", "y")} results in the message "did
		 * not find beans x, y".
		 * <p>表示找到了一个或多个结果。例如，{@code didNotFind("bean", "beans").items("x", "y")} 会生成消息 "did not find beans x, y"。</p>
		 *
		 * @param singular the article found in singular form
		 *                 <p>找到的单数形式的冠词</p>
		 * @param plural the article found in plural form
		 *               <p>找到的复数形式的冠词</p>
		 * @return an {@link ItemsBuilder}
		 * <p>一个 {@link ItemsBuilder}</p>
		 */
		public ItemsBuilder didNotFind(String singular, String plural) {
			return new ItemsBuilder(this, "did not find", singular, plural);
		}

		/**
		 * Indicates a single result. For example {@code resultedIn("yes")} results in the
		 * message "resulted in yes".
		 * <p>表示单个结果。例如，{@code resultedIn("yes")} 会生成消息 "resulted in yes"。</p>
		 *
		 * @param result the result
		 *               <p>结果</p>
		 * @return a built {@link ConditionMessage}
		 * <p>构建好的 {@link ConditionMessage}</p>
		 */
		public ConditionMessage resultedIn(Object result) {
			return because("resulted in " + result);
		}

		/**
		 * Indicates something is available. For example {@code available("money")}
		 * results in the message "money is available".
		 * <p>表示某事物可用。例如，{@code available("money")} 会生成消息 "money is available"。</p>
		 *
		 * @param item the item that is available
		 *             <p>可用的项</p>
		 * @return a built {@link ConditionMessage}
		 * <p>构建好的 {@link ConditionMessage}</p>
		 */
		public ConditionMessage available(String item) {
			return because(item + " is available");
		}

		/**
		 * Indicates something is not available. For example {@code notAvailable("time")}
		 * results in the message "time is not available".
		 * <p>表示某事物不可用。例如，{@code notAvailable("time")} 会生成消息 "time is not available"。</p>
		 *
		 * @param item the item that is not available
		 *             <p>不可用的项</p>
		 * @return a built {@link ConditionMessage}
		 * <p>构建好的 {@link ConditionMessage}</p>
		 */
		public ConditionMessage notAvailable(String item) {
			return because(item + " is not available");
		}

		/**
		 * Indicates the reason. For example {@code because("running Linux")} results in
		 * the message "running Linux".
		 * <p>表示原因。例如，{@code because("running Linux")} 会生成消息 "running Linux"。</p>
		 *
		 * @param reason the reason for the message
		 *               <p>消息的原因</p>
		 * @return a built {@link ConditionMessage}
		 * <p>构建好的 {@link ConditionMessage}</p>
		 */
		public ConditionMessage because(@Nullable String reason) {
			if (StringUtils.hasLength(reason)) {
				return new ConditionMessage(ConditionMessage.this,
						StringUtils.hasLength(this.condition) ? this.condition + " " + reason : reason);
			}
			return new ConditionMessage(ConditionMessage.this, this.condition);
		}

	}

	/**
	 * Builder used to create an {@link ItemsBuilder} for a condition.
	 * <p>用于为条件创建 {@link ItemsBuilder} 的构建器。</p>
	 */
	public final class ItemsBuilder {

		private final Builder condition;

		private final String reason;

		private final String singular;

		private final String plural;

		private ItemsBuilder(Builder condition, String reason, String singular, String plural) {
			this.condition = condition;
			this.reason = reason;
			this.singular = singular;
			this.plural = plural;
		}

		/**
		 * Used when no items are available. For example
		 * {@code didNotFind("any beans").atAll()} results in the message "did not find
		 * any beans".
		 * <p>当没有可用项时使用。例如，{@code didNotFind("any beans").atAll()} 会生成消息 "did not find any beans"。</p>
		 *
		 * @return a built {@link ConditionMessage}
		 * <p>构建好的 {@link ConditionMessage}</p>
		 */
		public ConditionMessage atAll() {
			return items(Collections.emptyList());
		}

		/**
		 * Indicate the items. For example
		 * {@code didNotFind("bean", "beans").items("x", "y")} results in the message "did
		 * not find beans x, y".
		 * <p>指示项。例如，{@code didNotFind("bean", "beans").items("x", "y")} 会生成消息 "did not find beans x, y"。</p>
		 *
		 * @param items the items (may be {@code null})
		 *              <p>项（可以为 {@code null}）</p>
		 * @return a built {@link ConditionMessage}
		 * <p>构建好的 {@link ConditionMessage}</p>
		 */
		public ConditionMessage items(Object @Nullable ... items) {
			return items(Style.NORMAL, items);
		}

		/**
		 * Indicate the items. For example
		 * {@code didNotFind("bean", "beans").items("x", "y")} results in the message "did
		 * not find beans x, y".
		 * <p>指示项。例如，{@code didNotFind("bean", "beans").items("x", "y")} 会生成消息 "did not find beans x, y"。</p>
		 *
		 * @param style the render style
		 *              <p>渲染样式</p>
		 * @param items the items (may be {@code null})
		 *              <p>项（可以为 {@code null}）</p>
		 * @return a built {@link ConditionMessage}
		 * <p>构建好的 {@link ConditionMessage}</p>
		 */
		public ConditionMessage items(Style style, Object @Nullable ... items) {
			return items(style, (items != null) ? Arrays.asList(items) : null);
		}

		/**
		 * Indicate the items. For example
		 * {@code didNotFind("bean", "beans").items(Collections.singleton("x")} results in
		 * the message "did not find bean x".
		 * <p>指示项。例如，{@code didNotFind("bean", "beans").items(Collections.singleton("x")} 会生成消息 "did not find bean x"。</p>
		 *
		 * @param items the source of the items (may be {@code null})
		 *              <p>项的来源（可以为 {@code null}）</p>
		 * @return a built {@link ConditionMessage}
		 * <p>构建好的 {@link ConditionMessage}</p>
		 */
		public ConditionMessage items(@Nullable Collection<?> items) {
			return items(Style.NORMAL, items);
		}

		/**
		 * Indicate the items with a {@link Style}. For example
		 * {@code didNotFind("bean", "beans").items(Style.QUOTE, Collections.singleton("x")}
		 * results in the message "did not find bean 'x'".
		 * <p>使用 {@link Style} 指示项。例如，{@code didNotFind("bean", "beans").items(Style.QUOTE, Collections.singleton("x")} 会生成消息 "did not find bean 'x'"。</p>
		 *
		 * @param style the render style
		 *              <p>渲染样式</p>
		 * @param items the source of the items (may be {@code null})
		 *              <p>项的来源（可以为 {@code null}）</p>
		 * @return a built {@link ConditionMessage}
		 * <p>构建好的 {@link ConditionMessage}</p>
		 */
		public ConditionMessage items(Style style, @Nullable Collection<?> items) {
			Assert.notNull(style, "'style' must not be null");
			StringBuilder message = new StringBuilder(this.reason);
			items = style.applyTo(items);
			if ((this.condition == null || items == null || items.size() <= 1)
					&& StringUtils.hasLength(this.singular)) {
				message.append(" ").append(this.singular);
			}
			else if (StringUtils.hasLength(this.plural)) {
				message.append(" ").append(this.plural);
			}
			if (!CollectionUtils.isEmpty(items)) {
				message.append(" ").append(StringUtils.collectionToDelimitedString(items, ", "));
			}
			return this.condition.because(message.toString());
		}

	}

	/**
	 * Render styles.
	 * <p>渲染样式。</p>
	 */
	public enum Style {

		/**
		 * Render with normal styling.
		 * <p>使用普通样式渲染。</p>
		 */
		NORMAL {

			@Override
			protected @Nullable Object applyToItem(@Nullable Object item) {
				return item;
			}

		},

		/**
		 * Render with the item surrounded by quotes.
		 * <p>使用引号包围项进行渲染。</p>
		 */
		QUOTE {

			@Override
			protected @Nullable String applyToItem(@Nullable Object item) {
				return (item != null) ? "'" + item + "'" : null;
			}

		};

		public @Nullable Collection<?> applyTo(@Nullable Collection<?> items) {
			if (items == null) {
				return null;
			}
			List<Object> result = new ArrayList<>(items.size());
			for (Object item : items) {
				Object applied = applyToItem(item);
				if (applied != null) {
					result.add(applied);
				}
			}
			return result;
		}

		abstract @Nullable Object applyToItem(@Nullable Object item);

	}

}