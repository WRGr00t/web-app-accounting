<#import "parts/common.ftlh" as c>
<#import "parts/login.ftlh" as l>

<@c.page>
<div>
    <@l.logout />
</div>
    <div>
        Hello, user!
    </div>
    <div>
        <span><a href="/user">User list</a></span><br>
        <a href="/inshift?date=${today}">Сегодня в смене</a>
    </div>
</@c.page>