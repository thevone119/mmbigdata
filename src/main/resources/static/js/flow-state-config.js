
var flow_state_code_config_map={};
var flow_state_name_config_map={};
(function () {
    var sn = function () {
        return new sn.prototype.init();
    };

    sn.prototype = {
        init: function () {
            try{
                $.ajax({ url: "/api/flow/kdorderquery/queryAllConfig",type: 'get',dataType:'json', async:false,success: function (d, status) {
                    if(d.success){
                        for(var i=0;i<d.data.length;i++){
                            flow_state_code_config_map[d.data[i].name] = d.data[i].code;
                            flow_state_name_config_map[d.data[i].code] = d.data[i].name;
                        }
                    }
                }});
            }catch(e){}
            return this;
        },
        queryName(code){
            if(code==null){
                return null;
            }
            var v = flow_state_name_config_map[code];
            if(v!=null && v.length>0){
                return v;
            }
            return code;
        },
        queryCode(name){
            if(name==null){
                return null;
            }
            var v = flow_state_code_config_map[name];
            if(v!=null && v.length>0){
                return v;
            }
            return name;
        }
    }
    sn.prototype.init.prototype = sn.prototype;
    window.FLOWCFG = sn();
})();



