package tachyon.worker.block;

import tachyon.conf.TachyonConf;
import tachyon.worker.block.allocator.Allocator;
import tachyon.worker.block.allocator.NaiveAllocator;
import tachyon.worker.block.evictor.Evictor;
import tachyon.worker.block.evictor.NaiveEvictor;
import tachyon.worker.block.meta.BlockMeta;
import tachyon.worker.block.meta.BlockWorkerMetadata;

/**
 * Central management for block level operations.
 */
public class BlockWorker {
  private final TachyonConf mTachyonConf;
  private final BlockWorkerMetadata mMetadata;

  private final Allocator mAllocator;
  private final Evictor mEvictor;

  public BlockWorker() {
    mTachyonConf = new TachyonConf();
    mMetadata = new BlockWorkerMetadata(mTachyonConf);

    mAllocator = new NaiveAllocator(mMetadata);
    mEvictor = new NaiveEvictor(mMetadata);
  }

  public String createBlock(int userId, long blockId, long blockSize, int tierHint) {
    BlockMeta meta = mAllocator.allocateBlock(userId, blockId, blockSize, tierHint);
    if (meta == null) {
      mEvictor.freeSpace(blockSize, tierHint);
      meta = mAllocator.allocateBlock(userId, blockId, blockSize, tierHint);
    }
    return meta.getTmpPath();
  }
}
