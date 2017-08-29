/*    */ package com.boco.common.util.xml;
/*    */ 
/*    */ import com.boco.common.util.io.FileHelper;
/*    */ import java.io.InputStream;
/*    */ import java.net.URL;
/*    */ import javax.xml.bind.JAXBContext;
/*    */ import javax.xml.bind.JAXBElement;
/*    */ import javax.xml.bind.Unmarshaller;
/*    */ 
/*    */ public abstract class AbstractXmlModel
/*    */ {
/*    */   private Object rootModel;
/*    */   private String packageName;
/*    */   protected JAXBContext jaxbContext;
/*    */ 
/*    */   public AbstractXmlModel(String packageName)
/*    */     throws Exception
/*    */   {
/* 36 */     this.packageName = packageName;
/* 37 */     this.jaxbContext = JAXBContext.newInstance(packageName);
/*    */   }
/*    */ 
/*    */   public AbstractXmlModel(String packageName, String modelFileName) throws Exception {
/* 41 */     this(packageName);
/* 42 */     loadModel(modelFileName);
/*    */   }
/*    */ 
/*    */   public void loadModel(String modelFileName) throws Exception {
/* 46 */     Unmarshaller unmarsh = this.jaxbContext.createUnmarshaller();
/* 47 */     InputStream in = null;
/*    */     try {
/* 49 */       in = FileHelper.getFileStream(modelFileName);
/* 50 */       if (in == null)
/* 51 */         in = Thread.currentThread().getContextClassLoader().getResource(modelFileName).openStream();
/*    */     }
/*    */     catch (Exception e) {
/* 54 */       in = Thread.currentThread().getContextClassLoader().getResource(modelFileName).openStream();
/*    */     }
/* 56 */     this.rootModel = unmarsh.unmarshal(in);
/*    */   }
/*    */ 
/*    */   protected Object getRootModel() {
/* 60 */     return ((JAXBElement)this.rootModel).getValue();
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.xml.AbstractXmlModel
 * JD-Core Version:    0.6.0
 */