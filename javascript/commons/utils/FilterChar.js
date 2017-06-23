function FilterChar () {
	
}
FilterChar.filterChinese=function filterChinese(desc){
      
            var error=desc.substring(desc.lastIndexOf(':')+1);
            error=error.substring(error.lastIndexOf('：')+1);
//            var str1=error.match(/[\u4e00-\u9fa5]/g);
//            var str2=(str1+'').replace(/[,"]/g,"").replace("null","");
//            var str2=(str1+'').replace("null","");
            var rows=error.lastIndexOf('not exactly one row');
            if(rows!=-1){
            	error='子查询多余一行';
            }
            return error;   
    }