<#macro head >
<!DOCTYPE html>
<html>
<head>
    <#include "baseHeader.ftl" />
<#nested>
</head>
</#macro>

<#macro content >
<body>
    <#nested>
</body>
</#macro>

<#macro script >
    <#nested>
</html>
</#macro>
<#--这个模板太超前，一般人怕不好维护，所以废弃了-->


