INSERT INTO T_SYS_P_MENU
  (CUID,
   MENU_ID,
   LABEL_CN,
   SYS_CODE,
   RELATED_PARENT_MENU_ID,
   HANDLER,
   SEQUENCE_NUM,
   DISABLED,
   LEVEL_NUM,
   ICON_CLS,
   EXPANDED)
VALUES
  ('RES_INDEX_CROSS_DEV_TOPOLINK',
   'RES_INDEX_CROSS_DEV_TOPOLINK',
   '交叉机汇聚网元拓扑连接',
   'TNMS',
   'RES_INDEX',
   '/cmp_res/grid/ResGridPanel.jsp?code=service_dict_maintain.CROSS_DEV_TOPOLINK',
   0,
   0,
   3,
   NULL,
   0);


insert into T_ELT_FILE_DEFINE (FILE_PATH, CUID, DATA_BO, HEAD_BO, SHEET_NUM, TITLE_ROW_NUM, DATA_ROW_NUM, TEMPLATE_CUID, FILE_BO, TEMP_TABLE, CALCULATE_BO)
values ('\cmp_res\import\model\CROSS_DEV_TOPOLINK.xls', '21', 'CrossDevTempDataHandler', 'CrossDevHeadDefineHandler', '0', '0', '1', 'CROSS_DEV_TOPOLINK', 'CrossDevFileDataHandler', 'T_ELT_ATTEMP_TRAPH', 'CrossDevCalculateHandler');



