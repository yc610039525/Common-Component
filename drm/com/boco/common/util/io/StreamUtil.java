/*     */ package com.boco.common.util.io;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.OutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.Reader;
/*     */ import java.io.Writer;
/*     */ 
/*     */ public class StreamUtil
/*     */ {
/*  20 */   public static int ioBufferSize = 32768;
/*  21 */   public static String encoding = "UTF-8";
/*     */ 
/*     */   public static void close(InputStream in)
/*     */   {
/*  35 */     if (in != null)
/*     */     {
/*     */       try
/*     */       {
/*  39 */         in.close();
/*     */       }
/*     */       catch (IOException ioe)
/*     */       {
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void close(OutputStream out)
/*     */   {
/*  60 */     if (out != null)
/*     */     {
/*     */       try
/*     */       {
/*  64 */         out.flush();
/*     */       }
/*     */       catch (IOException ioex)
/*     */       {
/*     */       }
/*     */ 
/*     */       try
/*     */       {
/*  74 */         out.close();
/*     */       }
/*     */       catch (IOException ioe)
/*     */       {
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void close(Reader in)
/*     */   {
/*  97 */     if (in != null)
/*     */     {
/*     */       try
/*     */       {
/* 101 */         in.close();
/*     */       }
/*     */       catch (IOException ioe)
/*     */       {
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void close(Writer out)
/*     */   {
/* 124 */     if (out != null)
/*     */     {
/*     */       try
/*     */       {
/* 128 */         out.flush();
/*     */       }
/*     */       catch (IOException ioex)
/*     */       {
/*     */       }
/*     */ 
/*     */       try
/*     */       {
/* 138 */         out.close();
/*     */       }
/*     */       catch (IOException ioe)
/*     */       {
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static int copy(InputStream input, OutputStream output)
/*     */     throws IOException
/*     */   {
/* 162 */     byte[] buffer = new byte[ioBufferSize];
/*     */ 
/* 164 */     int count = 0;
/*     */     while (true)
/*     */     {
/* 170 */       int read = input.read(buffer, 0, ioBufferSize);
/*     */ 
/* 172 */       if (read == -1)
/*     */       {
/*     */         break;
/*     */       }
/*     */ 
/* 178 */       output.write(buffer, 0, read);
/*     */ 
/* 180 */       count += read;
/*     */     }
/*     */ 
/* 184 */     return count;
/*     */   }
/*     */ 
/*     */   public static int copy(InputStream input, OutputStream output, int byteCount)
/*     */     throws IOException
/*     */   {
/* 197 */     byte[] buffer = new byte[ioBufferSize];
/*     */ 
/* 199 */     int count = 0;
/*     */ 
/* 203 */     while (byteCount > 0)
/*     */     {
/*     */       int read;
/*     */       int read;
/* 205 */       if (byteCount < ioBufferSize)
/*     */       {
/* 207 */         read = input.read(buffer, 0, byteCount);
/*     */       }
/*     */       else
/*     */       {
/* 211 */         read = input.read(buffer, 0, ioBufferSize);
/*     */       }
/*     */ 
/* 215 */       if (read == -1)
/*     */       {
/*     */         break;
/*     */       }
/*     */ 
/* 221 */       byteCount -= read;
/*     */ 
/* 223 */       count += read;
/*     */ 
/* 225 */       output.write(buffer, 0, read);
/*     */     }
/*     */ 
/* 229 */     return count;
/*     */   }
/*     */ 
/*     */   public static void copy(InputStream input, Writer output)
/*     */     throws IOException
/*     */   {
/* 241 */     copy(input, output, encoding);
/*     */   }
/*     */ 
/*     */   public static void copy(InputStream input, Writer output, int byteCount)
/*     */     throws IOException
/*     */   {
/* 254 */     copy(input, output, encoding, byteCount);
/*     */   }
/*     */ 
/*     */   public static void copy(InputStream input, Writer output, String encoding)
/*     */     throws IOException
/*     */   {
/* 266 */     copy(new InputStreamReader(input, encoding), output);
/*     */   }
/*     */ 
/*     */   public static void copy(InputStream input, Writer output, String encoding, int byteCount)
/*     */     throws IOException
/*     */   {
/* 279 */     copy(new InputStreamReader(input, encoding), output, byteCount);
/*     */   }
/*     */ 
/*     */   public static int copy(Reader input, Writer output)
/*     */     throws IOException
/*     */   {
/* 293 */     char[] buffer = new char[ioBufferSize];
/*     */ 
/* 295 */     int count = 0;
/*     */     int read;
/* 299 */     while ((read = input.read(buffer, 0, ioBufferSize)) >= 0)
/*     */     {
/* 301 */       output.write(buffer, 0, read);
/*     */ 
/* 303 */       count += read;
/*     */     }
/*     */ 
/* 307 */     output.flush();
/*     */ 
/* 309 */     return count;
/*     */   }
/*     */ 
/*     */   public static int copy(Reader input, Writer output, int charCount)
/*     */     throws IOException
/*     */   {
/* 321 */     char[] buffer = new char[ioBufferSize];
/*     */ 
/* 323 */     int count = 0;
/*     */ 
/* 327 */     while (charCount > 0)
/*     */     {
/*     */       int read;
/*     */       int read;
/* 329 */       if (charCount < ioBufferSize)
/*     */       {
/* 331 */         read = input.read(buffer, 0, charCount);
/*     */       }
/*     */       else
/*     */       {
/* 335 */         read = input.read(buffer, 0, ioBufferSize);
/*     */       }
/*     */ 
/* 339 */       if (read == -1)
/*     */       {
/*     */         break;
/*     */       }
/*     */ 
/* 345 */       charCount -= read;
/*     */ 
/* 347 */       count += read;
/*     */ 
/* 349 */       output.write(buffer, 0, read);
/*     */     }
/*     */ 
/* 353 */     return count;
/*     */   }
/*     */ 
/*     */   public static void copy(Reader input, OutputStream output)
/*     */     throws IOException
/*     */   {
/* 366 */     Writer out = new OutputStreamWriter(output);
/*     */ 
/* 368 */     copy(input, out);
/*     */ 
/* 370 */     out.flush();
/*     */   }
/*     */ 
/*     */   public static void copy(Reader input, OutputStream output, int charCount)
/*     */     throws IOException
/*     */   {
/* 382 */     copy(input, output, encoding, charCount);
/*     */   }
/*     */ 
/*     */   public static void copy(Reader input, OutputStream output, String encoding)
/*     */     throws IOException
/*     */   {
/* 394 */     Writer out = new OutputStreamWriter(output, encoding);
/*     */ 
/* 396 */     copy(input, out);
/*     */ 
/* 398 */     out.flush();
/*     */   }
/*     */ 
/*     */   public static void copy(Reader input, OutputStream output, String encoding, int charCount)
/*     */     throws IOException
/*     */   {
/* 411 */     Writer out = new OutputStreamWriter(output, encoding);
/*     */ 
/* 413 */     copy(input, out, charCount);
/*     */ 
/* 415 */     out.flush();
/*     */   }
/*     */ 
/*     */   public static byte[] readAvailableBytes(InputStream in)
/*     */     throws IOException
/*     */   {
/* 437 */     int l = in.available();
/*     */ 
/* 439 */     byte[] byteArray = new byte[l];
/*     */ 
/* 441 */     int i = 0;
/*     */     int j;
/* 443 */     while ((i < l) && ((j = in.read(byteArray, i, l - i)) >= 0))
/*     */     {
/* 445 */       i += j;
/*     */     }
/*     */ 
/* 449 */     if (i < l)
/*     */     {
/* 451 */       throw new IOException("Could not completely read from input stream.");
/*     */     }
/*     */ 
/* 456 */     return byteArray;
/*     */   }
/*     */ 
/*     */   public static byte[] readBytes(InputStream input)
/*     */     throws IOException
/*     */   {
/* 462 */     FastByteArrayOutputStream output = new FastByteArrayOutputStream();
/*     */ 
/* 464 */     copy(input, output);
/*     */ 
/* 466 */     return output.toByteArray();
/*     */   }
/*     */ 
/*     */   public static byte[] readBytes(InputStream input, int byteCount)
/*     */     throws IOException
/*     */   {
/* 473 */     FastByteArrayOutputStream output = new FastByteArrayOutputStream();
/*     */ 
/* 475 */     copy(input, output, byteCount);
/*     */ 
/* 477 */     return output.toByteArray();
/*     */   }
/*     */ 
/*     */   public static byte[] readBytes(Reader input)
/*     */     throws IOException
/*     */   {
/* 483 */     FastByteArrayOutputStream output = new FastByteArrayOutputStream();
/*     */ 
/* 485 */     copy(input, output);
/*     */ 
/* 487 */     return output.toByteArray();
/*     */   }
/*     */ 
/*     */   public static byte[] readBytes(Reader input, int byteCount)
/*     */     throws IOException
/*     */   {
/* 494 */     FastByteArrayOutputStream output = new FastByteArrayOutputStream();
/*     */ 
/* 496 */     copy(input, output, byteCount);
/*     */ 
/* 498 */     return output.toByteArray();
/*     */   }
/*     */ 
/*     */   public static byte[] readBytes(Reader input, String encoding)
/*     */     throws IOException
/*     */   {
/* 505 */     FastByteArrayOutputStream output = new FastByteArrayOutputStream();
/*     */ 
/* 507 */     copy(input, output, encoding);
/*     */ 
/* 509 */     return output.toByteArray();
/*     */   }
/*     */ 
/*     */   public static byte[] readBytes(Reader input, String encoding, int byteCount)
/*     */     throws IOException
/*     */   {
/* 516 */     FastByteArrayOutputStream output = new FastByteArrayOutputStream();
/*     */ 
/* 518 */     copy(input, output, encoding, byteCount);
/*     */ 
/* 520 */     return output.toByteArray();
/*     */   }
/*     */ 
/*     */   public static char[] readChars(InputStream input)
/*     */     throws IOException
/*     */   {
/* 529 */     FastCharArrayWriter output = new FastCharArrayWriter();
/*     */ 
/* 531 */     copy(input, output);
/*     */ 
/* 533 */     return output.toCharArray();
/*     */   }
/*     */ 
/*     */   public static char[] readChars(InputStream input, int charCount)
/*     */     throws IOException
/*     */   {
/* 540 */     FastCharArrayWriter output = new FastCharArrayWriter();
/*     */ 
/* 542 */     copy(input, output, charCount);
/*     */ 
/* 544 */     return output.toCharArray();
/*     */   }
/*     */ 
/*     */   public static char[] readChars(InputStream input, String encoding)
/*     */     throws IOException
/*     */   {
/* 551 */     FastCharArrayWriter output = new FastCharArrayWriter();
/*     */ 
/* 553 */     copy(input, output, encoding);
/*     */ 
/* 555 */     return output.toCharArray();
/*     */   }
/*     */ 
/*     */   public static char[] readChars(InputStream input, String encoding, int charCount)
/*     */     throws IOException
/*     */   {
/* 562 */     FastCharArrayWriter output = new FastCharArrayWriter();
/*     */ 
/* 564 */     copy(input, output, encoding, charCount);
/*     */ 
/* 566 */     return output.toCharArray();
/*     */   }
/*     */ 
/*     */   public static char[] readChars(Reader input)
/*     */     throws IOException
/*     */   {
/* 572 */     FastCharArrayWriter output = new FastCharArrayWriter();
/*     */ 
/* 574 */     copy(input, output);
/*     */ 
/* 576 */     return output.toCharArray();
/*     */   }
/*     */ 
/*     */   public static char[] readChars(Reader input, int charCount)
/*     */     throws IOException
/*     */   {
/* 583 */     FastCharArrayWriter output = new FastCharArrayWriter();
/*     */ 
/* 585 */     copy(input, output, charCount);
/*     */ 
/* 587 */     return output.toCharArray();
/*     */   }
/*     */ 
/*     */   public static boolean compare(InputStream input1, InputStream input2)
/*     */     throws IOException
/*     */   {
/* 608 */     if (!(input1 instanceof BufferedInputStream))
/*     */     {
/* 610 */       input1 = new BufferedInputStream(input1);
/*     */     }
/*     */ 
/* 614 */     if (!(input2 instanceof BufferedInputStream))
/*     */     {
/* 616 */       input2 = new BufferedInputStream(input2);
/*     */     }
/*     */ 
/* 620 */     int ch = input1.read();
/*     */ 
/* 622 */     while (ch != -1)
/*     */     {
/* 624 */       int ch2 = input2.read();
/*     */ 
/* 626 */       if (ch != ch2)
/*     */       {
/* 628 */         return false;
/*     */       }
/*     */ 
/* 632 */       ch = input1.read();
/*     */     }
/*     */ 
/* 636 */     int ch2 = input2.read();
/*     */ 
/* 638 */     return ch2 == -1;
/*     */   }
/*     */ 
/*     */   public static boolean compare(Reader input1, Reader input2)
/*     */     throws IOException
/*     */   {
/* 656 */     if (!(input1 instanceof BufferedReader))
/*     */     {
/* 658 */       input1 = new BufferedReader(input1);
/*     */     }
/*     */ 
/* 662 */     if (!(input2 instanceof BufferedReader))
/*     */     {
/* 664 */       input2 = new BufferedReader(input2);
/*     */     }
/*     */ 
/* 668 */     int ch = input1.read();
/*     */ 
/* 670 */     while (ch != -1)
/*     */     {
/* 672 */       int ch2 = input2.read();
/*     */ 
/* 674 */       if (ch != ch2)
/*     */       {
/* 676 */         return false;
/*     */       }
/*     */ 
/* 680 */       ch = input1.read();
/*     */     }
/*     */ 
/* 684 */     int ch2 = input2.read();
/*     */ 
/* 686 */     return ch2 == -1;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.io.StreamUtil
 * JD-Core Version:    0.6.0
 */