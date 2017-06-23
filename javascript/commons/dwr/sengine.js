dwr.engine._getObjectClassName = function(obj) {
  // Try to find the classname by stringifying the object's constructor
  // and extract <class> from "function <class>".
  if (obj && obj.constructor && obj.constructor.toString)
  {
  	try {
  		var str = obj.constructor.toString();
	    var regexpmatch = str.match(/function\s+(\w+)/);
	    if (regexpmatch && regexpmatch.length == 2) {
	      return regexpmatch[1];
	    }
  	}catch(e) {
  		return 'Object';
  	}
  }
}