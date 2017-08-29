/*     */ package com.boco.common.util.lang;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.common.util.except.UserException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Date;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Vector;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.LinkedBlockingQueue;
/*     */ import java.util.concurrent.Semaphore;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class QueueThreadPool<E>
/*     */ {
/*     */   public static final String PUBLIC_POOL_NAME = "PUBLIC";
/*  38 */   private Map<String, QueueThreadPool<E>.AbstractThreadEleHandler> threadEleHandlers = new ConcurrentHashMap();
/*     */ 
/*  40 */   private int checkPoolEleNum = 1000;
/*  41 */   private int checkPoolTime = 300;
/*  42 */   private int maxEleHandleTime = 0;
/*     */ 
/*  44 */   private long poolTotalRecvEleCount = 0L;
/*  45 */   private long poolTotalHandledEleCount = 0L;
/*  46 */   private long poolLastHandleEleCount = 0L;
/*  47 */   private long poolSysStartTime = System.currentTimeMillis();
/*  48 */   private long poolLastCheckTime = System.currentTimeMillis();
/*     */ 
/*  50 */   private String queuePoolName = "";
/*  51 */   private int maxEventThreadPoolNum = 100;
/*     */ 
/*     */   public QueueThreadPool(String queuePoolName) {
/*  54 */     this.queuePoolName = queuePoolName;
/*     */   }
/*     */ 
/*     */   public QueueThreadPool(String queuePoolName, int maxEleHandleTime, int checkPoolEleNum, int checkPoolTime) {
/*  58 */     this.checkPoolEleNum = checkPoolEleNum;
/*  59 */     this.maxEleHandleTime = maxEleHandleTime;
/*  60 */     this.checkPoolTime = checkPoolTime;
/*  61 */     this.queuePoolName = queuePoolName;
/*  62 */     LogHome.getLog().warn("创建队列线程池 queuePoolName=" + this.queuePoolName + ", maxEleHandleTime=" + maxEleHandleTime + ", checkPoolEleNum=" + checkPoolEleNum + ", checkPoolTime=" + checkPoolTime);
/*     */   }
/*     */ 
/*     */   public void createThreadPool(String threadPoolName, int maxThreadNum, boolean isSyncCheck, IQueueThreadHandler handler)
/*     */   {
/*  67 */     createThreadPool(threadPoolName, maxThreadNum, isSyncCheck, handler, 0, 0);
/*     */   }
/*     */ 
/*     */   public void createThreadPool(String threadPoolName, int maxThreadNum, boolean isSyncCheck, IQueueThreadHandler handler, int queueLenThreshold, int queueAddWaitTime)
/*     */   {
/*  72 */     if (maxThreadNum < 1) maxThreadNum = 1;
/*  73 */     if (handler == null) throw new UserException("事件处理器为NULL ！");
/*  74 */     if (threadPoolName == null) throw new UserException("线程名称为NULL !");
/*     */ 
/*  76 */     if (!this.threadEleHandlers.containsKey(threadPoolName)) {
/*  77 */       LogHome.getLog().warn("创建线程池: queuePoolName=" + this.queuePoolName + ", threadPoolName=" + threadPoolName + ", maxThreadNum=" + maxThreadNum);
/*  78 */       if (maxThreadNum > 1) {
/*  79 */         this.threadEleHandlers.put(threadPoolName, new DynThreadEleHandler(threadPoolName, maxThreadNum, isSyncCheck, handler, queueLenThreshold, queueAddWaitTime, null));
/*     */       }
/*     */       else
/*  82 */         this.threadEleHandlers.put(threadPoolName, new SingleThreadEleHandler(threadPoolName, isSyncCheck, handler, queueLenThreshold, queueAddWaitTime, null));
/*     */     }
/*     */   }
/*     */ 
/*     */   public void closeThreadPool(String threadPoolName)
/*     */   {
/*  89 */     AbstractThreadEleHandler handler = (AbstractThreadEleHandler)this.threadEleHandlers.get(threadPoolName);
/*  90 */     if (handler == null) {
/*  91 */       LogHome.getLog().info("未配置线程池的名称, queuePoolName=" + this.queuePoolName + ", threadPoolName=" + threadPoolName);
/*  92 */       handler = (AbstractThreadEleHandler)this.threadEleHandlers.get("PUBLIC");
/*     */     }
/*  94 */     if (handler.isQueueLocked) {
/*  95 */       handler.unlockQueue();
/*     */     }
/*  97 */     handler.addQueueElement(new QueueElement(null, QueueThreadPool.QueueElement.FLAG_ENUM.CLOSE_QUEUE, null));
/*  98 */     this.threadEleHandlers.remove(threadPoolName);
/*     */   }
/*     */ 
/*     */   public int getQueueThreadPoolNum() {
/* 102 */     return this.threadEleHandlers.size();
/*     */   }
/*     */ 
/*     */   public void addElement(String threadPoolName, E element) {
/* 106 */     if ((element == null) || (threadPoolName == null)) return;
/*     */ 
/* 108 */     AbstractThreadEleHandler handler = (AbstractThreadEleHandler)this.threadEleHandlers.get(threadPoolName);
/* 109 */     if (handler == null) {
/* 110 */       LogHome.getLog().info("未配置线程池的名称, queuePoolName=" + this.queuePoolName + ", threadPoolName=" + threadPoolName);
/* 111 */       handler = (AbstractThreadEleHandler)this.threadEleHandlers.get("PUBLIC");
/*     */     }
/*     */ 
/* 114 */     if (handler != null) {
/* 115 */       this.poolTotalRecvEleCount += 1L;
/* 116 */       handler.addQueueElement(new QueueElement(element, QueueThreadPool.QueueElement.FLAG_ENUM.EVENT, null));
/*     */     } else {
/* 118 */       LogHome.getLog().warn("抛弃无处理器事件, queuePoolName=" + this.queuePoolName + ", element=" + element);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isHaveThreadPool(String threadPoolName) {
/* 123 */     return this.threadEleHandlers.containsKey(threadPoolName);
/*     */   }
/*     */ 
/*     */   public void lockQueue(String threadPoolName, boolean isQueueClear) {
/* 127 */     if (threadPoolName == null) return;
/*     */ 
/* 129 */     AbstractThreadEleHandler handler = (AbstractThreadEleHandler)this.threadEleHandlers.get(threadPoolName);
/* 130 */     if (handler == null)
/* 131 */       LogHome.getLog().info("未配置线程池, queuePoolName=" + this.queuePoolName + ",threadPoolName：" + threadPoolName + ", 不能设定队列锁定");
/*     */     else
/* 133 */       handler.lockQueue(isQueueClear);
/*     */   }
/*     */ 
/*     */   public void unlockQueue(String threadPoolName)
/*     */   {
/* 138 */     if (threadPoolName == null) return;
/*     */ 
/* 140 */     AbstractThreadEleHandler handler = (AbstractThreadEleHandler)this.threadEleHandlers.get(threadPoolName);
/* 141 */     if (handler == null)
/* 142 */       LogHome.getLog().info("未配置线程池, threadPoolName：" + threadPoolName + ", 不能设定队列解锁");
/*     */     else
/* 144 */       handler.unlockQueue();
/*     */   }
/*     */ 
/*     */   public synchronized void checkThreadPool(boolean isMantory)
/*     */   {
/* 149 */     long poolHandledCount = this.poolTotalHandledEleCount - this.poolLastHandleEleCount;
/* 150 */     if ((isMantory) || ((this.checkPoolEleNum > 0) && (poolHandledCount >= this.checkPoolEleNum)) || ((this.checkPoolTime > 0) && (System.currentTimeMillis() - this.poolLastCheckTime >= this.checkPoolTime * 1000)))
/*     */     {
/* 152 */       long sysTimeGap = System.currentTimeMillis() - this.poolLastCheckTime;
/* 153 */       String logInfo = "事件统计[" + this.queuePoolName + "]：总系统时间(s)＝" + (System.currentTimeMillis() - this.poolSysStartTime) / 1000L + ", 系统时间(s)=" + sysTimeGap / 1000L + ", 接收事件总数=" + this.poolTotalRecvEleCount + ", 已处理事件总数=" + this.poolTotalHandledEleCount + ", 本次处理事件数=" + poolHandledCount + ", 未处理事件总数=" + (this.poolTotalRecvEleCount - this.poolTotalHandledEleCount);
/*     */ 
/* 156 */       if (poolHandledCount > 0L) {
/* 157 */         logInfo = logInfo + "，事件平均系统时间(ms)=" + sysTimeGap / poolHandledCount;
/*     */       }
/* 159 */       LogHome.getLog().warn(logInfo + "   --------------------------------");
/* 160 */       this.poolLastHandleEleCount = this.poolTotalHandledEleCount;
/* 161 */       Iterator it = this.threadEleHandlers.values().iterator();
/* 162 */       while (it.hasNext()) {
/* 163 */         ((AbstractThreadEleHandler)it.next()).checkHandler();
/*     */       }
/* 165 */       this.poolLastCheckTime = System.currentTimeMillis();
/*     */     }
/*     */   }
/*     */ 
/*     */   private class DynThreadEleHandler extends QueueThreadPool.AbstractThreadEleHandler
/*     */   {
/*     */     private QueueThreadPool<E>.DynThreadEleHandler.SynCheckCollection syncCheckCollection;
/*     */     private QueueThreadPool<E>.DynThreadEleHandler.DispatchThread dispatchThread;
/*     */ 
/*     */     private DynThreadEleHandler(String threadPoolName, int maxThreadNum, boolean isSyncCheck, IQueueThreadHandler handler, int queueLenThreshold, int queueAddWaitTime)
/*     */     {
/* 491 */       super(threadPoolName, maxThreadNum, isSyncCheck, handler, queueLenThreshold, queueAddWaitTime);
/*     */ 
/* 493 */       this.syncCheckCollection = new SynCheckCollection(maxThreadNum, null);
/* 494 */       this.dispatchThread = new DispatchThread(null);
/* 495 */       this.dispatchThread.start();
/*     */     }
/*     */ 
/*     */     protected int getHandlingEleCount()
/*     */     {
/* 500 */       return this.syncCheckCollection.getHandlingEleCount();
/*     */     }
/* 570 */     private class SynCheckCollection { private List<E> handlingElements = new Vector();
/* 571 */       private boolean isSyncWait = false;
/*     */ 
/* 573 */       private Semaphore finishSema = new Semaphore(0, true);
/*     */       private Semaphore threadCountSema;
/*     */ 
/* 577 */       private SynCheckCollection(int maxThreadNum) { this.threadCountSema = new Semaphore(maxThreadNum, true); }
/*     */ 
/*     */       private synchronized int getHandlingEleCount()
/*     */       {
/* 581 */         return this.handlingElements.size();
/*     */       }
/*     */ 
/*     */       private void addElements(List<QueueThreadPool.QueueElement<E>> newHandleElements) {
/*     */         try {
/* 586 */           this.threadCountSema.acquire();
/* 587 */           LogHome.getLog().debug("threadCountSema.acquire()");
/* 588 */           synchronized (this) {
/* 589 */             if (this.handlingElements.size() == 0) {
/* 590 */               QueueThreadPool.DynThreadEleHandler.this.startHandleSysTime = System.currentTimeMillis();
/*     */             }
/* 592 */             for (QueueThreadPool.QueueElement newHandleElement : newHandleElements)
/* 593 */               this.handlingElements.add(newHandleElement.element);
/*     */           }
/*     */         }
/*     */         catch (Exception ex) {
/* 597 */           LogHome.getLog().error("", ex);
/*     */         }
/*     */       }
/*     */ 
/*     */       private synchronized void finished(List<QueueThreadPool.QueueElement<E>> handledElements) {
/* 602 */         int runThreadNum = QueueThreadPool.DynThreadEleHandler.this.maxThreadNum - this.threadCountSema.availablePermits();
/* 603 */         if (runThreadNum > QueueThreadPool.DynThreadEleHandler.this.maxConcurrentCount) {
/* 604 */           QueueThreadPool.DynThreadEleHandler.this.maxConcurrentCount = runThreadNum;
/*     */         }
/* 606 */         for (QueueThreadPool.QueueElement handledElement : handledElements) {
/* 607 */           this.handlingElements.remove(handledElement.element);
/*     */         }
/*     */ 
/* 610 */         if (this.handlingElements.size() == 0) {
/* 611 */           QueueThreadPool.DynThreadEleHandler.this.handledSysTimeSum += System.currentTimeMillis() - QueueThreadPool.DynThreadEleHandler.this.startHandleSysTime;
/*     */         }
/*     */ 
/* 614 */         QueueThreadPool.DynThreadEleHandler.this.handledEleCount += handledElements.size();
/* 615 */         QueueThreadPool.DynThreadEleHandler.this.handledEleTotalCount += handledElements.size();
/* 616 */         QueueThreadPool.access$414(QueueThreadPool.this, handledElements.size());
/* 617 */         if (handledElements.size() > 1) {
/* 618 */           QueueThreadPool.DynThreadEleHandler.this.batchEleTotalCount += handledElements.size();
/*     */         }
/* 620 */         this.threadCountSema.release();
/* 621 */         LogHome.getLog().debug("threadCountSema.release()");
/* 622 */         if (this.isSyncWait) {
/* 623 */           this.finishSema.release();
/* 624 */           LogHome.getLog().debug("finishSema.release()");
/*     */         }
/*     */       }
/*     */ 
/*     */       private synchronized boolean checkSyncWait(E element) {
/* 629 */         this.isSyncWait = false;
/* 630 */         if ((QueueThreadPool.DynThreadEleHandler.this.isSynCheck) && (this.handlingElements.size() > 0)) {
/* 631 */           LogHome.getLog().debug("同步检查开始,handlingElements.size=" + this.handlingElements.size());
/* 632 */           long startTime = System.currentTimeMillis();
/* 633 */           this.isSyncWait = QueueThreadPool.DynThreadEleHandler.this.handler.isSyncWait(element, this.handlingElements);
/* 634 */           LogHome.getLog().debug("同步检查结束,耗时=" + (System.currentTimeMillis() - startTime));
/*     */         }
/* 636 */         return this.isSyncWait;
/*     */       }
/*     */ 
/*     */       private void waitForSyncOver(QueueThreadPool.QueueElement<E> queueEle) {
/* 640 */         LogHome.getLog().debug("同步等待检查开始，当前等待状态：" + this.finishSema.availablePermits());
/* 641 */         while (checkSyncWait(queueEle.element)) {
/*     */           try {
/* 643 */             LogHome.getLog().debug("同步等待开始 isSyncWait=" + this.isSyncWait);
/* 644 */             long startTime = System.currentTimeMillis();
/* 645 */             this.finishSema.acquire();
/*     */             QueueThreadPool.DynThreadEleHandler tmp89_86 = QueueThreadPool.DynThreadEleHandler.this; tmp89_86.syncWaitTimeSum = (int)(tmp89_86.syncWaitTimeSum + (System.currentTimeMillis() - startTime));
/* 647 */             LogHome.getLog().debug("同步等待结束 finishSema.acquire()，获取等待信号耗时：" + (System.currentTimeMillis() - startTime));
/*     */           } catch (Exception ex) {
/* 649 */             LogHome.getLog().error("", ex);
/*     */           }
/*     */         }
/* 652 */         LogHome.getLog().debug("同步等待检查结束，当前等待状态：" + this.finishSema.availablePermits());
/*     */       }
/*     */     }
/*     */ 
/*     */     private class DynHandleThread extends Thread
/*     */     {
/*     */       private List<QueueThreadPool.QueueElement<E>> handleElements;
/*     */ 
/*     */       public DynHandleThread()
/*     */       {
/* 546 */         this.handleElements = elements;
/*     */       }
/*     */ 
/*     */       public void run() {
/* 550 */         setName(QueueThreadPool.this.queuePoolName + "-DThread-" + QueueThreadPool.DynThreadEleHandler.this.threadPoolName + "-" + System.currentTimeMillis());
/* 551 */         LogHome.getLog().debug("动态处理线程，处理事件：queuePoolName=" + QueueThreadPool.this.queuePoolName + ",threadPoolName=" + QueueThreadPool.DynThreadEleHandler.this.threadPoolName + "，事件数=" + this.handleElements.size());
/*     */ 
/* 553 */         long startTime = 0L;
/*     */         try {
/* 555 */           startTime = System.currentTimeMillis();
/* 556 */           QueueThreadPool.DynThreadEleHandler.this.handleElement(this.handleElements);
/*     */         } catch (Throwable ex) {
/* 558 */           LogHome.getLog().error("", ex);
/*     */         } finally {
/* 560 */           QueueThreadPool.DynThreadEleHandler.this.syncCheckCollection.finished(this.handleElements);
/* 561 */           if (startTime > 0L) {
/* 562 */             QueueThreadPool.DynThreadEleHandler.this.handledCpuTimeSum += System.currentTimeMillis() - startTime;
/*     */           }
/* 564 */           this.handleElements = null;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     private class DispatchThread extends Thread
/*     */     {
/*     */       private DispatchThread()
/*     */       {
/*     */       }
/*     */ 
/*     */       public void run()
/*     */       {
/* 505 */         setName(new StringBuilder().append(QueueThreadPool.this.queuePoolName).append("-DispatchThread-").append(QueueThreadPool.DynThreadEleHandler.this.threadPoolName).toString());
/* 506 */         LogHome.getLog().debug(new StringBuilder().append("启动事件派发线程，ThreadName=").append(getName()).toString());
/*     */         while (true) {
/* 508 */           List handlingEles = null;
/* 509 */           long validSysStartTime = 0L;
/*     */           try {
/* 511 */             long time = System.currentTimeMillis();
/* 512 */             handlingEles = QueueThreadPool.DynThreadEleHandler.this.getQueueElements();
/* 513 */             LogHome.getLog().debug(new StringBuilder().append("得到需要处理的事件，耗时=").append(System.currentTimeMillis() - time).append(",得到消息数=").append(handlingEles != null ? handlingEles.size() : -1).toString());
/* 514 */             if (handlingEles != null) {
/* 515 */               validSysStartTime = System.currentTimeMillis();
/* 516 */               if (handlingEles.size() > 0) {
/* 517 */                 QueueThreadPool.DynThreadEleHandler.this.syncCheckCollection.waitForSyncOver((QueueThreadPool.QueueElement)handlingEles.get(0));
/* 518 */                 QueueThreadPool.DynThreadEleHandler.this.syncCheckCollection.addElements(handlingEles);
/* 519 */                 QueueThreadPool.DynThreadEleHandler.this.queueTotalEleCount -= handlingEles.size();
/*     */ 
/* 521 */                 QueueThreadPool.DynThreadEleHandler.DynHandleThread thread = new QueueThreadPool.DynThreadEleHandler.DynHandleThread(QueueThreadPool.DynThreadEleHandler.this, handlingEles);
/* 522 */                 thread.start();
/*     */               } else {
/* 524 */                 LogHome.getLog().debug("没有需要处理的事件");
/*     */               }
/*     */             } else {
/* 527 */               QueueThreadPool.DynThreadEleHandler.this.closeQueue();
/*     */ 
/* 533 */               if (validSysStartTime > 0L) {
/* 534 */                 QueueThreadPool.DynThreadEleHandler.this.dispatchCpuTimeSum += System.currentTimeMillis() - validSysStartTime;
/*     */               }
/* 536 */               QueueThreadPool.this.checkThreadPool(false); break;
/*     */             }
/*     */           }
/*     */           catch (Throwable ex)
/*     */           {
/* 531 */             LogHome.getLog().error("", ex);
/*     */           } finally {
/* 533 */             if (validSysStartTime > 0L) {
/* 534 */               QueueThreadPool.DynThreadEleHandler.this.dispatchCpuTimeSum += System.currentTimeMillis() - validSysStartTime;
/*     */             }
/* 536 */             QueueThreadPool.this.checkThreadPool(false);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private class SingleThreadEleHandler extends QueueThreadPool.AbstractThreadEleHandler
/*     */   {
/*     */     private QueueThreadPool<E>.SingleThreadEleHandler.SingleHandleThread handleThread;
/*     */ 
/*     */     private SingleThreadEleHandler(String threadPoolName, boolean isSyncCheck, IQueueThreadHandler handler, int queueLenThreshold, int queueAddWaitTime)
/*     */     {
/* 436 */       super(threadPoolName, 1, isSyncCheck, handler, queueLenThreshold, queueAddWaitTime);
/* 437 */       this.handleThread = new SingleHandleThread(null);
/* 438 */       this.handleThread.start();
/*     */     }
/*     */ 
/*     */     protected int getHandlingEleCount() {
/* 442 */       return (int)(this.recvTotalEleCount - this.handledEleTotalCount - this.queueTotalEleCount);
/*     */     }
/*     */     private class SingleHandleThread extends Thread {
/*     */       private SingleHandleThread() {
/*     */       }
/* 447 */       public void run() { setName(QueueThreadPool.this.queuePoolName + "-" + QueueThreadPool.SingleThreadEleHandler.this.threadPoolName);
/*     */         while (true) {
/* 449 */           long startTime = 0L;
/* 450 */           List handlingElements = null;
/*     */           try {
/* 452 */             handlingElements = QueueThreadPool.SingleThreadEleHandler.this.getQueueElements();
/* 453 */             if (handlingElements != null) {
/* 454 */               startTime = System.currentTimeMillis();
/* 455 */               QueueThreadPool.SingleThreadEleHandler.this.queueTotalEleCount -= handlingElements.size();
/* 456 */               QueueThreadPool.SingleThreadEleHandler.this.handleElement(handlingElements);
/*     */             } else {
/* 458 */               QueueThreadPool.SingleThreadEleHandler.this.closeQueue();
/*     */ 
/* 464 */               QueueThreadPool.SingleThreadEleHandler.this.maxConcurrentCount = 1;
/* 465 */               if ((handlingElements != null) && (handlingElements.size() > 0)) {
/* 466 */                 QueueThreadPool.SingleThreadEleHandler.this.handledEleCount += handlingElements.size();
/* 467 */                 QueueThreadPool.SingleThreadEleHandler.this.handledEleTotalCount += handlingElements.size();
/* 468 */                 QueueThreadPool.access$414(QueueThreadPool.this, handlingElements.size());
/* 469 */                 if (handlingElements.size() > 1) {
/* 470 */                   QueueThreadPool.SingleThreadEleHandler.this.batchEleTotalCount += handlingElements.size();
/*     */                 }
/*     */               }
/* 473 */               if (startTime > 0L) {
/* 474 */                 QueueThreadPool.SingleThreadEleHandler.this.handledCpuTimeSum += System.currentTimeMillis() - startTime;
/* 475 */                 QueueThreadPool.SingleThreadEleHandler.this.dispatchCpuTimeSum = QueueThreadPool.SingleThreadEleHandler.this.handledCpuTimeSum;
/* 476 */                 QueueThreadPool.SingleThreadEleHandler.this.handledSysTimeSum = QueueThreadPool.SingleThreadEleHandler.this.handledCpuTimeSum;
/*     */               }
/* 478 */               QueueThreadPool.this.checkThreadPool(false); break;
/*     */             }
/*     */           }
/*     */           catch (Throwable ex)
/*     */           {
/* 462 */             LogHome.getLog().error("", ex);
/*     */           } finally {
/* 464 */             QueueThreadPool.SingleThreadEleHandler.this.maxConcurrentCount = 1;
/* 465 */             if ((handlingElements != null) && (handlingElements.size() > 0)) {
/* 466 */               QueueThreadPool.SingleThreadEleHandler.this.handledEleCount += handlingElements.size();
/* 467 */               QueueThreadPool.SingleThreadEleHandler.this.handledEleTotalCount += handlingElements.size();
/* 468 */               QueueThreadPool.access$414(QueueThreadPool.this, handlingElements.size());
/* 469 */               if (handlingElements.size() > 1) {
/* 470 */                 QueueThreadPool.SingleThreadEleHandler.this.batchEleTotalCount += handlingElements.size();
/*     */               }
/*     */             }
/* 473 */             if (startTime > 0L) {
/* 474 */               QueueThreadPool.SingleThreadEleHandler.this.handledCpuTimeSum += System.currentTimeMillis() - startTime;
/* 475 */               QueueThreadPool.SingleThreadEleHandler.this.dispatchCpuTimeSum = QueueThreadPool.SingleThreadEleHandler.this.handledCpuTimeSum;
/* 476 */               QueueThreadPool.SingleThreadEleHandler.this.handledSysTimeSum = QueueThreadPool.SingleThreadEleHandler.this.handledCpuTimeSum;
/*     */             }
/* 478 */             QueueThreadPool.this.checkThreadPool(false);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private abstract class AbstractThreadEleHandler
/*     */   {
/*     */     protected String threadPoolName;
/* 194 */     protected int maxThreadNum = 1;
/* 195 */     protected boolean isSynCheck = false;
/*     */     protected IQueueThreadHandler handler;
/* 197 */     protected int queueLenThreshold = 0;
/* 198 */     protected int queueAddWaitTime = 200;
/*     */ 
/* 200 */     protected LinkedBlockingQueue<QueueThreadPool.QueueElement<E>> recvQueue = new LinkedBlockingQueue();
/* 201 */     protected Map<Object, List<QueueThreadPool.QueueElement<E>>> recvBatchElements = new Hashtable();
/*     */ 
/* 203 */     protected Semaphore queueLock = new Semaphore(0);
/* 204 */     protected boolean isQueueLocked = false;
/* 205 */     protected boolean isQueueEleDiscard = false;
/*     */ 
/* 207 */     protected int syncWaitTimeSum = 0;
/* 208 */     protected long dispatchCpuTimeSum = 0L;
/* 209 */     protected long handledCpuTimeSum = 0L;
/* 210 */     protected long handledSysTimeSum = 0L;
/* 211 */     protected long startHandleSysTime = System.currentTimeMillis();
/*     */ 
/* 213 */     protected int maxConcurrentCount = 0;
/* 214 */     protected long handledEleCount = 0L;
/* 215 */     protected long recvTotalEleCount = 0L;
/* 216 */     protected long handledEleTotalCount = 0L;
/* 217 */     protected long batchEleTotalCount = 0L;
/* 218 */     protected long queueTotalEleCount = 0L;
/* 219 */     protected long discardTotalEleCount = 0L;
/*     */ 
/*     */     protected AbstractThreadEleHandler(String threadPoolName, int maxThreadNum, boolean isSyncCheck, IQueueThreadHandler handler, int queueLenThreshold, int queueAddWaitTime)
/*     */     {
/* 223 */       this.threadPoolName = threadPoolName;
/* 224 */       this.maxThreadNum = maxThreadNum;
/* 225 */       this.isSynCheck = isSyncCheck;
/* 226 */       this.handler = handler;
/* 227 */       if (queueLenThreshold >= 0) {
/* 228 */         this.queueLenThreshold = queueLenThreshold;
/*     */       }
/* 230 */       if (queueAddWaitTime > 0)
/* 231 */         this.queueAddWaitTime = queueAddWaitTime;
/*     */     }
/*     */ 
/*     */     protected void addQueueElement(QueueThreadPool.QueueElement<E> queueElement)
/*     */     {
/* 236 */       if ((this.queueLenThreshold > 0) && (this.queueTotalEleCount > this.queueLenThreshold)) {
/*     */         try {
/* 238 */           if (this.queueAddWaitTime > 0)
/* 239 */             Thread.sleep(this.queueAddWaitTime);
/*     */         }
/*     */         catch (InterruptedException ex)
/*     */         {
/*     */         }
/*     */       }
/* 245 */       if ((this.isSynCheck) && (!queueElement.isLockQueueElement())) {
/* 246 */         Object eleKey = this.handler.getElementKey(queueElement.element);
/* 247 */         if (eleKey != null)
/* 248 */           synchronized (this.recvBatchElements) {
/* 249 */             List batchEelements = (List)this.recvBatchElements.get(eleKey);
/* 250 */             if (batchEelements == null) {
/* 251 */               batchEelements = new ArrayList();
/* 252 */               this.recvBatchElements.put(eleKey, batchEelements);
/* 253 */               batchEelements.add(queueElement);
/* 254 */               this.recvQueue.add(queueElement);
/*     */             } else {
/* 256 */               batchEelements.add(queueElement);
/*     */             }
/*     */           }
/*     */         else
/* 260 */           this.recvQueue.add(queueElement);
/*     */       }
/*     */       else {
/* 263 */         this.recvQueue.add(queueElement);
/*     */       }
/*     */ 
/* 266 */       if ((!queueElement.isLockQueueElement()) && (!queueElement.isCloseQueueElement())) {
/* 267 */         this.queueTotalEleCount += 1L;
/* 268 */         this.recvTotalEleCount += 1L;
/*     */       }
/*     */     }
/*     */ 
/*     */     protected List<QueueThreadPool.QueueElement<E>> getQueueElements()
/*     */     {
/* 274 */       List handlingEles = null;
/*     */       try
/*     */       {
/*     */         while (true) {
/* 278 */           QueueThreadPool.QueueElement takeElement = (QueueThreadPool.QueueElement)this.recvQueue.take();
/* 279 */           if (takeElement.isCloseQueueElement())
/*     */           {
/*     */             break;
/*     */           }
/* 283 */           if (takeElement.isLockQueueElement()) {
/* 284 */             LogHome.getLog().info("队列锁锁定开始, queuePoolName=" + QueueThreadPool.this.queuePoolName + ", threadPoolName=" + this.threadPoolName);
/* 285 */             this.handler.notifyQueueLock();
/* 286 */             this.isQueueEleDiscard = false;
/* 287 */             waitUnlockSignal();
/* 288 */             this.handler.notifyQueueUnlock();
/* 289 */             LogHome.getLog().info("队列锁解锁完成, queuePoolName=" + QueueThreadPool.this.queuePoolName + ", threadPoolName=" + this.threadPoolName);
/* 290 */             continue;
/*     */           }
/*     */ 
/* 293 */           if (this.isSynCheck) {
/* 294 */             Object eleKey = this.handler.getElementKey(takeElement.element);
/* 295 */             if (eleKey != null) {
/* 296 */               synchronized (this.recvBatchElements) {
/* 297 */                 List batchEelements = (List)this.recvBatchElements.remove(eleKey);
/* 298 */                 if (batchEelements != null) {
/* 299 */                   handlingEles = batchEelements;
/*     */                 }
/*     */               }
/*     */             }
/* 303 */             if (handlingEles == null) {
/* 304 */               handlingEles = new ArrayList();
/* 305 */               handlingEles.add(takeElement);
/*     */             }
/*     */           } else {
/* 308 */             handlingEles = new ArrayList();
/* 309 */             handlingEles.add(takeElement);
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (Exception ex)
/*     */       {
/* 315 */         LogHome.getLog().info("", ex);
/*     */       }
/* 317 */       return handlingEles;
/*     */     }
/*     */ 
/*     */     protected void closeQueue() {
/* 321 */       this.isQueueEleDiscard = true;
/* 322 */       int discardEleCount = this.recvQueue.size();
/* 323 */       QueueThreadPool.access$414(QueueThreadPool.this, discardEleCount);
/* 324 */       this.recvQueue.clear();
/* 325 */       this.queueTotalEleCount = 0L;
/* 326 */       LogHome.getLog().warn("事件统计[queuePoolName=" + QueueThreadPool.this.queuePoolName + ", threadPoolName=" + this.threadPoolName + ", discardEleCount=" + discardEleCount + "] 关闭 ！");
/*     */     }
/*     */ 
/*     */     protected void lockQueue(boolean isQueueClear) {
/*     */       try {
/* 331 */         LogHome.getLog().warn("队列锁锁定通知, queuePoolName=" + QueueThreadPool.this.queuePoolName + ", threadPoolName=" + this.threadPoolName + ", isQueueClear=" + isQueueClear + ", queueSize=" + this.recvQueue.size());
/*     */ 
/* 333 */         this.isQueueEleDiscard = isQueueClear;
/* 334 */         addQueueElement(QueueThreadPool.QueueElement.createLockElement());
/*     */       } catch (Exception ex) {
/* 336 */         LogHome.getLog().error("", ex);
/*     */       }
/*     */     }
/*     */ 
/*     */     protected void unlockQueue() {
/*     */       try {
/* 342 */         LogHome.getLog().warn("队列锁解锁通知, queuePoolName=" + QueueThreadPool.this.queuePoolName + ", threadPoolName=" + this.threadPoolName);
/* 343 */         this.queueLock.release();
/*     */       } catch (Exception ex) {
/* 345 */         LogHome.getLog().error("", ex);
/*     */       }
/*     */     }
/*     */ 
/*     */     protected void waitUnlockSignal() {
/*     */       try {
/* 351 */         if (this.queueLock.availablePermits() == 0) {
/* 352 */           this.isQueueLocked = true;
/*     */         }
/* 354 */         this.queueLock.acquire();
/* 355 */         this.isQueueLocked = false;
/*     */       } catch (Exception ex) {
/* 357 */         LogHome.getLog().error("", ex);
/*     */       }
/*     */     }
/*     */ 
/*     */     protected void checkHandler() {
/* 362 */       long sysTimeGap = System.currentTimeMillis() - QueueThreadPool.this.poolLastCheckTime;
/* 363 */       if (this.handledEleCount > 0L) {
/* 364 */         long eleCpuTime = this.handledCpuTimeSum / this.handledEleCount;
/* 365 */         if ((getHandlingEleCount() > 0) && (this.maxThreadNum > 1)) {
/* 366 */           this.handledSysTimeSum += System.currentTimeMillis() - this.startHandleSysTime;
/*     */         }
/* 368 */         long eleSysTime = this.handledSysTimeSum / this.handledEleCount;
/* 369 */         float dispatchRatio = (float)this.dispatchCpuTimeSum / (float)sysTimeGap;
/* 370 */         dispatchRatio = dispatchRatio > 1.0F ? 1.0F : dispatchRatio;
/* 371 */         float sysTimeRatio = (float)this.handledSysTimeSum / (float)sysTimeGap;
/* 372 */         sysTimeRatio = sysTimeRatio > 1.0F ? 1.0F : sysTimeRatio;
/* 373 */         LogHome.getLog().warn("事件统计：queuePoolName=" + QueueThreadPool.this.queuePoolName + ", threadPoolName=" + this.threadPoolName + ", 锁定状态=" + this.isQueueLocked + ", 接收的事件总数=" + this.recvTotalEleCount + ", 已处理事件总数" + this.handledEleTotalCount + "(" + this.batchEleTotalCount + ")" + ", 锁定抛弃事件数=" + this.discardTotalEleCount + ", 未处理事件数=" + this.queueTotalEleCount + ", 处理中事件数=" + getHandlingEleCount() + ", 本次处理事件数=" + this.handledEleCount + ", 最大并发数=" + this.maxConcurrentCount);
/*     */ 
/* 376 */         LogHome.getLog().warn("事件统计：queuePoolName=" + QueueThreadPool.this.queuePoolName + ", threadPoolName=" + this.threadPoolName + ", 平均有效系统时间(ms)=" + eleSysTime + ", 平均CPU时间(ms)=" + eleCpuTime + ", 系统时间(ms)=" + sysTimeGap + ", 等待同步时间(ms)=" + this.syncWaitTimeSum + ", 有效系统时间比例=" + (int)(sysTimeRatio * 100.0F) + "%" + ", 有效队列读取时间比例=" + (int)(dispatchRatio * 100.0F) + "%");
/*     */       }
/* 378 */       else if ((this.queueTotalEleCount > 0L) || (getHandlingEleCount() > 0)) {
/* 379 */         LogHome.getLog().warn("事件统计：queuePoolName=" + QueueThreadPool.this.queuePoolName + ", threadPoolName=" + this.threadPoolName + ", 锁定状态=" + this.isQueueLocked + ", 接收的事件总数=" + this.recvTotalEleCount + ", 已处理事件总数" + this.handledEleTotalCount + "(" + this.batchEleTotalCount + ")" + ", 未处理事件数=" + this.queueTotalEleCount + ", 处理中事件数=" + getHandlingEleCount() + ", 本次处理事件数=" + this.handledEleCount);
/*     */       }
/*     */ 
/* 384 */       this.handledEleCount = 0L;
/* 385 */       this.dispatchCpuTimeSum = 0L;
/* 386 */       this.maxConcurrentCount = 0;
/* 387 */       this.syncWaitTimeSum = 0;
/* 388 */       this.handledCpuTimeSum = 0L;
/* 389 */       this.handledSysTimeSum = 0L;
/* 390 */       this.startHandleSysTime = System.currentTimeMillis();
/*     */     }
/*     */ 
/*     */     protected void handleElement(List<QueueThreadPool.QueueElement<E>> handlingElements)
/*     */     {
/*     */       try
/*     */       {
/* 397 */         if (this.isQueueEleDiscard) {
/* 398 */           this.discardTotalEleCount += handlingElements.size();
/* 399 */           QueueThreadPool.QueueElement discardEle = (QueueThreadPool.QueueElement)handlingElements.get(0);
/* 400 */           LogHome.getLog().warn("事件队列抛弃：queuePoolName=" + QueueThreadPool.this.queuePoolName + ",threadPoolName=" + this.threadPoolName + "， 抛弃" + handlingElements.size() + "个事件：" + discardEle.element);
/*     */ 
/* 402 */           return;
/*     */         }
/*     */ 
/* 405 */         if (handlingElements.size() == 1) {
/* 406 */           long startTime = System.currentTimeMillis();
/* 407 */           this.handler.handle(this.threadPoolName, ((QueueThreadPool.QueueElement)handlingElements.get(0)).element);
/* 408 */           long handleTime = System.currentTimeMillis() - startTime;
/* 409 */           if (handleTime > QueueThreadPool.this.maxEleHandleTime) {
/* 410 */             LogHome.getLog().warn("对象处理大于设定门限: queuePoolName=" + QueueThreadPool.this.queuePoolName + ",threadPoolName=" + this.threadPoolName + ", 处理时间(ms)=" + handleTime + ", 开始处理时间=" + TimeFormatHelper.getFormatDate(new Date(startTime), "yyyy-MM-dd HH:mm:ss:SSS") + ", 问题对象：" + ((QueueThreadPool.QueueElement)handlingElements.get(0)).element);
/*     */           }
/*     */ 
/*     */         }
/* 414 */         else if (handlingElements.size() > 1) {
/* 415 */           List values = new ArrayList();
/* 416 */           for (QueueThreadPool.QueueElement handleElement : handlingElements) {
/* 417 */             values.add(handleElement.element);
/*     */           }
/* 419 */           this.handler.handle(this.threadPoolName, values);
/*     */         } else {
/* 421 */           LogHome.getLog().debug("没有需要处理的事件: queuePoolName=" + QueueThreadPool.this.queuePoolName + ",threadPoolName=" + this.threadPoolName + ",事件数=" + handlingElements.size());
/*     */         }
/*     */       } catch (Exception ex) {
/* 424 */         LogHome.getLog().error("", ex);
/*     */       }
/*     */     }
/*     */ 
/*     */     protected abstract int getHandlingEleCount();
/*     */   }
/*     */ 
/*     */   private static class QueueElement<K>
/*     */   {
/*     */     protected FLAG_ENUM flag;
/*     */     protected K element;
/*     */ 
/*     */     private QueueElement(K element, FLAG_ENUM flag)
/*     */     {
/* 175 */       this.element = element;
/* 176 */       this.flag = flag;
/*     */     }
/*     */ 
/*     */     protected static QueueElement createLockElement() {
/* 180 */       return new QueueElement(null, FLAG_ENUM.LOCK_QUEUE);
/*     */     }
/*     */ 
/*     */     protected boolean isLockQueueElement() {
/* 184 */       return this.flag == FLAG_ENUM.LOCK_QUEUE;
/*     */     }
/*     */ 
/*     */     protected boolean isCloseQueueElement() {
/* 188 */       return this.flag == FLAG_ENUM.CLOSE_QUEUE;
/*     */     }
/*     */ 
/*     */     protected static enum FLAG_ENUM
/*     */     {
/* 170 */       EVENT, LOCK_QUEUE, CLOSE_QUEUE;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.lang.QueueThreadPool
 * JD-Core Version:    0.6.0
 */