/*
* data used for some of the javascript tree examples. share! :-)
* 
* if you want a shorter code, check the json (JavaScript Object Notation) example at
* http://www.blueshoes.org/_bsJavascript/components/tree/examples/example_json.html
*/

	var a = new Array;
	a[0] = new Array;
	a[0]['caption']          = "yahoo133311";
	a[0]['url']              = "http://www.yahoo.com/";
	a[0]['target']           = "_blank";
	a[0]['isOpen']           = true;
	a[0]['isChecked']        = 2;
	a[0]['checkboxName']     = "chktest";
	a[0]['children']         = new Array;
	a[0]['children'][0]      = new Array;
	a[0]['children'][0]['caption']  = "yahoo sports";
	a[0]['children'][0]['url']      = "http://sports.yahoo.com/";
	a[0]['children'][0]['target']   = "_blank";
	a[0]['children'][0]['children'] = new Array;
	a[0]['children'][0]['children'][0] = new Array;
	a[0]['children'][0]['children'][0]['caption']  = "baseball";
	a[0]['children'][0]['children'][1] = new Array;
	a[0]['children'][0]['children'][1]['caption']  = "soccer";
	a[0]['children'][0]['children'][2] = new Array;
	a[0]['children'][0]['children'][2]['caption']  = "hockey";
	a[0]['children'][1]      = new Array;
	a[0]['children'][1]['caption']  = "yahoo finance";
	a[0]['children'][1]['url']      = "http://finance.yahoo.com/";
	a[0]['children'][1]['target']   = "_blank";
	a[1] = new Array;
	a[1]['caption']     = "microsoft";
	a[1]['children']    = new Array;
	a[1]['children'][0] = new Array;
	a[1]['children'][0]['caption']  = "knowledge base";
	a[1]['children'][0]['url']      = "http://kb.microsoft.com/";
	a[1]['children'][0]['target']   = "_self";
	a[1]['children'][0]['children'] = new Array;
	a[1]['children'][0]['children'][0] = new Array;
	a[1]['children'][0]['children'][0]['caption']  = "blah";
	a[1]['children'][0]['children'][0]['url']      = "http://www.blah.com/";
	a[1]['children'][0]['children'][0]['target']   = "_self";
	
	a[2] = new Array;
	a[2]['caption']     = "microsoft";