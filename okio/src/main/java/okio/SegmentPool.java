/*
 * Copyright (C) 2014 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package okio;

import javax.annotation.Nullable;

/**
 * A collection of unused segments, necessary to avoid GC churn and zero-fill.
 * This pool is a thread-safe static singleton.
 */
final class SegmentPool {
  /** The maximum number of bytes to pool. */
  // TODO: Is 64 KiB a good maximum size? Do we ever have that many idle segments?
  static final long MAX_SIZE = 64 * 1024; // 64 KiB.

  /** Singly-linked list of segments. */
  static @Nullable Segment next;

  /** Total bytes in this pool. */
  static long byteCount;

  private SegmentPool() {
  }

  public static long totalPooledTakes = 0;
  public static long totalPooledTakesDuration = 0;

  public static long totalNewTakes = 0;
  public static long totalNewTakesDuration = 0;

  static Segment take() {
    synchronized (SegmentPool.class) {
      if (next != null) {
//        System.out.println("take");
        totalPooledTakes++;
        long start = System.nanoTime();
        Segment result = next;
        next = result.next;
        result.next = null;
        byteCount -= Segment.SIZE;
        totalPooledTakesDuration  += System.nanoTime() - start;
        return result;
      }
    }
    totalNewTakes++;
    long start = System.nanoTime();
    Segment result = new Segment();
    totalNewTakesDuration += System.nanoTime() - start;
    return result; // Pool is empty. Don't zero-fill while holding a lock.
  }

  static void recycle(Segment segment) {
//    System.out.println("recycle 1");
    if (segment.next != null || segment.prev != null) throw new IllegalArgumentException();
    if (segment.shared) return; // This segment cannot be recycled.
//    System.out.println("recycle 2");
    synchronized (SegmentPool.class) {
      if (byteCount + Segment.SIZE > MAX_SIZE) return; // Pool is full.
      byteCount += Segment.SIZE;
//      System.out.println("recycle 3");
      segment.next = next;
      segment.pos = segment.limit = 0;
      next = segment;
    }
  }
}
