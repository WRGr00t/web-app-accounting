<#import "parts/common.ftlh" as c>
<#import "parts/login.ftlh" as l>
<@c.page>
<h4>Список пользователей</h4>
<table class="table table-striped">
    <thead>
        <tr>
            <th scope="col">Пользователь</th>
            <th scope="col">Email</th>
            <th scope="col">Роли</th>
            <th scope="col">Операции</th>
       </tr>
   </thead>
    <tbody>
        <#list users as user>
        <tr scope="row">
            <td>${user.username}</td>
            <#if user.email??>
                <td>${user.email}</td>
            <#else>
                <td>Email не установлен</td>
            </#if>

            <td><#list user.roles as role>${role}<#sep>, </#list></td>
            <td><a href="/user/${user.id}">edit</a></td>
        </tr>
        </#list>
    </tbody>
</table>
</@c.page>