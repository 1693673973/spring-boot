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

package org.springframework.boot.autoconfigure.ssl;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.time.Duration;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jspecify.annotations.Nullable;

import org.springframework.core.log.LogMessage;
import org.springframework.util.Assert;

/**
 * Watches files and directories and triggers a callback on change.
 *
 * <p>监视文件和目录，并在更改时触发回调。</p>
 *
 * @author Moritz Halbritter
 * @author Phillip Webb
 */
class FileWatcher implements Closeable {

	private static final Log logger = LogFactory.getLog(FileWatcher.class);

	private final Duration quietPeriod;

	private final Object lock = new Object();

	private @Nullable WatcherThread thread;

	/**
	 * Create a new {@link FileWatcher} instance.
	 *
	 * <p>创建一个新的 {@link FileWatcher} 实例。</p>
	 *
	 * @param quietPeriod the duration that no file changes should occur before triggering
	 * actions
	 *
	 * <p>在触发操作之前不应发生文件更改的持续时间</p>
	 */
	FileWatcher(Duration quietPeriod) {
		Assert.notNull(quietPeriod, "'quietPeriod' must not be null");
		this.quietPeriod = quietPeriod;
	}

	/**
	 * Watch the given files or directories for changes.
	 *
	 * <p>监视给定的文件或目录的更改。</p>
	 *
	 * @param paths the files or directories to watch
	 *
	 * <p>要监视的文件或目录</p>
	 *
	 * @param action the action to take when changes are detected
	 *
	 * <p>检测到更改时要执行的操作</p>
	 */
	void watch(Set<Path> paths, Runnable action) {
		Assert.notNull(paths, "'paths' must not be null");
		Assert.notNull(action, "'action' must not be null");
		if (paths.isEmpty()) {
			return;
		}
		synchronized (this.lock) {
			try {
				if (this.thread == null) {
					this.thread = new WatcherThread();
					this.thread.start();
				}
				this.thread.register(new Registration(getRegistrationPaths(paths), action));
			}
			catch (IOException ex) {
				throw new UncheckedIOException("Failed to register paths for watching: " + paths, ex);
			}
		}
	}

	/**
	 * Retrieves all {@link Path Paths} that should be registered for the specified
	 * {@link Path}. If the path is a symlink, changes to the symlink should be monitored,
	 * not just the file it points to. For example, for the given {@code keystore.jks}
	 * path in the following directory structure:<pre>
	 * +- stores
	 * |  +─ keystore.jks
	 * +- <em>data</em> -&gt; stores
	 * +─ <em>keystore.jks</em> -&gt; data/keystore.jks
	 * </pre> the resulting paths would include:
	 * <p>
	 * <ul>
	 * <li>{@code keystore.jks}</li>
	 * <li>{@code data/keystore.jks}</li>
	 * <li>{@code data}</li>
	 * <li>{@code stores/keystore.jks}</li>
	 * </ul>
	 *
	 * <p>检索所有应针对指定 {@link Path} 注册的 {@link Path Paths}。如果路径是符号链接，则应监控符号链接的更改，而不仅仅是它指向的文件。例如，对于以下目录结构中给定的 {@code keystore.jks} 路径：</p>
	 *
	 * <p>（代码块见英文部分）</p>
	 *
	 * <p>结果路径将包括：</p>
	 *
	 * <ul>
	 * <li>{@code keystore.jks}</li>
	 * <li>{@code data/keystore.jks}</li>
	 * <li>{@code data}</li>
	 * <li>{@code stores/keystore.jks}</li>
	 * </ul>
	 *
	 * @param paths the source paths
	 *
	 * <p>源路径</p>
	 *
	 * @return all possible {@link Path} instances to be registered
	 *
	 * <p>所有可能要注册的 {@link Path} 实例</p>
	 *
	 * @throws IOException if an I/O error occurs
	 *
	 * <p>如果发生 I/O 错误</p>
	 */
	private Set<Path> getRegistrationPaths(Set<Path> paths) throws IOException {
		Set<Path> result = new HashSet<>();
		for (Path path : paths) {
			collectRegistrationPaths(path, result);
		}
		return Collections.unmodifiableSet(result);
	}

	private void collectRegistrationPaths(Path path, Set<Path> result) throws IOException {
		path = path.toAbsolutePath();
		result.add(path);
		Path parent = path.getParent();
		if (parent != null && Files.isSymbolicLink(parent)) {
			result.add(parent);
			collectRegistrationPaths(resolveSiblingSymbolicLink(parent).resolve(path.getFileName()), result);
		}
		else if (Files.isSymbolicLink(path)) {
			collectRegistrationPaths(resolveSiblingSymbolicLink(path), result);
		}
	}

