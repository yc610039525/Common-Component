/*     */ package com.boco.common.util.io;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class FastByteArrayOutputStream extends OutputStream
/*     */ {
/*  12 */   private List<byte[]> buffers = new ArrayList();
/*     */   private int currentBufferIndex;
/*     */   private int filledBufferSum;
/*     */   private byte[] currentBuffer;
/*     */   private int count;
/*     */ 
/*     */   public FastByteArrayOutputStream()
/*     */   {
/*  31 */     this(1024);
/*     */   }
/*     */ 
/*     */   public FastByteArrayOutputStream(int size)
/*     */   {
/*  52 */     if (size < 0)
/*     */     {
/*  54 */       throw new IllegalArgumentException("Negative initial size: " + size);
/*     */     }
/*     */ 
/*  58 */     needNewBuffer(size);
/*     */   }
/*     */ 
/*     */   private byte[] getBuffer(int index)
/*     */   {
/*  64 */     return (byte[])this.buffers.get(index);
/*     */   }
/*     */ 
/*     */   private void needNewBuffer(int newcount)
/*     */   {
/*  70 */     if (this.currentBufferIndex < this.buffers.size() - 1)
/*     */     {
/*  74 */       this.filledBufferSum += this.currentBuffer.length;
/*     */ 
/*  76 */       this.currentBufferIndex += 1;
/*     */ 
/*  78 */       this.currentBuffer = getBuffer(this.currentBufferIndex);
/*     */     }
/*     */     else
/*     */     {
/*     */       int newBufferSize;
/*  86 */       if (this.currentBuffer == null)
/*     */       {
/*  88 */         int newBufferSize = newcount;
/*     */ 
/*  90 */         this.filledBufferSum = 0;
/*     */       }
/*     */       else
/*     */       {
/*  94 */         newBufferSize = Math.max(this.currentBuffer.length << 1, newcount - this.filledBufferSum);
/*     */ 
/* 100 */         this.filledBufferSum += this.currentBuffer.length;
/*     */       }
/*     */ 
/* 104 */       this.currentBufferIndex += 1;
/*     */ 
/* 106 */       this.currentBuffer = new byte[newBufferSize];
/*     */ 
/* 108 */       this.buffers.add(this.currentBuffer);
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void write(byte[] b, int off, int len)
/*     */   {
/* 122 */     if ((off < 0) || (off > b.length) || (len < 0) || (off + len > b.length) || (off + len < 0))
/*     */     {
/* 132 */       throw new IndexOutOfBoundsException();
/*     */     }
/* 134 */     if (len == 0)
/*     */     {
/* 136 */       return;
/*     */     }
/*     */ 
/* 140 */     int newcount = this.count + len;
/*     */ 
/* 142 */     int remaining = len;
/*     */ 
/* 144 */     int inBufferPos = this.count - this.filledBufferSum;
/*     */ 
/* 146 */     while (remaining > 0)
/*     */     {
/* 148 */       int part = Math.min(remaining, this.currentBuffer.length - inBufferPos);
/*     */ 
/* 150 */       System.arraycopy(b, off + len - remaining, this.currentBuffer, inBufferPos, part);
/*     */ 
/* 153 */       remaining -= part;
/*     */ 
/* 155 */       if (remaining > 0)
/*     */       {
/* 157 */         needNewBuffer(newcount);
/*     */ 
/* 159 */         inBufferPos = 0;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 165 */     this.count = newcount;
/*     */   }
/*     */ 
/*     */   public synchronized void write(int b)
/*     */   {
/* 181 */     write(new byte[] { (byte)b }, 0, 1);
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 192 */     return this.count;
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/*     */   }
/*     */ 
/*     */   public synchronized void reset()
/*     */   {
/* 220 */     this.count = 0;
/*     */ 
/* 222 */     this.filledBufferSum = 0;
/*     */ 
/* 224 */     this.currentBufferIndex = 0;
/*     */ 
/* 226 */     this.currentBuffer = getBuffer(this.currentBufferIndex);
/*     */   }
/*     */ 
/*     */   public synchronized void writeTo(OutputStream out)
/*     */     throws IOException
/*     */   {
/* 237 */     int remaining = this.count;
/*     */ 
/* 239 */     for (int i = 0; i < this.buffers.size(); i++)
/*     */     {
/* 241 */       byte[] buf = getBuffer(i);
/*     */ 
/* 243 */       int c = Math.min(buf.length, remaining);
/*     */ 
/* 245 */       out.write(buf, 0, c);
/*     */ 
/* 247 */       remaining -= c;
/*     */ 
/* 249 */       if (remaining == 0)
/*     */         break;
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized byte[] toByteArray()
/*     */   {
/* 266 */     int remaining = this.count;
/*     */ 
/* 268 */     int pos = 0;
/*     */ 
/* 270 */     byte[] newbuf = new byte[this.count];
/*     */ 
/* 272 */     for (int i = 0; i < this.buffers.size(); i++)
/*     */     {
/* 274 */       byte[] buf = getBuffer(i);
/*     */ 
/* 276 */       int c = Math.min(buf.length, remaining);
/*     */ 
/* 278 */       System.arraycopy(buf, 0, newbuf, pos, c);
/*     */ 
/* 280 */       pos += c;
/*     */ 
/* 282 */       remaining -= c;
/*     */ 
/* 284 */       if (remaining == 0)
/*     */       {
/*     */         break;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 292 */     return newbuf;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 304 */     return new String(toByteArray());
/*     */   }
/*     */ 
/*     */   public String toString(String enc)
/*     */     throws UnsupportedEncodingException
/*     */   {
/* 315 */     return new String(toByteArray(), enc);
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.io.FastByteArrayOutputStream
 * JD-Core Version:    0.6.0
 */