
SELECT * FROM T_CFG_SHEET S WHERE S.SHEET_CODE = 'ORDER';--SHEET_CODE
   |    |
   |    | RELATED_SHEET_CFG_CUID
   |    |
SELECT * FROM T_CFG_TASK where RELATED_SHEET_CFG_CUID='55';--99
   |
	 | 
SELECT * FROM T_ACT_SHEET WHERE RELATED_SHEET_CFG_CUID='55'(工单处于什么流程) 
	 ｜	                                                 | T_ACT_SHEET(RELATED_ORDER_CUID)
 --## A---->B    AB环节关系                              |            |
   ｜RELATED_SHEET_CFG_CUID                             | 
		                                                  T_IFACE_TRAPH(派单RELATED_APPLYSHEET_CUID)
	 ｜                                                                |
SELECT * FROM T_CFG_TASK_REL WHERE RELATED_SHEET_CFG_CUID='99'  --FROM_CODE/TO_CODE/SEQ_CODE /EXPRESS
                                                                     |
																																		 |
                                                     T_ACT_ORDER(派单fromcode)
																										 T_ACT_ORDER_DETAIL
       
SELECT * FROM T_ACT_ORDER;

SELECT * FROM T_ACT_ORDER_DETAIL;


SELECT SUBSTR('DISTRICT-00001-00009-00001', 1, 20) FROM DUAL;
SELECT * FROM T_CFG_SHEET CS;

SELECT * FROM T_CFG_TASK CT WHERE CUID='2-2';

SELECT * FROM T_CFG_TASK_REL CTR WHERE RELATED_SHEET_CFG_CUID='55';

SELECT * FROM T_ACT_SHEET;

SELECT * FROM T_ACT_TASK;

SELECT * FROM T_ACT_TASK_LINK;

select * from T_SYS_P_ROLE_REL;
	 
select * from T_SYS_P_ROLE;    

SELECT DISTINCT STATE FROM T_TASK_TO_SERVICE;

SELECT * FROM ATTEMP_TRAPH


SELECT COUNT(*)
  FROM T_TASK_TO_SERVICE, T_ACT_TASK T
 WHERE RELATED_TASK_CUID = T.CUID
 GROUP BY T.CUID
 
 
 SELECT DISTINCT COUNT(*)
   FROM T_TASK_TO_SERVICE S, ATTEMP_TRAPH T
  WHERE S.RELATED_SERVICE_CUID = T.CUID
  GROUP BY T.CUID   
  
  
  
															 |T_ACT_TASK_LINK===>T_SYS_P_ROLE_REL /SYS_USER/ORGANIZATION
							 irmsSheetId		 |
								IRMSTITLE			 | (dist/org/type/design_type)
 TRAPH_IFACE==>T_ACT_ORDER===>sheet==>task===>task_service===>traph 
  OUTSIDE_KEY_ID	|						|        |        	|           
			|						|						|        |state(end,run),SEQ_CODE,assign
			|						|						|state(end,run)	   	|
			|		T_ACT_ORDER_DETAIL						          |STATE(重新设计..)
			|
RELATED_APPLYSHEET_CUID






							
