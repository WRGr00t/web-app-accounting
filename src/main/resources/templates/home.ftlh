<#import "parts/common.ftlh" as c>
<#import "parts/login.ftlh" as l>

<@c.page>
    <div>
        Hello, user!
    </div>
    <div>
        <a href="/inshift?date=${today}">Сегодня в смене</a>
        <a href="/inshift?date=${tomorrow}">>>></a>
    </div>
</@c.page>