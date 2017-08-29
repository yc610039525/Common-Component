/*     */ package com.boco.common.util.io;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.Reader;
/*     */ import java.io.Writer;
/*     */ import java.net.URL;
/*     */ 
/*     */ public class FileHelper
/*     */ {
/*     */   public static void rename(String oldFileName, String newFileName)
/*     */   {
/*  39 */     File newFile = new File(newFileName);
/*  40 */     newFile.delete();
/*  41 */     File oldFile = new File(oldFileName);
/*  42 */     oldFile.renameTo(newFile);
/*  43 */     oldFile.delete();
/*     */   }
/*     */ 
/*     */   public static String getPathSeparator() {
/*  47 */     return File.separator;
/*     */   }
/*     */ 
/*     */   public static boolean isExist(String pathName) {
/*  51 */     File file = new File(pathName);
/*  52 */     return file.exists();
/*     */   }
/*     */ 
/*     */   public static String[] getFileNamesInDir(String dirPath) {
/*  56 */     File file = new File(dirPath);
/*  57 */     String[] fileNames = file.list();
/*  58 */     return fileNames;
/*     */   }
/*     */ 
/*     */   public static URL getFileURL(String filename) {
/*  62 */     return FileHelper.class.getClassLoader().getResource(filename);
/*     */   }
/*     */ 
/*     */   public static InputStream getFileStream(String filename) {
/*  66 */     return FileHelper.class.getClassLoader().getResourceAsStream(filename);
/*     */   }
/*     */ 
/*     */   public static void copyFile(File targetFile, File file)
/*     */   {
/*     */     try
/*     */     {
/*  76 */       FileInputStream inFile = new FileInputStream(file);
/*  77 */       FileOutputStream outFile = new FileOutputStream(targetFile);
/*  78 */       byte[] buffer = new byte[1024];
/*  79 */       int i = 0;
/*  80 */       while ((i = inFile.read(buffer)) != -1) {
/*  81 */         outFile.write(buffer, 0, i);
/*     */       }
/*  83 */       inFile.close();
/*  84 */       outFile.close();
/*     */     }
/*     */     catch (Exception ex)
/*     */     {
/* 101 */       ex.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void copyFile(File src, File des, String encoding) throws Exception
/*     */   {
/* 107 */     if (!des.exists()) {
/* 108 */       des.createNewFile();
/*     */     }
/* 110 */     String DEFAULT_ENCODING = "UTF-8";
/* 111 */     String encodingUse = encoding == null ? DEFAULT_ENCODING : encoding;
/* 112 */     Reader in = new BufferedReader(new InputStreamReader(new FileInputStream(src)));
/*     */ 
/* 115 */     Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(des), encodingUse));
/*     */ 
/* 118 */     char[] buffer = new char[4096];
/* 119 */     int readBytes = -1;
/* 120 */     while ((readBytes = in.read(buffer)) != -1) {
/* 121 */       out.write(buffer, 0, readBytes);
/*     */     }
/* 123 */     out.flush();
/*     */ 
/* 125 */     out.close();
/* 126 */     in.close();
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.io.FileHelper
 * JD-Core Version:    0.6.0
 */