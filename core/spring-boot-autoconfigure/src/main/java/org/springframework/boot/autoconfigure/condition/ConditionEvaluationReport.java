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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.jspecify.annotations.Nullable;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * Records condition evaluation details for reporting and logging.
 * <p>记录条件评估的详细信息，用于报告和日志记录。</p>
 *
 * @author Greg Turnquist
 * @author Dave Syer
 * @author Phillip Webb
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 * @since 1.0.0
 */
public final class ConditionEvaluationReport {

	private static final String BEAN_NAME = "autoConfigurationReport";

	private static final AncestorsMatchedCondition ANCESTOR_CONDITION = new AncestorsMatchedCondition();

	private final SortedMap<String, ConditionAndOutcomes> outcomes = new TreeMap<>();

	private boolean addedAncestorOutcomes;

	private @Nullable ConditionEvaluationReport parent;

	private final List<String> exclusions = new ArrayList<>();

	private final Set<String> unconditionalClasses = new HashSet<>();

	/**
	 * Private constructor.
	 * <p>私有构造函数。</p>
	 *
	 * @see #get(ConfigurableListableBeanFactory)
	 */
	private ConditionEvaluationReport() {
	}

	/**
	 * Record the occurrence of condition evaluation.
	 * <p>记录条件评估的发生情况。</p>
	 *
	 * @param source the source of the condition (class or method name)
	 *               <p>条件的来源（类名或方法名）</p>
	 * @param condition the condition evaluated
	 *                  <p>被评估的条件</p>
	 * @param outcome the condition outcome
	 *                <p>条件结果</p>
	 */
	public void recordConditionEvaluation(String source, Condition condition, ConditionOutcome outcome) {
		Assert.notNull(source, "'source' must not be null");
		Assert.notNull(condition, "'condition' must not be null");
		Assert.notNull(outcome, "'outcome' must not be null");
		this.unconditionalClasses.remove(source);
		this.outcomes.computeIfAbsent(source, (key) -> new ConditionAndOutcomes()).add(condition, outcome);
		this.addedAncestorOutcomes = false;
	}

	/**
	 * Records the names of the classes that have been excluded from condition evaluation.
	 * <p>记录已从条件评估中排除的类的名称。</p>
	 *
	 * @param exclusions the names of the excluded classes
	 *                   <p>被排除类的名称</p>
	 */
	public void recordExclusions(Collection<String> exclusions) {
		Assert.notNull(exclusions, "'exclusions' must not be null");
		this.exclusions.addAll(exclusions);
	}

	/**
	 * Records the names of the classes that are candidates for condition evaluation.
	 * <p>记录作为条件评估候选者的类的名称。</p>
	 *
	 * @param evaluationCandidates the names of the classes whose conditions will be
	 * evaluated
	 *                             <p>其条件将被评估的类的名称</p>
	 */
	public void recordEvaluationCandidates(List<String> evaluationCandidates) {
		Assert.notNull(evaluationCandidates, "'evaluationCandidates' must not be null");
		this.unconditionalClasses.addAll(evaluationCandidates);
	}

	/**
	 * Returns condition outcomes from this report, grouped by the source.
	 * <p>返回此报告中的条件结果，按来源分组。</p>
	 *
	 * @return the condition outcomes
	 * <p>条件结果</p>
	 */
	public Map<String, ConditionAndOutcomes> getConditionAndOutcomesBySource() {
		if (!this.addedAncestorOutcomes) {
			this.outcomes.forEach((source, sourceOutcomes) -> {
				if (!sourceOutcomes.isFullMatch()) {
					addNoMatchOutcomeToAncestors(source);
				}
			});
			this.addedAncestorOutcomes = true;
		}
		return Collections.unmodifiableMap(this.outcomes);
	}

	private void addNoMatchOutcomeToAncestors(String source) {
		String prefix = source + "$";
		this.outcomes.forEach((candidateSource, sourceOutcomes) -> {
			if (candidateSource.startsWith(prefix)) {
				ConditionOutcome outcome = ConditionOutcome
						.noMatch(ConditionMessage.forCondition("Ancestor " + source).because("did not match"));
				sourceOutcomes.add(ANCESTOR_CONDITION, outcome);
			}
		});
	}

	/**
	 * Returns the names of the classes that have been excluded from condition evaluation.
	 * <p>返回已从条件评估中排除的类的名称。</p>
	 *
	 * @return the names of the excluded classes
	 * <p>被排除类的名称</p>
	 */
	public List<String> getExclusions() {
		return Collections.unmodifiableList(this.exclusions);
	}

	/**
	 * Returns the names of the classes that were evaluated but were not conditional.
	 * <p>返回已评估但无条件的类的名称。</p>
	 *
	 * @return the names of the unconditional classes
	 * <p>无条件类的名称</p>
	 */
	public Set<String> getUnconditionalClasses() {
		Set<String> filtered = new HashSet<>(this.unconditionalClasses);
		this.exclusions.forEach(filtered::remove);
		return Collections.unmodifiableSet(filtered);
	}

	/**
	 * The parent report (from a parent BeanFactory if there is one).
	 * <p>父报告（如果有父 BeanFactory，则来自父 BeanFactory）。</p>
	 *
	 * @return the parent report (or null if there isn't one)
	 * <p>父报告（如果没有则为 null）</p>
	 */
	public @Nullable ConditionEvaluationReport getParent() {
		return this.parent;
	}

