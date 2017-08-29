/*     */ package com.boco.common.util.io;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.Writer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class FastCharArrayWriter extends Writer
/*     */ {
/*  15 */   private List<char[]> buffers = new ArrayList();
/*     */   private int currentBufferIndex;
/*     */   private int filledBufferSum;
/*     */   private char[] currentBuffer;
/*     */   private int count;
/*     */ 
/*     */   public FastCharArrayWriter()
/*     */   {
/*  37 */     this(1024);
/*     */   }
/*     */ 
/*     */   public FastCharArrayWriter(int size)
/*     */   {
/*  59 */     if (size < 0)
/*     */     {
/*  61 */       throw new IllegalArgumentException("Negative initial size: " + size);
/*     */     }
/*     */ 
/*  65 */     needNewBuffer(size);
/*     */   }
/*     */ 
/*     */   private char[] getBuffer(int index)
/*     */   {
/*  73 */     return (char[])this.buffers.get(index);
/*     */   }
/*     */ 
/*     */   private void needNewBuffer(int newcount)
/*     */   {
/*  81 */     if (this.currentBufferIndex < this.buffers.size() - 1)
/*     */     {
/*  85 */       this.filledBufferSum += this.currentBuffer.length;
/*     */ 
/*  89 */       this.currentBufferIndex += 1;
/*     */ 
/*  91 */       this.currentBuffer = getBuffer(this.currentBufferIndex);
/*     */     }
/*     */     else
/*     */     {
/*     */       int newBufferSize;
/*  99 */       if (this.currentBuffer == null)
/*     */       {
/* 101 */         int newBufferSize = newcount;
/*     */ 
/* 103 */         this.filledBufferSum = 0;
/*     */       }
/*     */       else
/*     */       {
/* 107 */         newBufferSize = Math.max(this.currentBuffer.length << 1, newcount - this.filledBufferSum);
/*     */ 
/* 113 */         this.filledBufferSum += this.currentBuffer.length;
/*     */       }
/*     */ 
/* 119 */       this.currentBufferIndex += 1;
/*     */ 
/* 121 */       this.currentBuffer = new char[newBufferSize];
/*     */ 
/* 123 */       this.buffers.add(this.currentBuffer);
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void write(char[] b, int off, int len)
/*     */   {
/* 141 */     if ((off < 0) || (off > b.length) || (len < 0) || (off + len > b.length) || (off + len < 0))
/*     */     {
/* 151 */       throw new IndexOutOfBoundsException();
/*     */     }
/* 153 */     if (len == 0)
/*     */     {
/* 155 */       return;
/*     */     }
/*     */ 
/* 159 */     int newcount = this.count + len;
/*     */ 
/* 161 */     int remaining = len;
/*     */ 
/* 163 */     int inBufferPos = this.count - this.filledBufferSum;
/*     */ 
/* 165 */     while (remaining > 0)
/*     */     {
/* 167 */       int part = Math.min(remaining, this.currentBuffer.length - inBufferPos);
/*     */ 
/* 169 */       System.arraycopy(b, off + len - remaining, this.currentBuffer, inBufferPos, part);
/*     */ 
/* 171 */       remaining -= part;
/*     */ 
/* 173 */       if (remaining > 0)
/*     */       {
/* 175 */         needNewBuffer(newcount);
/*     */ 
/* 177 */         inBufferPos = 0;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 183 */     this.count = newcount;
/*     */   }
/*     */ 
/*     */   public synchronized void write(int b)
/*     */   {
/* 203 */     write(new char[] { (char)b }, 0, 1);
/*     */   }
/*     */ 
/*     */   public synchronized void write(String s, int off, int len)
/*     */   {
/* 213 */     write(s.toCharArray(), off, len);
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 227 */     return this.count;
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void flush()
/*     */   {
/*     */   }
/*     */ 
/*     */   public synchronized void reset()
/*     */   {
/* 277 */     this.count = 0;
/*     */ 
/* 279 */     this.filledBufferSum = 0;
/*     */ 
/* 281 */     this.currentBufferIndex = 0;
/*     */ 
/* 283 */     this.currentBuffer = getBuffer(this.currentBufferIndex);
/*     */   }
/*     */ 
/*     */   public synchronized void writeTo(Writer out)
/*     */     throws IOException
/*     */   {
/* 297 */     int remaining = this.count;
/*     */ 
/* 299 */     for (int i = 0; i < this.buffers.size(); i++)
/*     */     {
/* 301 */       char[] buf = getBuffer(i);
/*     */ 
/* 303 */       int c = Math.min(buf.length, remaining);
/*     */ 
/* 305 */       out.write(buf, 0, c);
/*     */ 
/* 307 */       remaining -= c;
/*     */ 
/* 309 */       if (remaining == 0)
/*     */         break;
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized char[] toCharArray()
/*     */   {
/* 329 */     int remaining = this.count;
/*     */ 
/* 331 */     int pos = 0;
/*     */ 
/* 333 */     char[] newbuf = new char[this.count];
/*     */ 
/* 335 */     for (int i = 0; i < this.buffers.size(); i++)
/*     */     {
/* 337 */       char[] buf = getBuffer(i);
/*     */ 
/* 339 */       int c = Math.min(buf.length, remaining);
/*     */ 
/* 341 */       System.arraycopy(buf, 0, newbuf, pos, c);
/*     */ 
/* 343 */       pos += c;
/*     */ 
/* 345 */       remaining -= c;
/*     */ 
/* 347 */       if (remaining == 0)
/*     */       {
/*     */         break;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 355 */     return newbuf;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 371 */     return new String(toCharArray());
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.io.FastCharArrayWriter
 * JD-Core Version:    0.6.0
 */