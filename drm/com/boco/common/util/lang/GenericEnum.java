/*    */ package com.boco.common.util.lang;
/*    */ 
/*    */ import com.boco.common.util.except.UserException;
/*    */ import java.io.Serializable;
/*    */ import java.util.Collection;
/*    */ import java.util.Iterator;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ import java.util.TreeMap;
/*    */ 
/*    */ public class GenericEnum<E>
/*    */   implements Serializable
/*    */ {
/* 28 */   private final TreeMap<E, String> enumMap = new TreeMap();
/*    */ 
/*    */   public String getName(E value)
/*    */   {
/* 34 */     return (String)this.enumMap.get(value);
/*    */   }
/*    */ 
/*    */   public E getValue(String name) {
/* 38 */     Object value = null;
/* 39 */     if (this.enumMap.containsValue(name)) {
/* 40 */       Iterator i = this.enumMap.keySet().iterator();
/* 41 */       while (i.hasNext()) {
/* 42 */         Object key = i.next();
/* 43 */         String enumName = (String)this.enumMap.get(key);
/* 44 */         if (enumName.equals(name)) {
/* 45 */           value = key;
/* 46 */           break;
/*    */         }
/*    */       }
/*    */     } else {
/* 50 */       throw new UserException("[" + getClass().getSimpleName() + "]未知枚举名称：" + name);
/*    */     }
/*    */ 
/* 53 */     return value;
/*    */   }
/*    */ 
/*    */   public String[] getAllNames() {
/* 57 */     String[] names = new String[this.enumMap.size()];
/* 58 */     this.enumMap.values().toArray(names);
/* 59 */     return names;
/*    */   }
/*    */ 
/*    */   public void putEnum(E value, String name) {
/* 63 */     this.enumMap.put(value, name);
/*    */   }
/*    */ 
/*    */   public Map<E, String> getAllEnum() {
/* 67 */     return this.enumMap;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.lang.GenericEnum
 * JD-Core Version:    0.6.0
 */