	/**
	 * Attempt to find the {@link ConditionEvaluationReport} for the specified bean
	 * factory.
	 * <p>尝试为指定的 bean 工厂查找 {@link ConditionEvaluationReport}。</p>
	 *
	 * @param beanFactory the bean factory (may be {@code null})
	 *                    <p>bean 工厂（可以为 {@code null}）</p>
	 * @return the {@link ConditionEvaluationReport} or {@code null}
	 * <p>{@link ConditionEvaluationReport} 或 {@code null}</p>
	 */
	public static @Nullable ConditionEvaluationReport find(BeanFactory beanFactory) {
		if (beanFactory instanceof ConfigurableListableBeanFactory configurableListableBeanFactory) {
			return ConditionEvaluationReport.get(configurableListableBeanFactory);
		}
		return null;
	}

	/**
	 * Obtain a {@link ConditionEvaluationReport} for the specified bean factory.
	 * <p>为指定的 bean 工厂获取 {@link ConditionEvaluationReport}。</p>
	 *
	 * @param beanFactory the bean factory
	 *                    <p>bean 工厂</p>
	 * @return an existing or new {@link ConditionEvaluationReport}
	 * <p>现有的或新的 {@link ConditionEvaluationReport}</p>
	 */
	public static ConditionEvaluationReport get(ConfigurableListableBeanFactory beanFactory) {
		synchronized (beanFactory) {
			ConditionEvaluationReport report;
			if (beanFactory.containsSingleton(BEAN_NAME)) {
				report = beanFactory.getBean(BEAN_NAME, ConditionEvaluationReport.class);
			}
			else {
				report = new ConditionEvaluationReport();
				beanFactory.registerSingleton(BEAN_NAME, report);
			}
			locateParent(beanFactory.getParentBeanFactory(), report);
			return report;
		}
	}

	private static void locateParent(@Nullable BeanFactory beanFactory, ConditionEvaluationReport report) {
		if (beanFactory != null && report.parent == null && beanFactory.containsBean(BEAN_NAME)) {
			report.parent = beanFactory.getBean(BEAN_NAME, ConditionEvaluationReport.class);
		}
	}

	public ConditionEvaluationReport getDelta(ConditionEvaluationReport previousReport) {
		ConditionEvaluationReport delta = new ConditionEvaluationReport();
		this.outcomes.forEach((source, sourceOutcomes) -> {
			ConditionAndOutcomes previous = previousReport.outcomes.get(source);
			if (previous == null || previous.isFullMatch() != sourceOutcomes.isFullMatch()) {
				sourceOutcomes.forEach((conditionAndOutcome) -> delta.recordConditionEvaluation(source,
						conditionAndOutcome.getCondition(), conditionAndOutcome.getOutcome()));
			}
		});
		List<String> newExclusions = new ArrayList<>(this.exclusions);
		newExclusions.removeAll(previousReport.getExclusions());
		delta.recordExclusions(newExclusions);
		List<String> newUnconditionalClasses = new ArrayList<>(this.unconditionalClasses);
		newUnconditionalClasses.removeAll(previousReport.unconditionalClasses);
		delta.unconditionalClasses.addAll(newUnconditionalClasses);
		return delta;
	}

	/**
	 * Provides access to a number of {@link ConditionAndOutcome} items.
	 * <p>提供对多个 {@link ConditionAndOutcome} 项的访问。</p>
	 */
	public static class ConditionAndOutcomes implements Iterable<ConditionAndOutcome> {

		private final Set<ConditionAndOutcome> outcomes = new LinkedHashSet<>();

		public void add(Condition condition, ConditionOutcome outcome) {
			this.outcomes.add(new ConditionAndOutcome(condition, outcome));
		}

		/**
		 * Return {@code true} if all outcomes match.
		 * <p>如果所有结果都匹配，则返回 {@code true}。</p>
		 *
		 * @return {@code true} if a full match
		 * <p>如果完全匹配则返回 {@code true}</p>
		 */
		public boolean isFullMatch() {
			for (ConditionAndOutcome conditionAndOutcomes : this) {
				if (!conditionAndOutcomes.getOutcome().isMatch()) {
					return false;
				}
			}
			return true;
		}

		/**
		 * Return a {@link Stream} of the {@link ConditionAndOutcome} items.
		 * <p>返回 {@link ConditionAndOutcome} 项的 {@link Stream}。</p>
		 *
		 * @return a stream of the {@link ConditionAndOutcome} items.
		 * <p>{@link ConditionAndOutcome} 项的流。</p>
		 * @since 3.5.0
		 */
		public Stream<ConditionAndOutcome> stream() {
			return StreamSupport.stream(spliterator(), false);
		}

		@Override
		public Iterator<ConditionAndOutcome> iterator() {
			return Collections.unmodifiableSet(this.outcomes).iterator();
		}

	}

	/**
	 * Provides access to a single {@link Condition} and {@link ConditionOutcome}.
	 * <p>提供对单个 {@link Condition} 和 {@link ConditionOutcome} 的访问。</p>
	 */
	public static class ConditionAndOutcome {

		private final Condition condition;

		private final ConditionOutcome outcome;

		public ConditionAndOutcome(Condition condition, ConditionOutcome outcome) {
			this.condition = condition;
			this.outcome = outcome;
		}

		public Condition getCondition() {
			return this.condition;
		}

		public ConditionOutcome getOutcome() {
			return this.outcome;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null || getClass() != obj.getClass()) {
				return false;
			}
			ConditionAndOutcome other = (ConditionAndOutcome) obj;
			return (ObjectUtils.nullSafeEquals(this.condition.getClass(), other.condition.getClass())
					&& ObjectUtils.nullSafeEquals(this.outcome, other.outcome));
		}

		@Override
		public int hashCode() {
			return this.condition.getClass().hashCode() * 31 + this.outcome.hashCode();
		}

		@Override
		public String toString() {
			return this.condition.getClass() + " " + this.outcome;
		}

	}

	private static final class AncestorsMatchedCondition implements Condition {

		@Override
		public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
			throw new UnsupportedOperationException();
		}

	}

}