	private Path resolveSiblingSymbolicLink(Path path) throws IOException {
		return path.resolveSibling(Files.readSymbolicLink(path));
	}

	@Override
	public void close() throws IOException {
		synchronized (this.lock) {
			if (this.thread != null) {
				this.thread.close();
				this.thread.interrupt();
				try {
					this.thread.join();
				}
				catch (InterruptedException ex) {
					Thread.currentThread().interrupt();
				}
				this.thread = null;
			}
		}
	}

	/**
	 * The watcher thread used to check for changes.
	 *
	 * <p>用于检查更改的监视器线程。</p>
	 */
	private class WatcherThread extends Thread implements Closeable {

		private final WatchService watchService = FileSystems.getDefault().newWatchService();

		private final Map<WatchKey, List<Registration>> registrations = new ConcurrentHashMap<>();

		private volatile boolean running = true;

		WatcherThread() throws IOException {
			setName("ssl-bundle-watcher");
			setDaemon(true);
			setUncaughtExceptionHandler(this::onThreadException);
		}

		private void onThreadException(Thread thread, Throwable throwable) {
			logger.error("Uncaught exception in file watcher thread", throwable);
		}

		void register(Registration registration) throws IOException {
			Set<Path> directories = new HashSet<>();
			for (Path path : registration.paths()) {
				if (!Files.isRegularFile(path) && !Files.isDirectory(path)) {
					throw new IOException("'%s' is neither a file nor a directory".formatted(path));
				}
				Path directory = Files.isDirectory(path) ? path : path.getParent();
				directories.add(directory);
			}
			for (Path directory : directories) {
				WatchKey watchKey = register(directory);
				this.registrations.computeIfAbsent(watchKey, (key) -> new CopyOnWriteArrayList<>()).add(registration);
			}
		}

		private WatchKey register(Path directory) throws IOException {
			logger.debug(LogMessage.format("Registering '%s'", directory));
			return directory.register(this.watchService, StandardWatchEventKinds.ENTRY_CREATE,
					StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
		}

		@Override
		public void run() {
			logger.debug("Watch thread started");
			Set<Runnable> actions = new HashSet<>();
			while (this.running) {
				try {
					long timeout = FileWatcher.this.quietPeriod.toMillis();
					WatchKey key = this.watchService.poll(timeout, TimeUnit.MILLISECONDS);
					if (key == null) {
						actions.forEach(this::runSafely);
						actions.clear();
					}
					else {
						accumulate(key, actions);
						key.reset();
					}
				}
				catch (InterruptedException ex) {
					Thread.currentThread().interrupt();
				}
				catch (ClosedWatchServiceException ex) {
					logger.debug("File watcher has been closed");
					this.running = false;
				}
			}
			logger.debug("Watch thread stopped");
		}

		private void runSafely(Runnable action) {
			try {
				action.run();
			}
			catch (Throwable ex) {
				logger.error("Unexpected SSL reload error", ex);
			}
		}

		private void accumulate(WatchKey key, Set<Runnable> actions) {
			List<Registration> registrations = this.registrations.get(key);
			Path directory = (Path) key.watchable();
			for (WatchEvent<?> event : key.pollEvents()) {
				Path file = directory.resolve((Path) event.context());
				Assert.state(registrations != null, "'registrations' must not be null");
				for (Registration registration : registrations) {
					if (registration.manages(file)) {
						actions.add(registration.action());
					}
				}
			}
		}

		@Override
		public void close() throws IOException {
			this.running = false;
			this.watchService.close();
		}

	}

	/**
	 * An individual watch registration.
	 *
	 * <p>一个单独的监视注册。</p>
	 *
	 * @param paths the paths being registered
	 *
	 * <p>正在注册的路径</p>
	 *
	 * @param action the action to take
	 *
	 * <p>要执行的操作</p>
	 */
	private record Registration(Set<Path> paths, Runnable action) {

		boolean manages(Path file) {
			Path absolutePath = file.toAbsolutePath();
			return this.paths.contains(absolutePath) || isInDirectories(absolutePath);
		}

		private boolean isInDirectories(Path file) {
			return this.paths.stream().filter(Files::isDirectory).anyMatch(file::startsWith);
		}

	}

}