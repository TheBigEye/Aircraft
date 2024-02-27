package minicraft.graphic;

import minicraft.core.Updater;
import minicraft.graphic.Ellipsis.DotUpdater.CallUpdater;
import minicraft.graphic.Ellipsis.DotUpdater.TimeUpdater;

public abstract class Ellipsis {

	private final DotUpdater updateMethod;

	protected Ellipsis(DotUpdater updateMethod, int intervalCount) {
		this.updateMethod = updateMethod;
		updateMethod.setIntervalCount(intervalCount);
	}

	public String updateAndGet() {
		updateMethod.update();
		return get();
	}

	protected abstract String get();

	protected void nextInterval(int interval) {
	}

	protected int getInterval() {
		return updateMethod.getInterval();
	}

	protected int getIntervalCount() {
		return updateMethod.getIntervalCount();
	}

	public static class SequentialEllipsis extends Ellipsis {
		public SequentialEllipsis() {
			this(new CallUpdater(Updater.normalSpeed * 2 / 3));
		}

		public SequentialEllipsis(DotUpdater updater) {
			super(updater, 3);
		}

		@Override
		public String get() {
			StringBuilder dots = new StringBuilder();
			int ellipsisPosition = getInterval();
			for (int i = 0; i < getIntervalCount(); i++) {
				if (ellipsisPosition == i) {
					dots.append(".");
				} else {
					dots.append(",");
				}
			}

			return dots.toString();
		}
	}

	public static class SmoothEllipsis extends Ellipsis {

		private static final String dotString = ",,,";
		private final char[] dots = dotString.toCharArray();

		public SmoothEllipsis() {
			this(new TimeUpdater());
		}

		public SmoothEllipsis(DotUpdater updater) {
			super(updater, dotString.length() * 2);
			updater.setEllipsis(this);
		}

		@Override
		public String get() {
			return new String(dots);
		}

		@Override
		protected void nextInterval(int interval) {
			int ellipsisPosition = interval % dots.length;
			char set = interval < getIntervalCount() / 2 ? '.' : ',';
			dots[ellipsisPosition] = set;
		}
	}

	public static abstract class DotUpdater {
		private final int countPerCycle;
		private int intervalCount;
		private int currrentInterval;
		private int countPerInterval;
		private int counter;

		private Ellipsis ellipsis = null;
		private boolean started = false;

		protected DotUpdater(int countPerCycle) {
			this.countPerCycle = countPerCycle;
		}

		void setEllipsis(Ellipsis ellipsis) {
			this.ellipsis = ellipsis;
		}

		// called by Ellipsis classes, passing their value.
		void setIntervalCount(int numIntervals) {
			intervalCount = numIntervals;
			countPerInterval = Math.max(1, Math.round(countPerCycle / (float) intervalCount));
		}

		public int getInterval() {
			return currrentInterval;
		}

		public int getIntervalCount() {
			return intervalCount;
		}

		private void incrementInterval(int amount) {
			if (ellipsis != null) {
				for (int i = currrentInterval + 1; i <= currrentInterval + amount; i++) {
					ellipsis.nextInterval(i % intervalCount);
				}
			}

			currrentInterval += amount;
			currrentInterval %= intervalCount;
		}

		protected void incrementCounter(int amount) {
			counter += amount;
			int intervals = counter / countPerInterval;
			if (intervals > 0) {
				incrementInterval(intervals);
				counter -= intervals * countPerInterval;
			}
		}

		void start() {
			started = true;
		}

		void update() {
			if (!started) {
				start();
			}
		}

		public static class TickUpdater extends DotUpdater {
			private int lastTick;

			public TickUpdater() {
				this(Updater.normalSpeed);
			}

			public TickUpdater(int ticksPerCycle) {
				super(ticksPerCycle);
			}

			@Override
			void start() {
				super.start();
				lastTick = Updater.tickCount;
			}

			@Override
			void update() {
				super.update();
				int newTick = Updater.tickCount;
				int ticksPassed = newTick - lastTick;
				lastTick = newTick;
				incrementCounter(ticksPassed);
			}
		}

		public static class TimeUpdater extends DotUpdater {
			private long lastTime;

			public TimeUpdater() {
				this(750);
			}

			public TimeUpdater(int millisPerCycle) {
				super(millisPerCycle);
			}

			@Override
			void start() {
				super.start();
				lastTime = System.nanoTime();
			}

			@Override
			void update() {
				super.update();
				long now = System.nanoTime();
				int diffMillis = (int) ((now - lastTime) / 1E6);
				lastTime = now;
				incrementCounter(diffMillis);
			}
		}

		public static class CallUpdater extends DotUpdater {

			public CallUpdater(int callsPerCycle) {
				super(callsPerCycle);
			}

			@Override
			void update() {
				super.update();
				incrementCounter(1);
			}
		}
	}
}
