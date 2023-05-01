<#import "parts/common.ftlh" as c>
<#import "parts/login.ftlh" as l>
<#include "parts/security.ftlh">
<@c.page>
    <p class="h4">Привет, ${name}!</p>
    <div>
        <span>Это страница сервиса уточнения времени работы сотрудников на сменах, анализа наполненности смены и учета нагрузки сотрудников.</span><br>
        <#if !known>
            <b>Для продолжения работы необходимо авторизоваться.</b>
        </#if>
    </div>

</@c.page>